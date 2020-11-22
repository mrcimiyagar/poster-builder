package com.one.two.three.poster.back.models;

public class TxtFrame extends BaseFrame {

    private String text;
    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }

    private int textColor;
    public int getTextColor() {
        return textColor;
    }
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public TxtFrame(int x, int y, int width, int height, int textColor) {
        super(x, y, width, height);
        this.textColor = textColor;
        this.text = "";
    }
}