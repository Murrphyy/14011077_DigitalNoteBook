package com.example.melih.a14011077_digitalnotebook;

import android.net.Uri;

import java.io.Serializable;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class Note implements Serializable,Comparable<Note> {
    private String ID;
    private String address,title,details,priority;
    private Date date;
    private Calendar notificationDate;
    private int hour,minute;
    private NoteColor noteColor;
    private String coverImageUri;
    private PageStateHolder pageStateHolder;

    public Note(String title, NoteColor color, Date date){
        this.title=title;
        this.noteColor=color;
        this.hour=-1;
        this.minute=-1;
        this.date=date;
    }

    public Note(){
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public Date getDate() {
        return date;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute(){ return minute;}

    public NoteColor getNoteColor() {
        return noteColor;
    }

    public void setHour(int hour) {

        this.hour = hour;
    }

    public void setMinute(int minute){
        this.minute=minute;
    }

    public void setNoteColor(NoteColor noteColor) {
        this.noteColor = noteColor;
    }

    public void setAddress(String address){
        this.address=address;

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPriority() {

        return priority;
    }

    public void setCoverImageUri(String coverImageUri) {
        this.coverImageUri = coverImageUri;
    }

    public String getCoverImageUri() {

        return coverImageUri;
    }

    public PageStateHolder getPageStateHolder() {
        return pageStateHolder;
    }

    public void setPageStateHolder(PageStateHolder pageStateHolder) {
        this.pageStateHolder = pageStateHolder;
    }

    public Calendar getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(Calendar notificationDate) {
        this.notificationDate = notificationDate;
    }

    @Override
    public int compareTo(Note o) {
        return this.getDate().compareTo(o.getDate());
    }
}
