package com.nhlstenden.reader2.models;

public class Author {

    private int ID;
    private String fullName;

    public Author(int ID, String fullName) {
        this.ID = ID;
        this.fullName = fullName;
    }

    public int getID() {
        return ID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}
