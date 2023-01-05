package com.millermatute.triumph;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Task {

    private String task_title;
    private String task_description;
    private String task_due_date;
    private String task_date_completed;
    private String task_priority;
    private String task_person_creator;
    private String task_person_modifier;

    private String task_time_created;
    private String task_time_modified;

    private String task_date_created;
    private String task_date_modified;
    private String task_zone;

    // Date Utilities
    private LocalDate dateObj = LocalDate.now();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    // Time Utilities
    private LocalDateTime localDateTime = LocalDateTime.now();
    private ZoneId zoneId = ZoneId.systemDefault();
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    public Task() {

    }

    public Task(String task_title, String task_description, String task_due_date, String task_date_completed, String task_priority, String task_creator, String task_modifier) {
        this.task_title = task_title;
        this.task_description = task_description;
        this.task_due_date = task_due_date;
        this.task_date_completed = task_date_completed;
        this.task_priority = task_priority;
        this.task_person_creator = task_creator;
        this.task_person_modifier = task_modifier;

        // Automatic
        this.task_time_created = timeFormatter.format(localDateTime);
        this.task_time_modified = timeFormatter.format(localDateTime);

        this.task_date_created = dateObj.format(formatter);
        this.task_date_modified = dateObj.format(formatter);

        this.task_zone = zoneId.toString();
    }

    public String getTask_title() {return task_title;}
    public String getTask_description() {return task_description;}
    public String getTask_date_created() {return task_date_created;}
    public String getTask_date_modified() {return task_date_modified;}
    public String getTask_due_date() {return task_due_date;}
    public String getTask_date_completed() {return task_date_completed;}
    public String getTask_priority() {return task_priority;}
    public String getTask_person_creator() {return task_person_creator;}
    public String getTask_person_modifier() {return task_person_modifier;}
    public String getTask_time_created() {return task_time_created;}
    public String getTask_time_modified() {return task_time_modified;}
    public String getTask_zone() {return task_zone;}

    public void setTask_title(String task_title) {this.task_title = task_title;}
    public void setTask_description(String task_description) {this.task_description = task_description;}
    public void setTask_date_created(String task_date_created) {this.task_date_created = task_date_created;}
    public void setTask_date_modified(String task_date_modified) {this.task_date_modified = task_date_modified;}
    public void setTask_due_date(String task_due_date) {this.task_due_date = task_due_date;}
    public void setTask_date_completed(String task_date_completed) {this.task_date_completed = task_date_completed;}
    public void setTask_priority(String task_priority) {this.task_priority = task_priority;}
    public void setTask_person_modifier(String task_modifier) {this.task_person_modifier = task_modifier;}
    public void setTask_person_creator(String task_creator) {this.task_person_creator = task_creator;}
    public void setTask_time_created(String task_time_created) {this.task_time_created = task_time_created;}
    public void setTask_time_modified(String task_time_modified) {this.task_time_modified = task_time_modified;}
    public void setTask_zone(String task_zone) {this.task_zone = task_zone;}
}
