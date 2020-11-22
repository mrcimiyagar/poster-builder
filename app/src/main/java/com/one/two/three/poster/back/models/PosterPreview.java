package com.one.two.three.poster.back.models;

/**
 * Created by Pouyan-PC on 8/6/2017.
 */

public class PosterPreview {

    private String id;
    private String thumbnailPath;
    private String price;
    private String sku;
    private String url;
    private int rate;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private int frames;

    public PosterPreview(String id) {
        this.id = id;
        this.thumbnailPath = "";
        this.price = "";
        this.url = "";
        this.sku = "";
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
