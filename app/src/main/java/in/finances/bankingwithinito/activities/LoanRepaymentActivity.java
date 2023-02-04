package in.finances.bankingwithinito.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.AdminTransaction;
import in.finances.bankingwithinito.models.Transaction;

public class LoanRepaymentActivity extends AppCompatActivity {

    private TextView completeTransaction, balance;
    private EditText amount;
    private String bal, accNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_repayment);

        completeTransaction = findViewById(R.id.completeTransaction);
        balance = findViewById(R.id.balance);
        amount = findViewById(R.id.amount);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bal = getIntent().getStringExtra("balance");
        accNum = getIntent().getStringExtra("account_number");
        Double bala = Double.parseDouble(bal);

        balance.setText("Balance : Rs. " + bal);

        completeTransaction.setOnClickListener(view -> {
            if (amount.getText().length() == 0) {
                Toast.makeText(this, "Please enter the amount to repay", Toast.LENGTH_LONG).show();
            } else {
                String amt = amount.getText().toString();
                Double amtd = Double.parseDouble(amt);
                if (amtd <= 0.1 * bala) {
                    System.out.println("testinytt1");
                    Transaction transaction = new Transaction(System.currentTimeMillis(), "repayment", amtd);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
                    String customerUID = sharedPreferences.getString("customerUID", "");
                    DocumentReference docRef = db.collection("customers_account_spec").document(customerUID).collection("loan").document(accNum);
                    docRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> map = new HashMap<>();
                            ArrayList<Transaction> arrayList = (ArrayList<Transaction>) document.get("transactions");
                            arrayList.add(transaction);
                            map.put("transactions", arrayList);
                            Double balance = (Double) document.get("bal");
                            balance = balance - amtd;
                            map.put("bal", balance);

                            HashMap<String, Object> infor = new HashMap<>();
                            infor.put("balance", String.valueOf(balance));

                            FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").document(accNum).update(infor).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(LoanRepaymentActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show();
                                }
                            });

                            map.put("lastTransaction", System.currentTimeMillis());

                            docRef.update(map).addOnSuccessListener(aVoid -> Toast.makeText(LoanRepaymentActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show());
                            AdminTransaction adminTransaction = new AdminTransaction(customerUID, "loan", amt, System.currentTimeMillis(), "Loan Repayment");
                            FirebaseFirestore.getInstance().collection("admin_transactions").add(adminTransaction).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(LoanRepaymentActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LoanRepaymentActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Toast.makeText(this, "Error getting details", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "The repayment amount cannot be more than 10% of the total balance", Toast.LENGTH_LONG).show();
                }
            }
        });
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