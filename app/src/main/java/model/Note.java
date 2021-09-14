package model;

public class Note {
    private String title;
    private String content;
    private Integer color;

    public Note() {
    }

    public Note(String title, String content, Integer color) {
        this.title = title;
        this.content = content;
        this.color = color;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
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
}
