package com.example.enviromax;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseRegistrar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FirebaseDB {

    private final DatabaseReference m_DatabaseReference;
    private final Context m_context;

    public FirebaseDB(Context context) {
        FirebaseDatabase m_Database = FirebaseDatabase.getInstance("https://enviromax-8ead5-default-rtdb.europe-west1.firebasedatabase.app");
        m_DatabaseReference = m_Database.getReference();
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

    private final static String LAT = "lat";
    private final static String LNG = "lng";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getData(final CallBack_Data callBack_data, DataType type, float hour) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                m_DatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<WeightedLatLngAddress> weights = new ArrayList<WeightedLatLngAddress>();
                        ArrayList<MarkerOptions> markers = new ArrayList<MarkerOptions>();
                        try {
                            for (DataSnapshot rpiSnapshot : snapshot.child("Devices").getChildren()) {
                                String rpiName = rpiSnapshot.getKey().toString();
                                LatLng latLng = new LatLng(snapshot.child("Devices").child(rpiName).child("location").child(LAT).getValue(double.class), snapshot.child("Devices").child(rpiName).child("location").child(LNG).getValue(double.class));
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyyHH");
                                LocalDateTime theDate = LocalDateTime.now().minusHours((long)hour);
                                String assembleDate = dtf.format(theDate);

                                double valueFromDb = snapshot.child("data").child(assembleDate).child(rpiName).child(type.toString()).getValue(double.class);

                                WeightedLatLngAddress weightedAddress = new WeightedLatLngAddress(m_context, latLng, NormalizeData.normalizeData(type, valueFromDb));

                                weights.add(weightedAddress);
                                markers.add(new MarkerOptions().position(latLng).title(type.toString()).snippet("Address = " + weightedAddress.getAddress() + "\n" + "Current = " + String.format("%.2f", valueFromDb)));
                            }
                        } catch (Exception ignored) {
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
        };
        thread.start();
    }
}
