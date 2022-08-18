package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
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

public class Change_UserPass extends AppCompatActivity {
    EditText MKC,MK,XNMK;
    Button Change_BTN;
    TextView Change_backBTN,ShowHIde;
    ArrayList<User> UserList;
    String LOGING_NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_pass);

        initview();
        loadData();
        Change_backBTN.setOnClickListener(view -> {
            finish();
        });

        Change_BTN.setOnClickListener(view -> {
            String USER_OLD_PASS = "";
            for (User user : UserList){
                if(user.getTK().equals(LOGING_NAME)){
                    USER_OLD_PASS = user.getMK();
                    break;
                }
            }
            if(MKC.getText().toString().isEmpty() ||
                    MK.getText().toString().isEmpty() ||
                    XNMK.getText().toString().isEmpty() ){
                Toast.makeText(this, "Vui Lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }else {
                if(!MKC.getText().toString().equals(USER_OLD_PASS)){
                    Toast.makeText(this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                }else {
                    if (!MK.getText().toString().equals(XNMK.getText().toString())){
                        Toast.makeText(this, "Mật khẩu mới không trùng khớp", Toast.LENGTH_SHORT).show();
                    }else {
                        if(MK.getText().toString().equals(MKC.getText().toString())){
                            Toast.makeText(this, "Mật khẩu mới phải khác với mật khẩu củ", Toast.LENGTH_SHORT).show();
                        }else {
                            for(User user : UserList){
                                if(user.getTK().equals(LOGING_NAME)){
                                    user.setMK(XNMK.getText().toString());
                                    saveData();
                                    Toast.makeText(this, "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }
                    }
                }
            }
        });
        ShowHIde.setOnClickListener(view -> {
            if (MKC.getTransformationMethod() == null){
                MK.setTransformationMethod(new PasswordTransformationMethod());
                XNMK.setTransformationMethod(new PasswordTransformationMethod());
                MKC.setTransformationMethod(new PasswordTransformationMethod());
            }else{
                MKC.setTransformationMethod(null);
                XNMK.setTransformationMethod(null);
                MK.setTransformationMethod(null);
            }
        });
    }

    private void initview() {
        MKC = findViewById(R.id.Change_Old_MK);
        MK = findViewById(R.id.Change_MK);
        XNMK = findViewById(R.id.Change_XNMK);
        Change_backBTN = findViewById(R.id.Change_backID);
        Change_BTN = findViewById(R.id.Change_BTN);
        ShowHIde = findViewById(R.id.Show_ChangePass);
    }

    private void loadData() {
        // Tạo SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences0", MODE_PRIVATE);

        Gson gson = new Gson();

        String json = sharedPreferences.getString("User_List", null);

        Type type = new TypeToken<ArrayList<User>>() {}.getType();
        UserList = gson.fromJson(json, type);
        LOGING_NAME = sharedPreferences.getString("TK_LOGIN","");


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