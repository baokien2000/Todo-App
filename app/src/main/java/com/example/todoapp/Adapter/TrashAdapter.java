package com.example.todoapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.CheckBox;
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
import com.example.todoapp.TrashActivity;
import com.example.todoapp.add_event;
import com.example.todoapp.model.Item;

import java.util.ArrayList;

// Adapter cho Notes
public class TrashAdapter extends RecyclerView.Adapter<TrashAdapter.ViewHolder> {
    private static ArrayList<Item> items;
    ArrayList<String> newList;
    @LayoutRes private final int resLayoutId;
    public TrashAdapter(@NonNull ArrayList<Item> items){
        this.items = items;
        resLayoutId = item_layout;
    }

    @NonNull
    @Override
    public TrashAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(resLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrashAdapter.ViewHolder holder, int position) {
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

    public void filterList(ArrayList<Item> filteredList) {
        items = filteredList;
        notifyDataSetChanged();
    }
    protected class ViewHolder extends RecyclerView.ViewHolder{
        TextView TextViewTitle,Sentence,Time;
        LinearLayout ItemView2;
        CheckBox check;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            TextViewTitle = itemView.findViewById(R.id.text_view_title);
            Sentence = itemView.findViewById(R.id.Tv_Sentence);
            Time = itemView.findViewById(R.id.Tv_time);
            check = itemView.findViewById(R.id.checkman);
            ItemView2 = itemView.findViewById(R.id.Linear_Item);
        }

        public void bind(Item item , int position){
            check.setChecked(false);
            TextViewTitle.setText(item.getTitle());
            Sentence.setText(item.getSentence());
            Time.setText(item.getTime());

            // Thông báo không thể đọc notes
            ItemView2.setOnClickListener(view -> Toast.makeText(view.getContext(),
                    "Không thể đọc ghi chú! Vui lòng khôi phục để đọc hoặc chỉnh sửa ghi chú!",
                    Toast.LENGTH_SHORT).show());

            //Sự kiện on click trash items
            itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                menu.add("Xóa vĩnh viễn").setOnMenuItemClickListener(I -> {
                    items.remove(item);
                    notifyDataSetChanged();
                    MainActivity.TrashItem.remove(item);
                    if( items.size() == 0){
                        TrashActivity.recyclerView_trash.setVisibility(View.GONE);
                        TrashActivity.NOTFOUND.setVisibility(View.GONE);
                        TrashActivity.NOTHING.setVisibility(View.VISIBLE);
                    }else {
                        TrashActivity.recyclerView_trash.setVisibility(View.VISIBLE);
                        TrashActivity.NOTFOUND.setVisibility(View.GONE);
                        TrashActivity.NOTHING.setVisibility(View.GONE);
                    }
                    return true;
                });
                menu.add("Khôi phục").setOnMenuItemClickListener(I -> {
                    item.setDeleteDay(null);
                    MainActivity.items.add(item);
                    MainActivity.adapter.notifyDataSetChanged();


                    items.remove(item);
                    notifyDataSetChanged();
                    MainActivity.TrashItem.remove(item);
                    if( items.size() == 0){
                        TrashActivity.recyclerView_trash.setVisibility(View.GONE);
                        TrashActivity.NOTFOUND.setVisibility(View.GONE);
                        TrashActivity.NOTHING.setVisibility(View.VISIBLE);
                    }else {
                        TrashActivity.recyclerView_trash.setVisibility(View.VISIBLE);
                        TrashActivity.NOTFOUND.setVisibility(View.GONE);
                        TrashActivity.NOTHING.setVisibility(View.GONE);
                    }
                    MainActivity.recyclerView.setVisibility(View.VISIBLE);
                    MainActivity.NOTFOUND.setVisibility(View.GONE);
                    MainActivity.NOTHING.setVisibility(View.GONE);


                    return true;
                });
            });

        }


    }

}
