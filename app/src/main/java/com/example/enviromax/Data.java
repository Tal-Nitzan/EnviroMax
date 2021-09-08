package com.example.enviromax;

public class Data {
    private Device device;
    private String date;
    private double lat;
    private double lng;
    private double temp;
    private double humidity;

    public Data(Device device, String date, double lat, double lng, double temp, double humidity) {
        this.device = device;
        this.date = date;
        this.lat = lat;
        this.lng = lng;
        this.temp = temp;
        this.humidity = humidity;
    }

    public Device getDevice() {
        return device;
    }

    public String getDate() {
        return date;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public double getTemp() {
        return temp;
    }

    public double getHumidity() {
        return humidity;
    }
}
