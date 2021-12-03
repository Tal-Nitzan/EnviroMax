package com.example.enviromax;

public class Data {
    private Device device;
    private String date;
    private Location location;
    private double temp;
    private double humidity;

    public Data(Device device, String date, Location location, double temp, double humidity) {
        this.device = device;
        this.date = date;
        this.location = location;
        this.temp = temp;
        this.humidity = humidity;
    }

    public Device getDevice() {
        return device;
    }

    public String getDate() {
        return date;
    }

    public Location getLocation() {
        return location;
    }

    public double getTemp() {
        return temp;
    }

    public double getHumidity() {
        return humidity;
    }
}
