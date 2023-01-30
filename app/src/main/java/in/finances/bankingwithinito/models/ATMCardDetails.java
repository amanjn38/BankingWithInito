package in.finances.bankingwithinito.models;

import java.io.Serializable;

public class ATMCardDetails implements Serializable {

    private String acc_num;
    private String card_number;
    private String cvv;
    private String expiry_date;

    public ATMCardDetails(String acc_num, String card_number, String cvv, String expiry_date) {
        this.acc_num = acc_num;
        this.card_number = card_number;
        this.cvv = cvv;
        this.expiry_date = expiry_date;
    }

    public String getAcc_num() {
        return acc_num;
    }

    public void setAcc_num(String acc_num) {
        this.acc_num = acc_num;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }
}
