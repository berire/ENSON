package com.example.Mnemonica;

/**
 * Created by user on 5.5.2017.
 */

public class Word {
    private int startx, starty, endx, endy;
    private String context;
    public Word()
    {}
    public Word(int startx, int starty, int endx, int endy,String context)
    {
        this.startx=startx;
        this.starty=starty;
        this.endx=endx;
        this.endy=endy;
        this.context=context;

    }

    public String getcontext() {
        return context;
    }

    public void setcontext(String context) {
        this.context = context;
    }

    public void setEndx(int endx) {
        this.endx = endx;
    }

    public void setEndy(int endy) {
        this.endy = endy;
    }

    public void setStartx(int startx) {
        this.startx = startx;
    }

    public void setStarty(int starty) {
        this.starty = starty;
    }

    public int getEndx() {
        return endx;
    }

    public int getEndy() {
        return endy;
    }

    public int getStartx() {
        return startx;
    }

    public int getStarty() {
        return starty;
    }

}
