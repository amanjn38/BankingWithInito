package in.finances.bankingwithinito.models;

import java.io.Serializable;

public class Transaction implements Serializable {
    private Long Date;
    private String type;
    private String amount;

    public Transaction(Long date, String type, String amount) {
        Date = date;
        this.type = type;
        this.amount = amount;
    }

    public Long getDate() {
        return Date;
    }

    public void setDate(Long date) {
        Date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
