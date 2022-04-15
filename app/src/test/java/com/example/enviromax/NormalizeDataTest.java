package com.example.enviromax;

import org.junit.Test;
import static org.junit.Assert.*;

public class NormalizeDataTest {

    @Test
    public void NormalizeTemp_ProvideTemp_ResultsNormalized() {
        double givenTemp = 28;

        normalizeTestLogic(givenTemp, NormalizeData.TEMP, DataType.Temperature, 0);
    }

    @Test
    public void NormalizePressure_ProvidePressure_ResultsNormalized() {
        double givenPressure = 1200;

        normalizeTestLogic(givenPressure, NormalizeData.BAROMETER_PRESSURE, DataType.Pressure, 0);
    }

    @Test
    public void NormalizePollution_ProvidePollution_ResultsNormalized() {
        double givenPollution = 42000;

        normalizeTestLogic(givenPollution, NormalizeData.AIR_POLLUTION, DataType.Air_Pollution, 0);
    }

    @Test
    public void NormalizeHumidity_ProvideHumidity_ResultsNormalized() {
        double givenHumidity = 45;

        normalizeTestLogic(givenHumidity, NormalizeData.HUMIDITY, DataType.Humidity, 0);
    }

    private void normalizeTestLogic(double givenData, int normalizeConst, DataType type, double delta) {
        double wantedResult = givenData/normalizeConst;
        assertEquals(wantedResult, NormalizeData.normalizeData(type, givenData), delta);
    }
}