package com.example.todoapp.Adapter;

import static com.example.todoapp.R.layout.task_layout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.MainActivity;
import com.example.todoapp.R;
import com.example.todoapp.TaskActivity;
import com.example.todoapp.TaskDoneActivity;
import com.example.todoapp.model.Task;

import java.util.ArrayList;

// Adapter cho Notes
public class TaskDoneAdapter extends RecyclerView.Adapter<TaskDoneAdapter.ViewHolder> {
    private static ArrayList<Task> tasks;
    ArrayList<String> newList;
    @LayoutRes
    private final int resLayoutId;
    public TaskDoneAdapter(@NonNull ArrayList<Task> tasks){
        this.tasks = tasks;
        resLayoutId = task_layout;
    }

    @NonNull
    @Override
    public TaskDoneAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(resLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskDoneAdapter.ViewHolder holder, int position) {
        holder.bind(tasks.get(position),position);
    }

    @Override
    public int getItemCount() {
        return this.tasks.size();
    }

    public void setTask(@NonNull ArrayList<Task> tasks){
        this.tasks = tasks;
        notifyDataSetChanged();
    }


    protected class ViewHolder extends RecyclerView.ViewHolder{
        TextView TextViewTitle;
        LinearLayout ItemView2;
        CheckBox CancelCheck,check;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            ItemView2 = itemView.findViewById(R.id.Linear_Task);
            TextViewTitle = itemView.findViewById(R.id.text_view_title_task);
            CancelCheck = itemView.findViewById(R.id.Task_CheckBoxID);
            check = itemView.findViewById(R.id.checkdelete);
        }

        public void bind(Task task , int position) {
            check.setChecked(false);
            CancelCheck.setChecked(true);
            task.setDoneClick(true);
            TextViewTitle.setText(task.getTitle_Task());

            ItemView2.setOnClickListener(view -> {
                Toast.makeText(ItemView2.getContext(), "Bạn không thể chỉnh sửa các nhiệm vụ đã hoàn thành", Toast.LENGTH_SHORT).show();
            });


            ItemView2.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                menu.add("Xóa vĩnh viễn").setOnMenuItemClickListener(I -> {
                    TaskActivity.tasksDone.remove(task);
                    tasks.remove(task);
                    notifyDataSetChanged();

                    return true;

                });
            });
            CancelCheck.setOnClickListener(view -> {
                task.setPin(false);
                task.setClick(false);
                task.setDoneClick(false);
                TaskActivity.tasks.add(task);

                TaskActivity.tasksDone.remove(task);
                TaskActivity.adapter_Task.notifyDataSetChanged();
                tasks.remove(task);
                notifyDataSetChanged();

                if( tasks.size() == 0){
                    TaskDoneActivity.recyclerView_TaskDone.setVisibility(View.GONE);
                    TaskDoneActivity.NOTFOUND.setVisibility(View.GONE);
                    TaskDoneActivity.NOTHING.setVisibility(View.VISIBLE);
                }else {
                    TaskDoneActivity.recyclerView_TaskDone.setVisibility(View.VISIBLE);
                    TaskDoneActivity.NOTFOUND.setVisibility(View.GONE);
                    TaskDoneActivity.NOTHING.setVisibility(View.GONE);
                }
                TaskActivity.recyclerView_Task.setVisibility(View.VISIBLE);
                TaskActivity.NOTFOUND.setVisibility(View.GONE);
                TaskActivity.NOTHING.setVisibility(View.GONE);
            });
        }
    }

}
