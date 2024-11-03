package com.nhlstenden.reader2.models;

import com.nhlstenden.reader2.ComicParser.RarParser;
import com.nhlstenden.reader2.annotations.Exclude;
import javafx.scene.image.Image;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

public class Chapter {

    @Exclude
    private int Id;

    private int SerieID;
    private String Title;
    private Boolean Read;
    private Integer CurrentPage;
    private LocalDateTime LastRead;

    @Exclude
    private File file;

    @Exclude
    private List<Note> Notes;

    @Exclude
    private List<Image> Pages;

    public Chapter(String Title, File file, Boolean Read, Integer CurrentPage, LocalDateTime LastRead) {
        this.Title = Title;
        this.Read = Read;
        this.file = file;
        this.CurrentPage = CurrentPage;
        this.LastRead = LastRead;
    }

    public int getId() {
        return Id;
    }

    public int getSerieID() {
        return SerieID;
    }

    public String getTitle() {
        return Title;
    }

    public Boolean isRead() {
        return Read;
    }

    public File getFile() {
        return file;
    }

    public Integer getCurrentPage() {
        return CurrentPage;
    }

    public LocalDateTime getLastRead() {
        return LastRead;
    }

    public List<Note> getNotes() {
        return Notes;
    }

    public List<Image> getPages() {
        return Pages;
    }

    public Image getPage(int index) {
        return Pages.get(index);
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public void setSerieID(int serieID) {
        this.SerieID = serieID;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public void setRead(Boolean read) {
        Read = read;
    }

    public void setCurrentPage(Integer currentPage) {
        this.CurrentPage = currentPage;
    }

    public void setLastRead(LocalDateTime lastRead) {
        this.LastRead = lastRead;
    }

    public void setNotes(List<Note> Notes) {
        this.Notes = Notes;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setPages(List<Image> Pages) {
        this.Pages = Pages;
    }

}
