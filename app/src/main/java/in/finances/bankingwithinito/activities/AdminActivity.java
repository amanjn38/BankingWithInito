package in.finances.bankingwithinito.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        FirebaseFirestore.getInstance().collection("couponcodes").document("admin").collection("codes").get()
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

    }
}