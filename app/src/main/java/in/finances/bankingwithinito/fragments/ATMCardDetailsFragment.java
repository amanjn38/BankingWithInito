package in.finances.bankingwithinito.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.adapters.ATMAdapter;
import in.finances.bankingwithinito.models.ATMCardDetails;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ATMCardDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ATMCardDetailsFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ATMCardDetails> atmCardDetails;
    private ProgressDialog progressDialog;

    public ATMCardDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ATMCardDetailsActivities.
     */
    // TODO: Rename and change types and number of parameters
    public static ATMCardDetailsFragment newInstance(String param1, String param2) {
        ATMCardDetailsFragment fragment = new ATMCardDetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_a_t_m_card_details_activities, container, false);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("customerUID", Context.MODE_PRIVATE);
        String customerUID = sharedPreferences.getString("customerUID", "");
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("While we are loading your details..");
        progressDialog.show();
        atmCardDetails = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        System.out.println("testing accounts");
        if (customerUID != null) {
            FirebaseFirestore.getInstance().collection("customers_account_spec").document(customerUID).collection("savings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            String accNum = documentSnapshot.getString("accnum");
                            String cardNumber = documentSnapshot.getString("cardNumber");
                            String expiry = documentSnapshot.getString("expiry");
                            String cvv = documentSnapshot.getString("cvv");

                            ATMCardDetails atmCardDetail = new ATMCardDetails(accNum, cardNumber, cvv, expiry);
                            atmCardDetails.add(atmCardDetail);
                        }
                    }
                    progressDialog.dismiss();
                    ATMAdapter atmAdapter = new ATMAdapter(atmCardDetails, getContext());
                    recyclerView.setAdapter(atmAdapter);
                    atmAdapter.notifyDataSetChanged();
                }
            });

        }
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