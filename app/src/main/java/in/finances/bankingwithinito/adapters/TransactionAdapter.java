package in.finances.bankingwithinito.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.AdminTransaction;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final ArrayList<AdminTransaction> arrayList;
    private final Context context;

    public TransactionAdapter(ArrayList<AdminTransaction> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminTransaction adminTransaction = arrayList.get(position);
        holder.description.setText(adminTransaction.getTransaction_type());

        FirebaseFirestore.getInstance().collection("customers").document(adminTransaction.getCustomerUID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot d = task.getResult();
                if (d.contains("n")) {
                    String n = d.getString("n");
                    holder.customer_name.setText(n);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView customer_name, description;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customer_name = itemView.findViewById(R.id.customer_name);
            description = itemView.findViewById(R.id.description);
        }
    }

}
