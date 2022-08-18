package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.todoapp.Adapter.TaskAdapter;
import com.example.todoapp.model.Item;
import com.example.todoapp.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// activity cua Task
public class TaskActivity extends AppCompatActivity {
    Context context = (Context)this;
    String LOGIN_NAME;
    EditText Search_Task;
    ImageView AddBtn_Task;
    private ArrayList<Task> ListAfterDelete,PinList;
    public static FloatingActionButton AddTask;
    public static RecyclerView recyclerView_Task;
    public static TaskAdapter adapter_Task;
    public static String EXTRA_MESSAGE_TASK;
    public static ArrayList<Task> tasks,tasksDone;
    public static LinearLayout Task_Remove,Task_BottomLayout,Task_Pin,Task_UnPin,Task_ShareNote,Task_Timer;
    Integer SelectHour,SelectMin,Year,Mon,Day ;
    private int mYear, mMonth, mDay,Min,Hour;
    ConstraintLayout ConstraintLayout_TASK;
    public static LinearLayout NOTFOUND,NOTHING;
    @Override
    protected void onStop() {
        super.onStop();
        saveData();// Save trước khi Destroy
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        initView();
        //<----------------------------------------51800203--------------------------------------->
        recyclerView_Task.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        RecyclerView.VERTICAL,
                        false)
        ); // tạo recyclerView_Task

        loadData();

        adapter_Task = new TaskAdapter(tasks); // Tạo Adapter 1 với các Item vừa load
        recyclerView_Task.setAdapter(adapter_Task);

        // check xem co data ko? -> nếu ko có thì tạo vài data mẫu
        if(adapter_Task.getItemCount() == 0){
            SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_NAME, MODE_PRIVATE);
            String FIRST = sharedPreferences.getString(LOGIN_NAME+"FIRST", "");
            if(!LOGIN_NAME.isEmpty() && FIRST.equals("FALSE")){
                NOTHING.setVisibility(View.VISIBLE);
                recyclerView_Task.setVisibility(View.GONE);
                NOTFOUND.setVisibility(View.GONE);
            }else {
                tasks.add(new Task("Nhấn vào nút ở dưới góc phải màn hình để tạo nhiệm vụ mới"));
                tasks.add(new Task("Nhấn giữ Nhiệm vụ để chọn nhiệm vụ cần xóa"));
                tasks.add(new Task("Nhấn vào Nhiệm vụ để chỉnh sửa"));
            }
        }


        // Onclick của Add btn -> add activity
        AddBtn_Task.setOnClickListener(view -> {
            if (!MainActivity.LOGIN_NAME.isEmpty()){
                //Tạo dialog
                openDialog();
                // Dừng khoản 0.5s để dialog hiện ra rồi mới hiện keyboard
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    }
                }, 500);   //0.5 seconds
            }else {
                if(tasks.size() >= 5){
                    Toast.makeText(context, "Không thể tạo quá 5 nhiệm vụ! Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                }else {
                    //Tạo dialog
                    openDialog();
                    // Dừng khoản 0.5s để dialog hiện ra rồi mới hiện keyboard
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        }
                    }, 500);   //0.5 seconds
                }
            }


        });

        // seach items trong adapter theo title
        Search_Task.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        Task_Remove.setOnClickListener(view -> {
            ListAfterDelete = new ArrayList<>();
            for(Task task : tasks){
                if (task.getClick() == false ){
                    ListAfterDelete.add(task);
                }
            }
            // Tạo dialog về số lượng ghi chú cần xóa
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle("Xóa nhiệm vụ");
            builder1.setMessage("Xóa "+String.valueOf(tasks.size() -ListAfterDelete.size())+" nhiệm vụ?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Xóa",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            tasks.clear();
                            tasks.addAll(ListAfterDelete);
                            adapter_Task.notifyDataSetChanged();
//                            for(Item item : Trash){
//                                item.setPin(false);
//                            }
//                            TrashItem.addAll(Trash);
//                            Trash.clear();
                            Task_BottomLayout.setVisibility(View.GONE);
                            AddBtn_Task.setVisibility(View.VISIBLE);

                            if( tasks.size() == 0){
                                recyclerView_Task.setVisibility(View.GONE);
                                NOTFOUND.setVisibility(View.GONE);
                                NOTHING.setVisibility(View.VISIBLE);
                            }else {
                                recyclerView_Task.setVisibility(View.VISIBLE);
                                NOTFOUND.setVisibility(View.GONE);
                                NOTHING.setVisibility(View.GONE);
                            }
                        }
                    });

            builder1.setNegativeButton(
                    "Hủy",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ListAfterDelete.clear();
//                            Trash.clear();
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        });

        Task_ShareNote.setOnClickListener(view -> {
            String TaskTitle= null;
            for(Task task: tasks){
                if(task.getClick()){
                    TaskTitle = task.getTitle_Task();
                }
            }

            String ShareText = TaskTitle;
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,ShareText );

            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });

        Task_Pin.setOnClickListener(view -> {
            PinList= new ArrayList<>();
            ListAfterDelete = new ArrayList<>();
            for(Task task : tasks){

                if (task.getClick() == true ){
                    PinList.add(0, task);
                    task.setPin(true);
                    task.setClick(false);
                }
                else{
                    ListAfterDelete.add(task);
                }
            }
            tasks.clear();
            tasks.addAll(PinList);
            tasks.addAll(ListAfterDelete);
            adapter_Task.notifyDataSetChanged();
            Task_BottomLayout.setVisibility(View.GONE);
            AddTask.setVisibility(View.VISIBLE);
        });

        // bỏ ghim các Item
        Task_UnPin.setOnClickListener(view -> {
            PinList= new ArrayList<>();
            ListAfterDelete= new ArrayList<>();
            for(Task task: tasks){
                if(task.getPin() == true && task.getClick() == true){
                    PinList.add(task);
                    task.setPin(false);
                    task.setClick(false);
                }else{
                    ListAfterDelete.add(task);
                }
            }
            tasks.clear();
            tasks.addAll(ListAfterDelete);
            tasks.addAll(PinList);
            adapter_Task.notifyDataSetChanged();
            Task_BottomLayout.setVisibility(View.GONE);
            AddTask.setVisibility(View.VISIBLE);

        });
        Task_Timer.setOnClickListener(view -> {

            boolean Itemclick_timer = new Boolean(false);
            for(Task task : tasks){
                if(task.getClick() && !task.getTaskTime().isEmpty()){
                    Itemclick_timer = true;
                    break;
                }
            }
            PopupMenu Timermenu = new PopupMenu(this, view);
            if(Itemclick_timer == true){
                Timermenu.getMenu().add("Sửa đổi nhắc nhở").setOnMenuItemClickListener(I ->{
                    openChangeTimer();
                    return true;
                });
                Timermenu.getMenu().add("Hủy nhắc nhở").setOnMenuItemClickListener(I ->{
                    openDeleteTimer();
                    return true;
                });
            }else{
                Timermenu.getMenu().add("Đặt nhắc nhở").setOnMenuItemClickListener(I ->{
                    openSetTimer();
                    return true;
                });
            }
            Timermenu.show();


        });

        //<\----------------------------------------51800203--------------------------------------->

    }


    private void openChangeTimer() {
        Dialog dialog2 = new Dialog(TaskActivity.this);
        // làm mờ background
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // no Title
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setContentView
        dialog2.setContentView(R.layout.date_time_dialog);

        TextView date = dialog2.findViewById(R.id.Date_Id);
        TextView time = dialog2.findViewById(R.id.Time_Id);
        TextView lap = dialog2.findViewById(R.id.Lap_Id);
        for (Task task : tasks){
            if(task.getClick()){
                time.setText(task.getTaskTime());
                date.setText(task.getTaskDate());
                lap.setText(task.getTaskLap());
            }
        }
        lap.setOnClickListener(view1 -> {
            PopupMenu LapItem = new PopupMenu(dialog2.getContext(), view1);
            LapItem.getMenu().add("Một lần").setOnMenuItemClickListener(I -> {
                lap.setText("Một lần");
                return true;
            });
            LapItem.getMenu().add("Mỗi ngày").setOnMenuItemClickListener(I -> {
                lap.setText("Mỗi ngày");
                return true;
            });
            LapItem.getMenu().add("Mỗi tuần").setOnMenuItemClickListener(I -> {
                lap.setText("Mỗi tuần");
                return true;
            });
            LapItem.getMenu().add("Mỗi tháng").setOnMenuItemClickListener(I -> {
                lap.setText("Mỗi tháng");
                return true;
            });
            LapItem.show();

        });
        final Calendar calendar = Calendar.getInstance();
        // focus vào edittext khi dialog mở

        date.setOnClickListener(view1 -> {
            //show dialog
            String[] Time1 = date.getText().toString().split("/");
            mYear = Integer.parseInt(Time1[2]);
            mMonth = Integer.parseInt(Time1[1])-1;
            mDay = Integer.parseInt(Time1[0]);
            @SuppressLint("SetTextI18n")
            DatePickerDialog datePickerDialog = new DatePickerDialog(TaskActivity.this, (view2, year, month, dayOfMonth) -> {
                Year = year;
                Mon = month;
                Day = dayOfMonth;
                date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }, mYear, mMonth, mDay);
            // disable các ngày trước
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        time.setOnClickListener(view4 -> {

            Hour = calendar.get(Calendar.HOUR_OF_DAY);
            Min = calendar.get(Calendar.MINUTE);

            TimePickerDialog TimePicker = new TimePickerDialog(view4.getContext(), (timePicker, selectedHour, selectedMinute) -> {
                SelectHour = selectedHour;
                SelectMin = selectedMinute;
                String min;
                if(selectedMinute <10){
                    min = "0"+selectedMinute;
                }else {
                    min = String.valueOf(selectedMinute);
                }
                time.setText( selectedHour+ ":" + min);
            }, Hour, Min, true);//Yes 24 hour time
            TimePicker.setTitle("Select Time");
            TimePicker.show();



        });

        TextView TagDone = dialog2.findViewById(R.id.DateTimeDone);
        TextView TagCancel = dialog2.findViewById(R.id.DateTimeCancel);
        // Đổi màu nút "hoàn thành" khi editText chứa nội dung

        // Chỉ cho phép tạo nhiệm vụ khi Task có nội dung
        TagDone.setOnClickListener(view2 -> {
            for(Task task : tasks){
                if(task.getClick()){
                    task.setClick(false);
                    task.setTaskTime(time.getText().toString());
                    task.setTaskDate(date.getText().toString());
                    task.setTaskLap(lap.getText().toString());

                    String[] Date2 = date.getText().toString().split("/");
                    String[] Time2 = time.getText().toString().split(":");
                    int delayTime = (Integer.parseInt(Time2[1]) - calendar.get(Calendar.MINUTE))
                            + (Integer.parseInt(Time2[0]) - calendar.get(Calendar.HOUR_OF_DAY))*60
                            + (Integer.parseInt(Date2[0])-calendar.get(Calendar.DATE))*60*24
                            + (Integer.parseInt(Date2[1])-1-calendar.get(Calendar.MONTH))*30*60*24
                            + (Integer.parseInt(Date2[2])-calendar.get(Calendar.YEAR))*12*30*60*24;
                    WorkManager.getInstance(context).cancelAllWorkByTag(task.getTasks_id());

                    MainActivity.Notification_Title = task.getTitle_Task();
                    WorkRequest uploadWorkRequest =
                            new OneTimeWorkRequest.Builder(NotifyWorker.class)
                                    .setInitialDelay(delayTime,TimeUnit.MINUTES)
                                    .addTag(task.getTasks_id())
                                    .build();
                    WorkManager.getInstance(context).enqueue(uploadWorkRequest);
                    break;
                }
            }
            adapter_Task.notifyDataSetChanged();
            Task_BottomLayout.setVisibility(View.GONE);
            dialog2.dismiss();
            Toast.makeText(context, "Thay đổi nhắc nhở thành công", Toast.LENGTH_SHORT).show();
        });
        TagCancel.setOnClickListener(view2 -> {
            dialog2.dismiss();
        });
        Window window = dialog2.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        // move dialog to bottom & set full width

        window.setAttributes(wlp);
        dialog2.show();
    }
    private void openDeleteTimer() {
        Dialog dialog3 = new Dialog(TaskActivity.this);
        // làm mờ background
        dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // no Title
        dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setContentView
        dialog3.setContentView(R.layout.delete_date_time_dialog);

        TextView date = dialog3.findViewById(R.id.Delete_Date_Id);
        TextView time = dialog3.findViewById(R.id.Delete_Time_Id);
        TextView lap = dialog3.findViewById(R.id.Delete_Lap_Id);
        for (Task task : tasks){
            if(task.getClick()){
                time.setText(task.getTaskTime());
                date.setText(task.getTaskDate());
                lap.setText(task.getTaskLap());
            }
        }

        TextView TagDone = dialog3.findViewById(R.id.Delete_DateTimeDone);
        TextView TagCancel = dialog3.findViewById(R.id.Delete_DateTimeCancel);
        // Đổi màu nút "hoàn thành" khi editText chứa nội dung

        // Chỉ cho phép tạo nhiệm vụ khi Task có nội dung
        TagDone.setOnClickListener(view2 -> {
            for(Task task : tasks){
                if(task.getClick()){

                    task.setClick(false);
                    task.setTaskTime("");
                    task.setTaskDate("");
                    task.setTaskLap("");

                    WorkManager.getInstance(context).cancelAllWorkByTag(task.getTasks_id());
                    break;
                }
            }
            adapter_Task.notifyDataSetChanged();
            Task_BottomLayout.setVisibility(View.GONE);
            dialog3.dismiss();
            Toast.makeText(context, "Hủy bỏ nhắc nhở thành công", Toast.LENGTH_SHORT).show();
        });
        TagCancel.setOnClickListener(view2 -> {
            dialog3.dismiss();
        });
        Window window = dialog3.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        // move dialog to bottom & set full width

        window.setAttributes(wlp);
        dialog3.show();
    }
    private void openSetTimer() {
        Dialog dialog = new Dialog(TaskActivity.this);
        // làm mờ background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // no Title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setContentView
        dialog.setContentView(R.layout.date_time_dialog);

        TextView date = dialog.findViewById(R.id.Date_Id);
        TextView time = dialog.findViewById(R.id.Time_Id);
        TextView lap = dialog.findViewById(R.id.Lap_Id);
        lap.setOnClickListener(view1 -> {
            PopupMenu LapItem = new PopupMenu(dialog.getContext(), view1);
            LapItem.getMenu().add("Một lần").setOnMenuItemClickListener(I -> {
                lap.setText("Một lần");
                return true;
            });
            LapItem.getMenu().add("Mỗi ngày").setOnMenuItemClickListener(I -> {
                lap.setText("Mỗi ngày");
                return true;
            });
            LapItem.getMenu().add("Mỗi tuần").setOnMenuItemClickListener(I -> {
                lap.setText("Mỗi tuần");
                return true;
            });
            LapItem.getMenu().add("Mỗi tháng").setOnMenuItemClickListener(I -> {
                lap.setText("Mỗi tháng");
                return true;
            });
            LapItem.show();

        });
        final Calendar calendar = Calendar.getInstance();
        // focus vào edittext khi dialog mở
        date.setText(calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
        if( calendar.get(Calendar.MINUTE) < 10){
            time.setText(calendar.get(Calendar.HOUR_OF_DAY)+":"+"0"+calendar.get(Calendar.MINUTE));
        }else {
            time.setText(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
        }


        date.setOnClickListener(view1 -> {
            //show dialog

            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            @SuppressLint("SetTextI18n")
            DatePickerDialog datePickerDialog = new DatePickerDialog(TaskActivity.this, (view2, year, month, dayOfMonth) -> {
                Year = year;
                Mon = month;
                Day = dayOfMonth;
                date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }, mYear, mMonth, mDay);
            // disable các ngày trước
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        time.setOnClickListener(view4 -> {

            Hour = calendar.get(Calendar.HOUR_OF_DAY);
            Min = calendar.get(Calendar.MINUTE);

            TimePickerDialog TimePicker = new TimePickerDialog(view4.getContext(), (timePicker, selectedHour, selectedMinute) -> {
                SelectHour = selectedHour;
                SelectMin = selectedMinute;
                String min;
                if(selectedMinute <10){
                    min = "0"+selectedMinute;
                }else {
                    min = String.valueOf(selectedMinute);
                }
                time.setText( selectedHour+ ":" + min);
            }, Hour, Min, true);//Yes 24 hour time
            TimePicker.setTitle("Select Time");
            TimePicker.show();



        });
//
//            Intent myIntent = new Intent(this, NotificationService.class);
//            startService(myIntent);



        TextView TagDone = dialog.findViewById(R.id.DateTimeDone);
        TextView TagCancel = dialog.findViewById(R.id.DateTimeCancel);
        // Đổi màu nút "hoàn thành" khi editText chứa nội dung

        // Chỉ cho phép tạo nhiệm vụ khi Task có nội dung
        TagDone.setOnClickListener(view2 -> {
            for(Task task : tasks){
                if(task.getClick()){
                    task.setClick(false);
                    task.setTaskTime(time.getText().toString());
                    task.setTaskDate(date.getText().toString());
                    task.setTaskLap(lap.getText().toString());

                    String[] Date2 = date.getText().toString().split("/");
                    String[] Time2 = time.getText().toString().split(":");
                    int delayTime = (Integer.parseInt(Time2[1]) - calendar.get(Calendar.MINUTE))
                            + (Integer.parseInt(Time2[0]) - calendar.get(Calendar.HOUR_OF_DAY))*60
                            + (Integer.parseInt(Date2[0])-calendar.get(Calendar.DATE))*60*24
                            + (Integer.parseInt(Date2[1])-1-calendar.get(Calendar.MONTH))*30*60*24
                            + (Integer.parseInt(Date2[2])-calendar.get(Calendar.YEAR))*12*30*60*24;
                    MainActivity.Notification_Title = task.getTitle_Task();
                    WorkRequest uploadWorkRequest =
                            new OneTimeWorkRequest.Builder(NotifyWorker.class)
                                    .setInitialDelay(delayTime, TimeUnit.MINUTES)
                                    .addTag(task.getTasks_id())
                                    .build();
                    WorkManager.getInstance(context).enqueue(uploadWorkRequest);

                    break;
                }
            }
            adapter_Task.notifyDataSetChanged();
            Task_BottomLayout.setVisibility(View.GONE);
            dialog.dismiss();
            Toast.makeText(context, "Đặt nhắc nhở thành công", Toast.LENGTH_SHORT).show();
        });
        TagCancel.setOnClickListener(view2 -> {
            dialog.dismiss();
        });
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        // move dialog to bottom & set full width

        window.setAttributes(wlp);
        dialog.show();
    }

    private void initView() {
        recyclerView_Task = this.findViewById(R.id.Task_List); // List recyclerView_Task
        AddBtn_Task = findViewById(R.id.TaskAddId);// Add button và search của Tasks
        Search_Task = findViewById(R.id.Task_SearchID);
        AddTask = findViewById(R.id.TaskAddId);

        Task_Timer = findViewById(R.id.NotificationTasks);
        Task_BottomLayout = findViewById(R.id.Task_BOTTOM_layout); // Botomlayout
        Task_Remove = findViewById(R.id.DeleteTask); // Nút remove trong Botomlayout
        Task_Pin = findViewById(R.id.Pintasks); // Nút Pin trong Botomlayout
        Task_UnPin = findViewById(R.id.UnPintasks);//Icon UnPin trong Botomlayout
        Task_ShareNote = findViewById(R.id.ShareTasks);//Nút share notes
        ConstraintLayout_TASK = findViewById(R.id.ConstraintLayout_TASK);
        NOTFOUND = findViewById(R.id.NOTFOUND_Task);
        NOTHING = findViewById(R.id.NOTHING_TASK);
    }

    //<\----------------------------------------51800203--------------------------------------->
    // ẩn keyboard khi người dùng click vào màn hình


    private void openDialog() {
        // tạo dialog
        Dialog dialog = new Dialog(TaskActivity.this);
        // làm mờ background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // no Title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setContentView
        dialog.setContentView(R.layout.task_add_dialog);

        EditText tastText = dialog.findViewById(R.id.Tast_Id);
        // focus vào edittext khi dialog mở
        tastText.requestFocus();

        TextView TastDone = dialog.findViewById(R.id.TaskDone);
        TextView TastCancel = dialog.findViewById(R.id.TaskCancel);
        // Đổi màu nút "hoàn thành" khi editText chứa nội dung
        tastText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")){
                    TastDone.setTextColor(getResources().getColor(R.color.hintColor));
                }else {
                    TastDone.setTextColor(Color.parseColor("#39cbe4"));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        // Chỉ cho phép tạo nhiệm vụ khi Task có nội dung
        TastDone.setOnClickListener(view -> {
            int TColor = TastDone.getCurrentTextColor();
            if( TColor == Color.parseColor("#39cbe4")){
                String TaskValues = tastText.getText().toString();
                Task task = new Task(TaskValues);
                Random r = new Random();
                int i1 = r.nextInt(100 - 1) + 1;
                task.setTasks_id(TaskValues+String.valueOf(i1));
                tasks.add(task);
                adapter_Task.notifyDataSetChanged();
                recyclerView_Task.setAdapter(adapter_Task);
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(tastText.getWindowToken(), 0);
                }catch (Exception e){}
                if( tasks.size() == 0){
                    recyclerView_Task.setVisibility(View.GONE);
                    NOTFOUND.setVisibility(View.GONE);
                    NOTHING.setVisibility(View.VISIBLE);
                }else {
                    recyclerView_Task.setVisibility(View.VISIBLE);
                    NOTFOUND.setVisibility(View.GONE);
                    NOTHING.setVisibility(View.GONE);
                }
                dialog.dismiss();
            }
        });

        TastCancel.setOnClickListener(view -> {
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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

    }
    // class filter theo title
    private void filter(String text) { // tìm item theo titles
        ArrayList<Task> filteredList = new ArrayList<>();
        if (text.equals("")){
            adapter_Task.setTasks(tasks);
            recyclerView_Task.setAdapter(adapter_Task);
            if( tasks.size() == 0){
                recyclerView_Task.setVisibility(View.GONE);
                NOTFOUND.setVisibility(View.GONE);
                NOTHING.setVisibility(View.VISIBLE);
            }else {
                recyclerView_Task.setVisibility(View.VISIBLE);
                NOTFOUND.setVisibility(View.GONE);
                NOTHING.setVisibility(View.GONE);
            }
        }else{
            for (Task task : tasks) {
                if (task.getTitle_Task().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(task);
                }
            }
            adapter_Task.setTasks(filteredList);
            adapter_Task.notifyDataSetChanged();
            if(filteredList.size() == 0){
                recyclerView_Task.setVisibility(View.GONE);
                NOTFOUND.setVisibility(View.VISIBLE);
                NOTHING.setVisibility(View.GONE);
            }else {
                recyclerView_Task.setVisibility(View.VISIBLE);
                NOTFOUND.setVisibility(View.GONE);
                NOTHING.setVisibility(View.GONE);
            }
        }

    }
    @Override// ẩn keyboard khi người dùng click vào màn hình
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e){}
        Search_Task.clearFocus(); // bỏ focus ô search khi click ra ngoài
        return super.dispatchTouchEvent(ev);
    }
    // Show cảnh báo khi thoát khỏi ứng dụng
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean taskcheck = new Boolean(false);
        for (Task task : tasks){
            if(task.getClick()){
                taskcheck = true;
                break;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(taskcheck == true){
                for (Task task : tasks){
                    if(task.getClick()){
                        task.setClick(false);
                    }
                }
                TaskActivity.adapter_Task.notifyDataSetChanged();
                AddTask.setVisibility(View.VISIBLE);
                TaskActivity.Task_BottomLayout.setVisibility(View.GONE);
            }else {
                exitByBackKey();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {

        // do something when the button is clicked
        // do something when the button is clicked
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Thoát khỏi ứng dụng ?")
                .setPositiveButton("Thoát", (dialog, arg1) -> {
                    saveData();
                    TaskActivity.this.finishAffinity();
                })
                .setNegativeButton("Hủy", (dialog, arg1) -> dialog.cancel())
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Tạo menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem task) { // sự kiện click item Menu
        switch (task.getItemId()){
            case R.id.Notes_2:
                finish(); // trở vể activity Notes
                break;
            case R.id.Remove_All_2: // Xóa Tất cả Item sự kiện
                AlertDialog RemoveAll = new AlertDialog.Builder(this)
                        .setTitle("Xóa tất cả các ghi chú?!")
                        .setMessage("Tất cả ghi chú bị xóa vĩnh viễn!!")

                        .setPositiveButton("Xóa", (dialog, arg1) -> {
                            tasks.clear();
                            adapter_Task.notifyDataSetChanged();

                            recyclerView_Task.setVisibility(View.GONE);
                            NOTFOUND.setVisibility(View.GONE);
                            NOTHING.setVisibility(View.VISIBLE);
                            saveData();
                        })

                        .setNegativeButton("Hủy", (dialog, arg1) -> {
                            dialog.cancel();         // hủy dialog
                        })

                        .show();
                break;
            case R.id.Tast_Done:
                Intent TrashIntent= new Intent(this, TaskDoneActivity.class);
                startActivity(TrashIntent);
                break;

        }
        return super.onOptionsItemSelected(task);
    }

    private void loadData() {
        SharedPreferences MainsharedPreferences = getSharedPreferences("shared preferences0", MODE_PRIVATE);
        LOGIN_NAME = MainsharedPreferences.getString("TK_LOGIN","");

        if(LOGIN_NAME.isEmpty()){
            // Tạo SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("Defaul", MODE_PRIVATE);
//        // Tạo 2 biến gson cho items và ListAfterDelete

            Gson gson3 = new Gson();
            Gson gson4 = new Gson();


            String json3 = sharedPreferences.getString("Defaul"+"TaskItem", null);
            String json4 = sharedPreferences.getString("Defaul"+"TaskDoneItem", null);

            Type type1 = new TypeToken<ArrayList<Task>>() {}.getType();
            Type type2 = new TypeToken<ArrayList<Task>>() {}.getType();

            tasks = gson3.fromJson(json3, type1);
            tasksDone = gson4.fromJson(json4, type2);

            if (tasks == null) {
                tasks = new ArrayList<>();
            }
            if (tasksDone == null) {
                tasksDone = new ArrayList<>();
            }
        }else {
            // Tạo SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_NAME, MODE_PRIVATE);
//        // Tạo 2 biến gson cho items và ListAfterDelete

            Gson gson3 = new Gson();
            Gson gson4 = new Gson();


            String json3 = sharedPreferences.getString(LOGIN_NAME+"TaskItem", null);
            String json4 = sharedPreferences.getString(LOGIN_NAME+"TaskDoneItem", null);

            Type type1 = new TypeToken<ArrayList<Task>>() {}.getType();
            Type type2 = new TypeToken<ArrayList<Task>>() {}.getType();

            tasks = gson3.fromJson(json3, type1);
            tasksDone = gson4.fromJson(json4, type2);

            if (tasks == null) {
                tasks = new ArrayList<>();
            }
            if (tasksDone == null) {
                tasksDone = new ArrayList<>();
            }
        }

    }
    // Hàm saveData vào file json
    public void saveData() {
//
        if(LOGIN_NAME.isEmpty()){
            SharedPreferences sharedPreferences = getSharedPreferences("Defaul", MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();
            Gson gson2 = new Gson();


            String json3 = gson.toJson(tasks);
            String json4 = gson2.toJson(tasksDone);

            editor.putString("Defaul"+"TaskItem", json3);
            editor.putString("Defaul"+"TaskDoneItem", json4);

            editor.apply();
        }else {
            SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_NAME, MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();
            Gson gson2 = new Gson();


            String json3 = gson.toJson(tasks);
            String json4 = gson2.toJson(tasksDone);

            editor.putString(LOGIN_NAME+"TaskItem", json3);
            editor.putString(LOGIN_NAME+"TaskDoneItem", json4);

            editor.apply();
        }



    }
    //<\----------------------------------------51800203--------------------------------------->
}