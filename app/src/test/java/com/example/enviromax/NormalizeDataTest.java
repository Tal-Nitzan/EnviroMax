package com.example.enviromax;

import org.junit.Test;
import static org.junit.Assert.*;

public class NormalizeDataTest {

    @Test
    public void NormalizeTemp_ProvideTemp_ResultsNormalized() {
        double givenTemp = 28;

        normalizeTestLogic(givenTemp, NormalizeData.MIN_TEMP, NormalizeData.MAX_TEMP, DataType.Temperature);
    }

    @Test
    public void NormalizePressure_ProvidePressure_ResultsNormalized() {
        double givenPressure = 1200;

        normalizeTestLogic(givenPressure, NormalizeData.MIN_BAROMETER_PRESSURE, NormalizeData.MAX_BAROMETER_PRESSURE, DataType.Pressure);
    }

    @Test
    public void NormalizePollution_ProvidePollution_ResultsNormalized() {
        double givenPollution = 42000;

        normalizeTestLogic(givenPollution, NormalizeData.MIN_AIR_POLLUTION, NormalizeData.MAX_AIR_POLLUTION, DataType.Air_Pollution);
    }

    @Test
    public void NormalizeHumidity_ProvideHumidity_ResultsNormalized() {
        double givenHumidity = 45;

        normalizeTestLogic(givenHumidity, NormalizeData.MIN_HUMIDITY, NormalizeData.MAX_HUMIDITY, DataType.Humidity);
    }

    private void normalizeTestLogic(double givenData, int min, int max, DataType type) {
        double wantedResult = ((givenData - min) / (max - min));
        assertEquals(wantedResult, NormalizeData.normalizeData(type, givenData), 0);
    }
}