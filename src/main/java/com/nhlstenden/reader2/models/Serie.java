package com.nhlstenden.reader2.models;

import com.nhlstenden.reader2.annotations.Exclude;

import java.util.List;

public class Serie {

    @Exclude
    private int Id;

    private String Name;
    private String Description;
    private Boolean Completed;
    private Boolean Favorite;
    private Integer CurrentChapter;
    private Boolean Read;
    private String Gerne;

    @Exclude
    private Author Author;

    @Exclude
    private List<Tag> Tags;

    @Exclude
    private List<Chapter> Chapters;

    public Serie(String Name, String Description, Boolean Completed, Boolean Favorite, Integer CurrentChapter, boolean Read, String Gerne) {
        this.Name = Name;
        this.Description = Description;
        this.Completed = Completed;
        this.Favorite = Favorite;
        this.CurrentChapter = CurrentChapter;
        this.Read = Read;
        this.Gerne = Gerne;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }

    public List<Tag> getTags() {
        return Tags;
    }

    public Boolean isCompleted() {
        return Completed;
    }

    public Boolean isFavorite() {
        return Favorite;
    }

    public Integer getCurrentChapter() {
        return CurrentChapter;
    }

    public Boolean isRead() {
        return Read;
    }

    public String getGerne() {
        return Gerne;
    }

    public List<Chapter> getChapters() {
        return Chapters;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public void setTags(List<Tag> Tags) {
        this.Tags = Tags;
    }

    public void setCompleted(Boolean Completed) {
        this.Completed = Completed;
    }

    public void setFavorite(Boolean Favorite) {
        this.Favorite = Favorite;
    }

    public void setCurrentChapter(Integer CurrentChapter) {
        this.CurrentChapter = CurrentChapter;
    }

    public void setRead(Boolean Read) {
        this.Read = Read;
    }

    public void setChapters(List<Chapter> Chapters) {
        this.Chapters = Chapters;
    }

    public void setGerne(String Gerne) {
        this.Gerne = Gerne;
    }

}
