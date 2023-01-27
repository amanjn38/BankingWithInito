package in.finances.bankingwithinito.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.HashMap;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.Individual_Account;

public class LoanAccountActivity extends AppCompatActivity {

    private TextView generate_emi, createNewAccount;
    private Spinner spinner, time;
    private EditText amount, emi;
    private Double interest_rate;
    private int numMonths;
    private String balance, customerUID, error_msg;
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

        if (spinner.getSelectedItem().toString().equals("Home Loan ( Interest 7% )")) {
            interest_rate = 7.0;
        } else if (spinner.getSelectedItem().toString().equals("Car Loan ( Interest 8% )")) {
            interest_rate = 8.0;
        } else if (spinner.getSelectedItem().toString().equals("Personal Loan ( Interest 12% )")) {
            interest_rate = 12.0;
        } else if (spinner.getSelectedItem().toString().equals("Business Loan ( Interest 15% )")) {
            interest_rate = 15.0;
        }

        if (time.getSelectedItem().toString().equals("2")) {
            numMonths = 24;
        } else if (time.getSelectedItem().toString().equals("3")) {
            numMonths = 36;
        } else if (time.getSelectedItem().toString().equals("4")) {
            numMonths = 48;
        } else if (time.getSelectedItem().toString().equals("5")) {
            numMonths = 60;
        } else if (time.getSelectedItem().toString().equals("6")) {
            numMonths = 72;
        } else if (time.getSelectedItem().toString().equals("7")) {
            numMonths = 84;
        } else if (time.getSelectedItem().toString().equals("8")) {
            numMonths = 96;
        } else if (time.getSelectedItem().toString().equals("9")) {
            numMonths = 108;
        } else if (time.getSelectedItem().toString().equals("10")) {
            numMonths = 120;
        } else if (time.getSelectedItem().toString().equals("11")) {
            numMonths = 132;
        } else if (time.getSelectedItem().toString().equals("12")) {
            numMonths = 144;
        } else if (time.getSelectedItem().toString().equals("13")) {
            numMonths = 156;
        } else if (time.getSelectedItem().toString().equals("14")) {
            numMonths = 168;
        } else if (time.getSelectedItem().toString().equals("15")) {
            numMonths = 180;
        }

        generate_emi.setOnClickListener(view -> {
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
            progressDialog.setTitle("Please wait..");
            progressDialog.setMessage("We are creating your account..");
            progressDialog.setCancelable(false);
            progressDialog.show();
            balance = ((EditText) findViewById(R.id.name)).getText().toString();
            HashMap<String, Object> infor = new HashMap<>();
            String accNum = generate();
            Individual_Account individual_account = new Individual_Account(accNum, "current", balance);
            FirebaseFirestore.getInstance().collection("customers").document(customerUID).collection("savings").document(accNum).set(infor).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoanAccountActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
                }
            });

            FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").add(individual_account).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoanAccountActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
                }
            });
        });
        progressDialog.dismiss();
    }

    public static double calculateEMI(double principal, double annualInterestRate, int numMonths) {
        double monthlyInterestRate = annualInterestRate / (12 * 100);
        double emi = principal * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, numMonths) / (Math.pow(1 + monthlyInterestRate, numMonths) - 1);

        return emi;
    }

    public static String generate() {
        // Generate random number using SecureRandom
        SecureRandom secureRandom = new SecureRandom();
        long randomNumber = secureRandom.nextLong();

        // Format the number to have 16 digits
        DecimalFormat formatter = new DecimalFormat("00000000000000000");
        // return the unique bank account number
        return formatter.format(randomNumber);
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

}