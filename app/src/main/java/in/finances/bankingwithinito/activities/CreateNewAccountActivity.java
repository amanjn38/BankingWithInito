package in.finances.bankingwithinito.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import in.finances.bankingwithinito.R;

public class CreateNewAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        TextView savings = findViewById(R.id.savings);
        TextView current = findViewById(R.id.current);
        TextView loan = findViewById(R.id.loan);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        savings.setOnClickListener(view -> {
            Intent intent = new Intent(CreateNewAccountActivity.this, SavingsAccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("accountType", "SavingsAccount");
            startActivity(intent);
        });

        current.setOnClickListener(view -> {
            Intent intent = new Intent(CreateNewAccountActivity.this, SavingsAccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("accountType", "CurrentAccount");
            startActivity(intent);
        });

        loan.setOnClickListener(view -> {
            Intent intent = new Intent(CreateNewAccountActivity.this, LoanAccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
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