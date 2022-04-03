package com.example.enviromax;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseDB {

    private final DatabaseReference m_DatabaseReference;
    private final Context m_context;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public FirebaseDB(Context context) {
        FirebaseDatabase m_Database = FirebaseDatabase.getInstance("https://enviromax-8ead5-default-rtdb.europe-west1.firebasedatabase.app");
        m_DatabaseReference = m_Database.getReference("Devices");
        this.m_context = context;
    }

    public interface CallBack_Data {
        void dataReady(HashMap<IntensityEnum, ArrayList<WeightedLatLngAddress>> weights, HashMap<IntensityEnum, ArrayList<MarkerOptions>> markers);
    }

    public interface CallBack_Temperature extends CallBack_Data {
    }

    public interface CallBack_Barometer extends CallBack_Data {
    }

    public interface CallBack_AirPollution extends CallBack_Data {
    }

    public interface CallBack_Humidity extends CallBack_Data {
    }

    public void getData(final CallBack_Data callBack_data, DataType type) {
        m_DatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<IntensityEnum, ArrayList<WeightedLatLngAddress>> weights = initHashMap(WeightedLatLngAddress.class);
                HashMap<IntensityEnum, ArrayList<MarkerOptions>> markers = initHashMap(MarkerOptions.class);
                try {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        double valueFromDb = child.child("data").child("lastData").child(type.toString()).getValue(double.class);
                        LatLng latLng = new LatLng(child.child("location").child("lat").getValue(double.class), child.child("location").child("lng").getValue(double.class));
                        double intensity = NormalizeData.normalizeData(type, valueFromDb);

                        WeightedLatLngAddress weightedAddress = new WeightedLatLngAddress(m_context, latLng, intensity);
                        weights.get(IntensityEnum.intensityToIntensityEnum(intensity)).add(weightedAddress);
                        markers.get(IntensityEnum.intensityToIntensityEnum(intensity)).add(new MarkerOptions().position(latLng).title(type.toString()).snippet("Address = " + weightedAddress.getAddress() + "\n" + "Current = " + String.format("%.2f",valueFromDb) + "\n" + "Highest = " + "HIGHEST" + "\n" + "Average = " + "AVERAGE")); // TODO HIGHEST and AVERAGE
                    }
                } catch (Exception e) {  }
                if (weights.size() > 0 && callBack_data != null) {
                    callBack_data.dataReady(weights, markers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    enum IntensityEnum {
        LOW,
        LOW_MID,
        MID,
        MID_HIGH,
        HIGH,
        HIGHEST;

        public static IntensityEnum fromInteger(int x) {
            switch(x) {
                case 0:
                    return LOW;
                case 1:
                    return LOW_MID;
                case 2:
                    return MID;
                case 3:
                    return MID_HIGH;
                case 4:
                    return HIGH;
                case 5:
                    return HIGHEST;
            }
            return null;
        }

        public static IntensityEnum intensityToIntensityEnum(double value) {
            if (value < 1.01) {
                return LOW;
            } else if (value > 1.01 && value < 1.05) {
                return LOW_MID;
            } else if (value > 1.05 && value < 1.07) {
                return MID;
            } else if (value > 1.07 && value < 1.09) {
                return MID_HIGH;
            } else if (value > 1.09 && value < 1.11) {
                return HIGH;
            } else {
                return HIGHEST;
            }
        }


    }

    public static <T> HashMap<IntensityEnum, ArrayList<T>> initHashMap(Class<T> cls) {
        HashMap<IntensityEnum, ArrayList<T>> hashMap = new HashMap<IntensityEnum, ArrayList<T>>();
        hashMap.put(IntensityEnum.LOW, new ArrayList<T>());
        hashMap.put(IntensityEnum.LOW_MID, new ArrayList<T>());
        hashMap.put(IntensityEnum.MID, new ArrayList<T>());
        hashMap.put(IntensityEnum.MID_HIGH, new ArrayList<T>());
        hashMap.put(IntensityEnum.HIGH, new ArrayList<T>());
        hashMap.put(IntensityEnum.HIGHEST, new ArrayList<T>());
        return hashMap;
    }
}
