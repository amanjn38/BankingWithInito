package in.finances.bankingwithinito.models;

import java.io.Serializable;

public class CustomerDetails implements Serializable {

    private String n, add, e, ph, un, uid;
    private Double lt, lo;
    private long dob;

    public CustomerDetails() {

    }

    public CustomerDetails(String n, String add, String e, String ph, String un, String uid, Double lt, Double lo, long dob) {
        this.n = n;
        this.add = add;
        this.e = e;
        this.ph = ph;
        this.un = un;
        this.uid = uid;
        this.lt = lt;
        this.lo = lo;
        this.dob = dob;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Double getLt() {
        return lt;
    }

    public void setLt(Double lt) {
        this.lt = lt;
    }

    public Double getLo() {
        return lo;
    }

    public void setLo(Double lo) {
        this.lo = lo;
    }

    public long getDob() {
        return dob;
    }

    public void setDob(long dob) {
        this.dob = dob;
    }
}
