package com.example.enviromax;

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

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseDB {

    private FirebaseDatabase m_Database;
    private DatabaseReference m_DatabaseReference;

    public FirebaseDB() {
        m_Database = FirebaseDatabase.getInstance("https://enviromax-8ead5-default-rtdb.europe-west1.firebasedatabase.app");
        m_DatabaseReference = m_Database.getReference("Devices");
    }

    public interface CallBack_Data {
        void dataReady(HashMap<IntensityEnum, ArrayList<WeightedLatLng>> weights, HashMap<IntensityEnum, ArrayList<MarkerOptions>> markers);
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
                HashMap<IntensityEnum, ArrayList<WeightedLatLng>> weights = initHashMap(WeightedLatLng.class);
                HashMap<IntensityEnum, ArrayList<MarkerOptions>> markers = initHashMap(MarkerOptions.class);
                try {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        double valueFromDb = child.child("data").child("lastData").child(type.toString()).getValue(double.class);
                        LatLng latLng = new LatLng(child.child("location").child("lat").getValue(double.class), child.child("location").child("lng").getValue(double.class));
                        double intensity = NormalizeData.normalizeData(type, valueFromDb);

                        WeightedLatLng weight = new WeightedLatLng(latLng, intensity);
                        if (intensity < 1.01) {
                            weights.get(IntensityEnum.LOW).add(weight);
                            markers.get(IntensityEnum.LOW).add(new MarkerOptions().position(latLng).title(type.toString()).snippet("Current = " + valueFromDb + "\n" + "Highest = " + "HIGHEST" + "\n" + "Average = " + "AVERAGE")); // TODO HIGHEST and AVERAGE
                        } else if (intensity > 1.01 && intensity < 1.05) {
                            weights.get(IntensityEnum.LOW_MID).add(weight);
                            markers.get(IntensityEnum.LOW_MID).add(new MarkerOptions().position(latLng).title(type.toString()).snippet("Current = " + valueFromDb + "\n" + "Highest = " + "HIGHEST" + "\n" + "Average = " + "AVERAGE")); // TODO HIGHEST and AVERAGE
                        } else if (intensity > 1.05 && intensity < 1.07) {
                            weights.get(IntensityEnum.MID).add(weight);
                            markers.get(IntensityEnum.MID).add(new MarkerOptions().position(latLng).title(type.toString()).snippet("Current = " + valueFromDb + "\n" + "Highest = " + "HIGHEST" + "\n" + "Average = " + "AVERAGE")); // TODO HIGHEST and AVERAGE
                        } else if (intensity > 1.07 && intensity < 1.09) {
                            weights.get(IntensityEnum.MID_HIGH).add(weight);
                            markers.get(IntensityEnum.MID_HIGH).add(new MarkerOptions().position(latLng).title(type.toString()).snippet("Current = " + valueFromDb + "\n" + "Highest = " + "HIGHEST" + "\n" + "Average = " + "AVERAGE")); // TODO HIGHEST and AVERAGE
                        } else if (intensity > 1.09 && intensity < 1.11) {
                            weights.get(IntensityEnum.HIGH).add(weight);
                            markers.get(IntensityEnum.HIGH).add(new MarkerOptions().position(latLng).title(type.toString()).snippet("Current = " + valueFromDb + "\n" + "Highest = " + "HIGHEST" + "\n" + "Average = " + "AVERAGE")); // TODO HIGHEST and AVERAGE
                        } else {
                            weights.get(IntensityEnum.HIGHEST).add(weight);
                            markers.get(IntensityEnum.HIGHEST).add(new MarkerOptions().position(latLng).title(type.toString()).snippet("Current = " + valueFromDb + "\n" + "Highest = " + "HIGHEST" + "\n" + "Average = " + "AVERAGE")); // TODO HIGHEST and AVERAGE
                        }
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
    }

    private <T> HashMap<IntensityEnum, ArrayList<T>> initHashMap(Class<T> cls) {
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
