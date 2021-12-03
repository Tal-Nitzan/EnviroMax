package com.example.enviromax;

public class Device {
    private String id;
    private String name;
    private Location location;

    public Device(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public String getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }
}
