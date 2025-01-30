package com.mdanwarul.event;
import java.io.Serializable;

public class Event implements Serializable {
    private String id;
    private String name;
    private String date;
    private String description;
    private String userId; // New field to associate events with users

    // Constructor with userId for Firebase storage
    public Event(String id, String name, String date, String description, String userId) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.description = description;
        this.userId = userId;
    }

    // Default constructor for Firebase
    public Event() {
    }

    // Existing getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and setter for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
