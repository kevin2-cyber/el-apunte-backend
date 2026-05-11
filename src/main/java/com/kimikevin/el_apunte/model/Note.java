package com.kimikevin.el_apunte.model;

import java.util.Objects;

public class Note {
    private String title;
    private String content;
    private String date;

    public Note(String title, String content, String date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Note{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Note note)) return false;
        return Objects.equals(title, note.title) && Objects.equals(content, note.content) && Objects.equals(date, note.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, date);
    }
}
