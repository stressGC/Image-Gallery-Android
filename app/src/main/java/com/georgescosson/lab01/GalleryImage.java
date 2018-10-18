package com.georgescosson.lab01;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Georges on 16/10/2018.
 */

public class GalleryImage {

    private String title;
    private Bitmap image;
    private String tags;

    public GalleryImage(String title, String tags, Bitmap bitmap){
        this.title = title;
        this.image = bitmap;
        this.tags = tags;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setImage(Bitmap bitmap) {
        this.image = bitmap;
    }

    @Override
    public String toString(){
        return "Title : " + title + "\n" +
                "Tags : " + tags;
    }

    public void print(){
        Log.d("debug", this.toString());
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
