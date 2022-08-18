package com.example.todoapp.model;

// Task class
public class Tag {
    private String Title;
    private int NumNotes;
    public Tag(String Title,int NumNotes){
        this.Title = Title;
        this.NumNotes = NumNotes;
    }
    public String getTitle_Tag(){
        return Title;
    }
    public void setTitle(String Title){
        this.Title = Title;
    }

    public int getNumNotes(){
        return NumNotes;
    }
    public void setNumNotes(int NumNotes){ this.NumNotes = NumNotes; }


}
