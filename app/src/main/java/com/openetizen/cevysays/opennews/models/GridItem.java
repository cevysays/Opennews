package com.openetizen.cevysays.opennews.models;

/**
 * Created by cevyyufindra on 11/28/15.
 */
public class GridItem {
    private String image;
    private String title;
    private int album_ID;
    private int photo_ID;

    public int getPhoto_ID() {
        return photo_ID;
    }

    public void setPhoto_ID(int photo_ID) {
        this.photo_ID = photo_ID;
    }

    public int getAlbum_ID() {
        return album_ID;
    }

    public void setAlbum_ID(int album_ID) {
        this.album_ID = album_ID;
    }

    public GridItem() {
        super();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

