package in.finances.bankingwithinito.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import in.finances.bankingwithinito.R;

public class AccountDetailsActivity extends AppCompatActivity {

    public String balance, error_msg, customerUID;
    private TextView accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_account);

        accountType = findViewById(R.id.account_type);

    }


}