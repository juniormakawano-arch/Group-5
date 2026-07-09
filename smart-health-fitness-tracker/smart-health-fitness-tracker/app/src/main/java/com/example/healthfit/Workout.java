package com.example.healthfit;

public class Workout {
    private String name;
    private String duration;
    private String difficulty;
    private String description;
    private int imageResId; // To show an icon

    public Workout(String name, String duration, String difficulty, String description, int imageResId) {
        this.name = name;
        this.duration = duration;
        this.difficulty = difficulty;
        this.description = description;
        this.imageResId = imageResId;
    }

    // Getters
    public String getName() { return name; }
    public String getDuration() { return duration; }
    public String getDifficulty() { return difficulty; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
}