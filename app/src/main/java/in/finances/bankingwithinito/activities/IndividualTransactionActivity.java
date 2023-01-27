package in.finances.bankingwithinito.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.finances.bankingwithinito.R;

public class IndividualTransactionActivity extends AppCompatActivity {

    private TextView transaction_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_transaction);

        transaction_type = findViewById(R.id.transaction_type);

        EditText cardNumberInput = findViewById(R.id.cardNumber);
        cardNumberInput.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                // Insert char where needed.
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    char c = s.charAt(s.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(space));
                    }
                }
            }
        });

        String cardNumber = cardNumberInput.getText().toString().replace(" ", "");

        EditText expiryDate = findViewById(R.id.expirtDate);
        expiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (input.length() == 2 && !input.endsWith("/")) {
                    input = input + "/";
                    expiryDate.setText(input);
                    expiryDate.setSelection(input.length());
                }
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("MM/yy", Locale.getDefault());
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(expiryDate.getText().toString());
            //expiry date is valid
        } catch (ParseException e) {
            //expiry date is invalid
        }
    }
}