package com.example.enviromax;

import android.content.Context;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.io.IOException;
import java.util.Locale;

public class WeightedLatLngAddress implements Comparable<WeightedLatLngAddress> {
    WeightedLatLng weightedLatLng;
    String address;

    public WeightedLatLngAddress(Context context, LatLng latLng, double intensity) {
        weightedLatLng = new WeightedLatLng(latLng, intensity);
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            this.address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
        } catch (IOException exception) {
            this.address = "Unknown address";
        }
    }

    public WeightedLatLng getWeightedLatLng() {
        return weightedLatLng;
    }

    public void setWeightedLatLng(WeightedLatLng weightedLatLng) {
        this.weightedLatLng = weightedLatLng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean equals(WeightedLatLngAddress other) {
        return this.weightedLatLng.getIntensity() == other.weightedLatLng.getIntensity() && this.weightedLatLng.getPoint().equals(other.weightedLatLng.getPoint());
    }

    @Override
    public int compareTo(WeightedLatLngAddress other)
    {
        if (this.weightedLatLng.getIntensity() == other.weightedLatLng.getIntensity() && this.weightedLatLng.getPoint().equals(other.weightedLatLng.getPoint()))
            return 0;
        return 1;
    }
}
