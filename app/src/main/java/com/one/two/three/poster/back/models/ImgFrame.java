package com.one.two.three.poster.back.models;

public class ImgFrame extends BaseFrame {

    private String imagePath;
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    private String maskPath;
    public String getMaskPath() {
        return maskPath;
    }

    public ImgFrame(int x, int y, int width, int height, String maskPath) {
        super(x, y, width, height);
        this.imagePath = "";
        this.maskPath = maskPath;
    }
}