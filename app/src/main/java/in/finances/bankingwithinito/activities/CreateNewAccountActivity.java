package in.finances.bankingwithinito.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import in.finances.bankingwithinito.R;

public class CreateNewAccountActivity extends AppCompatActivity {

    private TextView savings, current, loan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        savings = findViewById(R.id.savings);
        current = findViewById(R.id.current);
        loan = findViewById(R.id.loan);

        savings.setOnClickListener(view -> {
            Intent intent = new Intent(CreateNewAccountActivity.this, SavingsAccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        current.setOnClickListener(view -> {
            Intent intent = new Intent(CreateNewAccountActivity.this, CurrentAccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        loan.setOnClickListener(view -> {
            Intent intent = new Intent(CreateNewAccountActivity.this, LoanAccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }
}