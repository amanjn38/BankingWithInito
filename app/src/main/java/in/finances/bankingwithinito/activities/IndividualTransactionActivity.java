package in.finances.bankingwithinito.activities;

import android.content.Context;
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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.AdminTransaction;
import in.finances.bankingwithinito.models.Transaction;

public class IndividualTransactionActivity extends AppCompatActivity {

    private TextView transaction_type, completeTransaction;
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
        SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
        customerUID = sharedPreferences.getString("customerUID", "");
        if (t_type.equalsIgnoreCase("atm_withdraw")) {
            ll.setVisibility(View.VISIBLE);
        }

        completeTransaction = findViewById(R.id.completeTransaction);
        completeTransaction.setOnClickListener(view -> {
            if (t_type.equalsIgnoreCase("normal_withdraw") && type.equalsIgnoreCase("savings")) {
                String amt = amount.getText().toString();

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = pref.edit();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currentDate = dateFormat.format(new Date());

                String tranAmt = pref.getString(currentDate, "0");

                if (tranAmt.equals("0")) {
                    if (Double.parseDouble(amt) <= 20000) {
                        editor.putString(currentDate, amt);
                        editor.apply();
                        normal_withdraw(amt);
                    } else {
                        Toast.makeText(this, "The maximum withdrawal limit is Rs. 50000 for a day", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //get the total amount of transaction of current date
                    System.out.println("withdrawal testing" + tranAmt + "   " + amt);
                    int totalAmt = Integer.parseInt(tranAmt) + Integer.parseInt(amt);
                    if (totalAmt > 50000) {
                        Toast.makeText(this, "Withdrawal limit exceeded", Toast.LENGTH_SHORT).show();
                    } else {
                        editor.putString(currentDate, String.valueOf(totalAmt));
                        editor.apply();
                        normal_withdraw(amt);
                    }
                }
            } else if (t_type.equalsIgnoreCase("atm_withdraw") && type.equalsIgnoreCase("savings")) {
                String amt = amount.getText().toString();
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = pref.edit();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currentDate = dateFormat.format(new Date());

                String tranAmt = pref.getString(currentDate, "0");
                if (tranAmt.equals("0")) {
                    editor.putString(currentDate, amt);
                    editor.apply();
                    atm_withdraw(amt);
                } else {
                    //get the total amount of transaction of current date
                    int totalAmt = Integer.parseInt(tranAmt) + Integer.parseInt(amt);
                    if (totalAmt > 50000) {
                        //display error message
                        Toast.makeText(this, "Withdrawal limit exceeded", Toast.LENGTH_SHORT).show();
                    } else {
                        atm_withdraw(amt);
                    }
                }

            } else if (t_type.equalsIgnoreCase("deposit") && type.equalsIgnoreCase("savings")) {
                String amt = amount.getText().toString();
                Double a = Double.parseDouble(amt);
                Transaction transaction = new Transaction(System.currentTimeMillis(), "deposit", a);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("customers").document(customerUID).collection("savings").document(accNum);
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> map = document.getData();
                        ArrayList<Transaction> arrayList = (ArrayList<Transaction>) map.get("transactions");
                        arrayList.add(transaction);
                        map.put("transactions", arrayList);
                        double balance = (double) map.get("bal");
                        balance = balance + Double.parseDouble(amt);
                        map.put("bal", balance);
                        HashMap<String, Object> infor = new HashMap<>();
                        infor.put("balance", balance);

                        FirebaseFirestore.getInstance().collection("customers_accounts").document(customerUID).collection("accounts").document(accNum).update(infor).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(IndividualTransactionActivity.this, "", Toast.LENGTH_LONG).show();
                            }
                        });
                        docRef.update(map).addOnSuccessListener(aVoid -> Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show());
                    } else {
                        Toast.makeText(this, "Error getting details", Toast.LENGTH_LONG).show();
                    }
                });

            } else if (t_type.equalsIgnoreCase("deposit") && type.equalsIgnoreCase("current")) {
                String amt = amount.getText().toString();
                Double a = Double.parseDouble(amt);
                Transaction transaction = new Transaction(System.currentTimeMillis(), "deposit", a);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("customers").document(customerUID).collection("current").document(accNum);
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> map = document.getData();
                        ArrayList<Transaction> arrayList = (ArrayList<Transaction>) map.get("transactions");
                        arrayList.add(transaction);
                        map.put("transactions", arrayList);
                        double balance = (double) map.get("bal");
                        balance = balance + Double.parseDouble(amt);
                        map.put("bal", balance);
                        HashMap<String, Object> infor = new HashMap<>();
                        infor.put("balance", balance);

                        FirebaseFirestore.getInstance().collection("customers_accounts").document(customerUID).collection("accounts").document(accNum).update(infor).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(IndividualTransactionActivity.this, "", Toast.LENGTH_LONG).show();
                            }
                        });
                        docRef.update(map).addOnSuccessListener(aVoid -> Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show());
                    } else {
                        Toast.makeText(this, "Error getting details", Toast.LENGTH_LONG).show();
                    }
                });
            } else if (t_type.equalsIgnoreCase("withdrawal") && type.equalsIgnoreCase("current")) {
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//            // Get the current account document
//            DocumentReference currentAcc = db.collection("customers_accounts_spec").document(customerUID)
//                    .collection("current").document(accNum);
//
//            // Get the current time
//            Calendar calendar = Calendar.getInstance();
//            int currentMonth = calendar.get(Calendar.MONTH);
//            int currentYear = calendar.get(Calendar.YEAR);
//            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
//            String monthlyKey = currentMonth + "/" + currentYear;
//            currentAcc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    // Check if the monthly transaction count field exists
//                    if (documentSnapshot.contains(monthlyKey)) {
//                        // If it exists, increment the count
//                        currentAcc.update(monthlyKey, FieldValue.increment(1));
//                        currentAcc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                int transactionCount = documentSnapshot.getLong(monthlyKey).intValue();
//                                if (transactionCount < 3) {
//                                    // Deduct Rs. 500 from the balance as a penalty
//                                    currentAcc.update("balance", FieldValue.increment(-500))
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    Log.d("PENALTY", "A penalty of Rs. 500 has been charged for not making at least 3 transactions this month");
//                                                }
//                                            });
//                                }
//
//                                // Deduct the amount from the balance
//                                currentAcc.update("balance", FieldValue.increment(-amount))
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                // Calculate the transaction charge
//                                                double transactionCharge = amount * 0.005;
//                                                transactionCharge = Math.min(transactionCharge, 500);
//
//                                                // Deduct the transaction charge from the balance
//                                                currentAcc.update("balance", FieldValue.increment(-transactionCharge))
//                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                            @Override
//                                                            public void onSuccess(Void aVoid) {
//                                                                // Log the transaction
//                                                                Log.d("WITHDRAW", "Withdrawal of Rs." + amount + " with a charge of Rs." + transactionCharge + " is successful");
//                                                            }
//                                                        });
//                                            }
//                                        });
//                            }
//                        });
//
//                    } else {
//                        // If it doesn't exist, create it with a value of 1
//                        currentAcc.update(monthlyKey, 1);
//                    }
//                }
//            });
//
//            // Update the transaction count for the current month
//            currentAcc.update("transactions_" + currentMonth + "_" + currentYear, FieldValue.increment(1))
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            // Check if the transaction count is less than 3
//                            currentAcc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                @Override
//                                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                    int transactionCount = documentSnapshot.getLong("transactions_" + currentMonth + "_" + currentYear).intValue();
//                                    if (transactionCount < 3) {
//                                        // Deduct Rs. 500 from the balance as a penalty
//                                        currentAcc.update("balance", FieldValue.increment(-500))
//                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//                                                        Log.d("PENALTY", "A penalty of Rs. 500 has been charged for not making at least 3 transactions this month");
//                                                    }
//                                                });
//                                    }
//
//                                    // Deduct the amount from the balance
//                                    currentAcc.update("balance", FieldValue.increment(-amount))
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    // Calculate the transaction charge
//                                                    double transactionCharge = amount * 0.005;
//                                                    transactionCharge = Math.min(transactionCharge, 500);
//
//                                                    // Deduct the transaction charge from the balance
//                                                    currentAcc.update("balance", FieldValue.increment(-transactionCharge))
//                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                @Override
//                                                                public void onSuccess(Void aVoid) {
//                                                                    // Log the transaction
//                                                                    Log.d("WITHDRAW", "Withdrawal of Rs." + amount + " with a charge of Rs." + transactionCharge + " is successful");
//                                                                }
//                                                            });
//                                                }
//                                            });
//                                }
//                            });
//                        }
//                    });
            }
        });
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

    private void normal_withdraw(String amt) {
        Double a = Double.parseDouble(amt);
        Transaction transaction = new Transaction(System.currentTimeMillis(), "withdrawal", a);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("customers_account_spec").document(customerUID).collection("savings").document(accNum);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Map<String, Object> map = document.getData();
                ArrayList<Transaction> arrayList = (ArrayList<Transaction>) map.get("transactions");
                arrayList.add(transaction);
                map.put("transactions", arrayList);
                System.out.println("withdrawal" + map.get("bal").getClass());
                Double balance = (Double) map.get("bal");
                if (Double.parseDouble(amt) < balance) {
                    balance = balance - Double.parseDouble(amt);
                    map.put("bal", balance);

                    HashMap<String, Object> infor = new HashMap<>();
                    infor.put("balance", balance);

                    FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").document(accNum).update(infor).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(IndividualTransactionActivity.this, "", Toast.LENGTH_LONG).show();
                        }
                    });
                    long dt = (long) map.get("dt");
                    dt = dt + 1;
                    map.put("dt", dt);
                    map.put("lastTransaction", System.currentTimeMillis());

                    AdminTransaction adminTransaction = new AdminTransaction(customerUID, "savings", amt, System.currentTimeMillis(), "Direct Withdrawal");
                    FirebaseFirestore.getInstance().collection("admin_transactions").add(adminTransaction).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(IndividualTransactionActivity.this, "", Toast.LENGTH_LONG).show();
                        }
                    });

                    docRef.update(map).addOnSuccessListener(aVoid -> Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show());

                } else {
                    Toast.makeText(this, "You don't have enough balance in your account", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Error getting details", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void atm_withdraw(final String amt) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference customerAccountRef = db.collection("customers_accounts_spec").document(customerUID).collection("savings").document(accNum);

        customerAccountRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && value.exists()) {
                int awiad = value.getLong("awiad").intValue();
                if (awiad < 5) {
                    customerAccountRef.update("awiad", awiad + 1);
                    atm_withdraw_helper(amt, "normal");
                } else {
                    if (Double.parseDouble(amt) <= 20000) {
                        atm_withdraw_helper(amt, "add_fees");
                        customerAccountRef.update("awiad", awiad + 1);
                    } else {
                        Toast.makeText(this, "The maximum withdrawal limit is Rs. 20000", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        customerAccountRef.update("awiad", 0);
    }

    private void atm_withdraw_helper(String amt, String type) {
        Double a = Double.parseDouble(amt);
        Transaction transaction = new Transaction(System.currentTimeMillis(), "withdrawal", a);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("customers_account_spec").document(customerUID).collection("savings").document(accNum);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Map<String, Object> map = document.getData();
                ArrayList<Transaction> arrayList = (ArrayList<Transaction>) map.get("transactions");
                arrayList.add(transaction);
                map.put("arrayListField", arrayList);
                Double balance = (Double) map.get("bal");

                if (Double.parseDouble(amt) < balance) {
                    balance = balance - Double.parseDouble(amt);
                    if (type.equalsIgnoreCase("add_fees")) {
                        if (balance > 500) {
                            balance = balance - 500.0;
                        } else {
                            Toast.makeText(this, "You don't have enough balance in your account", Toast.LENGTH_LONG).show();
                        }
                    }
                    map.put("bal", balance);

                    HashMap<String, Object> infor = new HashMap<>();
                    infor.put("balance", balance);

                    FirebaseFirestore.getInstance().collection("customers_accounts").document(customerUID).collection("accounts").document(accNum).update(infor).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show();
                        }
                    });
                    map.put("lastTransaction", System.currentTimeMillis());
                    AdminTransaction adminTransaction = new AdminTransaction(customerUID, "current", amt, System.currentTimeMillis(), "ATM Withdrawal");
                    FirebaseFirestore.getInstance().collection("admin_transactions").add(adminTransaction).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(IndividualTransactionActivity.this, "", Toast.LENGTH_LONG).show();
                        }
                    });
                    docRef.update(map).addOnSuccessListener(aVoid -> Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show());
                } else {
                    Toast.makeText(this, "You don't have enough balance in your account", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Error getting details", Toast.LENGTH_LONG).show();
            }
        });

    }
}