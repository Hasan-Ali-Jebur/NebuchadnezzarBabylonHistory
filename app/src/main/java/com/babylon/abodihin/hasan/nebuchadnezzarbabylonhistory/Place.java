package com.babylon.abodihin.hasan.nebuchadnezzarbabylonhistory;


import android.graphics.Bitmap;

public class Place {
    String placeId;
    String placeTitle;
    String placeDescription;
    String placeImage;
    int placeLikes;
    String placeComments;
    double placeLat;
    double placeLong;
    Bitmap placeBitmap;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceTitle() {
        return placeTitle;
    }

    public void setPlaceTitle(String placeTitle) {
        this.placeTitle = placeTitle;
    }

    public String getPlaceDescription() {
        return placeDescription;
    }

    public void setPlaceDescription(String placeDescription) {
        this.placeDescription = placeDescription;
    }

    public String getPlaceImage() {
        return placeImage;
    }

    public void setPlaceImage(String placeImage) {
        this.placeImage = placeImage;
    }

    public int getPlaceLikes() {
        return placeLikes;
    }

    public void setPlaceLikes(int placeLikes) {
        this.placeLikes = placeLikes;
    }

    public String getPlaceComments() {
        return placeComments;
    }

    public void setPlaceComments(String placeComments) {
        this.placeComments = placeComments;
    }

    public double getPlaceLat() {
        return placeLat;
    }

    public void setPlaceLat(double placeLat) {
        this.placeLat = placeLat;
    }

    public double getPlaceLong() {
        return placeLong;
    }

    public void setPlaceLong(double placeLong) {
        this.placeLong = placeLong;
    }

    public Bitmap getPlaceBitmap() {
        return placeBitmap;
    }

    public void setPlaceBitmap(Bitmap placeBitmap) {
        this.placeBitmap = placeBitmap;
    }
}
