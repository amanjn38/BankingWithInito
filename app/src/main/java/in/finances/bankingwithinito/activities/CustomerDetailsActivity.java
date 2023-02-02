package in.finances.bankingwithinito.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

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

        FirebaseFirestore.getInstance().collection("customers").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<CustomerDetails> snapshotCoupons = task.getResult().toObjects(CustomerDetails.class);
                        customerDetails.addAll(snapshotCoupons);
                    }
//                    Log.d("couponfrag1", coupons.toString());
                    CustomerDetailsAdapter customerDetailsAdapter = new CustomerDetailsAdapter(customerDetails, getApplicationContext());
                    recyclerView.setAdapter(customerDetailsAdapter);
                    customerDetailsAdapter.notifyDataSetChanged();
                });


    }
}