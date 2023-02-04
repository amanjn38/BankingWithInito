package in.finances.bankingwithinito.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.Transaction;

public class PassbookAdapter extends RecyclerView.Adapter<PassbookAdapter.ViewHolder> {

    private ArrayList<Transaction> arrayList;
    private Context context;

    public PassbookAdapter(ArrayList<Transaction> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.passbook_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = arrayList.get(position);
        holder.transaction_type.setText("Transaction type : " + transaction.getType());
        holder.amount.setText("Amount : " + transaction.getAmount());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
        Date resultdate = new Date(transaction.getDate());
        String date = sdf.format(resultdate);
        holder.transaction_date.setText("Date : " + date);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView amount, transaction_type, transaction_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.amount);
            transaction_date = itemView.findViewById(R.id.transaction_date);
            transaction_type = itemView.findViewById(R.id.transaction_type);
        }
    }

}
