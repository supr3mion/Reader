package com.nhlstenden.reader2.models;

public class Note {

    private int ID;
    private int ChapterId;
    private int PageNr;
    private String NoteText;

    public Note(int ID, int ChapterId, int PageNr, String NoteText) {
        this.ID = ID;
        this.ChapterId = ChapterId;
        this.PageNr = PageNr;
        this.NoteText = NoteText;
    }

    public int getID() {
        return ID;
    }

    public int getChapterId() {
        return ChapterId;
    }

    public int getPageNr() {
        return PageNr;
    }

    public String getNoteText() {
        return NoteText;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setChapterId(int ChapterId) {
        this.ChapterId = ChapterId;
    }

    public void setPageNr(int PageNr) {
        this.PageNr = PageNr;
    }

    public void setNoteText(String NoteText) {
        this.NoteText = NoteText;
    }

}
