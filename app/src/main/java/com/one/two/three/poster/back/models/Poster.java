package com.one.two.three.poster.back.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Poster implements Serializable {

    private String posterID;

    private BackFrame backFrame;

    private ArrayList<BaseFrame> frames;

    public String getPosterID() {
        return posterID;
    }

    public BackFrame getBackFrame() {
        return backFrame;
    }

    public ArrayList<BaseFrame> getFrames() {
        return frames;
    }

    public Poster(String posterID, BackFrame backFrame, ArrayList<BaseFrame> frames) {
        this.posterID = posterID;
        this.backFrame = backFrame;
        this.frames = new ArrayList<>(frames);
    }
}