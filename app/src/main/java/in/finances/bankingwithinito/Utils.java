package in.finances.bankingwithinito;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Utils {

    private static final int ACCOUNT_NUMBER_LENGTH = 16;
    private static final long MIN_ACCOUNT_NUMBER = 1000000000000000L;
    private static final long MAX_ACCOUNT_NUMBER = 9999999999999999L;

    public static long generateAccountNumber() {
        Random random = new Random();
        return MIN_ACCOUNT_NUMBER + (long) (random.nextDouble() * (MAX_ACCOUNT_NUMBER - MIN_ACCOUNT_NUMBER + 1));
    }

    public static String generateDate(int year, int month, int day) {
        String date = day + "";
        switch (day % 10) {
            case 1:
                date += "st";
                break;
            case 2:
                date += "nd";
                break;
            case 3:
                date += "rd";
                break;
            default:
                date += "th";
        }
        switch (month) {
            case 0:
                date += " Jan";
                break;
            case 1:
                date += " Feb";
                break;
            case 2:
                date += " Mar";
                break;
            case 3:
                date += " Apr";
                break;
            case 4:
                date += " May";
                break;
            case 5:
                date += " June";
                break;
            case 6:
                date += " July";
                break;
            case 7:
                date += " Aug";
                break;
            case 8:
                date += " Sept";
                break;
            case 9:
                date += " Oct";
                break;
            case 10:
                date += " Nov";
                break;
            case 11:
                date += " Dec";
                break;
        }
        return date;
    }

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }

}
