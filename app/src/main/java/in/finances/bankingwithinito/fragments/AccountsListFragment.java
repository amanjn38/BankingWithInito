package in.finances.bankingwithinito.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.adapters.AccountsAdapter;
import in.finances.bankingwithinito.models.Individual_Account;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountsListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Individual_Account> individual_accounts;
    private String customerUID;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    public AccountsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountsListActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountsListFragment newInstance(String param1, String param2) {
        AccountsListFragment fragment = new AccountsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accounts_list_activity, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("customerUID", Context.MODE_PRIVATE);
        String customerUID = sharedPreferences.getString("customerUID", "");

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        FirebaseFirestore.getInstance().collection("customers").document(customerUID).collection("accounts").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Individual_Account> snapshotCoupons = task.getResult().toObjects(Individual_Account.class);
                        individual_accounts.addAll(snapshotCoupons);
                    }
//                    Log.d("couponfrag1", coupons.toString());
                    AccountsAdapter accountsAdapter = new AccountsAdapter(individual_accounts, getContext());
                    recyclerView.setAdapter(accountsAdapter);
                    accountsAdapter.notifyDataSetChanged();
                });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}