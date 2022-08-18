package com.example.todoapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.model.Item;
import com.example.todoapp.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class Login_activity extends AppCompatActivity {
    TextView Forgot,SignUp;
    Button LoginBtn;
    ArrayList<User> UserList;
    EditText Login_TK,Login_MK;
    boolean Num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        loadData();
        Forgot.setOnClickListener(view -> {
            Intent myIntent = new Intent(this, Fogot_activity.class);
            startActivityForResult(myIntent,97);
        });
        SignUp.setOnClickListener(view -> {
            Intent myIntent = new Intent(this, SignUp_activity.class);
            startActivityForResult(myIntent,98);
        });
        LoginBtn.setOnClickListener(view -> {
                if (Login_TK.getText().toString().isEmpty() || Login_MK.getText().toString().isEmpty() ){
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }else {
                    String UserMk = "";
                    boolean Check = false;
                    for (User user : UserList){
                        if (user.getTK().equals(Login_TK.getText().toString())){
                            Check = true;
                            UserMk= user.getMK();
                            break;
                        }
                    }
                    if (Check == false){
                        Toast.makeText(this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                    }else {
                        if(!UserMk.equals(Login_MK.getText().toString())){
                            Toast.makeText(this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                        }else {
                            for (User user :UserList){
                                if(user.getTK().equals(Login_TK.getText().toString())){
                                    Num = user.getNUM();
                                    break;
                                }
                            }
                            if(Num == true){

                                AlertDialog RemoveAll = new AlertDialog.Builder(this)
                                        .setTitle("Bạn có muốn đồng bộ hóa dữ liệu không?!")
                                        .setMessage("Các Ghi chú và nhiệm vụ của bạn khi chưa đăng nhập sẽ được chuyển vào tài khoản")
                                        .setPositiveButton("Có", (dialog, arg1) -> {
                                            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                                            Intent i2 = new Intent(); // trả dữ liệu về sau khi người dùng tạo hoặc sửa notes

                                            ArrayList<String> ListEvent2 = new ArrayList<>();
                                            ListEvent2.add(Login_TK.getText().toString());
                                            ListEvent2.add("YES");

                                            i2.putStringArrayListExtra("LoginName",ListEvent2);

                                            setResult(Activity.RESULT_OK,i2);
                                            saveData();

                                            for (User user :UserList){
                                                if(user.getTK().equals(Login_TK.getText().toString())){
                                                    user.setNUM(false);
                                                    saveData2();
                                                    break;
                                                }
                                            }
                                            Login_TK.setText("");
                                            Login_TK.clearFocus();
                                            Login_MK.setText("");
                                            Login_MK.clearFocus();
                                            SharedPreferences sharedPreferences = getSharedPreferences(Login_TK.getText().toString(), MODE_PRIVATE);

                                            SharedPreferences.Editor editor = sharedPreferences.edit();


                                            editor.putString(Login_TK.getText().toString()+"FIRST", "TRUE");

                                            editor.apply();
                                            finish();

                                        })

                                        .setNegativeButton("Không", (dialog, arg1) -> {
                                            dialog.cancel();         // hủy dialog

                                            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                                            Intent i2 = new Intent(); // trả dữ liệu về sau khi người dùng tạo hoặc sửa notes

                                            ArrayList<String> ListEvent2 = new ArrayList<>();
                                            ListEvent2.add(Login_TK.getText().toString());
                                            ListEvent2.add("NO");

                                            i2.putStringArrayListExtra("LoginName",ListEvent2);
                                            setResult(Activity.RESULT_OK,i2);
                                            saveData();

                                            for (User user :UserList){
                                                if(user.getTK().equals(Login_TK.getText().toString())){
                                                    user.setNUM(false);
                                                    saveData2();
                                                    break;
                                                }
                                            }
                                            Login_TK.setText("");
                                            Login_TK.clearFocus();
                                            Login_MK.setText("");
                                            Login_MK.clearFocus();
                                            SharedPreferences sharedPreferences = getSharedPreferences(Login_TK.getText().toString(), MODE_PRIVATE);

                                            SharedPreferences.Editor editor = sharedPreferences.edit();


                                            editor.putString(Login_TK.getText().toString()+"FIRST", "TRUE");

                                            editor.apply();
                                            finish();
                                        })

                                        .show();
                            }else{
                                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                                Intent i2 = new Intent(); // trả dữ liệu về sau khi người dùng tạo hoặc sửa notes
                                ArrayList<String> ListEvent2 = new ArrayList<>();
                                ListEvent2.add(Login_TK.getText().toString());
                                ListEvent2.add("NO");

                                i2.putStringArrayListExtra("LoginName",ListEvent2);
                                setResult(Activity.RESULT_OK,i2);
                                saveData();
                                Login_TK.setText("");
                                Login_TK.clearFocus();
                                Login_MK.setText("");
                                Login_MK.clearFocus();


                                SharedPreferences sharedPreferences = getSharedPreferences("shared preferences0", MODE_PRIVATE);

                                SharedPreferences.Editor editor = sharedPreferences.edit();


                                editor.putString("FIRST", "FALSE");

                                editor.apply();
                                finish();

                            }





                        }
                    }
                }

        });

    }

    private void initView() {
        Forgot = findViewById(R.id.ForgotPassId);
        SignUp = findViewById(R.id.RegisterId);
        LoginBtn = findViewById(R.id.LoginBtn);
        Login_TK= findViewById(R.id.Login_TK);
        Login_MK= findViewById(R.id.Login_MK);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // vì có 1 vài trường hợp BottomLayout chưa được tắt thì người dùng nhấn sửa hoặc tạo ghi chú
        // nên phải ẩn BottomLayout khi quay lại từ các activity khác
        if (resultCode == RESULT_OK) {
            loadData();
        }
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
    public void saveData() {

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences0", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String LoginName = Login_TK.getText().toString();

        editor.putString("TK_LOGIN", LoginName);

        editor.apply();


    }
    public void saveData2() {

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences0", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String json = gson.toJson(UserList);

        editor.putString("User_List", json);

        editor.apply();


    }
}