package com.example.melih.a14011077_digitalnotebook;

import android.util.Log;
import android.view.View;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class PageStateHolder implements Serializable {
    int viewCount;
    ArrayList<View> views;
    ArrayList<String> viewTypes;
    ArrayList<String> pageState;
    ArrayList<String> viewContents;
    public PageStateHolder(){
        viewCount=0;
        pageState=new ArrayList<>();
        views=new ArrayList<>();
        viewTypes=new ArrayList<>();
        viewContents=new ArrayList<>();
    }

    public void setView(int viewType,View v,String content){
        if(viewType==1){
            viewTypes.add("txt");
        }else if(viewType==2){
            viewTypes.add("img");
        }else if(viewType==3){
            viewTypes.add("audio");
        }else if(viewType==4){
            viewTypes.add("video");
        }
        viewContents.add(content);
        views.add(v);
        viewCount++;
        pageState.add(Integer.toString(v.getId()));
    }

    public void removeView(View v){
        int i=0;
        boolean found=false;
        while(i<pageState.size() && !found){
            if(Integer.parseInt(pageState.get(i))==v.getId()){
                found=true;
                viewTypes.remove(i);
                pageState.remove(i);
                viewContents.remove(i);
            }
            i++;
        }
    }

    public void updateViewContent(View v,String content){
        int i=0;
        boolean found=false;
        while(i<pageState.size() && !found){
            if(Integer.parseInt(pageState.get(i))==v.getId()){
                found=true;
                viewContents.set(i,content);
            }
            i++;
        }
    }

    public int getViewCount() {
        return viewCount;
    }

    public ArrayList<View> getViews() {
        return views;
    }

    public ArrayList<String> getViewTypes() {
        return viewTypes;
    }

    public ArrayList<String> getPageState() {
        return pageState;
    }

    public ArrayList<String> getViewContents() {
        return viewContents;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void setViews(ArrayList<View> views) {
        this.views = views;
    }

    public void setViewTypes(ArrayList<String> viewTypes) {
        this.viewTypes = viewTypes;
    }

    public void setPageState(ArrayList<String> pageState) {
        this.pageState = pageState;
    }

    public void setViewContents(ArrayList<String> viewContents) {
        this.viewContents = viewContents;
    }
}
