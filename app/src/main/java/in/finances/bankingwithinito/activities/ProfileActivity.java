package in.finances.bankingwithinito.activities;

import static in.finances.bankingwithinito.models.SharedClass.Mail;
import static in.finances.bankingwithinito.models.SharedClass.Name;
import static in.finances.bankingwithinito.models.SharedClass.Phone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import in.finances.bankingwithinito.R;

public class ProfileActivity extends AppCompatActivity {

    private String name;
    private String mail;
    private String phone;
    private String currentPhotoPath;
    private String address;
    private double latitude, longitude;
    public static int AUTOCOMPLETE_SOURCE = 180;
    private EditText addressButton, nameEdit, phoneEdit, mailEdit;
    private boolean dialog_open = false;
    private boolean photoChanged = false;
    private String error_msg;
    private LatLng source;
    private FirebaseDatabase database;
    private final int RequestCode = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        database = FirebaseDatabase.getInstance();

        nameEdit = findViewById(R.id.name);
        phoneEdit = findViewById(R.id.phone2);
        mailEdit = findViewById(R.id.mail);
        getData();
        setTitle("DETE");
        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyAAzAER-HprZhx5zvmEYIjVlJfYSHj2-G8");
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

        addressButton = findViewById(R.id.address_modify);
        addressButton.setOnClickListener(l ->
                startActivityForResult(new Intent(ProfileActivity.this, MapPickerActivity.class), AUTOCOMPLETE_SOURCE));

        TextView confirm_reg = findViewById(R.id.back_order_button);
        confirm_reg.setOnClickListener(e -> {
            if (checkFields()) {
                storeDatabase();
            } else {
                Toast.makeText(getApplicationContext(), error_msg, Toast.LENGTH_LONG).show();
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getData() {
        SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
        String customerUID = sharedPreferences.getString("customerUID", "");
        FirebaseFirestore.getInstance().collection("customers").document(customerUID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot d = task.getResult();
                nameEdit.setText(d.get("n").toString());
                mailEdit.setText(d.get("e").toString());
                addressButton.setText(d.get("add").toString());
                phoneEdit.setText(d.get("ph").toString());
            }
        });

    }

    private boolean checkFields() {
        name = nameEdit.getText().toString();
        mail = mailEdit.getText().toString();
        phone = phoneEdit.getText().toString();
        address = addressButton.getText().toString();

        if (name.trim().length() == 0) {
            error_msg = "Fill name";
            return false;
        }
        if (phone.trim().length() == 10) {
            error_msg = "Fill phone number";
            return false;
        }

        if (address.trim().length() == 0) {
            error_msg = "Fill address";
            return false;
        }

        return true;
    }

    private void storeDatabase() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        Map<String, Object> profileMap = new HashMap<>();

        progressDialog.setTitle("Updating profile...");
        progressDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 1) && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            photoChanged = true;

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
                addressButton.setText(data.getStringExtra("name"));
                source = data.getParcelableExtra("location");
                latitude = source.latitude;
                longitude = source.longitude;
            }
        }

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
        savedInstanceState.putString(Phone, ((EditText) findViewById(R.id.phone2)).getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ((EditText) findViewById(R.id.name)).setText(savedInstanceState.getString(Name));
        ((EditText) findViewById(R.id.mail)).setText(savedInstanceState.getString(Mail));
        ((EditText) findViewById(R.id.phone2)).setText(savedInstanceState.getString(Phone));

    }
}