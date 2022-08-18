package com.example.todoapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.todoapp.R.layout.item_layout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.todoapp.MainActivity;
import com.example.todoapp.R;
import com.example.todoapp.Setting_Activity;
import com.example.todoapp.add_event;
import com.example.todoapp.model.Item;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// Adapter cho Notes
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private static ArrayList<Item> items;
    ArrayList<String> newList;

    @LayoutRes private final int resLayoutId;
    public ItemAdapter(@NonNull ArrayList<Item> items){
        this.items = items;
        resLayoutId = item_layout;
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(resLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        holder.bind(items.get(position),position);

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void setItems(@NonNull ArrayList<Item> items){
        this.items = items;
        notifyDataSetChanged();
    }


    protected class ViewHolder extends RecyclerView.ViewHolder{
        TextView TextViewTitle,Sentence,Time,TagValues;
        LinearLayout ItemView2,ItemTag;
        CheckBox check;
        ImageView Pin,Lock,Timer;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            TextViewTitle = itemView.findViewById(R.id.text_view_title); //Items title
            Sentence = itemView.findViewById(R.id.Tv_Sentence); //Items nội dung
            Time = itemView.findViewById(R.id.Tv_time); //Items time
            check = itemView.findViewById(R.id.checkman); // ô check của mỗi items
            ItemView2 = itemView.findViewById(R.id.Linear_Item); // Items
            Pin = itemView.findViewById(R.id.NotesPinIcon); // icon thể hiện items nào đang đươc ghim
            ItemTag = itemView.findViewById(R.id.ItemsTag); // Linear Chứa tag
            TagValues = itemView.findViewById(R.id.ItemTagValues);
            Lock = itemView.findViewById(R.id.NotesPassIcon);
            Timer = itemView.findViewById(R.id.Notes_Notifications_icon);
        }

        public void bind(Item item , int position){
            if((MainActivity.EditTitle != null)&& // check nếu edit không rỗng thì set lại item đó
                    (item.getTitle().equals(newList.get(0)))&&
                    (item.getSentence().equals(newList.get(1)))){
                item.setTitle(MainActivity.EditTitle);
                item.setSentence(MainActivity.EditSentence);
                item.setTime(MainActivity.EditTime);
                item.setpictureUri(MainActivity.EditUri);
                item.setVideoUri(MainActivity.EditVideoUri);
                item.setAudioUri(MainActivity.EditAudioUri);
                MainActivity.EditTitle = null;
                MainActivity.EditSentence = null;
                MainActivity.EditTime = null;
                MainActivity.EditUri = null;
                MainActivity.EditVideoUri=null;
                MainActivity.EditAudioUri=null;
            }

            if(item.getNote_Time() == null ){
                item.setNote_Time("");
            }
            if(item.getNote_Date() == null ){
                item.setNote_Date("");
            }
            if(item.getNote_Lap() == null ){
                item.setNote_Lap("");
            }
            Date today = new Date();
//                Date date = ;
            String Time3[] = item.getNote_Time().split(":");
            String Date3[] = item.getNote_Date().split("/");
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
                    item.setNote_Date("");
                    item.setNote_Time("");
                    item.setNote_Lap("");
                }
            }




            if(!item.getNote_Time().isEmpty()){
                Timer.setVisibility(View.VISIBLE);
            }else {
                Timer.setVisibility(View.GONE);
            }

            if(!item.getPass().isEmpty()){
                Sentence.setVisibility(View.GONE);
                Lock.setVisibility(View.VISIBLE);
            }else{
                Sentence.setVisibility(View.VISIBLE);
                Lock.setVisibility(View.GONE);
            }

            item.setClick(false);

            if (item.getClick() == true){
                check.setChecked(true);
            }else {
                check.setChecked(false);
            }
            check.setOnClickListener(view -> {
                if (check.isChecked() == true ){
                    item.setClick(true);
                }else {
                    item.setClick(false);
                }
                //Ẩn bottom menu khi không có item nào được click
                boolean Hide = true;
                for(Item item3 : items){
                    if (item3.getClick() == true){
                        Hide = false;
                    }
                }
                if (Hide == true){ // nếu ko có item được click thì
                    MainActivity.BottomLayout.setVisibility(View.GONE);// ẩn BottomLayout
                    MainActivity.AddNotes.setVisibility(View.VISIBLE); // hiện hide notes add button
                }

                //Show/hide Pin/Unpin
                int Num=0,unPinItem = 0;
                for(Item item3 : items){
                    if (item3.getClick()){
                        Num+=1;
                        if (item3.getPin() == false){
                            unPinItem += 1;
                        }
                    }
                }
                // Nếu có 1 trong các item được clik chưa được pin thì button Pin sẽ hiện
                // Button UnPin chỉ hiện trong trường hợp các Item được click đã được pin
                if(unPinItem != 0){
                    MainActivity.Pin.setVisibility(View.VISIBLE);
                    MainActivity.UnPin.setVisibility(View.GONE);
                }else {
                    MainActivity.Pin.setVisibility(View.GONE);
                    MainActivity.UnPin.setVisibility(View.VISIBLE);
                }

                //Vì mỗi lần chỉ share 1 notes qua ứng dụng khác
                // nên Ẩn button share Notes khi có trên 2 items clicks
                if(Num >1){
                    MainActivity.ShareNote.setVisibility(View.GONE);
                    MainActivity.TagNotes.setVisibility(View.GONE);
                    MainActivity.UnTagNotes.setVisibility(View.GONE);
                    MainActivity.SetPass.setVisibility(View.GONE);
                    MainActivity.NotesReminder.setVisibility(View.GONE);
                }else {
                    MainActivity.ShareNote.setVisibility(View.VISIBLE);
                    MainActivity.TagNotes.setVisibility(View.VISIBLE);
                    MainActivity.SetPass.setVisibility(View.VISIBLE);
                    MainActivity.NotesReminder.setVisibility(View.VISIBLE);


                    if(item.getTag().isEmpty()){
                        MainActivity.UnTagNotes.setVisibility(View.GONE);
                    }else{
                        MainActivity.UnTagNotes.setVisibility(View.VISIBLE);
                    }
                }


            });


            if (item.getPin() == true){ // nếu item được ghim thì hiện icon
                Pin.setVisibility(View.VISIBLE);
            }else {
                Pin.setVisibility(View.GONE);
            }

            //set data
            TextViewTitle.setText(item.getTitle());
            Sentence.setText(item.getSentence());
            Time.setText(item.getTime());
            TagValues.setText(item.getTag());
            if(TagValues.getText().toString().isEmpty() == false){
                ItemTag.setVisibility(View.VISIBLE);
            }else {
                ItemTag.setVisibility(View.GONE);
            }


            // sự kiện long lick note items
            ItemView2.setOnLongClickListener(view -> {

                MainActivity.BottomLayout.setVisibility(View.VISIBLE);// hiện BottomLayout
                MainActivity.AddNotes.setVisibility(View.GONE);
                if (item.getClick() == true){
                    item.setClick(false); // setClick items
                    check.setChecked(false);// ẩn checkbox items
                }else{
                    item.setClick(true); // setClick items
                    check.setChecked(true);// hiện checkbox items
                }

                boolean Hide2 = true;
                for(Item item3 : items){
                    if (item3.getClick() == true){
                        Hide2 = false;
                    }
                }
                if(Hide2 == true) {
                    MainActivity.BottomLayout.setVisibility(View.GONE);// ẩn BottomLayout
                    MainActivity.AddNotes.setVisibility(View.VISIBLE);// hiện AddNotes
                }

                //Show/hide Pin/Unpin
                int Num=0,unPinItem = 0;
                for(Item item1 : items){
                    if (item1.getClick()){
                        Num+=1;
                        if (item1.getPin() == false){
                            unPinItem += 1;
                        }
                    }
                }
                // Nếu có 1 trong các item được clik chưa được pin thì button Pin sẽ hiện
                // Button UnPin chỉ hiện trong trường hợp các Item được click đã được pin
                if(unPinItem != 0){
                    MainActivity.Pin.setVisibility(View.VISIBLE);
                    MainActivity.UnPin.setVisibility(View.GONE);
                }else {
                    MainActivity.Pin.setVisibility(View.GONE);
                    MainActivity.UnPin.setVisibility(View.VISIBLE);
                }

                //Vì mỗi lần chỉ share 1 notes qua ứng dụng khác
                // nên Ẩn button share Notes khi có trên 2 items clicks
                if(Num >1){
                    MainActivity.ShareNote.setVisibility(View.GONE);
                    MainActivity.TagNotes.setVisibility(View.GONE);
                    MainActivity.UnTagNotes.setVisibility(View.GONE);
                    MainActivity.SetPass.setVisibility(View.GONE);
                    MainActivity.NotesReminder.setVisibility(View.GONE);
                }else {
                    MainActivity.ShareNote.setVisibility(View.VISIBLE);
                    MainActivity.TagNotes.setVisibility(View.VISIBLE);
                    MainActivity.SetPass.setVisibility(View.VISIBLE);
                    MainActivity.NotesReminder.setVisibility(View.VISIBLE);
                    if(item.getTag().isEmpty()){
                        MainActivity.UnTagNotes.setVisibility(View.GONE);
                    }else{
                        MainActivity.UnTagNotes.setVisibility(View.VISIBLE);
                    }
                }

//                    if(TagValues.getText().toString().isEmpty() ){
//                        MainActivity.TagNotes.setVisibility(View.VISIBLE);
//                        MainActivity.UnTagNotes.setVisibility(View.GONE);
//                    }else {
//                        MainActivity.TagNotes.setVisibility(View.GONE);
//                        MainActivity.UnTagNotes.setVisibility(View.VISIBLE);
//                    }
                return true;
            });
            // chuyển đến trang add để xem hoặc sửa note
            ItemView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!item.getPass().isEmpty()){
                        Dialog dialog = new Dialog(ItemView2.getContext());
                        // làm mờ background
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        // no Title
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        // setContentView
                        dialog.setContentView(R.layout.login_notes_pass_dialog);

                        EditText pass = dialog.findViewById(R.id.LoginItemPass_Id);

                        pass.setTransformationMethod(new PasswordTransformationMethod());
                        // focus vào edittext khi dialog mở

                        TextView PassDone = dialog.findViewById(R.id.LoginItemPassDone);
                        TextView PassCancel = dialog.findViewById(R.id.LoginItemPassCancel);
                        TextView PassShow = dialog.findViewById(R.id.LoginItemPassShow);
                        // focus vào edittext khi dialog mở

                        // Chỉ cho phép tạo nhiệm vụ khi Task có nội dung
                        PassDone.setOnClickListener(view1 -> {
                            if(item.getPass().equals(pass.getText().toString())){
                                dialog.dismiss();
                                Intent intent = new Intent(view.getContext(), add_event.class);
                                newList = new ArrayList();
                                newList.add(TextViewTitle.getText().toString());
                                newList.add(Sentence.getText().toString());
                                newList.add(item.getpictureUri());
                                newList.add(item.getVideoUri());
                                newList.add(item.getAudioUri());
                                intent.putStringArrayListExtra("Note_Item_Value",newList);
                                ((Activity) view.getContext()).startActivityForResult(intent,40);
                            }else{
                                Toast.makeText(ItemView2.getContext(), "Sai mật khẩu" , Toast.LENGTH_SHORT).show();
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
                    }else{
                        Intent intent = new Intent(view.getContext(), add_event.class);
                        newList = new ArrayList();
                        newList.add(TextViewTitle.getText().toString());
                        newList.add(Sentence.getText().toString());
                        newList.add(item.getpictureUri());
                        newList.add(item.getVideoUri());
                        newList.add(item.getAudioUri());
                        intent.putStringArrayListExtra("Note_Item_Value",newList);
                        ((Activity) view.getContext()).startActivityForResult(intent,40);
                    }
                }
            });

        }


    }
}
