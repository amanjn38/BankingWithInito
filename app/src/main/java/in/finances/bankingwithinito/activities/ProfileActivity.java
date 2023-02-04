package in.finances.bankingwithinito.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import in.finances.bankingwithinito.R;

public class ProfileActivity extends AppCompatActivity {

    private EditText addressButton, nameEdit, phoneEdit, mailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        nameEdit = findViewById(R.id.name);
        phoneEdit = findViewById(R.id.phone2);
        mailEdit = findViewById(R.id.mail);
        addressButton = findViewById(R.id.address_modify);
        getData();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView confirm_reg = findViewById(R.id.back_order_button);
        confirm_reg.setOnClickListener(e -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.putExtra("fromProfileActivity", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getData() {
        SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
        String customerUID = sharedPreferences.getString("customerUID", "");
        FirebaseFirestore.getInstance().collection("customers").document(customerUID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot d = task.getResult();
                nameEdit.setText(d.get("n").toString());
                mailEdit.setText(d.get("e").toString());
                addressButton.setText(d.get("add").toString());
                phoneEdit.setText(d.get("ph").toString());
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
}