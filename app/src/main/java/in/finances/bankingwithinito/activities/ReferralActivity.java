package in.finances.bankingwithinito.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import in.finances.bankingwithinito.R;

public class ReferralActivity extends AppCompatActivity {

    private static final HashMap<Character, Integer> letterValues = new HashMap<Character, Integer>() {{
        put('A', 1);
        put('B', 3);
        put('C', 7);
        put('D', 15);
        put('E', 31);
        put('F', 63);
        put('G', 127);
        put('H', 255);
        put('I', 511);
        put('J', 1023);
        put('K', 2047);
        put('L', 4095);
        put('M', 8191);
        put('N', 16383);
        put('O', 32767);
        put('P', 65535);
        put('Q', 131071);
        put('R', 262143);
        put('S', 524287);
        put('T', 1048575);
        put('U', 2097151);
        put('V', 4194303);
        put('W', 8388607);
        put('X', 16777215);
        put('Y', 33554431);
        put('Z', 67108863);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);
    }

    public static int computeReferralCode(String name) {
        name = name.toUpperCase();
        int referralCode = 0;
        for (int i = 0; i < name.length(); i++) {
            referralCode += letterValues.get(name.charAt(i));
        }
        return referralCode;
    }


}