package in.finances.bankingwithinito.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.Transaction;

public class IndividualTransactionActivity extends AppCompatActivity {

    private TextView transaction_type;
    public String balance, error_msg, customerUID, type, accNum, t_type, daily_limit;
    private LinearLayout ll;
    private EditText amount, cardNumber, cvv, expiryDate;
    private final double NRV = 100000;
    private final double fee = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_transaction);

        transaction_type = findViewById(R.id.transaction_type);
        amount = findViewById(R.id.amount);
        cvv = findViewById(R.id.cvv);
        expiryDate = findViewById(R.id.expiryDate);
        balance = getIntent().getStringExtra("balance");
        accNum = getIntent().getStringExtra("account_number");
        type = getIntent().getStringExtra("account_type");
        t_type = getIntent().getStringExtra("type");
        ll = findViewById(R.id.ll);

        if (t_type.equalsIgnoreCase("atm_withdraw")) {
            ll.setVisibility(View.VISIBLE);
        }

        if (t_type.equalsIgnoreCase("normal_withdraw") && type.equalsIgnoreCase("savings")) {
            String amt = amount.getText().toString();

            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
            SharedPreferences.Editor editor = pref.edit();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = dateFormat.format(new Date());

            String tranAmt = pref.getString(currentDate, "0");

            if(tranAmt.equals("0"))
            {
                editor.putString(currentDate,amt);
                editor.apply();
            }
            else
            {
                //get the total amount of transaction of current date
                int totalAmt = Integer.parseInt(tranAmt) + Integer.parseInt(amt);
                if(totalAmt>50000)
                {
                    //display error message
                    Toast.makeText(this, "Withdrawal limit exceeded", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    editor.putString(currentDate,String.valueOf(totalAmt));
                    editor.apply();
                    Transaction transaction = new Transaction(System.currentTimeMillis(), "withdrawal", amt);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("customers").document(customerUID).collection("savings").document(accNum);
                    docRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Map<String,Object> map = document.getData();
                            ArrayList<Transaction> arrayList= (ArrayList<Transaction>) map.get("transactions");
                            arrayList.add(transaction);
                            map.put("arrayListField",arrayList);
                            map.put("bal", totalAmt);
                            int dt = (int) map.get("dt");
                            dt = dt + 1;
                            map.put("dt", dt);
                            Long lastTransaction = (Long) map.get("lastTransaction");

                            docRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(this, "Error getting details", Toast.LENGTH_LONG).show();
                        }
                    });

//                    HashMap<String, Object> infor = new HashMap<>();
//                    infor.put(balance, totalAmt);
//
//                    FirebaseFirestore.getInstance().collection("customers").document(customerUID).collection("savings").document(accNum).update(infor).addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(IndividualTransactionActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//                    FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").add(individual_account).addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(IndividualTransactionActivity.this, "Your acccount has been successfully created", Toast.LENGTH_LONG).show();
//                        }
//                    });
                }
            }
        }else if(t_type.equalsIgnoreCase("atm_withdraw") && type.equalsIgnoreCase("savings")){
            String amt = amount.getText().toString();

        }else if(t_type.equalsIgnoreCase("deposit") && type.equalsIgnoreCase("savings")){
            String amt = amount.getText().toString();

            Transaction transaction = new Transaction(System.currentTimeMillis(), "deposit", amt);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("customers").document(customerUID).collection("savings").document(accNum);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String,Object> map = document.getData();
                    ArrayList<Transaction> arrayList= (ArrayList<Transaction>) map.get("transactions");
                    arrayList.add(transaction);
                    map.put("transactions",arrayList);
                    double balance = (double) map.get("bal");
                    balance = balance + Double.parseDouble(amt);
                    map.put("bal", balance);
                    docRef.update(map).addOnSuccessListener(aVoid -> Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show());
                } else {
                    Toast.makeText(this, "Error getting details", Toast.LENGTH_LONG).show();
                }
            });
        }else if(t_type.equalsIgnoreCase("deposit") && type.equalsIgnoreCase("current")){
            String amt = amount.getText().toString();

            Transaction transaction = new Transaction(System.currentTimeMillis(), "deposit", amt);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("customers").document(customerUID).collection("current").document(accNum);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String,Object> map = document.getData();
                    ArrayList<Transaction> arrayList= (ArrayList<Transaction>) map.get("transactions");
                    arrayList.add(transaction);
                    map.put("transactions",arrayList);
                    double balance = (double) map.get("bal");
                    balance = balance + Double.parseDouble(amt);
                    map.put("bal", balance);
                    docRef.update(map).addOnSuccessListener(aVoid -> Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show());
                } else {
                    Toast.makeText(this, "Error getting details", Toast.LENGTH_LONG).show();
                }
            });
        }else if(t_type.equalsIgnoreCase("withdrawal") && type.equalsIgnoreCase("current")){
            
        }
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

        EditText expiryDate = findViewById(R.id.expiryDate);
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

    public void checkNRV(double balance) {
        Calendar now = Calendar.getInstance();
        if(now.get(Calendar.DAY_OF_MONTH) == 28) { // check for 28th of the month
            double currentNRV = balance + calculateTotalTransaction();
            if (currentNRV < NRV) {
                double shortfall = NRV - currentNRV;
                double feeToBeCharged = fee * shortfall / NRV;
                balance -= feeToBeCharged;
            }
        }
    }

}