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

public class CurrentAccountActivity extends AppCompatActivity {

    public String balance, error_msg, customerUID;
    private TextView createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_account);

        createAccount = findViewById(R.id.createAccount);
        createAccount.setOnClickListener(view -> {
            if (checkFields()) {
                balance = ((EditText) findViewById(R.id.name)).getText().toString();
                HashMap<String, Object> infor = new HashMap<>();
                int tt = 0;
                Long lastOpened = 0L;
                ArrayList<Transaction> transactions = new ArrayList<>();

                infor.put("bal", balance);
                infor.put("tt", tt); //total transactions in a month
                infor.put("lastopened", lastOpened);
                infor.put("transactions", transactions);

                FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").document("current").set(infor).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(CurrentAccountActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    private boolean checkFields() {
        balance = ((EditText) findViewById(R.id.name)).getText().toString();

        if (balance.trim().length() == 0) {
            error_msg = "Invalid e-mail";
            Toast.makeText(CurrentAccountActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        } else if (Integer.parseInt(balance) <= 100000) {
            error_msg = "Minimum amount to open a savings account is Rs. 10000";
            Toast.makeText(CurrentAccountActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}