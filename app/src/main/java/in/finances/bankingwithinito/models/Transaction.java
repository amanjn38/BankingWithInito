package in.finances.bankingwithinito.models;

import java.io.Serializable;

public class Transaction implements Serializable {
    private Long Date;
    private String type;
    private Double amount;

    public Transaction(Long date, String type, Double amount) {
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
