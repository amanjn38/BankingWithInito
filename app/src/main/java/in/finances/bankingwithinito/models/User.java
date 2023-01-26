package in.finances.bankingwithinito.models;

import java.io.Serializable;

public final class User implements Serializable {
    public String username;
    public String name;
    public String email;
    public String phone;
    public String addr;
    public String photoPath;

    public User(){
        this.username = "";
        this.name = "";
        this.email = "";
        this.phone = "";
        this.addr = "";
        this.photoPath = null;
    }

    public User(String username, String name, String email, String phone, String addr, String photoPath){
        this.username = username;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.addr = addr;
        this.photoPath = photoPath;
    }

    public String getUsername(){return username;}
    public String getName(){return name;}
    public String getEmail(){return email;}
    public String getPhone(){return phone;}
    public String getAddr() { return addr; }
    public String getPhotoPath(){return photoPath;}
}