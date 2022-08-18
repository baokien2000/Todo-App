package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Fogot_activity extends AppCompatActivity {
    EditText TK,MK,PIN,XNMK;
    Button ForgotBTN;
    TextView Forgot_backBTN,Show;
    ArrayList<User> UserList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fogot);

        initView();
        loadData();

        Forgot_backBTN.setOnClickListener(view -> {
            setResult(RESULT_OK);
            finish();
        });

        ForgotBTN.setOnClickListener(view -> {
            if(TK.getText().toString().isEmpty() || MK.getText().toString().isEmpty()||
                PIN.getText().toString().isEmpty() || XNMK.getText().toString().isEmpty()){
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }else {
                if (PIN.getText().toString().length()<6){
                    Toast.makeText(this, "Mã PIN phải có 6 ký tự", Toast.LENGTH_SHORT).show();
                }else {
                    boolean digitsOnly = TextUtils.isDigitsOnly(PIN.getText());
                    if (digitsOnly == false){
                        Toast.makeText(this, "Mã PIN phải là các số từ 0 đến 9", Toast.LENGTH_SHORT).show();
                    }else {
                        String UserPIN = "";
                        boolean Check = false;
                        for (User user : UserList){
                            if (user.getTK().equals(TK.getText().toString())){
                                Check = true;
                                UserPIN= user.getOTP();
                                break;
                            }
                        }
                        if (Check == false){
                            Toast.makeText(this, "Tài khoản hoặc mã PIN không chính xác", Toast.LENGTH_SHORT).show();
                        }else {
                            if (!UserPIN.equals(PIN.getText().toString())) {
                                Toast.makeText(this, "Tài khoản hoặc mã PIN không chính xác", Toast.LENGTH_SHORT).show();
                            } else {
                                if (!MK.getText().toString().equals(XNMK.getText().toString())){
                                    Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                                }else {
                                    for (User user : UserList){
                                        if (user.getTK().equals(TK.getText().toString())){
                                            user.setMK(XNMK.getText().toString());
                                            saveData();
                                            Toast.makeText(this, "Khôi phục mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                            TK.setText("");
                                            TK.clearFocus();
                                            MK.setText("");
                                            MK.clearFocus();
                                            PIN.setText("");
                                            PIN.clearFocus();
                                            XNMK.setText("");
                                            XNMK.clearFocus();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

            }
        });
        Show.setOnClickListener(view -> {
            if (MK.getTransformationMethod() == null){
                MK.setTransformationMethod(new PasswordTransformationMethod());
                XNMK.setTransformationMethod(new PasswordTransformationMethod());
                PIN.setTransformationMethod(new PasswordTransformationMethod());
            }else{
                PIN.setTransformationMethod(null);
                XNMK.setTransformationMethod(null);
                MK.setTransformationMethod(null);
            }
        });
    }

    private void initView() {
        TK = findViewById(R.id.Forgot_TK);
        MK = findViewById(R.id.Forgot_MK);
        PIN = findViewById(R.id.Forgot_PIN);
        XNMK = findViewById(R.id.Forgot_XNTK);

        ForgotBTN = findViewById(R.id.Forgot_KPMT);

        Forgot_backBTN = findViewById(R.id.Forgot_BackId);

        Show = findViewById(R.id.Show_ForgotPass);
    }

    private void loadData() {
        // Tạo SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences0", MODE_PRIVATE);

        Gson gson = new Gson();

        String json = sharedPreferences.getString("User_List", null);

        Type type = new TypeToken<ArrayList<User>>() {}.getType();
        UserList = gson.fromJson(json, type);


        if (UserList == null) {
            UserList = new ArrayList<>();
        }





    }
    // Hàm saveData vào file json
    public void saveData() {

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences0", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String json = gson.toJson(UserList);

        editor.putString("User_List", json);

        editor.apply();


    }
}