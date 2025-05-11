package com.example.demo_fbfmobile.model;

public class OnBoarding {
    int backgroudId;
    String title;
    String description;
    String descbtn;
    int logoId;

    public OnBoarding() {
    }

    public OnBoarding(int backgroudId, String title, String description, String descbtn, int logoId) {
        this.backgroudId = backgroudId;
        this.title = title;
        this.description = description;
        this.descbtn = descbtn;
        this.logoId = logoId;
    }

    public int getBackgroudId() {
        return backgroudId;
    }

    public void setBackgroudId(int backgroudId) {
        this.backgroudId = backgroudId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescbtn() {
        return descbtn;
    }

    public void setDescbtn(String descbtn) {
        this.descbtn = descbtn;
    }

    public int getLogoId() {
        return logoId;
    }

    public void setLogoId(int logoId) {
        this.logoId = logoId;
    }
}
