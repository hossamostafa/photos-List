package com.dev.testproject.model;

import java.io.Serializable;

public class PhotoItem implements Serializable {

    String image;
    String photographer;
    String location;

    public PhotoItem() {
    }

    public PhotoItem(String image, String location, String photographer) {
        this.image = image;
        this.photographer = photographer;
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
