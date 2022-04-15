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

    public FirebaseDB(Context context) {
        FirebaseDatabase m_Database = FirebaseDatabase.getInstance("https://enviromax-8ead5-default-rtdb.europe-west1.firebasedatabase.app");
        m_DatabaseReference = m_Database.getReference("Devices");
        this.m_context = context;
    }

    public interface CallBack_Data {
        void dataReady(ArrayList<WeightedLatLngAddress> weights, ArrayList<MarkerOptions> markers);
    }

    public interface CallBack_Temperature extends CallBack_Data {
    }

    public interface CallBack_Barometer extends CallBack_Data {
    }

    public interface CallBack_AirPollution extends CallBack_Data {
    }

    public interface CallBack_Humidity extends CallBack_Data {
    }

    private final static String DATA = "data";
    private final static String LAST_DATA = "lastData";
    private final static String LOCATION = "location";
    private final static String LAT = "lat";
    private final static String LNG = "lng";
    private final static String METADATA = "metadata";
    private final static String AVERAGE = "average";
    private final static String HIGHEST = "highest";


    public void getData(final CallBack_Data callBack_data, DataType type) {
        m_DatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<WeightedLatLngAddress> weights = new ArrayList<WeightedLatLngAddress>();
                ArrayList<MarkerOptions> markers = new ArrayList<MarkerOptions>();
                try {

                    for (DataSnapshot child : snapshot.getChildren()) {
                        double valueFromDb = child.child(DATA).child(LAST_DATA).child(type.toString()).getValue(double.class);
                        LatLng latLng = new LatLng(child.child(LOCATION).child(LAT).getValue(double.class), child.child(LOCATION).child(LNG).getValue(double.class));

                        WeightedLatLngAddress weightedAddress = new WeightedLatLngAddress(m_context, latLng, valueFromDb);
                        weights.add(weightedAddress);
                        markers.add(new MarkerOptions().position(latLng).title(type.toString()).snippet("Address = " + weightedAddress.getAddress() + "\n" + "Current = " + String.format("%.2f", valueFromDb) + "\n" + "Highest = HIGHEST" + "\n" + "Average = AVERAGE")); // TODO HIGHEST and AVERAGE
                    }
                } catch (Exception e) {
                }
                if (weights.size() > 0 && callBack_data != null) {
                    callBack_data.dataReady(weights, markers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
