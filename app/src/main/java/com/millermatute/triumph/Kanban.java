package com.millermatute.triumph;

public class Kanban {

    private String kanban_title;
    private String kanban_description;

    public Kanban() {}

    public Kanban(String kanban_title, String kanban_description) {
        this.kanban_title = kanban_title;
        this.kanban_description = kanban_description;
    }

    public String getKanban_title() {return kanban_title;}
    public String getKanban_description() {return kanban_description;}

    public void setKanban_title(String kanban_title) {this.kanban_title = kanban_title;}
    public void setKanban_description(String kanban_description) {this.kanban_description = kanban_description;}
}
