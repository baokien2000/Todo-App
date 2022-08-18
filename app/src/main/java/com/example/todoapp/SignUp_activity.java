package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.concurrent.ThreadLocalRandom;

public class SignUp_activity extends AppCompatActivity {
    EditText TK,MK,PIN,XNMK,Sent;
    Button SignUpBTN;
    TextView Sign_backBTN,Show;
    ArrayList<User> UserList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initview();
        loadData();

        Sign_backBTN.setOnClickListener(view -> {
            setResult(RESULT_OK);
            finish();
        });

        SignUpBTN.setOnClickListener(view -> {
            if (UserList.size() == 0){
                if(TK.getText().toString().isEmpty() || MK.getText().toString().isEmpty()
                        || PIN.getText().toString().isEmpty() || XNMK.getText().toString().isEmpty() ){
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }else {
                    if (PIN.getText().toString().length() != 6){
                        Toast.makeText(this, "Mã PIN phải có 6 kí tự", Toast.LENGTH_SHORT).show();
                    }else {
                        boolean digitsOnly = TextUtils.isDigitsOnly(PIN.getText());
                        if (digitsOnly == false){
                            Toast.makeText(this, "Mã PIN phải là các số từ 0 đến 9", Toast.LENGTH_SHORT).show();
                        }else {
                            if (MK.getText().toString().length()<6){
                                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                            }else {
                                if (!MK.getText().toString().equals(XNMK.getText().toString())){
                                    Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                                }else {
                                    User user = new User(TK.getText().toString(),MK.getText().toString());
                                    user.setOTP(PIN.getText().toString());
                                    UserList.add(user);
                                    saveData();
                                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                                    TK.setText("");
                                    TK.clearFocus();
                                    MK.setText("");
                                    MK.clearFocus();
                                    PIN.setText("");
                                    PIN.clearFocus();
                                    XNMK.setText("");
                                    XNMK.clearFocus();
                                }
                            }
                        }
                    }
                }
            }else {
                boolean Check = false;
                for (User user : UserList) {
                    if (user.getTK().equals(TK.getText().toString())) {
                        Toast.makeText(this, "Tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
                        Check = true;
                        break;
                    }
                }
                if (Check == false) {
                    if (TK.getText().toString().isEmpty() || MK.getText().toString().isEmpty()
                            || PIN.getText().toString().isEmpty() || XNMK.getText().toString().isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    } else {
                        if (MK.getText().toString().length() != 6) {
                            Toast.makeText(this, "Mật khẩu phải có 6 kí tự", Toast.LENGTH_SHORT).show();
                        } else {
                            boolean digitsOnly = TextUtils.isDigitsOnly(PIN.getText());
                            if (digitsOnly == false) {
                                Toast.makeText(this, "Mã PIN phải là các số từ 0 đến 9", Toast.LENGTH_SHORT).show();
                            } else {
                                if (MK.getText().toString().length() < 6) {
                                    Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (!MK.getText().toString().equals(XNMK.getText().toString())) {
                                        Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                                    } else {
                                        User user = new User(TK.getText().toString(), MK.getText().toString());
                                        user.setOTP(PIN.getText().toString());
                                        user.setNUM(true);
                                        UserList.add(user);
                                        saveData();
                                        Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                                        TK.setText("");
                                        TK.clearFocus();
                                        MK.setText("");
                                        MK.clearFocus();
                                        PIN.setText("");
                                        PIN.clearFocus();
                                        XNMK.setText("");
                                        XNMK.clearFocus();
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
            }else{
                XNMK.setTransformationMethod(null);
                MK.setTransformationMethod(null);
            }
        });
        Sent.setOnClickListener(view -> {
            int PINCode = ThreadLocalRandom.current().nextInt(100000, 999999);
            PIN.setText(String.valueOf(PINCode));
        });


    }

    private void initview() {
        TK = findViewById(R.id.SignUp_TK);
        MK = findViewById(R.id.SignUp_Mk);
        PIN = findViewById(R.id.SignUp_Pin);
        XNMK = findViewById(R.id.SignUp_XNMK);

        SignUpBTN = findViewById(R.id.SignUp_Btn);

        Sign_backBTN = findViewById(R.id.SignUp_BackId);

        Show = findViewById(R.id.Show_SignUpPass);
        Sent = findViewById(R.id.SentPin);
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