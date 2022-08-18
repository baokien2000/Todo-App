package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.Adapter.ItemAdapter;
import com.example.todoapp.Adapter.TagAdapter;
import com.example.todoapp.model.Item;
import com.example.todoapp.model.Tag;
import com.example.todoapp.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagManager extends AppCompatActivity {
    public static RecyclerView TagRecyclerView;
    EditText TagSearch;
    GridLayoutManager Gridlayout;
    ArrayList ItemTagList = new ArrayList() ;
    public static FloatingActionButton AddTags;
    public static TagAdapter TagAdapter;
    public static ArrayList<Tag> tags;
    private ArrayList<Tag> BackupList;
    public static ConstraintLayout ConstraintLayout_Tag;
    public static LinearLayout NOTFOUND,NOTHING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_manager);

        initView();

        Gridlayout = new GridLayoutManager(this,1); // Tạo grid với số cột bằng 1 -> listView
        TagRecyclerView.setLayoutManager(Gridlayout); // set recyclerView

        tags = new ArrayList<>(); // Tạo items trống

        // Show các tag
        for (Item item : MainActivity.items){
            try {
                String ItemTag = item.getTag();
                String[] Tags = ItemTag.split(",");
                if(ItemTag != ""){
                    for (String i : Tags){
                        if(ItemTagList.contains(i.replace(" ","")) == false && i !="" ){
                            tags.add(new Tag(i.replaceFirst("^ ", ""),0));
                            ItemTagList.add(i.replace(" ",""));
                        }
                    }
                }
            }catch (Exception e){}
        }
        // Đêm số lượng Notes của mỗi tag
        for (Tag tag: tags){
                int num =0;
                for(Item item:MainActivity.items) {
                    String ItemTag = item.getTag();
                    try {

                        if (  ItemTag.replaceFirst("^ ", "")
                                .contains(tag.getTitle_Tag()
                                        .replaceFirst("^ ", "")) == true) {
                            num += 1;
                        }

                    }catch (Exception e){}

                }
                tag.setNumNotes(num);
        }

        TagAdapter = new TagAdapter(tags);
        TagRecyclerView.setAdapter(TagAdapter);

        if( tags.size() == 0){
            TagRecyclerView.setVisibility(View.GONE);
            NOTFOUND.setVisibility(View.GONE);
            NOTHING.setVisibility(View.VISIBLE);
        }else {
            TagRecyclerView.setVisibility(View.VISIBLE);
            NOTFOUND.setVisibility(View.GONE);
            NOTHING.setVisibility(View.GONE);
        }

        // search Tag
        TagSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                BackupList = new ArrayList<>();
                BackupList.addAll(tags);
                filter(s.toString());
            }
        });

//        AddTags.setOnClickListener(view -> {
//            //Tạo dialog
//            openDialog();
//            // Dừng khoản 0.5s để dialog hiện ra rồi mới hiện keyboard
//            // nều ko dừng -> keyboard sẽ open -> close -> open
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//                }
//            }, 500);   //0.5 seconds
//
//        });
    }
    @Override
    public boolean onContextItemSelected(MenuItem I ) { // sự kiện click Context Item = Edit
        if (I.getTitle() == "Xóa") {
            saveData();
        }
        if (I.getTitle() == "Chỉnh sửa"){
            // Dừng khoản 0.5s để dialog hiện ra rồi mới hiện keyboard
            // nều ko dừng -> keyboard sẽ open -> close -> open
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//                }
//            }, 500);   //0.5 seconds
//            saveData();
        }
        return true;
    }

    private void initView() {
        TagRecyclerView = this.findViewById(R.id.Tags_List);// List recyclerView
//        AddTags =  findViewById(R.id.TagsAddId); // Nút add
        TagSearch = findViewById(R.id.Tags_SearchID); // Ô search
        NOTFOUND = findViewById(R.id.NOTFOUND_Tag);
        ConstraintLayout_Tag = findViewById(R.id.ConstraintLayout_Tag);
        NOTHING = findViewById(R.id.NOTHING_TAG);
    }

    private void filter(String text) {
        ArrayList<Tag> filteredList = new ArrayList<>();

        if (text.equals("")){
            TagAdapter.setTags(tags);
            TagRecyclerView.setAdapter(TagAdapter);
            if( tags.size() == 0){
                TagRecyclerView.setVisibility(View.GONE);
                NOTFOUND.setVisibility(View.GONE);
                NOTHING.setVisibility(View.VISIBLE);
            }else {
                TagRecyclerView.setVisibility(View.VISIBLE);
                NOTFOUND.setVisibility(View.GONE);
                NOTHING.setVisibility(View.GONE);
            }
        }else{
            for (Tag tag : tags) {
                if (tag.getTitle_Tag().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(tag);
                }
            }
            BackupList.clear();
            BackupList.addAll(filteredList);
            TagAdapter.setTags(filteredList);
            TagAdapter.notifyDataSetChanged();
            if(filteredList.size() == 0){
                TagRecyclerView.setVisibility(View.GONE);
                NOTFOUND.setVisibility(View.VISIBLE);
                NOTHING.setVisibility(View.GONE);
            }else {
                TagRecyclerView.setVisibility(View.VISIBLE);
                NOTFOUND.setVisibility(View.GONE);
                NOTHING.setVisibility(View.GONE);
            }
        }
    }
    public void saveData() {
        if(MainActivity.LOGIN_NAME.isEmpty()){
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences("Defaul", MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();

            String json = gson.toJson(MainActivity.items);
            String json2 = gson.toJson(MainActivity.TrashItem);

            editor.putString("Defaul"+"NotesItem", json);
            editor.putString("Defaul"+"TrashItem", json2);

            editor.apply();
        }else {
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences(MainActivity.LOGIN_NAME, MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();

            String json = gson.toJson(MainActivity.items);
            String json2 = gson.toJson(MainActivity.TrashItem);

            editor.putString(MainActivity.LOGIN_NAME+"NotesItem", json);
            editor.putString(MainActivity.LOGIN_NAME+"TrashItem", json2);

            editor.apply();
        }



    }
}
