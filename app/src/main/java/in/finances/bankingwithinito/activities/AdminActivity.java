package in.finances.bankingwithinito.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.adapters.TransactionAdapter;
import in.finances.bankingwithinito.models.AdminTransaction;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<AdminTransaction> adminTransactions;
    private TextView customerDetails, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        adminTransactions = new ArrayList<>();
        recyclerView = findViewById(R.id.transaction_recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        customerDetails = findViewById(R.id.customerDetails);
        logout = findViewById(R.id.logout);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore.getInstance().collection("admin_transactions").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<AdminTransaction> snapshotCoupons = task.getResult().toObjects(AdminTransaction.class);
                        adminTransactions.addAll(snapshotCoupons);
                    }
//                    Log.d("couponfrag1", coupons.toString());
                    TransactionAdapter transactionAdapter = new TransactionAdapter(adminTransactions, getApplicationContext());
                    recyclerView.setAdapter(transactionAdapter);
                    transactionAdapter.notifyDataSetChanged();
                });

        customerDetails.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, CustomerDetailsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        logout.setOnClickListener(view -> {
            auth.signOut();
            Intent mainActivity = new Intent(AdminActivity.this, StartActivity.class);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainActivity);
        });
    }
}