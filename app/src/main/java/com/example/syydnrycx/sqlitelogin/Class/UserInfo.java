package com.example.syydnrycx.sqlitelogin.Class;

import android.app.Application;

public class UserInfo extends Application{
    private String name;
    private String phone;
    private int total;

    public String getname(){
        return name;
    }
    public String getphone(){
        return phone;
    }
    public int gettotal(){
        return total;
    }

    public void setname(String n){
        this.name=n;
    }
    public void setphone(String n){
        this.phone=n;
    }
    public void settotal(int n){
        this.total=n;
    }
}
