package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.todoapp.Adapter.TaskDoneAdapter;
import com.example.todoapp.Adapter.TrashAdapter;
import com.example.todoapp.model.Item;
import com.example.todoapp.model.Task;
import com.google.gson.Gson;

import java.util.ArrayList;

public class TaskDoneActivity extends AppCompatActivity {
    public static RecyclerView recyclerView_TaskDone;
    EditText TaskDoneSearch;
    String LOGIN_NAME;
    ConstraintLayout ConstraintLayout_Task_DONE;
    public static LinearLayout NOTFOUND,NOTHING;
    public static ArrayList<Task> TaskDone_Items;
    public static TaskDoneAdapter TaskDone_Adapter;

    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_done);

        initView();
        loadData();
        recyclerView_TaskDone.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        RecyclerView.VERTICAL,
                        false)
        ); // tạo recyclerView_Task
        //Lấy các item bị xóa
        TaskDone_Items = new ArrayList<>();
        TaskDone_Items.addAll(TaskActivity.tasksDone);

        // Set Adapter
        TaskDone_Adapter = new TaskDoneAdapter(TaskDone_Items);// Tạo Adapter 1 với các Item vừa load
        recyclerView_TaskDone.setAdapter(TaskDone_Adapter);
        if( TaskDone_Items.size() == 0){
            recyclerView_TaskDone.setVisibility(View.GONE);
            NOTFOUND.setVisibility(View.GONE);
            NOTHING.setVisibility(View.VISIBLE);
        }else {
            recyclerView_TaskDone.setVisibility(View.VISIBLE);
            NOTFOUND.setVisibility(View.GONE);
            NOTHING.setVisibility(View.GONE);
        }

        TaskDoneSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void initView() {
        recyclerView_TaskDone = findViewById(R.id.TaskDone_List);
        TaskDoneSearch =findViewById(R.id.TaskDone_SearchID);
        ConstraintLayout_Task_DONE = findViewById(R.id.ConstraintLayout_Task_done);
        NOTFOUND = findViewById(R.id.NOTFOUND_Task_done);
        NOTHING = findViewById(R.id.NOTHING_TASK_DONE);
    }
    public void loadData(){
        SharedPreferences MainsharedPreferences = getSharedPreferences("shared preferences0", MODE_PRIVATE);
        LOGIN_NAME = MainsharedPreferences.getString("TK_LOGIN","");
    }
    public void saveData() {
//
        if(LOGIN_NAME.isEmpty()){
            SharedPreferences sharedPreferences = getSharedPreferences("Defaul", MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();
            Gson gson2 = new Gson();


            String json3 = gson.toJson(TaskActivity.tasks);
            String json4 = gson2.toJson(TaskActivity.tasksDone);

            editor.putString("Defaul"+"TaskItem", json3);
            editor.putString("Defaul"+"TaskDoneItem", json4);

            editor.apply();
        }else {
            SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_NAME, MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();
            Gson gson2 = new Gson();


            String json3 = gson.toJson(TaskActivity.tasks);
            String json4 = gson2.toJson(TaskActivity.tasksDone);

            editor.putString(LOGIN_NAME+"TaskItem", json3);
            editor.putString(LOGIN_NAME+"TaskDoneItem", json4);

            editor.apply();
        }

    }

    private void filter(String text) { // tìm item theo titles
        ArrayList<Task> filteredList = new ArrayList<>();
        if (text.equals("")){
            TaskDone_Adapter.setTask(TaskDone_Items);
            recyclerView_TaskDone.setAdapter(TaskDone_Adapter);
            if( TaskDone_Items.size() == 0){
                recyclerView_TaskDone.setVisibility(View.GONE);
                NOTFOUND.setVisibility(View.GONE);
                NOTHING.setVisibility(View.VISIBLE);
            }else {
                recyclerView_TaskDone.setVisibility(View.VISIBLE);
                NOTFOUND.setVisibility(View.GONE);
                NOTHING.setVisibility(View.GONE);
            }
        }else{
            for (Task task : TaskDone_Items) {
                if (task.getTitle_Task().toLowerCase().contains(text.toLowerCase())){
                    filteredList.add(task);
                }
            }
            TaskDone_Adapter.setTask(filteredList);
            TaskDone_Adapter.notifyDataSetChanged();
            if(filteredList.size() == 0){
                recyclerView_TaskDone.setVisibility(View.GONE);
                NOTFOUND.setVisibility(View.VISIBLE);
                NOTHING.setVisibility(View.GONE);
            }else {
                recyclerView_TaskDone.setVisibility(View.VISIBLE);
                NOTFOUND.setVisibility(View.GONE);
                NOTHING.setVisibility(View.GONE);
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Tạo menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_done_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // sự kiện click item Menu
        switch (item.getItemId()){
            case R.id.TaskDone_Back:
                saveData();
                finish();
                break;
            case R.id.TasksDone_removeAll:
                AlertDialog alertbox = new AlertDialog.Builder(this)
                        .setTitle("Xóa tất cả các nhiệm vụ đã hoàn thành?")
                        .setMessage("Tất cả nhiệm vụ sẽ bị xóa vĩnh viễn khỏi ứng dụng !!")

                        .setPositiveButton("Xóa", (dialog, arg1) -> {
                            TaskActivity.tasksDone.clear();
                            TaskDone_Items.clear();
                            TaskDone_Adapter.notifyDataSetChanged();
                            saveData();

                            recyclerView_TaskDone.setVisibility(View.GONE);
                            NOTFOUND.setVisibility(View.GONE);
                            NOTHING.setVisibility(View.VISIBLE);
                        })

                        .setNegativeButton("Hủy", (dialog, arg1) -> {
                            dialog.cancel();         // hủy dialog
                        })

                        .show();

        }
        return super.onOptionsItemSelected(item);
    }

}