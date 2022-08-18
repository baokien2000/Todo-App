package com.example.todoapp.model;

public class User {
    private String TK,MK;
    public User(String TK, String MK){
        this.TK = TK;
        this.MK = MK;
    }
    public String getTK(){
        return TK;
    }
    public void setTK(String TK){
        this.TK = TK;
    }

    public String getMK(){
        return MK;
    }
    public void setMK(String MK){ this.MK = MK;}

    String OTP;
    public String getOTP(){
        return OTP;
    }
    public void setOTP(String OTP){
        this.OTP = OTP;
    }

    String Phone;
    public String getPhone(){
        return Phone;
    }
    public void setPhone(String Phone){
        this.Phone = Phone;
    }

    boolean NUM = true;
    public boolean getNUM(){ return NUM; }
    public void setNUM(boolean NUM){ this.NUM = NUM; }
}
