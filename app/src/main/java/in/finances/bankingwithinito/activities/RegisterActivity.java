package in.finances.bankingwithinito.activities;

import static in.finances.bankingwithinito.models.SharedClass.CUSTOMER_PATH;
import static in.finances.bankingwithinito.models.SharedClass.Mail;
import static in.finances.bankingwithinito.models.SharedClass.Name;
import static in.finances.bankingwithinito.models.SharedClass.Phone;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.CustomerDetails;

public class RegisterActivity extends AppCompatActivity {

    private boolean dialog_open = false;

    private String name, mail, phone, psw, address, error_msg;
    private boolean update;
    private EditText address1;
    private double latitude, longitude;
    private FirebaseDatabase database;
    public static int AUTOCOMPLETE_SOURCE = 180;
    DatabaseReference myRef;
    private TextView date;
    public String ROOT_UID = "";
    private LatLng source;
    private static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static Random random = new Random();
    private int year, month, day;
    private Date date1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        update = getIntent().getBooleanExtra("update", false);
        Places.initialize(RegisterActivity.this, "AIzaSyAzScFoOmgLSQsVPK7QT4btN9wIhjoP4qM");
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        address1 = findViewById(R.id.address);
        address1.setOnClickListener(view ->
                startActivityForResult(new Intent(RegisterActivity.this, MapPickerActivity.class), AUTOCOMPLETE_SOURCE));
        TextView generateUID = findViewById(R.id.generateUID);
        TextView confirm_reg = findViewById(R.id.sign_up);

        year = month = day = -1;

        date = findViewById(R.id.date);

        generateUID.setOnClickListener(view -> {
            if (checkFields()) {
                String username = generateId();
                EditText strusername = findViewById(R.id.username);
                strusername.setText(username);
                generateUID.setVisibility(View.GONE);
                confirm_reg.setVisibility(View.VISIBLE);
            }
        });

        date.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view2, year, monthOfYear, dayOfMonth) -> {
                        this.year = year;
                        this.day = dayOfMonth;
                        this.month = monthOfYear;
                        date.setText(generateDate(year, monthOfYear, dayOfMonth));
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        confirm_reg.setOnClickListener(e -> {
            if (checkFields()) {
                if (!update) {
                    auth.createUserWithEmailAndPassword(mail, psw).addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Registered successfully, Please verify your email.", Toast.LENGTH_LONG).show();
                                        ROOT_UID = auth.getUid();
                                        storeDatabase();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    });
                } else {
                    storeDatabase();
                }
            }
        });

    }


    private void storeDatabase() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        date1 = getDate(year, month, day);
        myRef = database.getReference(CUSTOMER_PATH + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        progressDialog.setTitle("Creating profile...");
        progressDialog.show();
        HashMap<String, Object> infor = new HashMap<>();

        String strmail = ((EditText) findViewById(R.id.mail)).getText().toString();
        String strname = ((EditText) findViewById(R.id.name)).getText().toString();
        String straddr = ((EditText) findViewById(R.id.address)).getText().toString();
        String strphone = ((EditText) findViewById(R.id.time_text)).getText().toString();
        String username = ((EditText) findViewById(R.id.username)).getText().toString();

//        infor.put("e", strmail);
//        infor.put("n", strname);
//        infor.put("add", straddr);
//        infor.put("ph", strphone);
//        infor.put("un", username);
//        infor.put("lt", latitude);
//        infor.put("lo", longitude);
//        infor.put("dob", date1);
//        infor.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        CustomerDetails customerDetails = new CustomerDetails(strname, straddr, strmail, strphone, username, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                latitude, longitude, date1);
        FirebaseFirestore.getInstance().collection("customers").document(username).set(customerDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Your data has been stored successfully", Toast.LENGTH_LONG).show();
                SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("customerUID", username);
                editor.apply();
                HashMap<String, String> map = new HashMap<>();
                map.put("username", username);
                FirebaseFirestore.getInstance().collection("customers_usernames").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.putExtra("fromRegisterActivity", true);
                            intent.putExtra("customerUID", username);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    private boolean checkFields() {
        name = ((EditText) findViewById(R.id.name)).getText().toString();
        mail = ((EditText) findViewById(R.id.mail)).getText().toString();
        phone = ((EditText) findViewById(R.id.time_text)).getText().toString();
        address = ((EditText) findViewById(R.id.address)).getText().toString();
        psw = ((EditText) findViewById(R.id.password)).getText().toString();

        if (mail.trim().length() == 0 || !android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            error_msg = "Invalid e-mail";
            Toast.makeText(RegisterActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        } else if (address.trim().length() == 0) {
            error_msg = "Fill Address";
            Toast.makeText(RegisterActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        } else if (name.trim().length() == 0) {
            error_msg = "Fill name";
            Toast.makeText(RegisterActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        } else if (phone.trim().length() == 0) {
            error_msg = "Invalid phone number";
            Toast.makeText(RegisterActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        } else if (year == -1) {
            error_msg = "Please enter your Date of Birth";
            Toast.makeText(RegisterActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        } else if (psw.trim().length() == 0) {
            error_msg = "Please enter Password";
            Toast.makeText(RegisterActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 1) && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

        }

        if (requestCode == AUTOCOMPLETE_SOURCE) {
            if (resultCode == RESULT_OK) {
                address1.setText(data.getStringExtra("name"));
                source = data.getParcelableExtra("location");
                latitude = source.latitude;
                longitude = source.longitude;
            }
        }
        // Log.i("TAG", "Place: " + place.getAddress());
        if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i("TAG", status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Name, ((EditText) findViewById(R.id.name)).getText().toString());
        savedInstanceState.putString(Mail, ((EditText) findViewById(R.id.mail)).getText().toString());
        savedInstanceState.putString(Phone, ((EditText) findViewById(R.id.time_text)).getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ((EditText) findViewById(R.id.name)).setText(savedInstanceState.getString(Name));
        ((EditText) findViewById(R.id.mail)).setText(savedInstanceState.getString(Mail));
        ((EditText) findViewById(R.id.time_text)).setText(savedInstanceState.getString(Phone));
    }

    private String generateId() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            id.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return id.toString();
    }

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }

    public static String generateDate(int year, int month, int day) {
        String date = day + "";
        switch (day % 10) {
            case 1:
                date += "st";
                break;
            case 2:
                date += "nd";
                break;
            case 3:
                date += "rd";
                break;
            default:
                date += "th";
        }
        switch (month) {
            case 0:
                date += " Jan";
                break;
            case 1:
                date += " Feb";
                break;
            case 2:
                date += " Mar";
                break;
            case 3:
                date += " Apr";
                break;
            case 4:
                date += " May";
                break;
            case 5:
                date += " June";
                break;
            case 6:
                date += " July";
                break;
            case 7:
                date += " Aug";
                break;
            case 8:
                date += " Sept";
                break;
            case 9:
                date += " Oct";
                break;
            case 10:
                date += " Nov";
                break;
            case 11:
                date += " Dec";
                break;
        }
        return date;
    }

}
