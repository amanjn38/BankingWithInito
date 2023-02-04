package in.finances.bankingwithinito.activities;

import static in.finances.bankingwithinito.Utils.generateAccountNumber;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.Individual_Account;
import in.finances.bankingwithinito.models.Transaction;

public class SavingsAccountActivity extends AppCompatActivity {

    public String balance, error_msg, accountType;
    private TextView createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_account);

        createAccount = findViewById(R.id.createAccount);
        accountType = getIntent().getStringExtra("accountType");

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        createAccount.setOnClickListener(view -> {
            if (accountType.equals("SavingsAccount")) {
                if (checkFieldsForSavingsAccount()) {
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Please wait...");
                    progressDialog.setMessage("While we are creating your account...");
                    balance = ((EditText) findViewById(R.id.amount)).getText().toString();
                    Double b = Double.parseDouble(balance);
                    HashMap<String, Object> infor = new HashMap<>();
                    double awiad = 0.0;
                    int ttatm = 0;
                    int dt = 0;
                    Long lastOpened = 0L;
                    Long lastInterestCal = 0L;

                    ArrayList<Transaction> transactions = new ArrayList<>();

                    infor.put("bal", b);
                    infor.put("awiad", awiad);
                    infor.put("ttatm", ttatm);
                    infor.put("dt", dt);
                    infor.put("lastTransaction", lastOpened);
                    infor.put("transactions", transactions);
                    infor.put("lastInterestCal", lastInterestCal);
                    String card_number = generateCardNumber();
                    String expiryDate = generateExpiry();
                    String cvv = generateCVV();
                    String accNum = String.valueOf(generateAccountNumber());
                    infor.put("cardNumber", card_number);
                    infor.put("expiry", expiryDate);
                    infor.put("cvv", cvv);
                    infor.put("accnum", accNum);
                    infor.put("dateCreated", System.currentTimeMillis());
                    Individual_Account individual_account = new Individual_Account(accNum, "savings", balance);

                    SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
                    String customerUID = sharedPreferences.getString("customerUID", "");
                    FirebaseFirestore.getInstance().collection("customers_account_spec").document(customerUID).collection("savings").document(accNum).set(infor).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SavingsAccountActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });

                    FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").document(accNum).set(individual_account).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SavingsAccountActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SavingsAccountActivity.this, MainActivity.class);
                            intent.putExtra("customerUID", customerUID);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });

                }
            } else if (accountType.equals("CurrentAccount")) {
                if (checkFieldsForCurrentAccount()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
                    String customerUID = sharedPreferences.getString("customerUID", "");
                    FirebaseFirestore.getInstance().collection("customers").document(customerUID).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            long dob = (long) documentSnapshot.get("dob");

                            Calendar birthDate = Calendar.getInstance();
                            birthDate.setTimeInMillis(dob);

                            Calendar today = Calendar.getInstance();
                            int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
                            if (age >= 18) {
                                ProgressDialog progressDialog = new ProgressDialog(this);
                                progressDialog.setTitle("Please wait...");
                                progressDialog.setMessage("While we are creating your account...");
                                balance = ((EditText) findViewById(R.id.amount)).getText().toString();
                                HashMap<String, Object> infor = new HashMap<>();
                                int tt = 0;
                                Long lastOpened = 0L;
                                ArrayList<Transaction> transactions = new ArrayList<>();

                                double b = Double.parseDouble(balance);
                                infor.put("bal", b);
                                infor.put("tt", tt); //total transactions in a month
                                infor.put("lastopened", lastOpened);
                                infor.put("transactions", transactions);
                                String accNum = String.valueOf(generateAccountNumber());

                                infor.put("accnum", accNum);
                                Individual_Account individual_account = new Individual_Account(accNum, "current", balance);
                                FirebaseFirestore.getInstance().collection("customers_account_spec").document(customerUID).collection("current").document(accNum).set(infor).addOnCompleteListener(task11 -> {
                                    if (task11.isSuccessful()) {
                                        progressDialog.dismiss();
                                    }
                                });
                                FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").document(accNum).set(individual_account).addOnCompleteListener(task22 -> {
                                    if (task22.isSuccessful()) {
                                        Toast.makeText(SavingsAccountActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(SavingsAccountActivity.this, MainActivity.class);
                                        intent.putExtra("customerUID", customerUID);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                Toast.makeText(this, "The minimum age required to open a loan account is 18 years", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });


    }

    private boolean checkFieldsForSavingsAccount() {
        balance = ((EditText) findViewById(R.id.amount)).getText().toString();

        if (balance.trim().length() == 0) {
            error_msg = "Please enter the amount";
            Toast.makeText(SavingsAccountActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        } else if (Integer.parseInt(balance) < 10000) {
            error_msg = "Minimum amount to open a savings account is Rs. 10000";
            Toast.makeText(SavingsAccountActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private boolean checkFieldsForCurrentAccount() {
        balance = ((EditText) findViewById(R.id.amount)).getText().toString();


        if (balance.trim().length() == 0) {
            error_msg = "Please enter the amount";
            Toast.makeText(SavingsAccountActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        } else if (Integer.parseInt(balance) < 100000) {
            error_msg = "Minimum amount to open a current account is Rs. 100000";
            Toast.makeText(SavingsAccountActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private String generateCardNumber() {
        String cardNumber = "";
        for (int i = 0; i < 16; i++) {
            cardNumber += (int) (Math.random() * 10);
        }
        return cardNumber;
    }

    private String generateExpiry() {
        int month = (int) (Math.random() * 12) + 1;
        int year = (int) (Math.random() * 5) + 2025;
        return month + "/" + year;
    }

    private String generateCVV() {
        String cvv = "";
        for (int i = 0; i < 3; i++) {
            cvv += (int) (Math.random() * 10);
        }
        return cvv;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SavingsAccountActivity.this, CreateNewAccountActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}