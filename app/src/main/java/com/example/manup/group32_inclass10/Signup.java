package com.example.manup.group32_inclass10;

/**
 * Created by manup on 4/2/2018.
 */

public class Signup {

    String fname,lname,password,email;

    public Signup(String fname, String lname, String password, String email) {
        this.fname = fname;
        this.lname = lname;
        this.password = password;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Signup{" +
                "fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
