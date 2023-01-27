package in.finances.bankingwithinito.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import in.finances.bankingwithinito.R;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_account_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView account_type, account_number, balance;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            account_number = itemView.findViewById(R.id.account_number);
            account_type = itemView.findViewById(R.id.account_type);
            balance = itemView.findViewById(R.id.balance);
        }
    }

}
