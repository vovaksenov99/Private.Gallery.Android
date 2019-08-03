package com.privategallery.akscorp.privategalleryandroid.PHash.entry;

import com.privategallery.akscorp.privategalleryandroid.Essentials.Image;

/**
 * Created by gavin on 2017/3/27.
 */

public class Photo {
    public Image image;

    private long id;

    private int width;
    private int height;

    private String path;

    private String name;

    private String mimetype;

    private long size;

    private String finger = "";


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void getHeight(int height) {
        this.height = height;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFinger() {
        return finger;
    }

    public void setFinger(String finger) {
        this.finger = finger;
    }
}
