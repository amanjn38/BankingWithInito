package in.finances.bankingwithinito.activities;

import static in.finances.bankingwithinito.models.SharedClass.ROOT_UID;
import static in.finances.bankingwithinito.models.SharedClass.SIGNUP;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

import in.finances.bankingwithinito.R;

public class LoginActivity extends AppCompatActivity {

    private boolean fromRegisterActivity;
    private String email, password, errMsg = "", customerUID, customerUIDRegister;
    private EditText cUID;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private TextView signup;
    private String login_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        signup = findViewById(R.id.sign_up);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        login_type = getIntent().getStringExtra("login_type");
//        login_type = "customer";
        cUID = findViewById(R.id.email);
        customerUID = cUID.getText().toString();
        fromRegisterActivity = getIntent().getBooleanExtra("fromRegisterActivity", false);
        if (fromRegisterActivity) {
            customerUIDRegister = getIntent().getStringExtra("customerUID");
        }
        if (login_type.equals("customer")) {
            if (auth.getCurrentUser() == null) {
                System.out.println("Working");
                progressDialog.setTitle("Authenticating...");
                progressDialog.setMessage("Please wait.. This may take a moment..");
                findViewById(R.id.sign_up).setOnClickListener(e -> {
                    Intent login = new Intent(this, LoginActivity.class);
                    startActivityForResult(login, SIGNUP);
                });

                findViewById(R.id.login).setOnClickListener(h -> {
                    if (checkFields()) {
                        progressDialog.setTitle("Authenticating...");
                        progressDialog.setMessage("Please wait.. This may take a moment..");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        customerUID = cUID.getText().toString();
                        FirebaseFirestore.getInstance().collection("customers").document(customerUID).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Retrieve data as a HashMap
                                    HashMap<String, Object> data = (HashMap<String, Object>) document.getData();
                                    String email = data.get("e").toString();
                                    login(email);
//                                    Log.d(TAG, "Data: " + data);
                                } else {
//                                    Log.d(TAG, "No such document");
                                    Toast.makeText(LoginActivity.this, "Please enter correct username", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });

                    } else {
                        Snackbar.make(findViewById(R.id.email), errMsg, Snackbar.LENGTH_SHORT).show();
                    }
                });
            } else {
                System.out.println("Working22");
                ROOT_UID = auth.getUid();

                Intent fragment = new Intent(this, MainActivity.class);
                fragment.putExtra("customerUID", customerUIDRegister);
                startActivity(fragment);
                finish();
            }
        } else if (login_type.equals("admin")) {
            findViewById(R.id.login).setOnClickListener(view -> {
                if (checkFields()) {
                    progressDialog.setTitle("Authenticating...");
                    progressDialog.setMessage("Please wait.. This may take a moment..");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    customerUID = cUID.getText().toString();
                    if (customerUID.equals("admin")) {
                        admin_login("testing@inito.com");
                    } else {
                        Toast.makeText(this, "Please enter correct credentials", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                }
            });

        }

        signup.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

    }

    private void login(String email) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        ROOT_UID = auth.getUid();
                        progressDialog.dismiss();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("customerUID", customerUID);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    } else {
                        Log.d("LOGIN", "signInWithCredential:failure" + task.getException());
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(R.id.email), "Authentication Failed. Try again. Check your Internet Connection", Snackbar.LENGTH_SHORT).show();
//                                    Intent fragment = new Intent(this, in.example.restaurant.Startup.SelectionActivity.class);
//                                    startActivity(fragment);
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(R.id.email), "Authentication Failed. Try again.", Snackbar.LENGTH_SHORT).show();
                });

    }

    private void admin_login(String email) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        ROOT_UID = auth.getUid();
                        progressDialog.dismiss();

                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    } else {
                        Log.d("LOGIN", "signInWithCredential:failure" + task.getException());
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(R.id.email), "Authentication Failed. Try again. Check your Internet Connection", Snackbar.LENGTH_SHORT).show();
//                                    Intent fragment = new Intent(this, in.example.restaurant.Startup.SelectionActivity.class);
//                                    startActivity(fragment);
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(R.id.email), "Authentication Failed. Try again.", Snackbar.LENGTH_SHORT).show();
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == SIGNUP) {
            Intent fragment = new Intent(this, MainActivity.class);
            startActivity(fragment);
            finish();
        }
    }

    public boolean checkFields() {
        email = ((EditText) findViewById(R.id.email)).getText().toString();
        password = ((EditText) findViewById(R.id.password)).getText().toString();

        if (email.trim().length() == 0) {
            errMsg = "Invalid Mail";
            return false;
        }

        if (password.trim().length() == 0) {
            errMsg = "Fill password";
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}