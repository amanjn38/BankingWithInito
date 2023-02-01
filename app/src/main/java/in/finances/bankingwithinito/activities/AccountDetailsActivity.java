package in.finances.bankingwithinito.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.sql.Date;
import java.util.ArrayList;

import in.finances.bankingwithinito.R;
import in.finances.bankingwithinito.models.Transaction;

public class AccountDetailsActivity extends AppCompatActivity {

    public String balance, customerUID, type, accNum;
    private TextView accountType, withdraw, withdrawWithATM, deposit, downloadStatement;
    private EditText amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        accountType = findViewById(R.id.account_type);
        withdraw = findViewById(R.id.withdraw);
        withdrawWithATM = findViewById(R.id.withdrawWithATM);
        deposit = findViewById(R.id.deposit);
        balance = getIntent().getStringExtra("balance");
        accNum = getIntent().getStringExtra("account_number");
        type = getIntent().getStringExtra("account_type");
        amount = findViewById(R.id.amount);
        downloadStatement = findViewById(R.id.downloadStatement);

        switch (type) {
            case "savings":
                accountType.setText("Savings Acccount");
                break;
            case "loan":
                accountType.setText("Loan Acccount");
                break;
            case "current":
                accountType.setText("Current Acccount");
                break;
        }
        amount.setText(balance);


        withdraw.setOnClickListener(view -> {
            Intent intent = new Intent(AccountDetailsActivity.this, IndividualTransactionActivity.class);
            intent.putExtra("account_number", accNum);
            intent.putExtra("balance", balance);
            intent.putExtra("account_type", type);
            intent.putExtra("type", "normal_withdraw");
            startActivity(intent);
        });

        withdrawWithATM.setOnClickListener(view -> {
            Intent intent = new Intent(AccountDetailsActivity.this, IndividualTransactionActivity.class);
            intent.putExtra("account_number", accNum);
            intent.putExtra("balance", balance);
            intent.putExtra("account_type", type);
            intent.putExtra("type", "atm_withdraw");
            startActivity(intent);
        });

        deposit.setOnClickListener(view -> {
            Intent intent = new Intent(AccountDetailsActivity.this, IndividualTransactionActivity.class);
            intent.putExtra("account_number", accNum);
            intent.putExtra("balance", balance);
            intent.putExtra("account_type", type);
            intent.putExtra("type", "deposit");
            startActivity(intent);
        });

        downloadStatement.setOnClickListener(view -> {
            // create a new PDF document
            Document document = new Document();

            // set the file path and name
            String fileName = "Transactions.pdf";
            String filePath = Environment.getExternalStorageDirectory() + "/" + fileName;

            // create an instance of the PdfWriter using the file path and open the document
            try {
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
            } catch (DocumentException | FileNotFoundException e) {
                e.printStackTrace();
            }
            document.open();

            // create a table and set the number of columns
            PdfPTable table = new PdfPTable(5);

            // add the headers to the table
            table.addCell("Transaction ID");
            table.addCell("Type");
            table.addCell("Amount");
            table.addCell("Date");
            table.addCell("Description");

            FirebaseFirestore.getInstance().collection("customers_account_spec").document(customerUID).collection(type).document(accNum).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot d = task.getResult();
                        if (d.contains("transactions")) {
                            ArrayList<Transaction> arrayList = (ArrayList<Transaction>) d.get("transactions");
                            int x = 1;
                            for (Transaction transaction : arrayList) {
                                table.addCell(String.valueOf(x));
                                ++x;
                                table.addCell(transaction.getType());
                                table.addCell(transaction.getAmount());
                                Long date1 = transaction.getDate();
                                Date date = new Date(date1);
                                table.addCell(date.toString());
                            }

                            // add the table to the document
                            try {
                                document.add(table);
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }

                            // close the document
                            document.close();

                            File file = new File(filePath);
                            String mimeType = URLConnection.guessContentTypeFromName(file.getName());

                            if (mimeType == null) {
                                mimeType = "application/pdf";
                            }

                            try {
                                File externalFile = new File(Environment.getExternalStorageDirectory(), fileName);
                                FileUtils.copyFile(file, externalFile);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(externalFile), mimeType);
                                startActivity(intent);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });


        });
    }


}