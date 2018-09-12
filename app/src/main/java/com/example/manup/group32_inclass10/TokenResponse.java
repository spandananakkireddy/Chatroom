package com.example.manup.group32_inclass10;

/**
 * Created by manup on 4/2/2018.
 */

public class TokenResponse {

    int user_id;
    String token, status, user_email, user_fname, user_lname,user_role,password;

    public TokenResponse(int user_id, String token, String status, String user_email, String user_fname, String user_lname, String user_role, String password) {
        this.user_id = user_id;
        this.token = token;
        this.status = status;
        this.user_email = user_email;
        this.user_fname = user_fname;
        this.user_lname = user_lname;
        this.user_role = user_role;
        this.password=password;

    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_email() {
        return user_email;

    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_fname() {
        return user_fname;
    }

    public void setUser_fname(String user_fname) {
        this.user_fname = user_fname;
    }

    public String getUser_lname() {
        return user_lname;
    }

    public void setUser_lname(String user_lname) {
        this.user_lname = user_lname;
    }

    public String getUser_role() {
        return user_role;
    }

    public void setUser_role(String user_role) {
        this.user_role = user_role;
    }

    @Override
    public String toString() {
        return "TokenResponse{" +
                "user_id=" + user_id +
                ", token='" + token + '\'' +
                ", status='" + status + '\'' +
                ", user_email='" + user_email + '\'' +
                ", user_fname='" + user_fname + '\'' +
                ", user_lname='" + user_lname + '\'' +
                ", user_role='" + user_role + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
