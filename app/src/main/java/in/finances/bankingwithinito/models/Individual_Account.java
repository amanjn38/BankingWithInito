package in.finances.bankingwithinito.models;

import java.io.Serializable;

public class Individual_Account implements Serializable {
    private String account_number, type;
    private String balance;

    public Individual_Account() {

    }

    public Individual_Account(String account_number, String type, String balance) {
        this.account_number = account_number;
        this.type = type;
        this.balance = balance;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
