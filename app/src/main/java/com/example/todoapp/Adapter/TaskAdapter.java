package com.example.todoapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.todoapp.R.layout.task_layout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.MainActivity;
import com.example.todoapp.R;
import com.example.todoapp.TaskActivity;
import com.example.todoapp.TaskDoneActivity;
import com.example.todoapp.add_event;
import com.example.todoapp.model.Item;
import com.example.todoapp.model.Task;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// Adapter cho Tasks
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private static ArrayList<Task> tasks;
    ArrayList<String> newList;
    @LayoutRes private final int resLayoutId_Task;
    public TaskAdapter(@NonNull ArrayList<Task> tasks){
        this.tasks = tasks;
        resLayoutId_Task = task_layout;
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(resLayoutId_Task, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        holder.bind(tasks.get(position),position);
    }

    @Override
    public int getItemCount() {
        return this.tasks.size();
    }

    public void setTasks(@NonNull ArrayList<Task> tasks){
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{

        TextView TextViewTitle;
        LinearLayout ItemView4;
        CheckBox check,checkDone;
        ImageView Pin,Timer;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            TextViewTitle = itemView.findViewById(R.id.text_view_title_task);
            ItemView4 = itemView.findViewById(R.id.Linear_Task);
            check = itemView.findViewById(R.id.checkdelete); // ô check của mỗi items
            Pin = itemView.findViewById(R.id.Task_pin_Icon);
            Timer = itemView.findViewById(R.id.Task_Notification_Icon);
            checkDone = itemView.findViewById(R.id.Task_CheckBoxID);
        }

        public void bind(Task task , int position){
            if(tasks.size() == 0 ){
                TaskActivity.AddTask.setVisibility(View.VISIBLE);
                TaskActivity.Task_BottomLayout.setVisibility(View.GONE);

                TaskActivity.recyclerView_Task.setVisibility(View.GONE);
                TaskActivity.NOTFOUND.setVisibility(View.GONE);
                TaskActivity.NOTHING.setVisibility(View.VISIBLE);
            }else {
                TaskActivity.recyclerView_Task.setVisibility(View.VISIBLE);
                TaskActivity.NOTFOUND.setVisibility(View.GONE);
                TaskActivity.NOTHING.setVisibility(View.GONE);
            }
            if(task.getTaskTime() == null ){
                task.setTaskTime("");
            }
            if(task.getTaskDate() == null ){
                task.setTaskDate("");
            }
            if(task.getTaskLap() == null ){
                task.setTaskLap("");
            }

            Date today = new Date();
//                Date date = ;
            String Time3[] = task.getTaskTime().split(":");
            String Date3[] = task.getTaskDate().split("/");
            if (Time3.length != 1 && Date3.length != 1 ) {
                Calendar Timer = Calendar.getInstance();
                Timer.setTime(new Date());
                Timer.set(Calendar.HOUR_OF_DAY, Integer.parseInt(Time3[0]));// for 6 hour
                Timer.set(Calendar.MINUTE, Integer.parseInt(Time3[1]));// for 0 min
                Timer.set(Calendar.SECOND, 0);// for 0 sec
                Timer.set(Calendar.DATE, Integer.parseInt(Date3[0]));// for 0 sec
                Timer.set(Calendar.MONTH, Integer.parseInt(Date3[1])-1);// for 0 sec
                Timer.set(Calendar.YEAR, Integer.parseInt(Date3[2]));// for 0 sec

                if (today.after(Timer.getTime())) {
                    task.setTaskDate("");
                    task.setTaskTime("");
                    task.setTaskLap("");
                }
            }

            if(!task.getTaskTime().isEmpty()){
                Timer.setVisibility(View.VISIBLE);
            }else {
                Timer.setVisibility(View.GONE);
            }

            TextViewTitle.setText(task.getTitle_Task());


            if (task.getPin() == true){ // nếu item được ghim thì hiện icon
                Pin.setVisibility(View.VISIBLE);
            }else {
                Pin.setVisibility(View.GONE);
            }
            task.setDoneClick(false);
            if (task.getDoneClick() == true){
                checkDone.setChecked(true);
            }else {
                checkDone.setChecked(false);
            }

            checkDone.setOnClickListener(view -> {
                if (task.getDoneClick() == true){
                    task.setDoneClick(false); // setClick items
                    checkDone.setChecked(false);// ẩn checkbox items
                }else{
                    task.setDoneClick(true); // setClick items
                    checkDone.setChecked(true);// hiện checkbox items
                }
                task.setClick(false);
                TaskActivity.tasksDone.add(task);
                tasks.remove(task);
                if( tasks.size() == 0){
                    TaskActivity.recyclerView_Task.setVisibility(View.GONE);
                    TaskActivity.NOTFOUND.setVisibility(View.GONE);
                    TaskActivity.NOTHING.setVisibility(View.VISIBLE);

                    TaskActivity.Task_BottomLayout.setVisibility(View.GONE);
                    TaskActivity.AddTask.setVisibility(View.VISIBLE);
                }else {
                    TaskActivity.recyclerView_Task.setVisibility(View.VISIBLE);
                    TaskActivity.NOTFOUND.setVisibility(View.GONE);
                    TaskActivity.NOTHING.setVisibility(View.GONE);
                    TaskActivity.Task_BottomLayout.setVisibility(View.GONE);
                    TaskActivity.AddTask.setVisibility(View.VISIBLE);
                    for (Task task1: tasks){
                        task1.setClick(false);
                    }
                    notifyDataSetChanged();
                }
                notifyDataSetChanged();
                TaskActivity.adapter_Task.notifyDataSetChanged();

            });




            if (task.getClick() == true){
                check.setChecked(true);
            }else {
                check.setChecked(false);
            }
            check.setOnClickListener(view -> {
                if (task.getClick() == true){
                    task.setClick(false); // setClick items
                    check.setChecked(false);// ẩn checkbox items
                }else{
                    task.setClick(true); // setClick items
                    check.setChecked(true);// hiện checkbox items
                }

                boolean Hide2 = true;
                for(Task task3 : tasks){
                    if (task3.getClick() == true){
                        Hide2 = false;
                    }
                }
                if(Hide2 == true) {
                    TaskActivity.Task_BottomLayout.setVisibility(View.GONE);// hiện BottomLayout
                    TaskActivity.AddTask.setVisibility(View.VISIBLE);
                }

                //Show/hide Pin/Unpin
                int Num=0,unPinItem = 0;
                for(Task task1 : tasks){
                    if (task1.getClick()){
                        Num+=1;
                        if (task1.getPin() == false){
                            unPinItem += 1;
                        }
                    }
                }
                // Nếu có 1 trong các item được clik chưa được pin thì button Pin sẽ hiện
                // Button UnPin chỉ hiện trong trường hợp các Item được click đã được pin
                if(unPinItem != 0){
                    TaskActivity.Task_Pin.setVisibility(View.VISIBLE);
                    TaskActivity.Task_UnPin.setVisibility(View.GONE);
                }else {
                    TaskActivity.Task_Pin.setVisibility(View.GONE);
                    TaskActivity.Task_UnPin.setVisibility(View.VISIBLE);
                }

                //Vì mỗi lần chỉ share 1 notes qua ứng dụng khác
                // nên Ẩn button share Notes khi có trên 2 items clicks
                if(Num >1){
                    TaskActivity.Task_ShareNote.setVisibility(View.GONE);
                    TaskActivity.Task_Timer.setVisibility(View.GONE);
                }else {
                    TaskActivity.Task_ShareNote.setVisibility(View.VISIBLE);
                    TaskActivity.Task_Timer.setVisibility(View.VISIBLE);

                }
            });

            ItemView4.setOnClickListener(view -> {
                Dialog dialog = new Dialog(ItemView4.getContext());
                // làm mờ background
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                // no Title
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                // setContentView
                dialog.setContentView(R.layout.task_add_dialog);

                EditText tastText = dialog.findViewById(R.id.Tast_Id);
                tastText.setText(task.getTitle_Task());
                // focus vào edittext khi dialog mở
                tastText.requestFocus();

                TextView TastDone = dialog.findViewById(R.id.TaskDone);
                TextView TaskCancel = dialog.findViewById(R.id.TaskCancel);
                // Đổi màu nút "hoàn thành" khi editText chứa nội dung
                tastText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.toString().equals("")){
                            TastDone.setTextColor(ItemView4.getContext().getResources().getColor(R.color.hintColor));
                        }else {
                            TastDone.setTextColor(Color.parseColor("#39cbe4"));
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable editable) { }
                });
                // Chỉ cho phép tạo nhiệm vụ khi Task có nội dung
                TastDone.setOnClickListener(view1 -> {
                    int TColor = TastDone.getCurrentTextColor();
                    if( TColor == Color.parseColor("#39cbe4")){
                        String TaskValues = tastText.getText().toString();
                        task.setTitle(TaskValues);
                        TaskActivity.adapter_Task.notifyDataSetChanged();
                        try {
                            InputMethodManager imm = (InputMethodManager) ItemView4.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(tastText.getWindowToken(), 0);
                        }catch (Exception e){}
                        dialog.dismiss();
                    }
                });
                TaskCancel.setOnClickListener(view1 -> {
                    try {
                        InputMethodManager imm = (InputMethodManager) ItemView4.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(tastText.getWindowToken(), 0);
                    }catch (Exception e){}
                    dialog.dismiss();

                });
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();

                // move dialog to bottom & set full width
                wlp.gravity = Gravity.BOTTOM;
//        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(wlp);
                dialog.show();
                InputMethodManager imm = (InputMethodManager) ItemView4.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            });


            ItemView4.setOnLongClickListener(view -> {
                TaskActivity.Task_BottomLayout.setVisibility(View.VISIBLE);// hiện BottomLayout
                TaskActivity.AddTask.setVisibility(View.GONE);

                if (task.getClick() == true){
                    task.setClick(false); // setClick items
                    check.setChecked(false);// ẩn checkbox items
                }else{
                    task.setClick(true); // setClick items
                    check.setChecked(true);// hiện checkbox items
                }

                boolean Hide2 = true;
                for(Task task3 : tasks){
                    if (task3.getClick() == true){
                        Hide2 = false;
                    }
                }
                if(Hide2 == true) {
                    TaskActivity.Task_BottomLayout.setVisibility(View.GONE);// hiện BottomLayout
                    TaskActivity.AddTask.setVisibility(View.VISIBLE);
                }

                //Show/hide Pin/Unpin
                int Num=0,unPinItem = 0;
                for(Task task1 : tasks){
                    if (task1.getClick()){
                        Num+=1;
                        if (task1.getPin() == false){
                            unPinItem += 1;
                        }
                    }
                }
                // Nếu có 1 trong các item được clik chưa được pin thì button Pin sẽ hiện
                // Button UnPin chỉ hiện trong trường hợp các Item được click đã được pin
                if(unPinItem != 0){
                    TaskActivity.Task_Pin.setVisibility(View.VISIBLE);
                    TaskActivity.Task_UnPin.setVisibility(View.GONE);
                }else {
                    TaskActivity.Task_Pin.setVisibility(View.GONE);
                    TaskActivity.Task_UnPin.setVisibility(View.VISIBLE);
                }

                //Vì mỗi lần chỉ share 1 notes qua ứng dụng khác
                // nên Ẩn button share Notes khi có trên 2 items clicks
                if(Num >1){
                    TaskActivity.Task_ShareNote.setVisibility(View.GONE);
                    TaskActivity.Task_Timer.setVisibility(View.GONE);

                }else {
                    TaskActivity.Task_ShareNote.setVisibility(View.VISIBLE);
                    TaskActivity.Task_Timer.setVisibility(View.VISIBLE);
                }

                return true;
            });
//

        }
    }
}
