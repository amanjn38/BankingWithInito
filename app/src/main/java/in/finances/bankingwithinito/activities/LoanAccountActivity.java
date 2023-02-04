package in.finances.bankingwithinito.activities;

import static in.finances.bankingwithinito.Utils.generateAccountNumber;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.Individual_Account;

public class LoanAccountActivity extends AppCompatActivity {

    private TextView generate_emi, createNewAccount;
    private Spinner spinner, time;
    private EditText amount, emi;
    private Double interest_rate;
    private int numMonths;
    private String balance, error_msg, customerUID;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_account);

        progressDialog = new ProgressDialog(this);
        spinner = findViewById(R.id.spinner);
        generate_emi = findViewById(R.id.generate_emi);
        amount = findViewById(R.id.amount);
        time = findViewById(R.id.time);
        createNewAccount = findViewById(R.id.createNewAccount);
        emi = findViewById(R.id.emi);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        generate_emi.setOnClickListener(view -> {
            if (checkFieldsForLoanAccount()) {
                if (spinner.getSelectedItem().toString().equals("Home Loan ( Interest 7% )")) {
                    interest_rate = 7.0;
                } else if (spinner.getSelectedItem().toString().equals("Car Loan ( Interest 8% )")) {
                    interest_rate = 8.0;
                } else if (spinner.getSelectedItem().toString().equals("Personal Loan ( Interest 12% )")) {
                    interest_rate = 12.0;
                } else if (spinner.getSelectedItem().toString().equals("Business Loan ( Interest 15% )")) {
                    interest_rate = 15.0;
                }

                numMonths = Integer.parseInt(time.getSelectedItem().toString());
                numMonths = numMonths * 12;

                progressDialog.setTitle("Please wait..");
                progressDialog.setMessage("We are calculating the emi..");
                progressDialog.setCancelable(false);
                progressDialog.show();
                double emii = calculateEMI(Double.parseDouble(balance), interest_rate, numMonths);
                emi.setText(String.valueOf(emii));
                generate_emi.setVisibility(View.GONE);
                createNewAccount.setVisibility(View.VISIBLE);
                emi.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }

        });

        createNewAccount.setOnClickListener(view -> {
            if (checkFieldsForLoanAccount()) {
                SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
                customerUID = sharedPreferences.getString("customerUID", "");
                FirebaseFirestore.getInstance().collection("customers").document(customerUID).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        long dob = (long) documentSnapshot.get("dob");

                        Calendar birthDate = Calendar.getInstance();
                        birthDate.setTimeInMillis(dob);

                        Calendar today = Calendar.getInstance();
                        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
                        if (age >= 25) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            CollectionReference accountsRef = db.collection("customers_account_spec").document(customerUID).collection("savings");
                            accountsRef.get().addOnSuccessListener(querySnapshot -> {
                                if (querySnapshot.isEmpty()) {
                                    // check if the customer has a current account
                                    CollectionReference accountsRef1 = db.collection("customers_account_spec").document(customerUID).collection("current");
                                    accountsRef1.get().addOnSuccessListener(querySnapshot1 -> {
                                        if (querySnapshot1.isEmpty()) {
                                            Toast.makeText(LoanAccountActivity.this, "You should have a savings or current account to be eligible for loan", Toast.LENGTH_LONG).show();
                                        } else {
                                            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                                            CollectionReference accountsRef11 = db1.collection("customers_account_spec").document(customerUID).collection("savings");
                                            accountsRef11.get().addOnSuccessListener(querySnapshot11 -> {
                                                final int[] totalDeposit = {0};
                                                for (QueryDocumentSnapshot document : querySnapshot11) {
                                                    int balance = document.getDouble("bal").intValue();
                                                    totalDeposit[0] += balance;
                                                }
                                                // check if the customer has a current account
                                                CollectionReference accountsRef111 = db1.collection("customers_account_spec").document(customerUID).collection("current");
                                                accountsRef111.get().addOnSuccessListener(querySnapshot111 -> {
                                                    for (QueryDocumentSnapshot document : querySnapshot111) {
                                                        int balance = document.getDouble("bal").intValue();
                                                        totalDeposit[0] += balance;
                                                    }
                                                    double b = Double.parseDouble(balance);
                                                    if (b <= totalDeposit[0] * 40 / 100) {
                                                        progressDialog.setTitle("Please wait..");
                                                        progressDialog.setMessage("We are creating your account..");
                                                        progressDialog.setCancelable(false);
                                                        progressDialog.show();

                                                        HashMap<String, Object> infor = new HashMap<>();
                                                        double emii = calculateEMI(Double.parseDouble(balance), interest_rate, numMonths);
                                                        infor.put("emi", emii);
                                                        infor.put("time_period", numMonths / 12);
                                                        infor.put("interest_rate", interest_rate);
                                                        ArrayList<Transaction> transactions = new ArrayList<>();
                                                        infor.put("transactions", transactions);
                                                        String accNum = String.valueOf(generateAccountNumber());
                                                        infor.put("accNum", accNum);
                                                        infor.put("amount", b);
                                                        infor.put("lastInterestCalculationDate", new java.util.Date());
                                                        infor.put("lastTransaction", System.currentTimeMillis());
                                                        Individual_Account individual_account = new Individual_Account(accNum, "loan", balance);

                                                        FirebaseFirestore.getInstance().collection("customers_account_spec").document(customerUID).collection("loan").document(accNum).set(infor).addOnCompleteListener(task12 -> {
                                                            if (task12.isSuccessful()) {
                                                                Toast.makeText(LoanAccountActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
                                                            }
                                                        });

                                                        FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").document(accNum).set(individual_account).addOnCompleteListener(task13 -> {
                                                            if (task13.isSuccessful()) {
                                                                Toast.makeText(LoanAccountActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
                                                                progressDialog.dismiss();
                                                                Intent intent = new Intent(LoanAccountActivity.this, MainActivity.class);
                                                                intent.putExtra("customerUID", customerUID);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(intent);
                                                            }
                                                        });

                                                    } else {
                                                        Toast.makeText(LoanAccountActivity.this, "The loan amount cannot be greater than 40% of the total deposits", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            });

                                        }
                                    });
                                } else {
                                    FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                                    CollectionReference accountsRef1 = db1.collection("customers_account_spec").document(customerUID).collection("savings");
                                    accountsRef1.get().addOnSuccessListener(querySnapshot12 -> {
                                        final int[] totalDeposit = {0};
                                        for (QueryDocumentSnapshot document : querySnapshot12) {
                                            int balance = document.getDouble("bal").intValue();
                                            totalDeposit[0] += balance;
                                            System.out.println("totalDEPOSIT1" + totalDeposit[0]);

                                        }
                                        // check if the customer has a current account
                                        CollectionReference accountsRef112 = db1.collection("customers_account_spec").document(customerUID).collection("current");
                                        accountsRef112.get().addOnSuccessListener(querySnapshot121 -> {
                                            for (QueryDocumentSnapshot document : querySnapshot121) {
                                                int balance = document.getDouble("bal").intValue();
                                                totalDeposit[0] += balance;
                                                System.out.println("totalDEPOSIT2" + totalDeposit[0]);

                                            }
                                            double b = Double.parseDouble(balance);
                                            System.out.println("totalDEPOSIT" + totalDeposit[0]);
                                            if (b <= totalDeposit[0] * 40 / 100) {
                                                progressDialog.setTitle("Please wait..");
                                                progressDialog.setMessage("We are creating your account..");
                                                progressDialog.setCancelable(false);
                                                progressDialog.show();

                                                double b1 = Double.parseDouble(balance);
                                                HashMap<String, Object> infor = new HashMap<>();
                                                double emii = calculateEMI(b1, interest_rate, numMonths);
                                                infor.put("emi", emii);
                                                infor.put("time_period", numMonths / 12);
                                                infor.put("interest_rate", interest_rate);
                                                ArrayList<Transaction> transactions = new ArrayList<>();
                                                infor.put("transactions", transactions);
                                                String accNum = String.valueOf(generateAccountNumber());
                                                infor.put("accNum", accNum);
                                                infor.put("amount", b1);
                                                infor.put("bal", b1);
                                                infor.put("lastInterestCalculationDate", new java.util.Date());
                                                infor.put("lastTransaction", System.currentTimeMillis());
                                                Individual_Account individual_account = new Individual_Account(accNum, "loan", balance);

                                                FirebaseFirestore.getInstance().collection("customers_account_spec").document(customerUID).collection("loan").document(accNum).set(infor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task14) {
                                                        if (task14.isSuccessful()) {
                                                            Toast.makeText(LoanAccountActivity.this, "Your account has been successfully created", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });

                                                FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").document(accNum).set(individual_account).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task15) {
                                                        if (task15.isSuccessful()) {
                                                            Toast.makeText(LoanAccountActivity.this, "Your account has been successfully created", Toast.LENGTH_LONG).show();
                                                            progressDialog.dismiss();
                                                            Intent intent = new Intent(LoanAccountActivity.this, MainActivity.class);
                                                            intent.putExtra("customerUID", customerUID);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(LoanAccountActivity.this, "The loan amount cannot be greater than 40% of the total deposits", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    });

                                }
                            });

                        } else {
                            Toast.makeText(this, "The minimum age required to open a loan account is 25 years", Toast.LENGTH_LONG).show();
                        }


                    }
                });

            }
        });
        progressDialog.dismiss();
    }

    public static double calculateEMI(double p, double r, int t) {
        double emi;

//        System.out.println("testing" + p + " " + r + " " + t);
        r = r / (12 * 100); // one month interest

        emi = (p * r * (float) Math.pow(1 + r, t))
                / (float) (Math.pow(1 + r, t) - 1);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(emi));
    }

    private boolean checkFieldsForLoanAccount() {
        balance = ((EditText) findViewById(R.id.amount)).getText().toString();

        if (balance.trim().length() == 0) {
            error_msg = "Please enter the amount";
            Toast.makeText(LoanAccountActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        } else if (Integer.parseInt(balance) < 500000) {
            error_msg = "Minimum amount to open a loan account is Rs. 500000";
            Toast.makeText(LoanAccountActivity.this, error_msg, Toast.LENGTH_LONG).show();
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