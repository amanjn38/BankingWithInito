package in.finances.bankingwithinito.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import in.finances.bankingwithinito.R;

public class AccountDetailsActivity extends AppCompatActivity {

    public String balance, error_msg, customerUID, type, accNum;
    private TextView accountType, withdraw, withdrawWithATM, deposit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        accountType = findViewById(R.id.account_type);
        withdraw = findViewById(R.id.withdraw);
        withdrawWithATM = findViewById(R.id.withdrawWithATM);
        deposit = findViewById(R.id.deposit);
        balance = getIntent().getStringExtra("balance");
        accNum = getIntent().getStringExtra("account_number");
        type = getIntent().getStringExtra("account_type");

        accountType.setText(type);
        withdraw.setOnClickListener(view -> {
            Intent intent = new Intent(AccountDetailsActivity.this, IndividualTransactionActivity.class);
            intent.putExtra("account_number", accNum);
            intent.putExtra("balance", balance);
            intent.putExtra("account_type", type);
            intent.putExtra("type", "normal_withdraw");
            startActivity(intent);
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
            Intent intent = new Intent(AccountDetailsActivity.this, IndividualTransactionActivity.class);
            intent.putExtra("account_number", accNum);
            intent.putExtra("balance", balance);
            intent.putExtra("account_type", type);
            intent.putExtra("type", "deposit");
            startActivity(intent);
        });
    }


}