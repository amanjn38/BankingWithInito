package in.finances.bankingwithinito.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

import in.finances.bankingwithinito.R;

public class ReferralActivity extends AppCompatActivity {

    private TextView generateReferralCode, referralCodetxt;
    private EditText accNum;

    private static final HashMap<Character, Integer> letterValues = new HashMap<Character, Integer>() {{
        put('A', 1);
        put('B', 3);
        put('C', 7);
        put('D', 15);
        put('E', 31);
        put('F', 63);
        put('G', 127);
        put('H', 255);
        put('I', 511);
        put('J', 1023);
        put('K', 2047);
        put('L', 4095);
        put('M', 8191);
        put('N', 16383);
        put('O', 32767);
        put('P', 65535);
        put('Q', 131071);
        put('R', 262143);
        put('S', 524287);
        put('T', 1048575);
        put('U', 2097151);
        put('V', 4194303);
        put('W', 8388607);
        put('X', 16777215);
        put('Y', 33554431);
        put('Z', 67108863);
    }};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        generateReferralCode = findViewById(R.id.generateReferralCode);
        accNum = findViewById(R.id.accNum);
        referralCodetxt = findViewById(R.id.referralCodetxt);
        generateReferralCode.setOnClickListener(view -> {
            String acc = accNum.getText().toString();
            if (acc.length() != 16) {
                Toast.makeText(this, "Please enter 16 digit account number", Toast.LENGTH_LONG).show();
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
                String customerUID = sharedPreferences.getString("customerUID", "");
                FirebaseFirestore.getInstance().collection("customers").document(customerUID).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.contains("n")) {
                            String name = document.getString("n");
                            String[] arr = name.split(" ");
                            computeReferralCode(arr[0]);
                        }
                    }
                });
            }
        });
    }

    public void computeReferralCode(String name) {
        name = name.toUpperCase();
        int referralCode = 0;
        for (int i = 0; i < name.length(); i++) {
            referralCode += letterValues.get(name.charAt(i));
        }
        referralCodetxt.setText(String.valueOf(referralCode));
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