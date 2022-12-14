package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.todoapp.Adapter.ItemAdapter;
import com.example.todoapp.model.Item;
import com.example.todoapp.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

// activity cua Notes
public class MainActivity extends AppCompatActivity {
    public static String LOGIN_NAME;
    private Menu menu;
    public static FloatingActionButton AddNotes;
    public static ImageView PinIcon;
    public static LinearLayout Remove,BottomLayout,Pin,UnPin,ShareNote,SetPass,TagNotes,UnTagNotes,NotesReminder;
    public static String EditTitle,EditSentence,EditTime,EditUri,EditVideoUri,EditAudioUri,EXTRA_MESSAGE,Notification_Title,Notification_ContentText;
    public static ItemAdapter adapter;
    public static ArrayList<Item> TrashItem,items;
    private ArrayList<Item>  ListAfterDelete,Trash, BackupList,PinList,filteredTag;
    private Context context = (Context)this;
    static public MenuItem itemView;
    public static GridLayoutManager Gridlayout;
    public static RecyclerView recyclerView;
    private EditText Search;
    private TextView TagName;
    private ArrayList ItemTagList = new ArrayList() ;
    public static LinearLayout TagLinear,NOTFOUND,NOTHING;
    Integer SelectHour,SelectMin,Year,Mon,Day ;
    private int mYear, mMonth, mDay,Min,Hour;
    private int lastSelectedHour = -1;
    private int lastSelectedMinute = -1;
    public static String Size_Text,Color_Text,View_Text,Sort_Text,Delete_Text;
    @Override
    protected void onStop() {
        super.onStop();
        saveData();// Save tr?????c khi Destroy
//        startService(new Intent(this, NotificationService.class));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //<----------------------------------------51800203--------------------------------------->
        initView(); // findViewById

        // custom ActionBar
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.title_main);

        // T???o recyclerView
        Gridlayout = new GridLayoutManager(this,1); // T???o grid v???i s??? c???t b???ng 1 -> listView
        recyclerView.setLayoutManager(Gridlayout); // set recyclerView

        items = new ArrayList<>(); // T???o items tr???ng

        // Load Data
        loadData();// data bao g???m 2 list l?? notes v?? trash
        // List ????? ch???a c??c item sau khi x??a
        if (TrashItem == null){ // n???u data sau khi load l??n r???ng th?? t???o Arraylist m???i
            TrashItem = new ArrayList<>();
        }

        adapter = new ItemAdapter(items); // T???o Adapter 1 v???i c??c Item v???a load
        recyclerView.setAdapter(adapter);
        // check xem co data ko? -> n???u ko c?? th?? t???o v??i data m???u cho m??n h??nh ????? tr???ng
        // c??ng nh?? h?????ng d???n s?? c??ch d??ng note
        if(adapter.getItemCount() == 0){

            SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_NAME, MODE_PRIVATE);
            String FIRST = sharedPreferences.getString(LOGIN_NAME+"FIRST", "");
            if(!LOGIN_NAME.isEmpty() && FIRST.equals("FALSE")){
                NOTHING.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else {
                Item item = new Item("T???o ghi ch?? m???i",
                        "Nh???n v??o n??t ??? d?????i g??c ph???i m??n h??nh ????? t???o ghi ch??",
                        "Th???i gian t???o ghi ch??");
                item.setNotes_id("id1");
                items.add(item);

                Item item2 = new Item("S???a ghi ch??",
                        "Nh???n v??o ghi ch?? c???n s???a sau ???? nh???n l??u ??? g??c tr??n m??n h??nh. ",
                        "Th???i gian s???a ghi ch??");
                item2.setNotes_id("id2");
                items.add(item2);

                Item item3 = new Item("X??a ghi ch??",
                        "Nh???n gi??? ghi ch?? c???n x??a, Ghi ch?? s??? ???????c chuy???n v??o th??ng r??c trong 30 ng??y tr?????c khi b??? x??a ho??n to??n ",
                        "Th???i gian");
                item3.setNotes_id("id3");
                items.add(item3);
            }



        }




        // Onclick c???a n??t add sau ???? chuy???n sang add activity
        AddNotes.setOnClickListener(view -> {
            if (!LOGIN_NAME.isEmpty()){
                Intent ADDEVENT = new Intent(this,add_event.class);
                startActivityForResult(ADDEVENT,20);
            }else {
                if(items.size() >= 5){
                    Toast.makeText(context, "Kh??ng th??? t???o qu?? 5 ghi ch??! Vui l??ng ????ng nh???p", Toast.LENGTH_SHORT).show();
                }else {
                    Intent ADDEVENT = new Intent(this,add_event.class);
                    startActivityForResult(ADDEVENT,20);
                }
            }

        });
        // seach items trong adapter theo title
        Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
//                BackupList = new ArrayList<>();
//                BackupList.addAll(items);
                filter(s.toString());
            }
        });

        //X??a c??c item ???? ch???n
        Remove.setOnClickListener(view -> {

            boolean lock = false;
            for(Item item: items){
                if(item.getClick()){
                    if(!item.getPass().isEmpty()){
                        lock= true;
                    }
                }
            }
            if(lock == true){
                Toast.makeText(context, "Vui l??ng m??? kh??a", Toast.LENGTH_SHORT).show();
            }else{
                Trash=  new ArrayList<>();
                ListAfterDelete = new ArrayList<>();
                for(Item item : items){
                    if (item.getClick() == false ){
                        ListAfterDelete.add(item);
                    }else{
                        Trash.add(item);
                    }
                }
                // T???o dialog v??? s??? l?????ng ghi ch?? c???n x??a
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("X??a ghi ch??");
                builder1.setMessage("X??a "+String.valueOf(items.size() -ListAfterDelete.size())+" ghi ch???");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "X??a",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                items.clear();
                                items.addAll(ListAfterDelete);
                                adapter.notifyDataSetChanged();
                                for(Item item : Trash){
                                    item.setPin(false);
                                }
                                TrashItem.addAll(Trash);
                                Trash.clear();
                                BottomLayout.setVisibility(View.GONE);
                                AddNotes.setVisibility(View.VISIBLE);

                                if(items.size() == 0){
                                    recyclerView.setVisibility(View.GONE);
                                    NOTHING.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                builder1.setNegativeButton(
                        "H???y",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ListAfterDelete.clear();
                                Trash.clear();
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }


        });

        // Ghim c??c Item

        Pin.setOnClickListener(view -> {
            PinList= new ArrayList<>();
            ListAfterDelete = new ArrayList<>();
            for(Item item : items){

                if (item.getClick() == true ){
                    PinList.add(0, item);
                    item.setPin(true);
                    item.setClick(false);
                }
                else{
                    ListAfterDelete.add(item);
                }
            }
            items.clear();
            items.addAll(PinList);
            items.addAll(ListAfterDelete);
            adapter.notifyDataSetChanged();
            BottomLayout.setVisibility(View.GONE);
            AddNotes.setVisibility(View.VISIBLE);
        });

        // b??? ghim c??c Item
        UnPin.setOnClickListener(view -> {
            PinList= new ArrayList<>();
            ListAfterDelete= new ArrayList<>();
            for(Item item: items){
                if(item.getPin() == true && item.getClick() == true){
                    PinList.add(item);
                    item.setPin(false);
                }else{
                    ListAfterDelete.add(item);
                }
            }
            items.clear();
            items.addAll(ListAfterDelete);
            items.addAll(PinList);
            adapter.notifyDataSetChanged();
            BottomLayout.setVisibility(View.GONE);
            AddNotes.setVisibility(View.VISIBLE);

        });

        // chia s??? ghi ch?? sang ???ng d???ng kh??c
        ShareNote.setOnClickListener(view -> {
            boolean lock = false;
            for(Item item: items){
                if(item.getClick()){
                    if(item.getPass().isEmpty()){
                        lock =false;
                    }else{
                        lock= true;
                    }
                    break;
                }
            }
            if(lock == true){
                Toast.makeText(context, "Vui l??ng m??? kh??a", Toast.LENGTH_SHORT).show();
            }else{
                String ItemTitle= null;
                String ItemContent = null;
                for(Item item: items){
                    if(item.getClick()){
                        ItemTitle = item.getTitle();
                        ItemContent = item.getSentence();
                    }
                }

                String ShareText = ItemTitle + "\n" + ItemContent;
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,ShareText );

                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }

        });

        //G??n nh??n cho Notes
        TagNotes.setOnClickListener(view -> {
            //T???o dialog
            openDialog();
            // D???ng kho???n 0.5s ????? dialog hi???n ra r???i m???i hi???n keyboard
            // n???u ko d???ng -> keyboard s??? open -> close -> open
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                }
            }, 500);   //0.5 seconds


        });
        // String[] Tags = Addtag.split(",");
        // Toast.makeText(context, Tags[0], Toast.LENGTH_SHORT).show();
//        UnTagNotes.performLongClick();
        UnTagNotes.setOnClickListener(view ->{
            String Addtag = null;
            for (Item item : items) {
                if (item.getClick()) {
                    Addtag = item.getTag();
                    String[] Tags = Addtag.split(",");
                    PopupMenu UnTagmenu = new PopupMenu(this, view);

                    for(int i = 0;i < Tags.length;i++) {
                        String TagI = Tags[i];
                        String Addtagcopy = Addtag;
                        int num = i;
                        UnTagmenu.getMenu().add(Tags[i]).setOnMenuItemClickListener(I -> {
                            String TagAfterDelete;
                            if ( num == 0){
                                if (Tags.length == 1){
                                    TagAfterDelete = Addtagcopy.replace(TagI,"");
                                }else{
                                    TagAfterDelete = Addtagcopy.replace(TagI+",","");
                                }

                            }else{
                                TagAfterDelete= Addtagcopy.replace(","+TagI,"");
                            }


                            item.setTag(TagAfterDelete);
                            adapter.notifyDataSetChanged();
                            BottomLayout.setVisibility(View.GONE);
                            AddNotes.setVisibility(View.VISIBLE);
                            return true;
                        });
                    }
                    UnTagmenu.show();
                    break;
                }

            }


        });
        if (ItemTagList.size() == 0){
            ItemTagList.add("T???t c???");
            ItemTagList.add("Kh??ng nh??n");
        }
        filteredTag = new ArrayList<>();
        TagName.setOnClickListener(view -> {
            for (Item item : items){
                try {
                    String ItemTag = item.getTag();
                    String[] Tags = ItemTag.split(",");
                    if(ItemTag != ""){
                        for (String i : Tags){
                            if(ItemTagList.contains(i.replace(" ","")) == false && i !="" ){
                                ItemTagList.add(i.replace(" ",""));
                            }
                        }
                    }
                }catch (Exception e){}
            }
            PopupMenu Tagmenu = new PopupMenu(this, view);
            for(Object i : ItemTagList){
                Tagmenu.getMenu().add(String.valueOf(i)).setOnMenuItemClickListener(I -> {
                    TagFilter(String.valueOf(i));
                    TagName.setText(String.valueOf(i));
                    return true;
                });
            }
            Tagmenu.show();
        });

        SetPass.setOnClickListener(view -> {
//            openSetPassDialog();
            boolean Itemclick_pass = new Boolean(false);
            for(Item item : items){
                if(item.getClick() && !item.getPass().isEmpty()){
                    Itemclick_pass = true;
                    break;
                }
            }
            PopupMenu SetPassmenu = new PopupMenu(this, view);
            if(Itemclick_pass == true){
                SetPassmenu.getMenu().add("Thay ?????i m???t kh???u").setOnMenuItemClickListener(I ->{
                    openChangePassDialog();
                    return true;
                });
                SetPassmenu.getMenu().add("X??a m???t kh???u").setOnMenuItemClickListener(I ->{
                    openDeletePassDialog();
                    return true;
                });
            }else{
                SetPassmenu.getMenu().add("?????t m???t kh???u").setOnMenuItemClickListener(I ->{
                    openSetPassDialog();
                    return true;
                });
            }
            SetPassmenu.show();
        });

        NotesReminder.setOnClickListener(view -> {

            boolean Itemclick_timer = new Boolean(false);
            for(Item item : items){
                if(item.getClick() && !item.getNote_Time().isEmpty()){
                    Itemclick_timer = true;
                    break;
                }
            }
            PopupMenu Timermenu = new PopupMenu(this, view);
            if(Itemclick_timer == true){
                Timermenu.getMenu().add("S???a ?????i nh???c nh???").setOnMenuItemClickListener(I ->{
                    openChangeTimer();
                    return true;
                });
                Timermenu.getMenu().add("H???y nh???c nh???").setOnMenuItemClickListener(I ->{
                    openDeleteTimer();
                    return true;
                });
            }else{
                Timermenu.getMenu().add("?????t nh???c nh???").setOnMenuItemClickListener(I ->{
                    openSetTimer();
                    return true;
                });
            }
            Timermenu.show();


        });
        //<\---------------------------------------51800203--------------------------------------->
        Search.clearFocus();

        //<----------------------------------------MSSV1--------------------------------------->
        // mn ????? code c???a m??nh trong n??y cho d??? quan l?? nha
        // nh??? ghi ch?? th??ch ph???n m??nh code n???a
        //<\----------------------------------------MSSV1--------------------------------------->


        //<----------------------------------------MSSV2--------------------------------------->
        // mn ????? code c???a m??nh trong n??y cho d??? quan l?? nha
        // nh??? ghi ch?? th??ch ph???n m??nh code n???a
        //<\----------------------------------------MSSV2--------------------------------------->
    }

    private void openDeleteTimer() {
        Dialog dialog3 = new Dialog(MainActivity.this);
        // l??m m??? background
        dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // no Title
        dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setContentView
        dialog3.setContentView(R.layout.delete_date_time_dialog);

        TextView date = dialog3.findViewById(R.id.Delete_Date_Id);
        TextView time = dialog3.findViewById(R.id.Delete_Time_Id);
        TextView lap = dialog3.findViewById(R.id.Delete_Lap_Id);
        for (Item item : items){
            if(item.getClick()){
                time.setText(item.getNote_Time());
                date.setText(item.getNote_Date());
                lap.setText(item.getNote_Lap());
            }
        }

        TextView TagDone = dialog3.findViewById(R.id.Delete_DateTimeDone);
        TextView TagCancel = dialog3.findViewById(R.id.Delete_DateTimeCancel);
        // ?????i m??u n??t "ho??n th??nh" khi editText ch???a n???i dung

        // Ch??? cho ph??p t???o nhi???m v??? khi Task c?? n???i dung
        TagDone.setOnClickListener(view2 -> {
            for(Item item : items){
                if(item.getClick()){

                    item.setClick(false);
                    item.setNote_Time("");
                    item.setNote_Date("");
                    item.setNote_Lap("");
                    WorkManager.getInstance(context).cancelAllWorkByTag(item.getNotes_id());
                    break;
                }
            }
            adapter.notifyDataSetChanged();
            BottomLayout.setVisibility(View.GONE);
            dialog3.dismiss();
            Toast.makeText(context, "H???y b??? nh???c nh??? th??nh c??ng", Toast.LENGTH_SHORT).show();
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

    private void openChangeTimer() {
        Dialog dialog2 = new Dialog(MainActivity.this);
        // l??m m??? background
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // no Title
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setContentView
        dialog2.setContentView(R.layout.date_time_dialog);

        TextView date = dialog2.findViewById(R.id.Date_Id);
        TextView time = dialog2.findViewById(R.id.Time_Id);
        TextView lap = dialog2.findViewById(R.id.Lap_Id);
        for (Item item : items){
            if(item.getClick()){
                time.setText(item.getNote_Time());
                date.setText(item.getNote_Date());
                lap.setText(item.getNote_Lap());
            }
        }
        lap.setOnClickListener(view1 -> {
            PopupMenu LapItem = new PopupMenu(dialog2.getContext(), view1);
            LapItem.getMenu().add("M???t l???n").setOnMenuItemClickListener(I -> {
                lap.setText("M???t l???n");
                return true;
            });
            LapItem.getMenu().add("M???i ng??y").setOnMenuItemClickListener(I -> {
                lap.setText("M???i ng??y");
                return true;
            });
            LapItem.getMenu().add("M???i tu???n").setOnMenuItemClickListener(I -> {
                lap.setText("M???i tu???n");
                return true;
            });
            LapItem.getMenu().add("M???i th??ng").setOnMenuItemClickListener(I -> {
                lap.setText("M???i th??ng");
                return true;
            });
            LapItem.show();

        });
        final Calendar calendar = Calendar.getInstance();
        // focus v??o edittext khi dialog m???

        date.setOnClickListener(view1 -> {
            //show dialog
            String[] Time1 = date.getText().toString().split("/");
            mYear = Integer.parseInt(Time1[2]);
            mMonth = Integer.parseInt(Time1[1])-1;
            mDay = Integer.parseInt(Time1[0]);
            @SuppressLint("SetTextI18n")
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, (view2, year, month, dayOfMonth) -> {
                Year = year;
                Mon = month;
                Day = dayOfMonth;
                date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }, mYear, mMonth, mDay);
            // disable c??c ng??y tr?????c
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
        // ?????i m??u n??t "ho??n th??nh" khi editText ch???a n???i dung

        // Ch??? cho ph??p t???o nhi???m v??? khi Task c?? n???i dung
        TagDone.setOnClickListener(view2 -> {
            for(Item item : items){
                if(item.getClick()){
                    item.setClick(false);
                    item.setNote_Time(time.getText().toString());
                    item.setNote_Date(date.getText().toString());
                    item.setNote_Lap(lap.getText().toString());

                    String[] Date2 = date.getText().toString().split("/");
                    String[] Time2 = time.getText().toString().split(":");
                    int delayTime = (Integer.parseInt(Time2[1]) - calendar.get(Calendar.MINUTE))
                            + (Integer.parseInt(Time2[0]) - calendar.get(Calendar.HOUR_OF_DAY))*60
                            + (Integer.parseInt(Date2[0])-calendar.get(Calendar.DATE))*60*24
                            + (Integer.parseInt(Date2[1])-1-calendar.get(Calendar.MONTH))*30*60*24
                            + (Integer.parseInt(Date2[2])-calendar.get(Calendar.YEAR))*12*30*60*24;
                    WorkManager.getInstance(context).cancelAllWorkByTag(item.getNotes_id());

                    Notification_Title = item.getTitle();
                    Notification_ContentText= item.getSentence();
                    WorkRequest uploadWorkRequest =
                            new OneTimeWorkRequest.Builder(NotifyWorker.class)
                                    .setInitialDelay(delayTime,TimeUnit.MINUTES)
                                    .addTag(item.getNotes_id())
                                    .build();
                    WorkManager.getInstance(context).enqueue(uploadWorkRequest);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
            BottomLayout.setVisibility(View.GONE);
            dialog2.dismiss();
            Toast.makeText(context, "Thay ?????i nh???c nh??? th??nh c??ng", Toast.LENGTH_SHORT).show();
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


    private void openSetTimer() {
        Dialog dialog = new Dialog(MainActivity.this);
        // l??m m??? background
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
            LapItem.getMenu().add("M???t l???n").setOnMenuItemClickListener(I -> {
                lap.setText("M???t l???n");
                return true;
            });
            LapItem.getMenu().add("M???i ng??y").setOnMenuItemClickListener(I -> {
                lap.setText("M???i ng??y");
                return true;
            });
            LapItem.getMenu().add("M???i tu???n").setOnMenuItemClickListener(I -> {
                lap.setText("M???i tu???n");
                return true;
            });
            LapItem.getMenu().add("M???i th??ng").setOnMenuItemClickListener(I -> {
                lap.setText("M???i th??ng");
                return true;
            });
            LapItem.show();

        });
        final Calendar calendar = Calendar.getInstance();
        // focus v??o edittext khi dialog m???
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
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, (view2, year, month, dayOfMonth) -> {
                Year = year;
                Mon = month;
                Day = dayOfMonth;
                date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }, mYear, mMonth, mDay);
            // disable c??c ng??y tr?????c
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
        // ?????i m??u n??t "ho??n th??nh" khi editText ch???a n???i dung

        // Ch??? cho ph??p t???o nhi???m v??? khi Task c?? n???i dung
        TagDone.setOnClickListener(view2 -> {
            for(Item item : items){
                if(item.getClick()){
                    item.setClick(false);
                    item.setNote_Time(time.getText().toString());
                    item.setNote_Date(date.getText().toString());
                    item.setNote_Lap(lap.getText().toString());

                    String[] Date2 = date.getText().toString().split("/");
                    String[] Time2 = time.getText().toString().split(":");
                    int delayTime = (Integer.parseInt(Time2[1]) - calendar.get(Calendar.MINUTE))
                            + (Integer.parseInt(Time2[0]) - calendar.get(Calendar.HOUR_OF_DAY))*60
                            + (Integer.parseInt(Date2[0])-calendar.get(Calendar.DATE))*60*24
                            + (Integer.parseInt(Date2[1])-1-calendar.get(Calendar.MONTH))*30*60*24
                            + (Integer.parseInt(Date2[2])-calendar.get(Calendar.YEAR))*12*30*60*24;
                    Notification_Title = item.getTitle();
                    Notification_ContentText= item.getSentence();
                    WorkRequest uploadWorkRequest =
                            new OneTimeWorkRequest.Builder(NotifyWorker.class)
                                    .setInitialDelay(delayTime,TimeUnit.MINUTES)
                                    .addTag(item.getNotes_id())
                                    .build();
                    WorkManager.getInstance(context).enqueue(uploadWorkRequest);

                    break;
                }
            }
            adapter.notifyDataSetChanged();
            BottomLayout.setVisibility(View.GONE);
            dialog.dismiss();
            Toast.makeText(context, "?????t nh???c nh??? th??nh c??ng", Toast.LENGTH_SHORT).show();
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void notificationDialog(Context c) {
        NotificationManager notificationManager = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "tutorialspoint_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Tutorialspoint")
                //.setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(Notification_Title)
                .setContentText(Notification_ContentText)
                .setContentInfo("Information");

        notificationManager.notify(1, notificationBuilder.build());


    }
    private void initView() {
        recyclerView = this.findViewById(R.id.Notes_List);// List recyclerView
        AddNotes =  findViewById(R.id.NoteAddId); // N??t add
        Search = findViewById(R.id.Notes_SearchID); // ?? search
        PinIcon = findViewById(R.id.MainPin); // Icon Pin trong bottom layout
        BottomLayout = findViewById(R.id.BOTTOM_layout); // Botomlayout
        Remove = findViewById(R.id.DeleteNotes); // N??t remove trong Botomlayout
        Pin = findViewById(R.id.PinNotes); // N??t Pin trong Botomlayout
        UnPin = findViewById(R.id.UnPinNotes);//Icon UnPin trong Botomlayout
        ShareNote = findViewById(R.id.ShareNotes);//N??t share notes
        TagNotes = findViewById(R.id.NotesTag);//N??t setTag
        UnTagNotes = findViewById(R.id.NotesUnTag); // remove Tag
        TagName = findViewById(R.id.TagNameFill);
        TagLinear = findViewById(R.id.TagLinear);
        SetPass = findViewById(R.id.PassNotes);
        NotesReminder = findViewById(R.id.reminderNotes);
        NOTFOUND = findViewById(R.id.NOTFOUND_MAIN);
        NOTHING = findViewById(R.id.NOTHING_MAIN);
    }


    //<----------------------------------------MSSV1--------------------------------------->
    // mn ????? code c???a m??nh trong n??y cho d??? quan l?? nha
    // nh??? ghi ch?? th??ch ph???n m??nh code n???a
    //<\----------------------------------------MSSV1--------------------------------------->



    //<----------------------------------------MSSV2--------------------------------------->
    // mn ????? code c???a m??nh trong n??y cho d??? quan l?? nha
    // nh??? ghi ch?? th??ch ph???n m??nh code n???a
    //<\----------------------------------------MSSV2--------------------------------------->



    //<----------------------------------------51800203--------------------------------------->

    // m??? dialog t???o tag m???i
    private void openDialog() {
        // t???o dialog
        Dialog dialog = new Dialog(MainActivity.this);
        // l??m m??? background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // no Title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setContentView
        dialog.setContentView(R.layout.tag_add_dialog);

        EditText tastText = dialog.findViewById(R.id.Tag_Id);
        // focus v??o edittext khi dialog m???
        tastText.requestFocus();

        TextView TagDone = dialog.findViewById(R.id.TagDone);
        TextView TagCancel = dialog.findViewById(R.id.TagCancel);
        // ?????i m??u n??t "ho??n th??nh" khi editText ch???a n???i dung
        tastText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")){
                    TagDone.setTextColor(getResources().getColor(R.color.hintColor));
                }else {
                    TagDone.setTextColor(Color.parseColor("#39cbe4"));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        // Ch??? cho ph??p t???o nhi???m v??? khi Task c?? n???i dung
        TagDone.setOnClickListener(view -> {
            int TColor = TagDone.getCurrentTextColor();
            if( TColor == Color.parseColor("#39cbe4")){
                String TagValues = tastText.getText().toString();
                for (Item item : items){
                    if(item.getClick()){
                        String Addtag = item.getTag();
                        if (Addtag.isEmpty() == true){
                            item.setTag(TagValues);
                        }else {
                            String NewTag = item.getTag() + " , " + TagValues ;
                            item.setTag(NewTag);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(tastText.getWindowToken(), 0);
                }catch (Exception e){}
                dialog.dismiss();
                BottomLayout.setVisibility(View.GONE);
            }
        });
        TagCancel.setOnClickListener(view -> {
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
    private void openChangePassDialog() {
        Dialog dialog = new Dialog(MainActivity.this);
        // l??m m??? background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // no Title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setContentView
        dialog.setContentView(R.layout.change_notes_pass_dialog);

        EditText OldPass = dialog.findViewById(R.id.OldPass_Id);
        EditText NewPass = dialog.findViewById(R.id.newPass_Id);
        EditText confirmPass = dialog.findViewById(R.id.Confirm_New_Pass_Id);

        OldPass.setTransformationMethod(new PasswordTransformationMethod());
        NewPass.setTransformationMethod(new PasswordTransformationMethod());
        confirmPass.setTransformationMethod(new PasswordTransformationMethod());

        // focus v??o edittext khi dialog m???

        TextView PassDone = dialog.findViewById(R.id.ChangeItemPassDone);
        TextView PassCancel = dialog.findViewById(R.id.ChangeItemPassCancel);
        TextView PassShow = dialog.findViewById(R.id.ChangeItemPassShow);
        // ?????i m??u n??t "ho??n th??nh" khi editText ch???a n???i dung

        // Ch??? cho ph??p t???o nhi???m v??? khi Task c?? n???i dung
        PassDone.setOnClickListener(view -> {
            String OldPassValues = OldPass.getText().toString();
            String NewPassValues = NewPass.getText().toString();
            String PassConfirmValues = confirmPass.getText().toString();
            if( OldPassValues.equals("") || NewPassValues.equals("") || (PassConfirmValues.equals(""))){
                Toast.makeText(context, "Vui l??ng ??i???n ?????y ????? m???t kh???u", Toast.LENGTH_SHORT).show();
            }else {
                if (NewPass.length() < 6) {
                    Toast.makeText(context, "M???t kh???u ph???i c?? ??t nh???t 6 k?? t???", Toast.LENGTH_SHORT).show();
                } else {
                    if (!NewPassValues.equals(PassConfirmValues)) {
                        Toast.makeText(context, "M???t kh???u kh??ng tr??ng kh???p", Toast.LENGTH_SHORT).show();
                    } else {
                        if (NewPassValues.equals(OldPassValues)) {
                            Toast.makeText(context, "M???t kh???u m???i ph???i kh??c v???i m???t kh???u c??", Toast.LENGTH_SHORT).show();
                        } else {
                            for (Item item : items) {
                                if (item.getClick()) {
                                    item.setPass(PassConfirmValues);
                                    adapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                    BottomLayout.setVisibility(View.GONE);
                                    AddNotes.setVisibility(View.VISIBLE);
                                    Toast.makeText(context, "Thay ?????i m???t kh???u th??nh c??ng", Toast.LENGTH_SHORT).show();
                                    break;
                                }

                            }
                        }
                    }
                }
            }
        });
        PassCancel.setOnClickListener(view -> {
            dialog.dismiss();
            Search.clearFocus();
        });

        PassShow.setOnClickListener(view -> {
            if (OldPass.getTransformationMethod() == null){
                OldPass.setTransformationMethod(new PasswordTransformationMethod());
                NewPass.setTransformationMethod(new PasswordTransformationMethod());
                confirmPass.setTransformationMethod(new PasswordTransformationMethod());
            }else{
                OldPass.setTransformationMethod(null);
                NewPass.setTransformationMethod(null);
                confirmPass.setTransformationMethod(null);
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        // move dialog to bottom & set full width
//        wlp.gravity = Gravity.BOTTOM;
////        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        dialog.show();
    }
    private void openDeletePassDialog() {
        Dialog dialog = new Dialog(MainActivity.this);
        // l??m m??? background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // no Title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setContentView
        dialog.setContentView(R.layout.login_notes_pass_dialog);

        EditText pass = dialog.findViewById(R.id.LoginItemPass_Id);

        pass.setTransformationMethod(new PasswordTransformationMethod());
        // focus v??o edittext khi dialog m???

        TextView PassDone = dialog.findViewById(R.id.LoginItemPassDone);
        TextView PassCancel = dialog.findViewById(R.id.LoginItemPassCancel);
        TextView PassShow = dialog.findViewById(R.id.LoginItemPassShow);
        // focus v??o edittext khi dialog m???

        PassDone.setOnClickListener(view1 -> {
            for(Item item : items){
                if(item.getClick()){
                    if(item.getPass().equals(pass.getText().toString())){
                        item.setPass("");
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                        Toast.makeText(context, "M???t kh???u ???? ???????c x??a th??nh c??ng", Toast.LENGTH_SHORT).show();
                        BottomLayout.setVisibility(View.GONE);
                        AddNotes.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(context, "Sai m???t kh???u" , Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
        PassCancel.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        PassShow.setOnClickListener(view1 -> {
            if (pass.getTransformationMethod() == null){
                pass.setTransformationMethod(new PasswordTransformationMethod());
            }else{
                pass.setTransformationMethod(null);
            }
        });
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        window.setAttributes(wlp);
        dialog.show();
    }
    private void openSetPassDialog() {

        // t???o dialog
        Dialog dialog = new Dialog(MainActivity.this);
        // l??m m??? background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // no Title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setContentView
        dialog.setContentView(R.layout.set_pass_dialog);

        EditText pass = dialog.findViewById(R.id.pass_Id);
        EditText confirmPass = dialog.findViewById(R.id.Confirm_password_Id);

        pass.setTransformationMethod(new PasswordTransformationMethod());
        confirmPass.setTransformationMethod(new PasswordTransformationMethod());
        // focus v??o edittext khi dialog m???

        TextView PassDone = dialog.findViewById(R.id.PassDone);
        TextView PassCancel = dialog.findViewById(R.id.PassCancel);
        TextView PassShow = dialog.findViewById(R.id.ShowPass);
        // ?????i m??u n??t "ho??n th??nh" khi editText ch???a n???i dung

        // Ch??? cho ph??p t???o nhi???m v??? khi Task c?? n???i dung
        PassDone.setOnClickListener(view -> {
            String PassValues = pass.getText().toString();
            String PassConfirmValues = confirmPass.getText().toString();
            if( PassValues.equals("") || (PassConfirmValues.equals(""))){
                Toast.makeText(context, "Vui l??ng ??i???n ?????y ????? m???t kh???u", Toast.LENGTH_SHORT).show();
            }else{
                if(PassValues.length() <6){
                    Toast.makeText(context, "M???t kh???u ph???i c?? ??t nh???t 6 k?? t???", Toast.LENGTH_SHORT).show();
                }else{
                    if(!PassValues.equals(PassConfirmValues)){
                        Toast.makeText(context, "M???t kh???u kh??ng tr??ng kh???p", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        for (Item item : items) {
                            if (item.getClick()) {
                                item.setPass(PassConfirmValues);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                                BottomLayout.setVisibility(View.GONE);
                                AddNotes.setVisibility(View.VISIBLE);
                                break;
                            }

                        }
                    }
                }

            }
        });
        PassCancel.setOnClickListener(view -> {
            dialog.dismiss();
            Search.clearFocus();
        });

        PassShow.setOnClickListener(view -> {
            if (pass.getTransformationMethod() == null){
                pass.setTransformationMethod(new PasswordTransformationMethod());
                confirmPass.setTransformationMethod(new PasswordTransformationMethod());
            }else{
                pass.setTransformationMethod(null);
                confirmPass.setTransformationMethod(null);
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        // move dialog to bottom & set full width
//        wlp.gravity = Gravity.BOTTOM;
////        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        dialog.show();
    }
    // class filter theo title

    private void filter(String text) {
        ArrayList<Item> filteredList = new ArrayList<>();
        if(filteredTag.size() == 0){
            if (text.equals("")){
                adapter.setItems(items);
                recyclerView.setAdapter(adapter);
                if( items.size() == 0){
                    recyclerView.setVisibility(View.GONE);
                    NOTFOUND.setVisibility(View.GONE);
                    NOTHING.setVisibility(View.VISIBLE);
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    NOTFOUND.setVisibility(View.GONE);
                    NOTHING.setVisibility(View.GONE);
                }

            }else{
                for (Item item : items) {
                    if (item.getTitle().toLowerCase().contains(text.toLowerCase())
                            || item.getSentence().toLowerCase().contains(text.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
                adapter.setItems(filteredList);
                adapter.notifyDataSetChanged();

                if(filteredList.size() == 0){
                    recyclerView.setVisibility(View.GONE);
                    NOTFOUND.setVisibility(View.VISIBLE);
                    NOTHING.setVisibility(View.GONE);
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    NOTFOUND.setVisibility(View.GONE);
                    NOTHING.setVisibility(View.GONE);
                }

            }

        }else{
            if (text.equals("")){
                adapter.setItems(filteredTag);
                recyclerView.setAdapter(adapter);
            }else{
                for (Item item : filteredTag) {
                    if (item.getTitle().toLowerCase().contains(text.toLowerCase())
                            || item.getSentence().toLowerCase().contains(text.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
                adapter.setItems(filteredList);
                adapter.notifyDataSetChanged();



            }
        }
    }
    private void TagFilter(String text){
        filteredTag = new ArrayList<>();

        if (text.equals("T???t c???")){
            adapter.setItems(items);
            adapter.notifyDataSetChanged();
            filteredTag = new ArrayList<>();
        }else
            if(text.equals("Kh??ng nh??n")){
                for(Item item : items){
                    if(item.getTag().equals("")){
                        filteredTag.add(item);
                    }
                }
                adapter.setItems(filteredTag);
                adapter.notifyDataSetChanged();
            }else{
                for (Item item : items) {
                    String[] Tags = item.getTag().split(" , ");
                    for(String i:Tags){
                        if(i.replace(" ","").equals(text.replace(" ",""))){
                            filteredTag.add(item);
                        }
                    }
                }
                adapter.setItems(filteredTag);
                adapter.notifyDataSetChanged();
            }
    }
//    @Override
//    public void onBackPressed() {
//        Search.clearFocus();
//        super.onBackPressed();
//        Search.clearFocus();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // T???o menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
//        itemView = menu.findItem(R.id.Item_view); // title c???a menu Xem theo danh s??ch ho???c ?? l?????i
//        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences2", MODE_PRIVATE);
//
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        if(View_Text.equals("?? l?????i")){
//            Gridlayout.setSpanCount(2);
//            itemView.setTitle("Xem theo danh s??ch");
//            editor.putString("View_Text", "Danh s??ch");
//
//        }else {
//            Gridlayout.setSpanCount(1);
//            itemView.setTitle("Xem theo ?? l?????i");
//            editor.putString("View_Text", "?? l?????i");
//
//        }
//        editor.apply();

        MenuItem NAMELOGIN = menu.findItem(R.id.MenuUser);
        MenuItem LOGIN = menu.findItem(R.id.Login);
        if (!LOGIN_NAME.isEmpty()){
            NAMELOGIN.setTitle(LOGIN_NAME);
            NAMELOGIN.setVisible(true);
            LOGIN.setVisible(false);
        }

        this.menu = menu;
        return true;
    }
    private void updateMenuTitles() {
        MenuItem NAMELOGIN = menu.findItem(R.id.MenuUser);
        MenuItem LOGIN = menu.findItem(R.id.Login);
        if (!LOGIN_NAME.isEmpty()){
            NAMELOGIN.setTitle(LOGIN_NAME);
            NAMELOGIN.setVisible(true);
            LOGIN.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // s??? ki???n click item Menu
        switch (item.getItemId()){
            case R.id.Login:
                Intent myIntent2 = new Intent(this, Login_activity.class);
                startActivityForResult(myIntent2,99);
                break;
            case R.id.Logout:
                saveData();
                SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_NAME, MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();


                editor.putString(LOGIN_NAME+"FIRST", "FALSE");

                editor.apply();

                LOGIN_NAME ="";
                SharedPreferences MainsharedPreferences = getSharedPreferences("shared preferences0", MODE_PRIVATE);
                SharedPreferences.Editor Maineditor = MainsharedPreferences.edit();
                Maineditor.putString("TK_LOGIN", LOGIN_NAME);
                Maineditor.apply();
                finish();
                startActivity(getIntent());
                break;
            case R.id.Change_User_Pass:
                Intent myIntent3 = new Intent(this, Change_UserPass.class);
                startActivity(myIntent3);
                break;
            case R.id.Tasks: // chuy???n sang activity Task
                Intent myIntent = new Intent(this, TaskActivity.class);
                startActivity(myIntent);
                break;
            case R.id.Remove_All: // X??a T???t c??? Item s??? ki???n
                AlertDialog RemoveAll = new AlertDialog.Builder(this)
                        .setTitle("X??a t???t c??? c??c ghi ch???!")
                        .setMessage("T???t c??? ghi ch?? s??? ???????c chuy???n v??o th??ng r??c!!")

                        .setPositiveButton("X??a", (dialog, arg1) -> {
                            TrashItem.addAll(items);
                            items.clear();
                            adapter.notifyDataSetChanged();

                            saveData();

                            recyclerView.setVisibility(View.GONE);
                            NOTFOUND.setVisibility(View.GONE);
                            NOTHING.setVisibility(View.VISIBLE);
                        })

                        .setNegativeButton("H???y", (dialog, arg1) -> {
                            dialog.cancel();         // h???y dialog
                        })

                        .show();
                break;
//            case R.id.Item_view:
//                if (Gridlayout.getSpanCount() == 1){
//                    Gridlayout.setSpanCount(2);
//                    itemView.setTitle("Xem theo danh s??ch");
//                    adapter.notifyDataSetChanged();
//
//                    SharedPreferences sharedPreferences = getSharedPreferences("shared preferences2", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("View_Text","Danh s??ch");
//                    editor.apply();
//
//
//                }else{
//                    Gridlayout.setSpanCount(1);
//                    itemView.setTitle("Xem theo ?? l?????i");
//                    adapter.notifyDataSetChanged();
//
//                }
//                SharedPreferences sharedPreferences = getSharedPreferences("shared preferences2", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("View_Text","?? l?????i");
//                editor.apply();
//
//                break;
            case R.id.TrashIdMenu: // chuy???n sang th??ng r??c
                Intent TrashIntent= new Intent(this, TrashActivity.class);
                startActivity(TrashIntent);
                break;
            case R.id.TagsManager:
                Intent TagIntent= new Intent(this, TagManager.class);
                startActivity(TagIntent);
                break;
            case R.id.Notes_Setting:
                Intent SettingIntent= new Intent(this, Setting_Activity.class);
                startActivity(SettingIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // v?? c?? 1 v??i tr?????ng h???p BottomLayout ch??a ???????c t???t th?? ng?????i d??ng nh???n s???a ho???c t???o ghi ch??
        // n??n ph???i ???n BottomLayout khi quay l???i t??? c??c activity kh??c
        if (resultCode == Activity.RESULT_CANCELED ){ // ???n BottomLayout sau khi tr??? v???
            MainActivity.BottomLayout.setVisibility(View.GONE);
            AddNotes.setVisibility(View.VISIBLE);
            for (Item item:items){
                item.setClick(false);
            }
        }

        // requestCode == 20 c???a s??? ki???n Add notes
        // requestCode == 40 c???a s??? ki???n Edit notes

        if (requestCode == 20 && resultCode == Activity.RESULT_OK) { // Sau khi ??i???n th??ng tin v?? tr??? v??? m??n h??nh ch??nh
            MainActivity.BottomLayout.setVisibility(View.GONE);
            ArrayList<String> List = data.getStringArrayListExtra(EXTRA_MESSAGE);// L???y c??c th??ng tin qua ExtraMess
            String Title = List.get(0); // ti??u ?????
            String Place = List.get(1); // n???i dung
            String Time = List.get(2) ; // th???i gian t???o
            String selectPicture = List.get(3);
            String selectVideo = List.get(4);
            String selectAudio = List.get(5);
            Item item = new Item(Title, Place, Time);
            item.setpictureUri(selectPicture);
            item.setVideoUri(selectVideo);
            item.setAudioUri(selectAudio);
            item.setNotes_id(Title+Place+Time);
            items.add(item);
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            NOTHING.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
        if (requestCode == 40 && resultCode == Activity.RESULT_OK){
            ArrayList<String> List = data.getStringArrayListExtra(EXTRA_MESSAGE);// L???y c??c th??ng tin qua ExtraMess
            EditTitle = List.get(0); // ti??u ?????
            EditSentence = List.get(1); // n???i dung
            EditTime = List.get(2) ; // th???i gian s???a
            EditUri = List.get(3);
            EditVideoUri = List.get(4);
            EditAudioUri = List.get(5);
            MainActivity.BottomLayout.setVisibility(View.GONE);
            AddNotes.setVisibility(View.VISIBLE);

        }
        if (requestCode == 99 && resultCode == Activity.RESULT_OK){
            ArrayList<String> List = data.getStringArrayListExtra("LoginName");
            LOGIN_NAME = List.get(0);
            String DONGBO =List.get(1);
            if(DONGBO.equals("YES")){
                SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Gson gson = new Gson();

                String json = gson.toJson(items);
                String json2 = gson.toJson(TrashItem);
                String json3 = gson.toJson(TaskActivity.tasks);

                editor.putString(LOGIN_NAME+"NotesItem", json);
                editor.putString(LOGIN_NAME+"TrashItem", json2);

                editor.putString(LOGIN_NAME+"TaskItem", json3);
                editor.apply();
            }
            updateMenuTitles();
            finish();
            startActivity(getIntent());
        }
    }


    @Override// ???n keyboard khi ng?????i d??ng click v??o m??n h??nh
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        Search.clearFocus(); // b??? focus ?? search khi click ra ngo??i
        return super.dispatchTouchEvent(ev);
    }

    // Show c???nh b??o khi tho??t kh???i ???ng d???ng
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean BackCheck = new Boolean(false);
        for(Item item : items){
           if(item.getClick()){
               BackCheck = true;
               break;
           }
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(BackCheck == true){
                for(Item item : items){
                    if(item.getClick()){
                        item.setClick(false);
                    }
                }
                BottomLayout.setVisibility(View.GONE);
                AddNotes.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }else {
                exitByBackKey();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Tho??t kh???i ???ng d???ng ?")

                .setPositiveButton("Tho??t", (dialog, arg1) -> {
                    saveData();
                    finishAffinity();        // k???t th??c t???t c??? c??c activity
                })

                .setNegativeButton("H???y", (dialog, arg1) -> {
                    dialog.cancel();         // h???y dialog
                })

                .show();
    }
    // H??m loadData t??? file json

    private void loadData() {
        SharedPreferences MainsharedPreferences = getSharedPreferences("shared preferences0", MODE_PRIVATE);
        LOGIN_NAME = MainsharedPreferences.getString("TK_LOGIN","");

        if(LOGIN_NAME.isEmpty()){
            // T???o SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("Defaul", MODE_PRIVATE);

//        // T???o 2 bi???n gson cho items v?? ListAfterDelete
            Gson gson = new Gson();
            Gson gson2 = new Gson();
            Gson gson3 = new Gson();

            String json = sharedPreferences.getString("Defaul"+"NotesItem", null);
            String json2 = sharedPreferences.getString("Defaul"+"TrashItem", null);
            String json3 = sharedPreferences.getString("Defaul"+"TaskItem", null);

            Type type = new TypeToken<ArrayList<Item>>() {}.getType();
            Type type1 = new TypeToken<ArrayList<Task>>() {}.getType();
            items = gson.fromJson(json, type);
            TrashItem = gson2.fromJson(json2, type);
            TaskActivity.tasks = gson3.fromJson(json3, type1);

            if (items == null) {
                items = new ArrayList<>();
            }


            Size_Text = sharedPreferences.getString("Defaul"+"Size_Text", "");
            Sort_Text=sharedPreferences.getString("Defaul"+"Sort_Text", "");
            Color_Text=sharedPreferences.getString("Defaul"+"Color_Text", "");
            View_Text=sharedPreferences.getString("Defaul"+"View_Text", "");
            Delete_Text=sharedPreferences.getString("Defaul"+"Delete_Text", "");

            if(Size_Text.equals("")){
                Size_Text = "V???a";
                Sort_Text="Th???i gian t???o g???n nh???t";
                Color_Text="Tr???ng";
                View_Text="Danh s??ch";
                Delete_Text="30 ng??y";

            }
        }else {

            // T???o SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_NAME, MODE_PRIVATE);

//        // T???o 2 bi???n gson cho items v?? ListAfterDelete
            Gson gson = new Gson();
            Gson gson2 = new Gson();
            Gson gson3 = new Gson();

            String json = sharedPreferences.getString(LOGIN_NAME+"NotesItem", null);
            String json2 = sharedPreferences.getString(LOGIN_NAME+"TrashItem", null);
            String json3 = sharedPreferences.getString(LOGIN_NAME+"TaskItem", null);

            Type type = new TypeToken<ArrayList<Item>>() {}.getType();
            Type type1 = new TypeToken<ArrayList<Task>>() {}.getType();
            items = gson.fromJson(json, type);
            TrashItem = gson2.fromJson(json2, type);
            TaskActivity.tasks = gson3.fromJson(json3, type1);

            if (items == null) {
                items = new ArrayList<>();
            }


            Size_Text = sharedPreferences.getString(LOGIN_NAME+"Size_Text", "");
            Sort_Text=sharedPreferences.getString(LOGIN_NAME+"Sort_Text", "");
            Color_Text=sharedPreferences.getString(LOGIN_NAME+"Color_Text", "");
            View_Text=sharedPreferences.getString(LOGIN_NAME+"View_Text", "");
            Delete_Text=sharedPreferences.getString(LOGIN_NAME+"Delete_Text", "");

            if(Size_Text.equals("")){
                Size_Text = "V???a";
                Sort_Text="Th???i gian t???o g???n nh???t";
                Color_Text="Tr???ng";
                View_Text="Danh s??ch";
                Delete_Text="30 ng??y";

            }
        }


    }
    // H??m saveData v??o file json
    public void saveData() {

        if(LOGIN_NAME.isEmpty()){
            SharedPreferences sharedPreferences = getSharedPreferences("Defaul", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();

            String json = gson.toJson(items);
            String json2 = gson.toJson(TrashItem);
            String json3 = gson.toJson(TaskActivity.tasks);

            editor.putString("Defaul"+"NotesItem", json);
            editor.putString("Defaul"+"TrashItem", json2);

            editor.putString("Defaul"+"TaskItem", json3);
            editor.apply();
        }else {
            SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();

            String json = gson.toJson(items);
            String json2 = gson.toJson(TrashItem);
            String json3 = gson.toJson(TaskActivity.tasks);

            editor.putString(LOGIN_NAME+"NotesItem", json);
            editor.putString(LOGIN_NAME+"TrashItem", json2);

            editor.putString(LOGIN_NAME+"TaskItem", json3);
            editor.apply();
        }



    }
    //<\----------------------------------------51800203--------------------------------------->

}