package com.one.two.three.poster.back.models;

/**
 * Created by Pouyan-PC on 12/14/2017.
 */

public class TextStyle {

    private int textColor;
    private TextShadow textShadow;
    private TextStroke textStroke;
    private TextBackground textBackground;

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public TextShadow getTextShadow() {
        return textShadow;
    }

    public void setTextShadow(TextShadow textShadow) {
        this.textShadow = textShadow;
    }

    public TextStroke getTextStroke() {
        return textStroke;
    }

    public void setTextStroke(TextStroke textStroke) {
        this.textStroke = textStroke;
    }

    public TextBackground getTextBackground() {
        return textBackground;
    }

    public void setTextBackground(TextBackground textBackground) {
        this.textBackground = textBackground;
    }

}
