package in.finances.bankingwithinito.activities;

import static in.finances.bankingwithinito.Utils.generateAccountNumber;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.Individual_Account;
import in.finances.bankingwithinito.models.Transaction;

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
        SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
        customerUID = sharedPreferences.getString("customerUID", "");
        generate_emi.setOnClickListener(view -> {
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
            double emii = calculateEMI(Double.parseDouble(amount.getText().toString()), interest_rate, numMonths);
            emi.setText(String.valueOf(emii));
            generate_emi.setVisibility(View.GONE);
            createNewAccount.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
        });

        createNewAccount.setOnClickListener(view -> {
            if (checkFieldsForLoanAccount()) {
                if (check()) {
                    String[] arr = check2();
                    String bal = arr[1];
                    balance = amount.getText().toString();
                    if (Double.parseDouble(balance) <= 0.4 * Double.parseDouble(bal) && Objects.equals(arr[0], "true")) {
                        progressDialog.setTitle("Please wait..");
                        progressDialog.setMessage("We are creating your account..");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        HashMap<String, Object> infor = new HashMap<>();
                        infor.put("emi", calculateEMI(Double.parseDouble(balance), interest_rate, numMonths));
                        infor.put("time_period", numMonths / 12);
                        infor.put("interest_rate", interest_rate);
                        ArrayList<Transaction> transactions = new ArrayList<>();
                        infor.put("transactions", transactions);
                        String accNum = String.valueOf(generateAccountNumber());
                        infor.put("accNum", accNum);
                        infor.put("amount", amount);
                        infor.put("lastInterestCalculationDate", new java.util.Date());
                        Individual_Account individual_account = new Individual_Account(accNum, "loan", balance);

                        FirebaseFirestore.getInstance().collection("customers_account_spec").document(customerUID).collection("loan").document(accNum).set(infor).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoanAccountActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
                            }
                        });

                        FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").document(accNum).set(individual_account).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoanAccountActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoanAccountActivity.this, MainActivity.class);
                                intent.putExtra("customerUID", customerUID);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

        });
        progressDialog.dismiss();
    }

    public static double calculateEMI(double p, double r, int t) {
        double emi;

        System.out.println("testing" + p + " " + r + " " + t);
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
            error_msg = "Minimum amount to open a savings account is Rs. 500000";
            Toast.makeText(LoanAccountActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private boolean check() {
        final boolean[] isOldEnough = new boolean[1];
        FirebaseFirestore.getInstance().collection("customers").document(customerUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    long dob = (long) documentSnapshot.get("dob");

                    Calendar birthDate = Calendar.getInstance();
                    birthDate.setTimeInMillis(dob);

                    Calendar today = Calendar.getInstance();
                    int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

                    if (age >= 25) {
                        isOldEnough[0] = true;
                    }
                }
            }
        });
        return isOldEnough[0];
    }

    private String[] check2() {
        final String[] hasAccount = new String[2];
        hasAccount[0] = "false";

        FirebaseFirestore.getInstance().collection("customers_account_spec").document(customerUID).collection("accounts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.getId().equals("savings") || documentSnapshot.getId().equals("current")) {
                            hasAccount[0] = "true";
                        }
                        if (documentSnapshot.contains("bal")) {
                            double bal = Double.parseDouble(documentSnapshot.getString("bal"));
                            double bal2 = Double.parseDouble(hasAccount[1]);
                            double total = bal + bal2;
                            hasAccount[1] = String.valueOf(total);
                        }
                    }

                }
            }
        });
        return hasAccount;

    }
}