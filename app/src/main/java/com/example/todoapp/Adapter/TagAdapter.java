package com.example.todoapp.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.todoapp.R.layout.tag_layout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.MainActivity;
import com.example.todoapp.R;
import com.example.todoapp.TagManager;
import com.example.todoapp.model.Item;
import com.example.todoapp.model.Tag;
import com.google.gson.Gson;

import java.util.ArrayList;

// Adapter cho Tags
public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    private static ArrayList<Tag> tags;
    @LayoutRes private final int resLayoutId_Tag;
    public TagAdapter(@NonNull ArrayList<Tag> tags){
        this.tags = tags;
        resLayoutId_Tag = tag_layout;
    }
    private Context context;
    @NonNull
    @Override
    public TagAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(resLayoutId_Tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TagAdapter.ViewHolder holder, int position) {
        holder.bind(tags.get(position),position);
    }

    @Override
    public int getItemCount() {
        return this.tags.size();
    }

    public void setTags(@NonNull ArrayList<Tag> tags){
        this.tags = tags;
        notifyDataSetChanged();
    }


    protected class ViewHolder extends RecyclerView.ViewHolder{
        TextView TextViewTitle,Numnotes;
        LinearLayout ItemView3;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            TextViewTitle = itemView.findViewById(R.id.text_view_title_tag);
            ItemView3 = itemView.findViewById(R.id.Linear_Tag);
            Numnotes = itemView.findViewById(R.id.TagNumNote);
        }

        public void bind(Tag tag , int position){
            TextViewTitle.setText(tag.getTitle_Tag());
            Numnotes.setText(String.valueOf(tag.getNumNotes()));

            itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                menu.add("Xóa").setOnMenuItemClickListener(I -> {
                    tags.remove(tag);
                    notifyDataSetChanged();

                    for(Item item : MainActivity.items){
//                        try{
                            String ItemTag = item.getTag();
                            if(ItemTag.replaceFirst("^ ", "").contains(tag.getTitle_Tag().replaceFirst("^ ", "")) == true) {
                                String[] Tags = ItemTag.split(" , ");
                                String Tag = new String();
                                for(String i : Tags){
                                    if(i.equals(tag.getTitle_Tag()) == false){
                                        Tag = Tag +" , "+i;
                                    }
                                }
                                Tag = Tag.replaceFirst("^ , ", "");
                                item.setTag(Tag);
                                MainActivity.adapter.notifyDataSetChanged();
                                if( tags.size() == 0){
                                    TagManager.TagRecyclerView.setVisibility(View.GONE);
                                    TagManager.NOTFOUND.setVisibility(View.GONE);
                                    TagManager.NOTHING.setVisibility(View.VISIBLE);
                                }else {
                                    TagManager.TagRecyclerView.setVisibility(View.VISIBLE);
                                    TagManager.NOTFOUND.setVisibility(View.GONE);
                                    TagManager.NOTHING.setVisibility(View.GONE);
                                }
                            }
//                        }catch (Exception e){}

                    }
                    return false;
                });
                menu.add("Chỉnh sửa").setOnMenuItemClickListener(I -> {



                    Dialog dialog = new Dialog(ItemView3.getContext());
                    // làm mờ background
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    // no Title
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    // setContentView
                    dialog.setContentView(R.layout.add_new_tag_dialog);

                    EditText tastText = dialog.findViewById(R.id.ManagerTag_Id);
                    // focus vào edittext khi dialog mở
                    tastText.requestFocus();
                    tastText.setText(tag.getTitle_Tag());
                    TextView TastDone = dialog.findViewById(R.id.ManagerTagDone);
                    TextView TastCancel = dialog.findViewById(R.id.ManagerTagCancel);
//                     Đổi màu nút "hoàn thành" khi editText chứa nội dung
                    tastText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (charSequence.toString().equals("")){
                                TastDone.setTextColor(ItemView3.getResources().getColor(R.color.hintColor));
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
                            String OldTag = tag.getTitle_Tag();
                            tag.setTitle(TaskValues);
                            notifyDataSetChanged();
                            for(Item item : MainActivity.items){
                                String ItemTag = item.getTag();
                                if(ItemTag.replaceFirst("^ ", "").
                                        contains(OldTag.replaceFirst("^ ", "")) == true) {

                                    String[] Tags = ItemTag.split(" , ");
                                    String Tag = new String();
                                    for(String i : Tags){
                                        if(i.equals(OldTag) == false){
                                            Tag = Tag +" , "+i;
                                        }else{
                                            Tag = Tag +" , "+ tag.getTitle_Tag();
                                        }
                                    }
                                    Tag = Tag.replaceFirst("^ , ", "");
                                    item.setTag(Tag);
                                    MainActivity.adapter.notifyDataSetChanged();
                                    notifyDataSetChanged();
                                }
                            }

                            MainActivity.adapter.notifyDataSetChanged();
                            notifyDataSetChanged();
                            dialog.dismiss();
                            SharedPreferences MainsharedPreferences = ItemView3.getContext()
                                    .getSharedPreferences("shared preferences0", ItemView3.getContext().MODE_PRIVATE);
                            String LOGIN_NAME = MainsharedPreferences.getString("TK_LOGIN","");

                            if (LOGIN_NAME.isEmpty()){
                                SharedPreferences sharedPreferences = ItemView3.getContext().
                                        getSharedPreferences("Defaul",
                                                ItemView3.getContext().MODE_PRIVATE);

                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                Gson gson = new Gson();

                                String json = gson.toJson(MainActivity.items);
                                String json2 = gson.toJson(MainActivity.TrashItem);


                                editor.putString("Defaul"+"NotesItem", json);
                                editor.putString("Defaul"+"TrashItem", json2);

                                editor.apply();
                            }else {
                                SharedPreferences sharedPreferences = ItemView3.getContext().
                                        getSharedPreferences(LOGIN_NAME,
                                                ItemView3.getContext().MODE_PRIVATE);

                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                Gson gson = new Gson();

                                String json = gson.toJson(MainActivity.items);
                                String json2 = gson.toJson(MainActivity.TrashItem);


                                editor.putString(LOGIN_NAME+"NotesItem", json);
                                editor.putString(LOGIN_NAME+"TrashItem", json2);

                                editor.apply();
                            }

                        }
                    });
                    TastCancel.setOnClickListener(view -> {
                        dialog.dismiss();
                    });
                    Window window = dialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();

                    wlp.gravity = Gravity.BOTTOM;
                    wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    window.setAttributes(wlp);
                    dialog.show();

                    return true;
                });

            });
        }


    }


}
