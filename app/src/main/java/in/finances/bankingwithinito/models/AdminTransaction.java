package in.finances.bankingwithinito.models;

import java.io.Serializable;

public class AdminTransaction implements Serializable {
    private String customerUID;
    private String account_type;
    private String amount;
    private Long time;

    public AdminTransaction() {

    }

    public AdminTransaction(String customerUID, String account_type, String amount, Long time) {
        this.customerUID = customerUID;
        this.account_type = account_type;
        this.amount = amount;
        this.time = time;
    }

    public String getCustomerUID() {
        return customerUID;
    }

    public void setCustomerUID(String customerUID) {
        this.customerUID = customerUID;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
