package in.finances.bankingwithinito.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.ATMCardDetails;

public class ATMAdapter extends RecyclerView.Adapter<ATMAdapter.ViewHolder> {

    private ArrayList<ATMCardDetails> atmCardDetails;
    private Context context;

    public ATMAdapter(ArrayList<ATMCardDetails> atmCardDetails, Context context) {
        this.atmCardDetails = atmCardDetails;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.atm_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ATMCardDetails atmCardDetai = atmCardDetails.get(position);
        holder.account_number_text_view.setText(atmCardDetai.getAcc_num());
        holder.expiryDate.setText(atmCardDetai.getExpiry_date());
        holder.cvvNumber.setText(atmCardDetai.getCvv());
        holder.atmCardNumber.setText(atmCardDetai.getCard_number());
    }

    @Override
    public int getItemCount() {
        return atmCardDetails.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView account_number_text_view, atmCardNumber, cvvNumber, expiryDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            account_number_text_view = itemView.findViewById(R.id.account_number_text_view);
            atmCardNumber = itemView.findViewById(R.id.atmCardNumber);
            cvvNumber = itemView.findViewById(R.id.cvvNumber);
            expiryDate = itemView.findViewById(R.id.expiryDate);
        }
    }
}
