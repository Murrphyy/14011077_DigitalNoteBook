package com.example.melih.a14011077_digitalnotebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class SharedPreferencesHandler {
    SharedPreferences pref;
    Context context;
    public SharedPreferencesHandler(Context context){
        pref=context.getSharedPreferences("Notes",Context.MODE_PRIVATE);
    }

    public void storeNote(Note note){
        Set<String> notesID=getAllID();
        if(notesID==null){
            notesID=new LinkedHashSet<>();
        }
        String newKey;
        do{
            newKey=getRandomKey();
        }while( notesID.contains(newKey));

        notesID.add(newKey);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString("note_title"+newKey,note.getTitle());
        editor.putString("note_priority"+newKey,note.getPriority());
        editor.putInt("note_color_a"+newKey,note.getNoteColor().getA());
        editor.putInt("note_color_r"+newKey,note.getNoteColor().getR());
        editor.putInt("note_color_g"+newKey,note.getNoteColor().getG());
        editor.putInt("note_color_b"+newKey,note.getNoteColor().getB());
        if(note.getAddress()!=null)
            editor.putString("note_address"+newKey,note.getAddress());
        if(note.getDetails()!=null)
            editor.putString("note_details"+newKey,note.getDetails());
        if(note.getHour()!=-1)
            editor.putInt("note_hour"+newKey,note.getHour());
        if(note.getMinute()!=-1)
            editor.putInt("note_minute"+newKey,note.getMinute());
        if(note.getDate()!=null){
            SimpleDateFormat format=new SimpleDateFormat("HH:mm dd-MM-yyyy");
            editor.putString("note_date"+newKey,format.format(note.getDate()));
        }
        if(note.getCoverImageUri()!=null){
            editor.putString("note_image_uri"+newKey,note.getCoverImageUri());
        }
        editor.putStringSet("notes_id",notesID);
        PageStateHolder psh=note.getPageStateHolder();
        ArrayList<String> pageState=psh.getPageState();
        ArrayList<String> viewContents=psh.getViewContents();
        ArrayList<String> viewTypes=psh.getViewTypes();
        JSONArray jsonArray = new JSONArray(pageState);
        editor.putString("note_page_state"+newKey,jsonArray.toString());
        int i=0;
        for(String val: pageState){
            editor.putString("note_viewtype"+newKey+val,viewTypes.get(i));
            editor.putString("note_viewcontent"+newKey+val,viewContents.get(i));
            i++;
        }
        editor.apply();



    }

    public String getRandomKey(){
        return UUID.randomUUID().toString();
    }

    public Set<String> getAllID(){
        return pref.getStringSet("notes_id",null);
    }

    public ArrayList<Note> readNotes(){
        ArrayList<Note> notes=new ArrayList<>();
        Set<String> notesID=getAllID();
        if(notesID!=null){
            for(String key:notesID){
                Note note=new Note();
                NoteColor nc=new NoteColor();
                int a=pref.getInt("note_color_a"+key,255);
                int r=pref.getInt("note_color_r"+key,255);
                int g=pref.getInt("note_color_g"+key,255);
                int b=pref.getInt("note_color_b"+key,255);
                nc.setARGB(a,r,g,b);
                note.setNoteColor(nc);
                note.setTitle(pref.getString("note_title"+key,null));
                note.setAddress(pref.getString("note_address"+key,null));
                note.setDetails(pref.getString("note_details"+key,null));
                note.setMinute(pref.getInt("note_minute"+key,0));
                note.setHour(pref.getInt("note_hour"+key,0));
                SimpleDateFormat format=new SimpleDateFormat("HH:mm dd-MM-yyyy");
                try{
                    Date date=format.parse(pref.getString("note_date"+key,"00:00 01-01-1950"));
                    note.setDate(date);
                }catch(Exception e){
                    e.printStackTrace();
                }

                String imgUri=pref.getString("note_image_uri"+key,null);
                if(imgUri!=null)
                    note.setCoverImageUri(imgUri);

                PageStateHolder psh=new PageStateHolder();
                ArrayList<String> viewContents=new ArrayList<>();
                ArrayList<String> viewTypes=new ArrayList<>();
                JSONArray jsonArray=null;
                try{
                    jsonArray=new JSONArray(pref.getString("note_page_state"+key,null));
                }catch(Exception e){
                    e.printStackTrace();
                }

                if(jsonArray!=null){
                    ArrayList<String> pageState=new ArrayList<>();
                    try{
                        for (int i = 0; i < jsonArray.length(); i++) {
                            pageState.add(jsonArray.get(i).toString());
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    psh.setPageState(pageState);
                    int i=0;
                    for(String val : psh.getPageState()){
                        viewContents.add(pref.getString("note_viewcontent"+key+val,null));
                        viewTypes.add(pref.getString("note_viewtype"+key+val,null));
                        i++;
                    }
                    psh.setViewContents(viewContents);
                    psh.setViewTypes(viewTypes);
                    note.setPageStateHolder(psh);
                }

                note.setID(key);
                notes.add(note);
            }
        }
        return notes;
    }

    public void clearPreferences(){
        SharedPreferences.Editor editor=pref.edit();
        editor.clear();
        editor.apply();
    }

    public void updateNote(Note note){
        SharedPreferences.Editor editor=pref.edit();
        PageStateHolder psh=note.getPageStateHolder();
        ArrayList<String> pageState=psh.getPageState();
        ArrayList<String> viewContents=psh.getViewContents();
        ArrayList<String> viewTypes=psh.getViewTypes();
        JSONArray jsonArray = new JSONArray(pageState);
        editor.putString("note_page_state"+note.getID(),jsonArray.toString());
        int i=0;
        for(String val: pageState){
            editor.putString("note_viewtype"+note.getID()+val,viewTypes.get(i));
            editor.putString("note_viewcontent"+note.getID()+val,viewContents.get(i));
            i++;
        }

        editor.apply();
    }

    public void removeNote(Note note){
        SharedPreferences.Editor editor=pref.edit();
        String key=note.getID();
        editor.remove("note_title"+key);
        editor.remove("note_priority"+key);
        editor.remove("note_color_a"+key);
        editor.remove("note_color_r"+key);
        editor.remove("note_color_g"+key);
        editor.remove("note_color_b"+key);
        editor.remove("note_address"+key);
        editor.remove("note_details"+key);
        editor.remove("note_hour"+key);
        editor.remove("note_minute"+key);
        editor.remove("note_date"+key);
        editor.remove("note_image_uri"+key);
        editor.remove("note_page_state"+key);
        editor.remove("note_viewcontent"+key);
        editor.remove("note_viewtype"+key);
        Set<String> notesID=getAllID();
        notesID.remove(key);
        editor.putStringSet("notes_id",notesID);
        editor.apply();
    }





}
