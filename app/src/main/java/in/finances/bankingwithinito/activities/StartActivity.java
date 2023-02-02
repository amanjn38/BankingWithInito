package in.finances.bankingwithinito.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import in.finances.bankingwithinito.R;

public class StartActivity extends AppCompatActivity {

    private TextView admin_login, customer_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        admin_login = findViewById(R.id.admin_login);
        customer_login = findViewById(R.id.customer_login);

        admin_login.setOnClickListener(view -> {
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("login_type", "admin");
            startActivity(intent);
        });

        customer_login.setOnClickListener(view -> {
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("login_type", "customer");
            startActivity(intent);
        });
    }
}