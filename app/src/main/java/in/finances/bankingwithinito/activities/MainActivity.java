package in.finances.bankingwithinito.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.fragments.ATMCardDetailsFragment;
import in.finances.bankingwithinito.fragments.AccountsListFragment;
import in.finances.bankingwithinito.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity implements
        AccountsListFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        ATMCardDetailsFragment.OnFragmentInteractionListener {

    private String customerUID;
    private FloatingActionButton addAccount;
    private boolean fromProfileActivity;

    @SuppressLint("NonConstantResourceId")
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                //onRefuseOrder();
                if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof AccountsListFragment)) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccountsListFragment()).commit();
                }
                return true;
            case R.id.navigation_profile:
                //onRefuseOrder();
                if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof ProfileFragment)) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
                return true;
            case R.id.navigation_atm:
                //onRefuseOrder();
                if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof ATMCardDetailsFragment)) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ATMCardDetailsFragment()).commit();
                }
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        addAccount = findViewById(R.id.addAccount);
        customerUID = getIntent().getStringExtra("customerUID");
        fromProfileActivity = getIntent().getBooleanExtra("fromProfileActivity", false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (fromProfileActivity) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        }
        System.out.println("CustomerUID " + customerUID);
        SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("customerUID", customerUID);
        editor.apply();
        FirebaseFirestore.getInstance().collection("customers_usernames").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot d = task.getResult();
                    if (d.contains("username")) {
                        String customerUID = d.getString("username").toString();
                        SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("customerUID", customerUID);
                        editor.apply();
                    }
                }

            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new AccountsListFragment()).commit();

        addAccount.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreateNewAccountActivity.class);
            intent.putExtra("customerUID", customerUID);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        //savings account interest Calculation
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (currentDay == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            FirebaseFirestore.getInstance().collection("customers_account_spec").document(customerUID).collection("savings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            long dateCreated = documentSnapshot.getLong("dateCreated");
                            double bal = documentSnapshot.getDouble("bal");
                            int lastInt = (int) documentSnapshot.get("lastInterestCal");
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(dateCreated);
                            int month = calendar.get(Calendar.MONTH);

                            int currentMonth = calendar.get(Calendar.MONTH);
                            if (lastInt == 0) {
                                double interest = bal * 0.06 / 12 * (currentMonth - lastInt);
                                bal += interest;
                            } else {
                                double interest = bal * 0.06 / 12 * (currentMonth - lastInt);
                                bal += interest;
                            }
                            String accNum = documentSnapshot.getString("accnum");
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference docRef = db.collection("customers").document(customerUID).collection("savings").document(accNum);

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("bal", bal);
                            map.put("lastInterestCal", currentMonth);

                            docRef.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }
                    }
                }
            });
        }

        if (currentDay == 28) {
            FirebaseFirestore.getInstance().collection("customers_account_spec").document(customerUID).collection("savings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            double bal = documentSnapshot.getDouble("bal");
                            double penalty = 0;
                            if (bal == 0.0) {
                                penalty = 1000;
                            } else if (bal > 100000) {
                                penalty = 0;
                            } else {
                                penalty = (1000 * bal) / 100000;
                            }
                            bal = bal - penalty;
                            String accNum = documentSnapshot.getString("accnum");
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference docRef = db.collection("customers").document(customerUID).collection("savings").document(accNum);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("bal", bal);
                            docRef.update(map).addOnCompleteListener(task1 -> {

                            });
                        }
                    }
                }
            });

            FirebaseFirestore.getInstance().collection("customers_account_spec").document(customerUID).collection("current").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            double bal = documentSnapshot.getDouble("bal");
                            double penalty = 0;
                            if (bal == 0.0) {
                                penalty = 5000;
                            } else if (bal > 500000) {
                                penalty = 0;
                            } else {
                                penalty = (5000 * bal) / 500000;
                            }
                            bal = bal - penalty;
                            String accNum = documentSnapshot.getString("accnum");
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference docRef = db.collection("customers").document(customerUID).collection("current").document(accNum);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("bal", bal);
                            docRef.update(map).addOnCompleteListener(task1 -> {

                            });
                        }
                    }
                }
            });

        }
        //loan account interest calculation
        final java.util.Date currentDate11 = calendar.getTime();
        if (currentDay == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            FirebaseFirestore.getInstance().collection("customers_account_spec").document(customerUID).collection("loan").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            double balance = documentSnapshot.getDouble("bal");
                            double interestRate = documentSnapshot.getDouble("interest_rate");
                            java.util.Date lastInterestCalculationDate = documentSnapshot.getDate("lastInterestCalculationDate");

                            // Calculate the number of half-years since the last interest calculation
                            long timeSinceLastInterestCalculation = currentDate11.getTime() - lastInterestCalculationDate.getTime();
                            int numHalfYears = (int) (timeSinceLastInterestCalculation / (365.25 / 2 * 24 * 60 * 60 * 1000));

                            // Calculate the compounded interest
                            for (int i = 0; i < numHalfYears; i++) {
                                balance *= (1 + interestRate / 2);
                            }
                            String accNum = documentSnapshot.getString("accnum");
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference docRef = db.collection("customers").document(customerUID).collection("current").document(accNum);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("bal", balance);
                            map.put("lastInterestCalculationDate", currentDate11);
                            docRef.update(map).addOnCompleteListener(task1 -> {

                            });
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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