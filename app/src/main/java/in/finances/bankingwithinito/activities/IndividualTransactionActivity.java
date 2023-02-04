package in.finances.bankingwithinito.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.AdminTransaction;
import in.finances.bankingwithinito.models.Transaction;

public class IndividualTransactionActivity extends AppCompatActivity {

    private TextView transaction_type, completeTransaction;
    public String balance, error_msg, customerUID, type, accNum, t_type, daily_limit, cardNumberS, cvvS, expiryS;
    private LinearLayout ll;
    private EditText amount, cardNumber, cvv, expiryDate;
    private ProgressDialog progressDialog;
    private TextInputLayout textInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_transaction);

        textInputLayout = findViewById(R.id.holderCardNumber);

        transaction_type = findViewById(R.id.transaction_type);
        amount = findViewById(R.id.amount);
        cvv = findViewById(R.id.cvv);
        expiryDate = findViewById(R.id.expiryDate);
        balance = getIntent().getStringExtra("balance");
        accNum = getIntent().getStringExtra("account_number");
        type = getIntent().getStringExtra("account_type");
        t_type = getIntent().getStringExtra("type");
        ll = findViewById(R.id.ll);
        cardNumber = findViewById(R.id.cardNumber);
        SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
        customerUID = sharedPreferences.getString("customerUID", "");
        if (t_type.equalsIgnoreCase("atm_withdraw")) {
            ll.setVisibility(View.VISIBLE);
            cardNumber.setVisibility(View.VISIBLE);
            textInputLayout.setVisibility(View.VISIBLE);
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("While we are completing the transaction..");
        progressDialog.show();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        completeTransaction = findViewById(R.id.completeTransaction);
        completeTransaction.setOnClickListener(view -> {
            if (t_type.equalsIgnoreCase("normal_withdraw") && type.equalsIgnoreCase("savings")) {
                transaction_type.setText("Withdraw");
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
                transaction_type.setText("Withdraw");

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
                transaction_type.setText("Deposit");

                String amt = amount.getText().toString();
                Double a = Double.parseDouble(amt);
                Transaction transaction = new Transaction(System.currentTimeMillis(), "deposit", a);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("customers_account_spec").document(customerUID).collection("savings").document(accNum);
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> map = new HashMap<>();
                        ArrayList<Transaction> arrayList = (ArrayList<Transaction>) document.get("transactions");
                        arrayList.add(transaction);
                        map.put("transactions", arrayList);
                        double balance = document.getDouble("bal");
                        balance = balance + Double.parseDouble(amt);
                        map.put("bal", balance);
                        HashMap<String, Object> infor = new HashMap<>();
                        infor.put("balance", balance);
                        AdminTransaction adminTransaction = new AdminTransaction(customerUID, "savings", amt, System.currentTimeMillis(), "Deposit");
                        FirebaseFirestore.getInstance().collection("admin_transactions").add(adminTransaction).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show();
                            }
                        });
                        docRef.update(map).addOnSuccessListener(aVoid -> Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show());

                        FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").document(accNum).update(infor).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(IndividualTransactionActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                    } else {
                        Toast.makeText(this, "Error getting details", Toast.LENGTH_LONG).show();
                    }
                });

            } else if (t_type.equalsIgnoreCase("deposit") && type.equalsIgnoreCase("current")) {
                transaction_type.setText("Deposit");

                String amt = amount.getText().toString();
                Double a = Double.parseDouble(amt);
                Transaction transaction = new Transaction(System.currentTimeMillis(), "deposit", a);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("customers_account_spec").document(customerUID).collection("current").document(accNum);
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            System.out.println("mapvalue" + document);
                            Map<String, Object> map = new HashMap<>();
                            System.out.println("mapvalue" + map);
                            ArrayList<Transaction> arrayList = (ArrayList<Transaction>) document.get("transactions");
                            if (arrayList == null) {
                                ArrayList<Transaction> arrayList1 = new ArrayList<>();
                                arrayList1.add(transaction);
                                map.put("transactions", arrayList1);
                            } else {
                                arrayList.add(transaction);
                                map.put("transactions", arrayList);
                            }
                            double d = document.getDouble("bal");
                            d = d + Double.parseDouble(amt);
                            map.put("bal", d);
                            HashMap<String, Object> infor = new HashMap<>();
                            infor.put("balance", d);

                            AdminTransaction adminTransaction = new AdminTransaction(customerUID, "current", amt, System.currentTimeMillis(), "Deposit");
                            FirebaseFirestore.getInstance().collection("admin_transactions").add(adminTransaction).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show();
                                }
                            });
                            docRef.update(map).addOnSuccessListener(aVoid -> Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show());

                            FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").document(accNum).update(infor).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(IndividualTransactionActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Toast.makeText(this, "Document does not exist", Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Toast.makeText(this, "Error getting details", Toast.LENGTH_LONG).show();
                    }
                });
            } else if (t_type.equalsIgnoreCase("withdrawal") && type.equalsIgnoreCase("current")) {
                transaction_type.setText("Withdraw");

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String amt = amount.getText().toString();
                Double a = Double.parseDouble(amt);
                // Get the current account document
                DocumentReference currentAcc = db.collection("customers_account_spec").document(customerUID)
                        .collection("current").document(accNum);

                // Get the current time
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentYear = calendar.get(Calendar.YEAR);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                String monthlyKey = currentMonth + currentYear + "";
                currentAcc.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.contains(monthlyKey)) {
                            currentAcc.update(monthlyKey, FieldValue.increment(1));
                            currentAcc.get().addOnCompleteListener(task12 -> {
                                if (task12.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot1 = task12.getResult();
                                    int transactionCount = documentSnapshot1.getLong(monthlyKey).intValue();
                                    current_account_withdrawal(a, transactionCount);

                                } else {
                                    System.out.println("working1111" + "error2311");

                                }
                            });
                        } else {
                            current_account_withdrawal(a, 1);
                        }
                    } else {
                        System.out.println("working1111" + "error");
                    }
                });
            }
        });
        cardNumber.addTextChangedListener(new TextWatcher() {
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
                Map<String, Object> map = new HashMap<>();
                ArrayList<Transaction> arrayList = (ArrayList<Transaction>) document.get("transactions");
                arrayList.add(transaction);
                map.put("transactions", arrayList);
                Double balance = (Double) document.get("bal");
                if (Double.parseDouble(amt) < balance) {
                    balance = balance - Double.parseDouble(amt);
                    map.put("bal", balance);

                    HashMap<String, Object> infor = new HashMap<>();
                    infor.put("balance", balance);

                    FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").document(accNum).update(infor).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                    long dt = (long) map.get("dt");
                    dt = dt + 1;
                    map.put("dt", dt);
                    map.put("lastTransaction", System.currentTimeMillis());

                    docRef.update(map).addOnSuccessListener(aVoid -> Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_SHORT).show());

                    AdminTransaction adminTransaction = new AdminTransaction(customerUID, "savings", amt, System.currentTimeMillis(), "Direct Withdrawal");
                    FirebaseFirestore.getInstance().collection("admin_transactions").add(adminTransaction).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(IndividualTransactionActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }
                    });
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
        cardNumberS = cardNumber.getText().toString().replace(" ", "");
        cvvS = cvv.getText().toString();
        System.out.println("testinacc" + customerUID + "  " + accNum);
        DocumentReference customerAccountRef = db.collection("customers_account_spec").document(customerUID).collection("savings").document(accNum);
        customerAccountRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                expiryS = expiryDate.getText().toString();

                HashMap<String, Object> map = new HashMap<>();

                boolean dateEqual = false;
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/yyyy");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/yy");
                try {
                    Date date1 = dateFormat1.parse(documentSnapshot.getString("expiry"));
                    Date date2 = dateFormat2.parse(expiryS);
                    if (date1.compareTo(date2) == 0) {
                        dateEqual = true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (documentSnapshot.getString("cardNumber").equalsIgnoreCase(cardNumberS) && documentSnapshot.getString("cvv").equalsIgnoreCase(cvvS) && dateEqual) {
                    int awiad = documentSnapshot.getLong("awiad").intValue();
                    if (awiad < 5) {
                        map.put("awiad", awiad + 1);
                        customerAccountRef.update(map).addOnCompleteListener(task12 -> atm_withdraw_helper(amt, "normal"));

                    } else {
                        if (Double.parseDouble(amt) <= 20000) {
                            map.put("awiad", awiad + 1);
                            customerAccountRef.update(map).addOnCompleteListener(task1 -> atm_withdraw_helper(amt, "add_fees"));
                        } else {
                            Toast.makeText(IndividualTransactionActivity.this, "The maximum withdrawal limit is Rs. 20000", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(IndividualTransactionActivity.this, "Please enter correct card details", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void atm_withdraw_helper(String amt, String type) {
        cvvS = cvv.getText().toString();

        Double a = Double.parseDouble(amt);
        Transaction transaction = new Transaction(System.currentTimeMillis(), "withdrawal", a);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("customers_account_spec").document(customerUID).collection("savings").document(accNum);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Map<String, Object> map = new HashMap<>();

                ArrayList<Transaction> arrayList = (ArrayList<Transaction>) document.get("transactions");
                arrayList.add(transaction);
                map.put("transactions", arrayList);
                Double balance = (Double) document.get("bal");

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

                    FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").document(accNum).update(infor).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show();
                        }
                    });
                    map.put("lastTransaction", System.currentTimeMillis());
                    docRef.update(map).addOnSuccessListener(aVoid -> Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show());

                    AdminTransaction adminTransaction = new AdminTransaction(customerUID, "current", amt, System.currentTimeMillis(), "ATM Withdrawal");
                    FirebaseFirestore.getInstance().collection("admin_transactions").add(adminTransaction).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(IndividualTransactionActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
                } else {
                    Toast.makeText(this, "You don't have enough balance in your account", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "Error getting details", Toast.LENGTH_LONG).show();
            }
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

    private void current_account_withdrawal(double a, int transactions) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference currentAcc = db.collection("customers_account_spec").document(customerUID)
                .collection("current").document(accNum);

        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        String monthlyKey = currentMonth + currentYear + "";
        Transaction transaction = new Transaction(System.currentTimeMillis(), "Withdrawal", a);
        currentAcc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Map<String, Object> map = new HashMap<>();
                    map.put(monthlyKey, 1);
                    ArrayList<Transaction> arrayList = (ArrayList<Transaction>) documentSnapshot.get("transactions");
                    arrayList.add(transaction);
                    map.put("transactions", arrayList);
                    double balance = documentSnapshot.getDouble("bal");
                    if (transactions < 3) {
                        balance = balance - 500;
                    }
                    balance = balance - a;

                    double transactionCharge = a * 0.005;
                    transactionCharge = Math.min(transactionCharge, 500);
                    balance = balance - transactionCharge;
                    map.put("bal", balance);

                    currentAcc.update(map).addOnCompleteListener(task14 -> {
                        if (task14.isSuccessful()) {

                        }
                    });

                    HashMap<String, Object> infor = new HashMap<>();
                    infor.put("bal", String.valueOf(balance));
                    FirebaseFirestore.getInstance().collection("customers_account").document(customerUID).collection("accounts").document(accNum).update(infor).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        AdminTransaction adminTransaction = new AdminTransaction(customerUID, "current", String.valueOf(a), System.currentTimeMillis(), "Withdrawal");
        FirebaseFirestore.getInstance().collection("admin_transactions").add(adminTransaction).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                progressDialog.dismiss();
                Toast.makeText(IndividualTransactionActivity.this, "Transaction Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(IndividualTransactionActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }
}