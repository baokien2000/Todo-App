package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.model.Item;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Setting_Activity extends AppCompatActivity {
    public static ImageView Size_Icon,Color_Icon,View_Icon,Delete_Icon;
    LinearLayout Size_Linear,Color_Linear,View_Linear,Delete_Linear;
    public static TextView Size_Text,Color_Text,View_Text,Delete_Text;
    String LOGIN_NAME;
    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initview();
        onclick();
        loadData();
    }

    private void onclick() {

        Size_Linear.setOnClickListener(view -> {
            PopupMenu Sizemenu = new PopupMenu(Size_Icon.getContext(), view);
            Sizemenu.getMenu().add("Rất nhỏ").setOnMenuItemClickListener(I -> {
                Size_Text.setText("Rất nhỏ");
                return true;
            });
            Sizemenu.getMenu().add("Nhỏ").setOnMenuItemClickListener(I -> {
                Size_Text.setText("Nhỏ");
                return true;
            });

            Sizemenu.getMenu().add("Vừa").setOnMenuItemClickListener(I -> {
                Size_Text.setText("Vừa");
                return true;
            });

            Sizemenu.getMenu().add("Lớn").setOnMenuItemClickListener(I -> {
                Size_Text.setText("Lớn");
                return true;
            });

            Sizemenu.getMenu().add("Rất lớn").setOnMenuItemClickListener(I -> {
                Size_Text.setText("Rất lớn");
                return true;
            });

            Sizemenu.setGravity(Gravity.RIGHT);
            Sizemenu.show();
        });



        Color_Linear.setOnClickListener(view -> {
            PopupMenu Colormenu = new PopupMenu(Size_Icon.getContext(), view);
            Colormenu.getMenu().add("Trắng").setOnMenuItemClickListener(I -> {
                Color_Text.setText("Trắng");

                return true;
            });

            Colormenu.getMenu().add("Vàng").setOnMenuItemClickListener(I -> {
                Color_Text.setText("Vàng");
                return true;
            });

            Colormenu.getMenu().add("Xanh").setOnMenuItemClickListener(I -> {
                Color_Text.setText("Xanh");
                return true;
            });

            Colormenu.getMenu().add("Đỏ").setOnMenuItemClickListener(I -> {
                Color_Text.setText("Đỏ");
                return true;
            });
            Colormenu.getMenu().add("Xanh lá").setOnMenuItemClickListener(I -> {
                Color_Text.setText("Xanh lá");
                return true;
            });

            Colormenu.setGravity(Gravity.RIGHT);
            Colormenu.show();
        });

        View_Linear.setOnClickListener(view -> {
            PopupMenu AppViewmenu = new PopupMenu(Size_Icon.getContext(), view);
            AppViewmenu.getMenu().add("Danh sách").setOnMenuItemClickListener(I -> {
                View_Text.setText("Danh sách");
                View_Icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_list_24));
                MainActivity.Gridlayout.setSpanCount(1);
                MainActivity.adapter.notifyDataSetChanged();
                return true;
            });

            AppViewmenu.getMenu().add("Ô lưới").setOnMenuItemClickListener(I -> {
                View_Text.setText("Ô lưới");
                View_Icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_grid_view_24));
                MainActivity.Gridlayout.setSpanCount(2);
                MainActivity.adapter.notifyDataSetChanged();
                return true;
            });


            AppViewmenu.setGravity(Gravity.RIGHT);
            AppViewmenu.show();
        });
        Delete_Linear.setOnClickListener(view -> {
            PopupMenu Deletemenu = new PopupMenu(Size_Icon.getContext(), view);

            Deletemenu.getMenu().add("60 ngày").setOnMenuItemClickListener(I -> {
                Delete_Text.setText("60 ngày");
                return true;
            });
            Deletemenu.getMenu().add("30 ngày").setOnMenuItemClickListener(I -> {
                Delete_Text.setText("30 ngày");
                return true;
            });

            Deletemenu.getMenu().add("20 ngày").setOnMenuItemClickListener(I -> {
                Delete_Text.setText("20 ngày");
                return true;
            });

            Deletemenu.getMenu().add("10 ngày").setOnMenuItemClickListener(I -> {
                Delete_Text.setText("10 ngày");
                return true;
            });
            Deletemenu.getMenu().add("Không xóa").setOnMenuItemClickListener(I -> {
                Delete_Text.setText("Không xóa");
                return true;
            });



            Deletemenu.setGravity(Gravity.RIGHT);
            Deletemenu.show();
        });
    }

    private void initview() {
        Size_Icon = findViewById(R.id.TextSizeIcon);
        Color_Icon = findViewById(R.id.colorIcon);
        View_Icon = findViewById(R.id.Setting_listIcon);
        Delete_Icon = findViewById(R.id.Setting_DeleteIcon);

        Size_Linear = findViewById(R.id.TextSize_Linear);
        Color_Linear = findViewById(R.id.Color_Linear);
        View_Linear = findViewById(R.id.AppView_Linear);
        Delete_Linear = findViewById(R.id.AppDeleteDay_Linear);

        Size_Text = findViewById(R.id.Text_Text);
        Color_Text = findViewById(R.id.Color_Text);
        View_Text  = findViewById(R.id.AppView_Text);
        Delete_Text = findViewById(R.id.AppDeleteDay_Text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Tạo menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Setting_Back:
                saveData();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        SharedPreferences MainsharedPreferences = getSharedPreferences("shared preferences0", MODE_PRIVATE);
        LOGIN_NAME = MainsharedPreferences.getString("TK_LOGIN","");
        // Tạo SharedPreferences
        if(LOGIN_NAME.isEmpty()){
            SharedPreferences sharedPreferences = getSharedPreferences("Defaul", MODE_PRIVATE);
            String text;
            text = sharedPreferences.getString("Defaul"+"Size_Text","");
            if(!text.equals("")) {
                Size_Text.setText(sharedPreferences.getString("Defaul"+"Size_Text", ""));
                Color_Text.setText(sharedPreferences.getString("Defaul"+"Color_Text", ""));
                View_Text.setText(sharedPreferences.getString("Defaul"+"View_Text", ""));
                Delete_Text.setText(sharedPreferences.getString("Defaul"+"Delete_Text", ""));
                if (View_Text.getText().toString().equals("Ô lưới")) {
                    View_Icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_grid_view_24));
                } else {
                    View_Icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_list_24));
                }
            }

        }else {
            SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_NAME, MODE_PRIVATE);
            String text;
            text = sharedPreferences.getString(LOGIN_NAME+"Size_Text","");
            if(!text.equals("")) {
                Size_Text.setText(sharedPreferences.getString(LOGIN_NAME+"Size_Text", ""));
                Color_Text.setText(sharedPreferences.getString(LOGIN_NAME+"Color_Text", ""));
                View_Text.setText(sharedPreferences.getString(LOGIN_NAME+"View_Text", ""));
                Delete_Text.setText(sharedPreferences.getString(LOGIN_NAME+"Delete_Text", ""));
                if (View_Text.getText().toString().equals("Ô lưới")) {
                    View_Icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_grid_view_24));
                } else {
                    View_Icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_list_24));
                }
            }

        }

    }

    private void saveData() {
        if(LOGIN_NAME.isEmpty()){
            SharedPreferences sharedPreferences = getSharedPreferences("Defaul", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("Defaul"+"Size_Text",Size_Text.getText().toString());
            editor.putString("Defaul"+"Color_Text",Color_Text.getText().toString());
            editor.putString("Defaul"+"View_Text",View_Text.getText().toString());
            editor.putString("Defaul"+"Delete_Text",Delete_Text.getText().toString());

            editor.apply();

        }else {
            SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(LOGIN_NAME+"Size_Text",Size_Text.getText().toString());
            editor.putString(LOGIN_NAME+"Color_Text",Color_Text.getText().toString());
            editor.putString(LOGIN_NAME+"View_Text",View_Text.getText().toString());
            editor.putString(LOGIN_NAME+"Delete_Text",Delete_Text.getText().toString());

            editor.apply();
        }


    }
}