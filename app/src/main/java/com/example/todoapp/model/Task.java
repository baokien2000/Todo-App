package com.example.todoapp.model;

import java.util.Date;

// Task class
public class Task {
    private String Title,Sentence,Time;
    public Task(String Title){
        this.Title = Title;
    }
    public String getTitle_Task(){
        return Title;
    }
    public void setTitle(String Title){
        this.Title = Title;
    }

    boolean Click;
    public boolean getClick(){
        return Click;
    }
    public void setClick(boolean Click){
        this.Click = Click;
    }

    boolean DoneClick;
    public boolean getDoneClick(){
        return DoneClick;
    }
    public void setDoneClick(boolean DoneClick){
        this.DoneClick = DoneClick;
    }


    boolean Pin;
    public boolean getPin(){
        return Pin;
    }
    public void setPin(boolean Pin){ this.Pin = Pin; }

//    String Task_Time= "";
//    public String getTask_Time(){ return Task_Time; }
//    public void setTask_Time(String Task_Time){ this.Task_Time = Task_Time; }

    String TaskTime= "";
    public String getTaskTime(){ return TaskTime; }
    public void setTaskTime(String TaskTime){ this.TaskTime = TaskTime; }

    String TaskDate= "";
    public String getTaskDate(){ return TaskDate; }
    public void setTaskDate(String TaskDate){ this.TaskDate = TaskDate; }

    String TaskLap= "";
    public String getTaskLap(){ return TaskLap; }
    public void setTaskLap(String TaskLap){ this.TaskLap = TaskLap; }

    String Tasks_id= "";
    public String getTasks_id(){ return Tasks_id; }
    public void setTasks_id(String Tasks_id){ this.Tasks_id = Tasks_id; }
}
