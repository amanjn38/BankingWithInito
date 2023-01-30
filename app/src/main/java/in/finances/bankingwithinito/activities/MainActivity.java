package in.finances.bankingwithinito.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.fragments.ATMCardDetailsFragment;
import in.finances.bankingwithinito.fragments.AboutFragment;
import in.finances.bankingwithinito.fragments.AccountsListFragment;
import in.finances.bankingwithinito.fragments.ProfileFragment;
import in.finances.bankingwithinito.models.CustomerDetails;

public class MainActivity extends AppCompatActivity implements
        AboutFragment.OnFragmentInteractionListener,
        AccountsListFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        ATMCardDetailsFragment.OnFragmentInteractionListener {

    private String customerUID;
    private FloatingActionButton addAccount;

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
            case R.id.navigation_about:
                if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof AboutFragment)) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
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
        SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("customerUID", customerUID);
        editor.apply();

        FirebaseFirestore.getInstance().collection("customers").document(customerUID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CustomerDetails customerDetails = task.getResult().toObject(CustomerDetails.class);

                        Gson gson = new Gson();

                        String json = gson.toJson(customerDetails);
                        SharedPreferences sharedPreferences1 = getSharedPreferences("customerDetails", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                        editor1.putString("customerDetails", json);
                        editor1.apply();
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
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}