package in.finances.bankingwithinito;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Random;

import in.finances.bankingwithinito.adapters.TransactionAdapter;
import in.finances.bankingwithinito.models.AdminTransaction;
import in.finances.bankingwithinito.models.CustomerDetails;

public class Utils {

    private static final int ACCOUNT_NUMBER_LENGTH = 16;
    private static final long MIN_ACCOUNT_NUMBER = 1000000000000000L;
    private static final long MAX_ACCOUNT_NUMBER = 9999999999999999L;

    public static long generateAccountNumber() {
        Random random = new Random();
        return MIN_ACCOUNT_NUMBER + (long)(random.nextDouble() * (MAX_ACCOUNT_NUMBER - MIN_ACCOUNT_NUMBER + 1));
    }
}
