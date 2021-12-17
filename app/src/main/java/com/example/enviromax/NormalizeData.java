package com.example.enviromax;

enum DataType {
    Temperature,
    Pressure,
    Air_Pollution,
    Humidity
}

public class NormalizeData {
    public static final int TEMP = 24;
    public static final int BAROMETER_PRESSURE = 1000;
    public static final int AIR_POLLUTION = 40000;
    public static final int HUMIDITY = 38;


    public static double normalizeData(DataType type, double value) throws IllegalArgumentException {
        switch (type) {
            case Temperature:
                return value/TEMP;
            case Pressure:
                return value/BAROMETER_PRESSURE;
            case Air_Pollution:
                return value/AIR_POLLUTION;
            case Humidity:
                return value/HUMIDITY;
            default:
                throw new IllegalArgumentException("Invalid DataType");
        }
    }
}
