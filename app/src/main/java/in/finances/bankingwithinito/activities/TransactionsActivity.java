package in.finances.bankingwithinito.activities;

import static in.finances.bankingwithinito.Utils.generateDate;
import static in.finances.bankingwithinito.Utils.getDate;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.adapters.PassbookAdapter;
import in.finances.bankingwithinito.models.Transaction;

public class TransactionsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView startTime, endTime, showTransactions;
    private Spinner spinner;
    private ArrayList<Transaction> arrayList;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    public String balance, customerUID, type, accNum, t_type, error_msg;
    private int startyear, startmonth, startday, endyear, endmonth, endday;
    private Date startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        arrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.transaction_recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        showTransactions = findViewById(R.id.showTransactions);
        spinner = findViewById(R.id.spinner);
        balance = getIntent().getStringExtra("balance");
        accNum = getIntent().getStringExtra("account_number");
        type = getIntent().getStringExtra("account_type");
        t_type = getIntent().getStringExtra("type");
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        startyear = startmonth = startday = endyear = endmonth = endday = -1;

        startTime.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view2, year, monthOfYear, dayOfMonth) -> {
                        this.startyear = year;
                        this.startday = dayOfMonth;
                        this.startmonth = monthOfYear;
                        startTime.setText(generateDate(year, monthOfYear, dayOfMonth));
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        endTime.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view2, year, monthOfYear, dayOfMonth) -> {
                        this.endyear = year;
                        this.endday = dayOfMonth;
                        this.endmonth = monthOfYear;
                        endTime.setText(generateDate(year, monthOfYear, dayOfMonth));
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        showTransactions.setOnClickListener(view -> {
            if (checkFields()) {
                startDate = getDate(startyear, startmonth, startday);
                endDate = getDate(endyear, endmonth, endday);
                getTransactions(startDate, endDate);
            }
        });

        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Perform action when an item is selected from the spinner
        String item = parent.getItemAtPosition(position).toString();
        if (item.equalsIgnoreCase("Weekly")) {
            filterTransactionsWeekly();
        } else if (item.equalsIgnoreCase("Monthly")) {
            filterTransactionsMonthly();
        } else {
            filterTransactionsYearly();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    private boolean checkFields() {
        if (startyear == -1) {
            error_msg = "Please select start date";
            Toast.makeText(TransactionsActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        } else if (endyear == -1) {
            error_msg = "Please select end date ";
            Toast.makeText(TransactionsActivity.this, error_msg, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void getTransactions(Date startDate, Date endDate) {
        arrayList = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("customerUID", Context.MODE_PRIVATE);
        customerUID = sharedPreferences.getString("customerUID", "");
        FirebaseFirestore.getInstance().collection("customers_account_spec").document(customerUID).collection(type).document(accNum).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot d = task.getResult();
                if (d.contains("transactions")) {
                    ArrayList<Map<String, Object>> transactions;
                    transactions = (ArrayList<Map<String, Object>>) d.get("transactions");
                    System.out.println("pdfworking2");

                    for (Map<String, Object> transaction : transactions) {
                        System.out.println("pdfworking222 " + transaction);

                        Long date = (Long) transaction.get("date");
                        Double amount = (Double) transaction.get("amount");
                        String type = (String) transaction.get("type");

                        if (date >= startDate.getTime() && date <= endDate.getTime()) {
                            Transaction transaction1 = new Transaction(date, type, amount);
                            arrayList.add(transaction1);
                        }
                    }

                    PassbookAdapter passbookAdapter = new PassbookAdapter(arrayList, getApplicationContext());
                    recyclerView.setAdapter(passbookAdapter);
                    passbookAdapter.notifyDataSetChanged();

                }
            }
        });
    }

    private void filterTransactionsWeekly() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        Date startDate = calendar.getTime();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date endDate = calendar.getTime();
        getTransactions(startDate, endDate);
        // Update the UI with the filtered transactions
    }

    private void filterTransactionsMonthly() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date endDate = calendar.getTime();
        getTransactions(startDate, endDate);
        // Update the UI with the filtered transactions
    }

    private void filterTransactionsYearly() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date endDate = calendar.getTime();
        getTransactions(startDate, endDate);
        // Update the UI with the filtered transactions
    }

}