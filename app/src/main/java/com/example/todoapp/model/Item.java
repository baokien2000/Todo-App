package com.example.todoapp.model;

import android.net.Uri;

import java.util.Date;

// Notes class
public class Item {
    private String Title,Sentence,Time;
    public Item(String Title, String Sentence,String Time){
        this.Title = Title;
        this.Sentence = Sentence;
        this.Time = Time;
    }
    public String getTitle(){
        return Title;
    }
    public void setTitle(String Title){
        this.Title = Title;
    }

    public String getSentence(){
        return Sentence;
    }
    public void setSentence(String Sentence){
        this.Sentence = Sentence;
    }

    public String getTime(){
        return Time;
    }
    public void setTime(String Time){
        this.Time = Time;
    }

    boolean Click;
    public boolean getClick(){
        return Click;
    }
    public void setClick(boolean Click){
        this.Click = Click;
    }

    Date DeleteDay;
    public Date getDeleteDay(){
        return DeleteDay;
    }
    public void setDeleteDay(Date DeleteDay){
        this.DeleteDay = DeleteDay;
    }

    boolean Pin;
    public boolean getPin(){
        return Pin;
    }
    public void setPin(boolean Pin){ this.Pin = Pin; }

    boolean Share;
    public boolean getShare(){
        return Share;
    }
    public void setShare(boolean Share){ this.Share = Share; }

    String Tag= "";
    public String getTag(){ return Tag; }
    public void setTag(String Tag){ this.Tag = Tag; }

    String Pass= "";
    public String getPass(){ return Pass; }
    public void setPass(String Pass){ this.Pass = Pass; }

    String Note_Time= "";
    public String getNote_Time(){ return Note_Time; }
    public void setNote_Time(String Note_Time){ this.Note_Time = Note_Time; }

    String Note_Date= "";
    public String getNote_Date(){ return Note_Date; }
    public void setNote_Date(String Note_Date){ this.Note_Date = Note_Date; }

    String Note_Lap= "";
    public String getNote_Lap(){ return Note_Lap; }
    public void setNote_Lap(String Note_Lap){ this.Note_Lap = Note_Lap; }

    String Notes_id= "";
    public String getNotes_id(){ return Notes_id; }
    public void setNotes_id(String Notes_id){ this.Notes_id = Notes_id; }

    String pictureUri="";
    public String getpictureUri(){ return pictureUri; }
    public void setpictureUri(String pictureUri){ this.pictureUri = pictureUri; }

    String VideoUri="";
    public String getVideoUri(){ return VideoUri; }
    public void setVideoUri(String VideoUri){ this.VideoUri = VideoUri; }

    String AudioUri="";
    public String getAudioUri(){ return AudioUri; }
    public void setAudioUri(String AudioUri){ this.AudioUri = AudioUri; }
}
