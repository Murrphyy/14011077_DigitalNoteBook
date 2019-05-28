package com.example.melih.a14011077_digitalnotebook;

import java.io.Serializable;

public class NoteColor implements Serializable {
    public int a,r,g,b;
    public NoteColor(){

    }

    public void setARGB(int a,int r,int g,int b){
        this.a=a;
        this.b=b;
        this.r=r;
        this.g=g;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getG() {
        return g;
    }

    public int getR() {
        return r;
    }
}
