package com.example.healthfit;

public class Workout {
    private String name;
    private String duration;
    private String level;

    public Workout(String name, String duration, String level) {
        this.name = name;
        this.duration = duration;
        this.level = level;
    }

    public String getName() { return name; }
    public String getDuration() { return duration; }
    public String getLevel() { return level; }
}