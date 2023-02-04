package in.finances.bankingwithinito.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.adapters.CustomerDetailsAdapter;
import in.finances.bankingwithinito.models.CustomerDetails;

public class CustomerDetailsActivity extends AppCompatActivity {

    private ArrayList<CustomerDetails> customerDetails;
    private String customerUID;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        customerDetails = new ArrayList<>();
        recyclerView = findViewById(R.id.details_recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FirebaseFirestore.getInstance().collection("customers").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                        List<CustomerDetails> snapshotCoupons = task.getResult().toObjects(CustomerDetails.class);
//                        customerDetails.addAll(snapshotCoupons);
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            String add = documentSnapshot.getString("add");
                            String n = documentSnapshot.getString("n");
                            String e = documentSnapshot.getString("e");
                            String ph = documentSnapshot.getString("ph");
                            String un = documentSnapshot.getString("un");
                            String uid = documentSnapshot.getString("uid");
                            double lt = documentSnapshot.getDouble("lt");
                            double lo = documentSnapshot.getDouble("lo");
                            long dob = documentSnapshot.getLong("dob");
                            CustomerDetails customerDetails1 = new CustomerDetails(n, add, e, ph, un, uid, lt, lo, dob);
                            customerDetails.add(customerDetails1);
                        }
                    }
//                    Log.d("couponfrag1", coupons.toString());
                    CustomerDetailsAdapter customerDetailsAdapter = new CustomerDetailsAdapter(customerDetails, getApplicationContext());
                    recyclerView.setAdapter(customerDetailsAdapter);
                    customerDetailsAdapter.notifyDataSetChanged();
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