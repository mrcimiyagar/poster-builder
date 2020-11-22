package com.one.two.three.poster.back.models;

public class BackFrame extends BaseFrame {

    String backgroundPath;
    public String getBackgroundPath() {
        return this.backgroundPath;
    }

    public BackFrame(int width, int height, String backgroundPath) {
        super(0, 0, width, height);
        this.backgroundPath = backgroundPath;
    }
}