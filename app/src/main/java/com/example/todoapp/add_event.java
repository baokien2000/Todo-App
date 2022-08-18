package com.example.todoapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import com.example.todoapp.model.Item;
import com.example.todoapp.model.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;


public class add_event extends AppCompatActivity {
    EditText Title,Sentence;
    TextView Time,AudioNotes;
    Context context =(Context) this;
    ImageView Note_image,pause,play,stop;
    Uri selectedImageUri,selectedVideoUri,selectedVoiceUri;
    Bitmap bitmap;
    File mypath;
    VideoView Note_video;
    String filename,videoName,Voicename;
    MediaController mc;
    MediaPlayer voice_mc;
    LinearLayout AudioLayout,AddLinear;
    String path2;
    String Size_Text,Color_Text,View_Text,Sort_Text,Delete_Text;
    private Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        initView();
        loadData();


        switch (Color_Text) {
            case "Trắng":
                break;
            case "Vàng":
                AddLinear.setBackgroundColor(Color.parseColor("#f7f8ea"));
                break;
            case "Xanh": // chuyển sang activity Task
                AddLinear.setBackgroundColor(Color.parseColor("#ebf6fc"));
                break;
            case "Đỏ":
                AddLinear.setBackgroundColor(Color.parseColor("#fff6ef"));
                break;
            case "Xanh lá":
                AddLinear.setBackgroundColor(Color.parseColor("#f1fdf1"));
                break;
        }

        play.setOnClickListener(view -> {
            if (voice_mc == null) {
                voice_mc = new MediaPlayer();
                try {
                    voice_mc.setDataSource(path2);
                    voice_mc.prepare();
                    voice_mc.start();
                } catch (IOException e) { }
                try {
                    voice_mc = MediaPlayer.create(add_event.this, selectedVoiceUri);
                    voice_mc.start();
                }catch (Exception e2){ }
            } else {
                voice_mc.start();
            }


        });
        pause.setOnClickListener(view -> {
            voice_mc.pause();
        });
        stop.setOnClickListener(view -> {
            voice_mc.stop();
            voice_mc = null;
        });

        mc = new MediaController(Note_video.getContext());
        Note_video.setMediaController(mc);

        // if EXTRA_EDITMESSAGE != null - dong lenh chay khi nhan edit item
        // điền các ô nhập dữ liệu bằng dữ liệu của item click
        try {
            Intent i = getIntent();
            ArrayList<String> List2 = i.getStringArrayListExtra("Note_Item_Value");

            if (List2.get(0) != null) {
//                if(Size_Text.equals("")){
//                    Title.setText(String.valueOf(List2.get(0)));
//                    Sentence.setText(String.valueOf(List2.get(1)));
//                }else {
//
//                }
                Title.setText(String.valueOf(List2.get(0)));
                Sentence.setText(String.valueOf(List2.get(1)));

            }

            if (!List2.get(2).isEmpty()) {
                loadImageFromStorage(List2.get(2));
                filename = List2.get(2);
            }

        } catch (Exception ignored) {
        }

        try {
            Intent i = getIntent();
            ArrayList<String> List2 = i.getStringArrayListExtra("Note_Item_Value");
            if (!List2.get(3).isEmpty()) {
                videoName = List2.get(3);

                loadVideoFromStorage(videoName);

            }
        } catch (Exception ignored) {
        }

        try {
            Intent i = getIntent();
            ArrayList<String> List2 = i.getStringArrayListExtra("Note_Item_Value");
            if (!List2.get(4).equals("")) {
                Voicename = List2.get(4);
                loadAudioFromStorage(Voicename);

            }
        } catch (Exception ignored) {
        }
        Note_image.setOnClickListener(view -> {
            PopupMenu ImagePopup = new PopupMenu(this, view);
            ImagePopup.getMenu().add("Xóa").setOnMenuItemClickListener(I -> {
                Note_image.setImageBitmap(null);
                Note_image.setVisibility(View.GONE);
                filename = "";
                return true;
            });
            ImagePopup.show();
        });
        Note_video.setOnLongClickListener(view -> {
            PopupMenu ImagePopup2 = new PopupMenu(this, view);
            ImagePopup2.getMenu().add("Xóa").setOnMenuItemClickListener(I -> {
                Note_video.setVideoURI(null);
                Note_video.setVisibility(View.GONE);
                videoName = "";
                return true;
            });
            ImagePopup2.show();
            return true;
        });
        AudioLayout.setOnLongClickListener(view -> {
            PopupMenu ImagePopup2 = new PopupMenu(this, view);
            ImagePopup2.getMenu().add("Xóa").setOnMenuItemClickListener(I -> {
                AudioLayout.setVisibility(View.GONE);
                Voicename = "";
                return true;
            });
            ImagePopup2.show();
            return true;
        });

        switch (Size_Text) {
            case "Rất nhỏ":
                Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                Sentence.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                break;
            case "Nhỏ":
                Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                Sentence.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                break;
            case "Vừa": // chuyển sang activity Task
                break;
            case "Lớn":
                Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                Sentence.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
                break;
            case "Rất lớn":
                Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);
                Sentence.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                break;
        }

        Title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateMenuTitles();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        Sentence.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateMenuTitles();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void initView() {
        Title = findViewById(R.id.Et_title);
        Sentence = findViewById(R.id.Et_Sentence);
        Time = findViewById(R.id.Tv_time);
        Note_image = findViewById(R.id.Image_View);
        Note_video = findViewById(R.id.Notes_video);
        play = findViewById(R.id.Note_Voice_play);
        pause = findViewById(R.id.Note_Voice_pause);
        stop = findViewById(R.id.Note_Voice_stop);
        AudioLayout = findViewById(R.id.Audio_linear);
        AudioNotes = findViewById(R.id.voiceId);
        AddLinear = findViewById(R.id.AddLinearID);
    }

    //     Tạo Option menu
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_notes_menu, menu);
        if(menu instanceof MenuBuilder){ // hiện icon ở menu thả
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        this.menu = menu;
        updateMenuTitles();
        return true;
    }
    private void updateMenuTitles() {
        MenuItem SAVE_BTN = menu.findItem(R.id.Notes_add_save_btn);
        if ( Title.getText().toString().isEmpty() && Sentence.getText().toString().isEmpty()){
            SAVE_BTN.setVisible(false);
        }else {
            SAVE_BTN.setVisible(true);
        }

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            //sự kiện click Item save
            case R.id.Notes_add_save_btn:
                int hour,minute,Year,Month,Day;
                String minute0;
                final Calendar calendar = Calendar.getInstance();
                // lấy time và day hiện tại
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);

                // có trường hợp đưa int về string dẫn đến mất số 0
                // vd 10:04 mà lại hiện là 10:4
                if (minute >= 10) { //  check nếu nhỏ hơn 10 thì thêm số 0 vào
                    minute0 = String.valueOf(minute);
                } else {
                    minute0 = "0" + String.valueOf(minute);
                }

                Year = calendar.get(Calendar.YEAR);// năm hiện tại
                Month = calendar.get(Calendar.MONTH);// tháng hiện tại
                Day = calendar.get(Calendar.DAY_OF_MONTH);// ngày hiện tại

                String Times =  String.valueOf(Year)+ "/"+ String.valueOf(Month+1) +"/"+ String.valueOf(Day) +"  "+ String.valueOf(hour) + ":" +String.valueOf(minute0) ;

                Intent i2 = new Intent(); // trả dữ liệu về sau khi người dùng tạo hoặc sửa notes
                ArrayList<String> ListEvent = new ArrayList<>();
                ListEvent.add(Title.getText().toString());
                ListEvent.add(Sentence.getText().toString());
                ListEvent.add(Times);
                ListEvent.add(filename);
                ListEvent.add(videoName);
                ListEvent.add(Voicename);
                i2.putStringArrayListExtra(MainActivity.EXTRA_MESSAGE,ListEvent);
                setResult(Activity.RESULT_OK,i2);

                finish();
                break;
            case R.id.Notes_add_back_btn:
                if(Title.getText().toString().isEmpty() && Sentence.getText().toString().isEmpty()){
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }else{

                    AlertDialog RemoveAll = new AlertDialog.Builder(this)
                            .setTitle("Bạn có muốn lưu các thay đổi?!")
                            .setPositiveButton("Lưu", (dialog, arg1) -> {
                                int hour2,minute2,Year2,Month2,Day2;
                                String minute3;
                                final Calendar calendar2 = Calendar.getInstance();
                                // lấy time và day hiện tại
                                hour2 = calendar2.get(Calendar.HOUR_OF_DAY);
                                minute2 = calendar2.get(Calendar.MINUTE);

                                // có trường hợp đưa int về string dẫn đến mất số 0
                                // vd 10:04 mà lại hiện là 10:4
                                if (minute2 >= 10) { //  check nếu nhỏ hơn 10 thì thêm số 0 vào
                                    minute3 = String.valueOf(minute2);
                                } else {
                                    minute3 = "0" + String.valueOf(minute2);
                                }

                                Year2 = calendar2.get(Calendar.YEAR);// năm hiện tại
                                Month2 = calendar2.get(Calendar.MONTH);// tháng hiện tại
                                Day2 = calendar2.get(Calendar.DAY_OF_MONTH);// ngày hiện tại

                                String Times2 =  String.valueOf(Year2)+ "/"+ String.valueOf(Month2+1) +"/"+ String.valueOf(Day2) +"  "+ String.valueOf(hour2) + ":" +String.valueOf(minute3) ;

                                Intent i3 = new Intent(); // trả dữ liệu về sau khi người dùng tạo hoặc sửa notes
                                ArrayList<String> ListEvent2 = new ArrayList<>();
                                ListEvent2.add(Title.getText().toString());
                                ListEvent2.add(Sentence.getText().toString());
                                ListEvent2.add(Times2);
                                ListEvent2.add(filename);
                                ListEvent2.add(videoName);
                                ListEvent2.add(Voicename);
                                i3.putStringArrayListExtra(MainActivity.EXTRA_MESSAGE,ListEvent2);
                                setResult(Activity.RESULT_OK,i3);

                                finish();
                            })

                            .setNegativeButton("Hủy", (dialog, arg1) -> {
                                dialog.cancel();         // hủy dialog
                                setResult(Activity.RESULT_CANCELED);
                                finish();
                            })

                            .show();
                }


                break;
            case R.id.Notes_add_Image_btn:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 60);
                break;
            case R.id.Notes_add_Video_btn:
                Intent intent2 = new Intent();
                intent2.setType("video/*");
                intent2.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent2, "Select Video"), 70);
                break;
            case R.id.Notes_add_Voice_btn:
                Intent intent3 = new Intent();
                intent3.setType("audio/*");
                intent3.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent3, "Select Video"), 80);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 60  && resultCode == Activity.RESULT_OK) { // Ảnh

            selectedImageUri = data.getData();
            String path = selectedImageUri.getPath();
            filename =path.substring(path.lastIndexOf("/")+1).replace(":","");
            if (null != selectedImageUri) {

                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);

                    Note_image.setImageBitmap(bitmap);

                    Note_image.setVisibility(View.VISIBLE);
                    saveToInternalStorage(bitmap,filename);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

        }
        if (requestCode == 70  && resultCode == Activity.RESULT_OK) { // Video
            selectedVideoUri = data.getData();
            String path = selectedVideoUri.getPath();
            videoName =path.substring(path.lastIndexOf("/")+1).replace(":","");
            if (null != selectedVideoUri) {

                Note_video.setVisibility(View.VISIBLE);
                Note_video.setVideoURI(selectedVideoUri);
                saveVideoToInternalStorage(selectedVideoUri);
            }
        }
        if (requestCode == 80 && resultCode == Activity.RESULT_OK) { // Audio
            selectedVoiceUri = data.getData();
            path2 = selectedVoiceUri.getPath();
            Voicename =path2.substring(path2.lastIndexOf("/")+1).replace(":","");
            if (null != selectedVoiceUri) {
                AudioLayout.setVisibility(View.VISIBLE);
                voice_mc = MediaPlayer.create(add_event.this, selectedVoiceUri);
                saveAudioToInternalStorage(selectedVoiceUri);
            }
        }

    }
    private void saveAudioToInternalStorage(Uri data){
        try {

            ContextWrapper cw = new ContextWrapper(getApplicationContext());

            File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);

            ///
            File newfile;

            AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data, "r");
            FileInputStream in = videoAsset.createInputStream();
//
//            File filepath = Environment.getExternalStorageDirectory();
//            File dir = new File(filepath.getAbsolutePath() + "/" +"imageDir" + "/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            newfile = new File(dir, "save_"+Voicename+".mp3");

            if (newfile.exists()) newfile.delete();



            OutputStream out = new FileOutputStream(newfile);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadAudioFromStorage(String Voicename)
    {
        AudioLayout.setVisibility(View.VISIBLE);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File f=new File(directory,"save_"+Voicename+".mp3");

        path2 = f.getAbsolutePath();
        voice_mc = new  MediaPlayer();
        try {
            voice_mc.setDataSource(path2);
            voice_mc.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }



//        voice_mc = MediaPlayer.create(this, Uri.parse(Environment.getExternalStorageDirectory().getPath()+ "/Download/"+"save_"+videoName+".mp3"));
//        voice_mc = MediaPlayer.create(add_event.this, f.getPath());

    }


    private void saveVideoToInternalStorage(Uri data){
        try {

            ContextWrapper cw = new ContextWrapper(getApplicationContext());

            File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);

            ///
            File newfile;

            AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data, "r");
            FileInputStream in = videoAsset.createInputStream();
//
//            File filepath = Environment.getExternalStorageDirectory();
//            File dir = new File(filepath.getAbsolutePath() + "/" +"imageDir" + "/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            newfile = new File(dir, "save_"+videoName+".mp4");

            if (newfile.exists()) newfile.delete();



            OutputStream out = new FileOutputStream(newfile);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadVideoFromStorage(String videoName)
    {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File f=new File(directory,"save_"+videoName+".mp4");

        String path = f.getAbsolutePath();

        Note_video.setVideoPath(path);

        Note_video.setVisibility(View.VISIBLE);
    }

    private String saveToInternalStorage(Bitmap bitmapImage,String bitmapname){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        mypath=new File(directory,bitmapname+"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
    private void loadImageFromStorage(String bitmapname)
    {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File f=new File(directory,bitmapname+"profile.jpg");

        Bitmap bmImg = BitmapFactory.decodeFile(f.getAbsolutePath());

        Note_image.setImageBitmap(bmImg);

        Note_image.setVisibility(View.VISIBLE);
    }

    private void loadData() {
        // Tạo SharedPreferences
        if(MainActivity.LOGIN_NAME.isEmpty()){
            SharedPreferences sharedPreferences = getSharedPreferences("Defaul", MODE_PRIVATE);
//        // Tạo 2 biến gson cho items và ListAfterDelete
            Size_Text = sharedPreferences.getString("Defaul"+"Size_Text", "");
            Sort_Text=sharedPreferences.getString("Defaul"+"Sort_Text", "");
            Color_Text=sharedPreferences.getString("Defaul"+"Color_Text", "");
            View_Text=sharedPreferences.getString("Defaul"+"View_Text", "");
            Delete_Text=sharedPreferences.getString("Defaul"+"Delete_Text", "");
        }else {
            SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.LOGIN_NAME, MODE_PRIVATE);
//        // Tạo 2 biến gson cho items và ListAfterDelete
            Size_Text = sharedPreferences.getString(MainActivity.LOGIN_NAME+"Size_Text", "");
            Sort_Text=sharedPreferences.getString(MainActivity.LOGIN_NAME+"Sort_Text", "");
            Color_Text=sharedPreferences.getString(MainActivity.LOGIN_NAME+"Color_Text", "");
            View_Text=sharedPreferences.getString(MainActivity.LOGIN_NAME+"View_Text", "");
            Delete_Text=sharedPreferences.getString(MainActivity.LOGIN_NAME+"Delete_Text", "");
        }


    }
}