package in.finances.bankingwithinito.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.CustomerDetails;

public class CustomerDetailsAdapter extends RecyclerView.Adapter<CustomerDetailsAdapter.ViewHolder> {

    private ArrayList<CustomerDetails> customerDetails;
    private Context context;

    public CustomerDetailsAdapter(ArrayList<CustomerDetails> customerDetails, Context context) {
        this.customerDetails = customerDetails;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_details_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerDetails customerDetails1 = customerDetails.get(position);
        holder.customerUID.setText(customerDetails1.getUid());
        holder.detailAddress.setText(customerDetails1.getAdd());
        holder.detailEmail.setText(customerDetails1.getE());
        holder.detailName.setText(customerDetails1.getN());
        holder.detailPhone.setText(customerDetails1.getPh());
    }

    @Override
    public int getItemCount() {
        return customerDetails.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView customerUID, detailPhone, detailEmail, detailName, detailAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerUID = itemView.findViewById(R.id.account_number_text_view);
            detailAddress = itemView.findViewById(R.id.atmCardNumber);
            detailEmail = itemView.findViewById(R.id.cvvNumber);
            detailName = itemView.findViewById(R.id.expiryDate);
            detailAddress = itemView.findViewById(R.id.atm_card_card_view);
        }
    }
}
