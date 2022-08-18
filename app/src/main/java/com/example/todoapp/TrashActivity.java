package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.Adapter.ItemAdapter;
import com.example.todoapp.Adapter.TrashAdapter;
import com.example.todoapp.model.Item;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TrashActivity extends AppCompatActivity {
    GridLayoutManager Gridlayout;
    public  static RecyclerView recyclerView_trash;
    EditText TrashSearch;
    private ArrayList<Item> Trash_Items,BackupList;
    private TrashAdapter Trash_Adapter;
    TextView Caution;
    String NumberDeleteday,LOGIN_NAME;
    public  static LinearLayout NOTFOUND,NOTHING;
    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        initView(); // findviewbyId
        loadData();
        Gridlayout = new GridLayoutManager(this,1);
        recyclerView_trash.setLayoutManager(Gridlayout);
        //Lấy các item bị xóa
        Trash_Items = new ArrayList<>();
        Trash_Items.addAll(MainActivity.TrashItem);


        // Set Adapter
        Trash_Adapter = new TrashAdapter(Trash_Items);// Tạo Adapter 1 với các Item vừa load
        recyclerView_trash.setAdapter(Trash_Adapter);

        if( Trash_Items.size() == 0){
            recyclerView_trash.setVisibility(View.GONE);
            NOTFOUND.setVisibility(View.GONE);
            NOTHING.setVisibility(View.VISIBLE);
        }else {
            recyclerView_trash.setVisibility(View.VISIBLE);
            NOTFOUND.setVisibility(View.GONE);
            NOTHING.setVisibility(View.GONE);
        }
        Caution.setText("Các ghi sẽ được lưu trong thùng rác "+ NumberDeleteday+  " trươc khi bị xóa vĩnh viễn");
        String[] NUM = NumberDeleteday.split(" ");
        boolean digitsOnly = TextUtils.isDigitsOnly(NUM[0]);

        // Lấy ngày h hiện tại
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DATE, 30); // Cộng thêm 30 ngày
        Date deleteday = c.getTime();

        ArrayList<Item> ListAfterDelete = new ArrayList<>();
        for (Item item: Trash_Items){
            if (item.getDeleteDay() == null){
                if (digitsOnly == true){
                    item.setDeleteDay(deleteday); // Gán DeleteDay cho item
                }
            }
            if(item.getDeleteDay().after(today)){
                ListAfterDelete.add(item);  // Xóa các item quá 30 ngày
            }
        }
        Trash_Items.clear();
        Trash_Items.addAll(ListAfterDelete);
        Trash_Adapter.notifyDataSetChanged();
        ListAfterDelete.clear();

//        search
        TrashSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                BackupList = new ArrayList<>();
                BackupList.addAll(Trash_Items);
                filter(s.toString());
            }
        });
    }

    private void initView() {
        TrashSearch = findViewById(R.id.Trash_SearchID);// search
        recyclerView_trash = this.findViewById(R.id.Trash_List);//Tạo recyclerView
        Caution = findViewById(R.id.TrashCaution);
        NOTFOUND = findViewById(R.id.NOTFOUND_Trash);
        NOTHING = findViewById(R.id.NOTHING_TRASK);
    }

    private void filter(String text) { // tìm item theo titles
        ArrayList<Item> filteredList = new ArrayList<>();
        if (text.equals("")){
            Trash_Adapter.setItems(Trash_Items);
            recyclerView_trash.setAdapter(Trash_Adapter);
            if( Trash_Items.size() == 0){
                recyclerView_trash.setVisibility(View.GONE);
                NOTFOUND.setVisibility(View.GONE);
                NOTHING.setVisibility(View.VISIBLE);
            }else {
                recyclerView_trash.setVisibility(View.VISIBLE);
                NOTFOUND.setVisibility(View.GONE);
                NOTHING.setVisibility(View.GONE);
            }
        }else{
            for (Item item : Trash_Items) {
                if (item.getTitle().toLowerCase().contains(text.toLowerCase())
                        || item.getSentence().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            BackupList.clear();
            BackupList.addAll(filteredList);
            Trash_Adapter.setItems(filteredList);
            Trash_Adapter.notifyDataSetChanged();
            if(filteredList.size() == 0){
                recyclerView_trash.setVisibility(View.GONE);
                NOTFOUND.setVisibility(View.VISIBLE);
                NOTHING.setVisibility(View.GONE);
            }else {
                recyclerView_trash.setVisibility(View.VISIBLE);
                NOTFOUND.setVisibility(View.GONE);
                NOTHING.setVisibility(View.GONE);
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Tạo menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trash_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // sự kiện click item Menu
        switch (item.getItemId()){
            case R.id.Trash_Back:
                saveData();
                finish();
                break;
            case R.id.Remove_All_trash: // Xóa Tất cả
                AlertDialog alertbox = new AlertDialog.Builder(this)
                        .setTitle("Xóa tất cả các ghi chú?")
                        .setMessage("Tất cả ghi chú sẽ bị xóa vĩnh viễn khỏi ứng dụng !!")

                        .setPositiveButton("Xóa", (dialog, arg1) -> {
                            Trash_Items.clear();// Xóa Tất cả
                            MainActivity.TrashItem.clear();
                            Trash_Adapter.notifyDataSetChanged();
                            saveData();

                            recyclerView_trash.setVisibility(View.GONE);
                            NOTFOUND.setVisibility(View.GONE);
                            NOTHING.setVisibility(View.VISIBLE);
                        })

                        .setNegativeButton("Hủy", (dialog, arg1) -> {
                            dialog.cancel();         // hủy dialog
                        })

                        .show();

                break;
            case R.id.KhoiPhuc_AllTrash: // Khôi phục tất cả
                AlertDialog alertbox2 = new AlertDialog.Builder(this)
                        .setTitle("Khôi phục tất cả các ghi chú?")
                        .setMessage("Tất cả ghi chú sẽ được khôi phục !!")

                        .setPositiveButton("Khôi phục", (dialog, arg1) -> {
                            Trash_Items.clear();// Xóa Tất cả
                            for(Item item2 : MainActivity.TrashItem){
                                item2.setDeleteDay(null);
                            }
                            MainActivity.items.addAll(MainActivity.TrashItem);
                            Trash_Items.clear();
                            MainActivity.adapter.notifyDataSetChanged();
                            Trash_Adapter.notifyDataSetChanged();
                            saveData();
                        })

                        .setNegativeButton("Hủy", (dialog, arg1) -> {
                            dialog.cancel();         // hủy dialog
                        })

                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveData() { // save

        if(LOGIN_NAME.isEmpty()){
            SharedPreferences sharedPreferences = getSharedPreferences("Defaul", MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();

            String json = gson.toJson(MainActivity.items);
            String json2 = gson.toJson(MainActivity.TrashItem);

            editor.putString("Defaul"+"NotesItem", json);
            editor.putString("Defaul"+"TrashItem", json2);

            editor.apply();
        }else {
            SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_NAME, MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();

            String json = gson.toJson(MainActivity.items);
            String json2 = gson.toJson(MainActivity.TrashItem);

            editor.putString(LOGIN_NAME+"NotesItem", json);
            editor.putString(LOGIN_NAME+"TrashItem", json2);

            editor.apply();
        }


    }
    private void loadData() {
        // Tạo SharedPreferences
        SharedPreferences MainsharedPreferences = getSharedPreferences("shared preferences0", MODE_PRIVATE);
        LOGIN_NAME = MainsharedPreferences.getString("TK_LOGIN","");
        if(LOGIN_NAME.isEmpty()){
            SharedPreferences sharedPreferences = getSharedPreferences("Defaul", MODE_PRIVATE);
            NumberDeleteday = sharedPreferences.getString("Defaul"+"Delete_Text","");
        }else {
            SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_NAME, MODE_PRIVATE);
            NumberDeleteday = sharedPreferences.getString(LOGIN_NAME+"Delete_Text","");
        }
        if (NumberDeleteday.isEmpty()){
            NumberDeleteday = "30 ngày";
        }

    }
}