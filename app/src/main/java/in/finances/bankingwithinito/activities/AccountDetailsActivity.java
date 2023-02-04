package in.finances.bankingwithinito.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import in.finances.bankingwithinito.R;

public class AccountDetailsActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 100;
    public String balance, customerUID, type, accNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        TextView accountType = findViewById(R.id.account_type);
        TextView withdraw = findViewById(R.id.withdraw);
        TextView withdrawWithATM = findViewById(R.id.withdrawWithATM);
        TextView deposit = findViewById(R.id.deposit);
        balance = getIntent().getStringExtra("balance");
        accNum = getIntent().getStringExtra("account_number");
        type = getIntent().getStringExtra("account_type");
        EditText amount = findViewById(R.id.amount);
        TextView downloadStatement = findViewById(R.id.downloadStatement);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switch (type) {
            case "savings":
                accountType.setText("Savings Acccount");
                break;
            case "loan":
                accountType.setText("Loan Acccount");
                withdraw.setVisibility(View.GONE);
                withdrawWithATM.setVisibility(View.GONE);
                break;
            case "current":
                accountType.setText("Current Acccount");
                withdrawWithATM.setVisibility(View.GONE);
                break;
        }
        amount.setText(balance);


        withdraw.setOnClickListener(view -> {
            if (type.equalsIgnoreCase("current")) {
                Intent intent = new Intent(AccountDetailsActivity.this, IndividualTransactionActivity.class);
                intent.putExtra("account_number", accNum);
                intent.putExtra("balance", balance);
                intent.putExtra("account_type", type);
                intent.putExtra("type", "withdrawal");
                startActivity(intent);
            } else {
                Intent intent = new Intent(AccountDetailsActivity.this, IndividualTransactionActivity.class);
                intent.putExtra("account_number", accNum);
                intent.putExtra("balance", balance);
                intent.putExtra("account_type", type);
                intent.putExtra("type", "normal_withdraw");
                startActivity(intent);
            }

        });

        withdrawWithATM.setOnClickListener(view -> {
            Intent intent = new Intent(AccountDetailsActivity.this, IndividualTransactionActivity.class);
            intent.putExtra("account_number", accNum);
            intent.putExtra("balance", balance);
            intent.putExtra("account_type", type);
            intent.putExtra("type", "atm_withdraw");
            startActivity(intent);
        });

        deposit.setOnClickListener(view -> {
            if (type.equalsIgnoreCase("loan")) {
                Intent intent = new Intent(AccountDetailsActivity.this, LoanRepaymentActivity.class);
                intent.putExtra("account_number", accNum);
                intent.putExtra("balance", balance);
                intent.putExtra("account_type", type);
                intent.putExtra("type", "deposit");
                startActivity(intent);
            } else {
                Intent intent = new Intent(AccountDetailsActivity.this, IndividualTransactionActivity.class);
                intent.putExtra("account_number", accNum);
                intent.putExtra("balance", balance);
                intent.putExtra("account_type", type);
                intent.putExtra("type", "deposit");
                startActivity(intent);
            }

        });

        downloadStatement.setOnClickListener(view -> {
            Intent intent = new Intent(AccountDetailsActivity.this, TransactionsActivity.class);
            intent.putExtra("account_number", accNum);
            intent.putExtra("balance", balance);
            intent.putExtra("account_type", type);
            intent.putExtra("type", "deposit");
            startActivity(intent);
        });
    }


}