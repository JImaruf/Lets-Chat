package com.example.letschat.Models;

public class Status {
    private String imageURL;
    private  long lastUpdated;

    public Status() {
    }

    public Status(String imageURL, long lastUpdated) {
        this.imageURL = imageURL;
        this.lastUpdated = lastUpdated;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
