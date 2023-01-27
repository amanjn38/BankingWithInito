package in.finances.bankingwithinito.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.activities.AccountDetailsActivity;
import in.finances.bankingwithinito.models.Individual_Account;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.ViewHolder> {

    private ArrayList<Individual_Account> individual_accounts;
    private Context context;


    public AccountsAdapter(ArrayList<Individual_Account> individual_accounts, Context context) {
        this.individual_accounts = individual_accounts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_account_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Individual_Account individual_account = individual_accounts.get(position);
        holder.account_type.setText(individual_account.getType());
        holder.balance.setText(individual_account.getBalance());
        holder.account_number.setText(individual_account.getAccount_number());

        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, AccountDetailsActivity.class);
            intent.putExtra("account_number", individual_account.getAccount_number());
            intent.putExtra("balance", individual_account.getBalance());
            intent.putExtra("account_type", individual_account.getType());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return individual_accounts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView account_type, account_number, balance;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            account_number = itemView.findViewById(R.id.account_number);
            account_type = itemView.findViewById(R.id.account_type);
            balance = itemView.findViewById(R.id.balance);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }

}
