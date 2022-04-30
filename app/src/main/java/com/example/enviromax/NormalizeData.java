package com.example.enviromax;

enum DataType {
    Temperature,
    Pressure,
    Air_Pollution,
    Humidity
}

public class NormalizeData {
    // 23 ((23-22) / (28-22))
    public static final int MIN_TEMP = 20;
    public static final int MAX_TEMP = 28;

    public static final int MIN_BAROMETER_PRESSURE = 950;
    public static final int MAX_BAROMETER_PRESSURE = 1100;

    public static final int MIN_AIR_POLLUTION = 37000;
    public static final int MAX_AIR_POLLUTION = 45000;

    public static final int MIN_HUMIDITY = 35;
    public static final int MAX_HUMIDITY = 42;


    public static double normalizeData(DataType type, double value) throws IllegalArgumentException {
        switch (type) {
            case Temperature:
                return normalizeAlgorithm(value, MIN_TEMP, MAX_TEMP);
            case Pressure:
                return normalizeAlgorithm(value, MIN_BAROMETER_PRESSURE, MAX_BAROMETER_PRESSURE);
            case Air_Pollution:
                return normalizeAlgorithm(value, MIN_AIR_POLLUTION, MAX_AIR_POLLUTION);
            case Humidity:
                return normalizeAlgorithm(value, MIN_HUMIDITY, MAX_HUMIDITY);
            default:
                throw new IllegalArgumentException("Invalid DataType");
        }
    }

    private static double normalizeAlgorithm(double value, int min, int max) {
        return ((value - min) / (max - min));
    }
}
