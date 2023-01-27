package in.finances.bankingwithinito.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.Individual_Account;
import in.finances.bankingwithinito.models.Transaction;

public class SavingsAccountActivity extends AppCompatActivity {

    public String balance, error_msg, customerUID, accountType;
    private TextView createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_account);

        createAccount = findViewById(R.id.createAccount);
        accountType = getIntent().getStringExtra("accountType");


        createAccount.setOnClickListener(view -> {
            if (accountType.equals("SavingsAccount")) {
                if (checkFieldsForSavingsAccount()) {
                    balance = ((EditText) findViewById(R.id.amount)).getText().toString();
                    HashMap<String, Object> infor = new HashMap<>();
                    double awiad = 0.0;
                    int ttatm = 0;
                    int dt = 0;
                    Long lastOpened = 0L;
                    ArrayList<Transaction> transactions = new ArrayList<>();

                    infor.put("bal", balance);
                    infor.put("awiad", awiad);
                    infor.put("ttatm", ttatm);
                    infor.put("dt", dt);
                    infor.put("lastopened", lastOpened);
                    infor.put("transactions", transactions);
                    String card_number = generateCardNumber();
                    String expiryDate = generateExpiry();
                    String cvv = generateCVV();
                    String accNum = generate();
                    infor.put("cardNumber", card_number);
                    infor.put("expiry", expiryDate);
                    infor.put("cvv", cvv);
                    infor.put("accnum", accNum);

                    Individual_Account individual_account = new Individual_Account(accNum, "savings", balance);
                    FirebaseFirestore.getInstance().collection("customers").document(customerUID).collection("savings").document(accNum).set(infor).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SavingsAccountActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
                        }
                    });

                    FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").add(individual_account).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SavingsAccountActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            } else if (accountType.equals("CurrentAccount")) {
                if (checkFieldsForCurrentAccount()) {
                    balance = ((EditText) findViewById(R.id.name)).getText().toString();
                    HashMap<String, Object> infor = new HashMap<>();
                    int tt = 0;
                    Long lastOpened = 0L;
                    ArrayList<Transaction> transactions = new ArrayList<>();

                    infor.put("bal", balance);
                    infor.put("tt", tt); //total transactions in a month
                    infor.put("lastopened", lastOpened);
                    infor.put("transactions", transactions);
                    String accNum = generate();

                    infor.put("accnum", accNum);
                    Individual_Account individual_account = new Individual_Account(accNum, "current", balance);
                    FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").document("current").set(infor).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SavingsAccountActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
                        }
                    });
                    FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").add(individual_account).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SavingsAccountActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } else if (accountType.equals("LoanAccount")) {

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

    public static String generate() {
        // Generate random number using SecureRandom
        SecureRandom secureRandom = new SecureRandom();
        long randomNumber = secureRandom.nextLong();

        // Format the number to have 16 digits
        DecimalFormat formatter = new DecimalFormat("00000000000000000");
        String formattedNumber = formatter.format(randomNumber);

        // return the unique bank account number
        return formattedNumber;
    }
}