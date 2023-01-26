package in.finances.bankingwithinito.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.fragments.ATMCardDetailsFragment;
import in.finances.bankingwithinito.fragments.AboutFragment;
import in.finances.bankingwithinito.fragments.AccountsListFragment;
import in.finances.bankingwithinito.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity implements
        AboutFragment.OnFragmentInteractionListener,
        AccountsListFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        ATMCardDetailsFragment.OnFragmentInteractionListener {

    private String customerUID;

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

        customerUID = getIntent().getStringExtra("customerUID");
        SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("customerUID", customerUID);
        editor.commit();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new AccountsListFragment()).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}