package in.finances.bankingwithinito.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.Transaction;

public class SavingsAccountActivity extends AppCompatActivity {

    public String balance, error_msg, customerUID;
    private TextView createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_account);

        createAccount = findViewById(R.id.createAccount);

        createAccount.setOnClickListener(view -> {
            if (checkFields()) {
                balance = ((EditText) findViewById(R.id.name)).getText().toString();
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
                infor.put("cardNumber", card_number);
                infor.put("expiry", expiryDate);
                infor.put("cvv", cvv);

                FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").document("savings").set(infor).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SavingsAccountActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    private boolean checkFields() {
        balance = ((EditText) findViewById(R.id.name)).getText().toString();

        if (balance.trim().length() == 0) {
            error_msg = "Invalid e-mail";
            Toast.makeText(SavingsAccountActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        } else if (Integer.parseInt(balance) <= 10000) {
            error_msg = "Minimum amount to open a savings account is Rs. 10000";
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

}