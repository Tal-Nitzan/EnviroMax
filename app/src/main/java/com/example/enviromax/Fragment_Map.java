package com.example.enviromax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fragment_Map extends androidx.fragment.app.Fragment implements OnMapReadyCallback {

    private GoogleMap m_googleMap;
    private boolean firstLocationUpdateFlag = false;
    private FusedLocationProviderClient fusedLocationClient;
    private DataType dataType;
    private ArrayList<TileOverlay> m_tilesOverlay;
    private HashMap<FirebaseDB.IntensityEnum, ArrayList<MarkerOptions>> m_markers;
    private List<Marker> m_activeMarkers = new ArrayList<Marker>();
    FloatingActionButton map_BTN_temp;
    FloatingActionButton map_BTN_barPressure;
    FloatingActionButton map_BTN_airPollution;
    FloatingActionButton map_BTN_Humidity;
    List<WeightedLatLng> m_weightedLatLngs = new ArrayList<WeightedLatLng>();
    Toast toastMessage;
    private Slider map_SLIDER_markers;

    float[] startpoints = {
            0.1F, 0.2F, 0.3F, 0.4F, 0.6F, 1.0F
    };

    private void findViews() {
        map_BTN_temp = (FloatingActionButton) getView().findViewById(R.id.map_BTN_temp);
        map_BTN_barPressure = (FloatingActionButton) getView().findViewById(R.id.map_BTN_barPressure);
        map_BTN_airPollution = (FloatingActionButton) getView().findViewById(R.id.map_BTN_airPollution);
        map_BTN_Humidity = (FloatingActionButton) getView().findViewById(R.id.map_BTN_Humidity);
        map_SLIDER_markers = (Slider) getView().findViewById(R.id.map_SLIDER_markers);
    }

    private void initViews() {
        map_BTN_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataType == DataType.Temperature) {
                    showSnackbarHeatMapAlreadyLoaded(R.string.Temperature);
                    return;
                }
                else if (dataType != null) { // It is something else, should remove the current tile.
                    removeHeatMap();
                    removeActiveMarkers();
                }
                dataType = DataType.Temperature; // Change current type to temperature and update the tile.
                addHeatMapWeighted();
                showSnackbarHeatMapLoaded(R.string.Temperature);
            }
        });

        map_BTN_barPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataType == DataType.Pressure) {
                    showSnackbarHeatMapAlreadyLoaded(R.string.Barometer_Pressure);
                    return;
                }
                else if (dataType != null) { // It is something else, should remove the current tile.
                    removeHeatMap();
                    removeActiveMarkers();
                }
                showSnackbarHeatMapLoaded(R.string.Barometer_Pressure);
                dataType = DataType.Pressure; // Change current type to barometer and update the tile.
                addHeatMapWeighted();
            }
        });

        map_BTN_airPollution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataType == DataType.Air_Pollution) {
                    showSnackbarHeatMapAlreadyLoaded(R.string.Air_Pollution);
                    return;
                }
                else if (dataType != null) { // It is something else, should remove the current tile.
                    removeHeatMap();
                    removeActiveMarkers();
                }
                showSnackbarHeatMapLoaded(R.string.Air_Pollution);
                dataType = DataType.Air_Pollution; // Change current type to air pollution and update the tile.
                addHeatMapWeighted();
            }
        });

        map_BTN_Humidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataType == DataType.Humidity) {
                    showSnackbarHeatMapAlreadyLoaded(R.string.Humidity);
                    return;
                }
                else if (dataType != null) { // It is something else, should remove the current tile.
                    removeHeatMap();
                    removeActiveMarkers();
                }
                showSnackbarHeatMapLoaded(R.string.Humidity);
                dataType = DataType.Humidity; // Change current type to humidity and update the tile.
                addHeatMapWeighted();
            }
        });


        map_SLIDER_markers.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) {
                    return "No Markers";
                }
                return FirebaseDB.IntensityEnum.fromInteger((int)value-1).toString();
            }
        });

        map_SLIDER_markers.addOnChangeListener(new Slider.OnChangeListener() {

            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                updateMarkers((int)value);
            }
        });
        map_SLIDER_markers.setEnabled(false);
    }

    private void updateMarkers(int value) {
        if (m_activeMarkers.size() > 0) {
            removeActiveMarkers();
        }
        if (value == 0) {
            return;
        }
        for (int i=value-1; i <= FirebaseDB.IntensityEnum.HIGHEST.ordinal(); i++) {
            for (MarkerOptions marker : m_markers.get(FirebaseDB.IntensityEnum.fromInteger(i))) {
                m_activeMarkers.add(m_googleMap.addMarker(marker));
            }
        }
    }

    private void removeActiveMarkers() {
        for (Marker marker : m_activeMarkers) {
            marker.remove();
        }
        m_activeMarkers.clear();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(requireActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        findViews();
        initViews();

        m_tilesOverlay = new ArrayList<TileOverlay>();
        m_markers = new HashMap<FirebaseDB.IntensityEnum, ArrayList<MarkerOptions>>();
        m_activeMarkers = new ArrayList<Marker>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_googleMap = googleMap;
        m_googleMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
            return;
        }
        m_googleMap.setMyLocationEnabled(true);
        // Focus on current location.
        if (m_googleMap != null) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                m_googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13f));
                            }
                        }
                    });
        }
        setInfoWindowAdapter();
    }

    public void setInfoWindowAdapter() {
        m_googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    public void addHeatMapWeighted() {
        map_SLIDER_markers.setEnabled(true);
//        Gradient gradient = new Gradient(colors, startpoints);
        m_weightedLatLngs.clear();
        try {
            switch(dataType) {
                case Temperature:
                    // TODO get TEMP data from DB
                    FirebaseDB.CallBack_Temperature callBack_temperature = new FirebaseDB.CallBack_Temperature() {
                        @Override
                        public void dataReady(HashMap<FirebaseDB.IntensityEnum, ArrayList<WeightedLatLng>> weights, HashMap<FirebaseDB.IntensityEnum, ArrayList<MarkerOptions>> markers) {
                            try {
                                removeHeatMap();
                                removeActiveMarkers();
                            } catch (Exception ex) {}
//                            for(HashMap.Entry<FirebaseDB.IntensityEnum, ArrayList<WeightedLatLng>> entry : weights.entrySet())
//                            {
                            for (int i=FirebaseDB.IntensityEnum.LOW.ordinal(); i <= FirebaseDB.IntensityEnum.HIGHEST.ordinal(); i++) {
                                if (weights.get(FirebaseDB.IntensityEnum.fromInteger(i)).size() == 0) {
                                    continue; // Skip empty entries
                                }
                                Gradient gradient = new Gradient(Utils.translateIntensityToColors(FirebaseDB.IntensityEnum.fromInteger(i)), startpoints, 500);
                                HeatmapTileProvider provider = new HeatmapTileProvider.Builder().weightedData(weights.get(FirebaseDB.IntensityEnum.fromInteger(i))).radius(30).gradient(gradient).build();
                                m_tilesOverlay.add(m_googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider)));
                            }

                            m_markers = markers;
                            updateMarkers((int)map_SLIDER_markers.getValue());

//                            for (int i=FirebaseDB.IntensityEnum.LOW.ordinal(); i <= FirebaseDB.IntensityEnum.HIGHEST.ordinal(); i++) {
//                                for (MarkerOptions marker : markers.get(FirebaseDB.IntensityEnum.fromInteger(i))) {
//                                    Marker newMarker = m_googleMap.addMarker(marker);
//                                    newMarker.setVisible(false);
//                                    m_markers.add(newMarker);
//                                }
//                            }
                        }
                    };
                    new FirebaseDB().getData(callBack_temperature, DataType.Temperature);
                    break;
                case Pressure:
                    FirebaseDB.CallBack_Barometer callBack_barometer = new FirebaseDB.CallBack_Barometer() {
                        @Override
                        public void dataReady(HashMap<FirebaseDB.IntensityEnum, ArrayList<WeightedLatLng>> weights, HashMap<FirebaseDB.IntensityEnum, ArrayList<MarkerOptions>> markers) {
                            try {
                                removeActiveMarkers();
                                removeHeatMap();
                            } catch (Exception ex) {}

                            for (int i=FirebaseDB.IntensityEnum.LOW.ordinal(); i <= FirebaseDB.IntensityEnum.HIGHEST.ordinal(); i++) {
                                if (weights.get(FirebaseDB.IntensityEnum.fromInteger(i)).size() == 0) {
                                    continue; // Skip empty entries
                                }
                                Gradient gradient = new Gradient(Utils.translateIntensityToColors(FirebaseDB.IntensityEnum.fromInteger(i)), startpoints, 500);
                                HeatmapTileProvider provider = new HeatmapTileProvider.Builder().weightedData(weights.get(FirebaseDB.IntensityEnum.fromInteger(i))).radius(30).gradient(gradient).build();
                                m_tilesOverlay.add(m_googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider)));
                            }

                            m_markers = markers;
                            updateMarkers((int)map_SLIDER_markers.getValue());
                        }
                    };
                    new FirebaseDB().getData(callBack_barometer, DataType.Pressure);
                    break;
                case Air_Pollution:
                    FirebaseDB.CallBack_AirPollution callBack_airPollution = new FirebaseDB.CallBack_AirPollution() {
                        @Override
                        public void dataReady(HashMap<FirebaseDB.IntensityEnum, ArrayList<WeightedLatLng>> weights, HashMap<FirebaseDB.IntensityEnum, ArrayList<MarkerOptions>> markers) {
                            try {
                                removeActiveMarkers();
                                removeHeatMap();
                            } catch (Exception ex) {}
                            for (int i=FirebaseDB.IntensityEnum.LOW.ordinal(); i <= FirebaseDB.IntensityEnum.HIGHEST.ordinal(); i++) {
                                if (weights.get(FirebaseDB.IntensityEnum.fromInteger(i)).size() == 0) {
                                    continue; // Skip empty entries
                                }
                                Gradient gradient = new Gradient(Utils.translateIntensityToColors(FirebaseDB.IntensityEnum.fromInteger(i)), startpoints, 500);
                                HeatmapTileProvider provider = new HeatmapTileProvider.Builder().weightedData(weights.get(FirebaseDB.IntensityEnum.fromInteger(i))).radius(30).gradient(gradient).build();
                                m_tilesOverlay.add(m_googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider)));
                            }

                            m_markers = markers;
                            updateMarkers((int)map_SLIDER_markers.getValue());
                        }
                    };
                    new FirebaseDB().getData(callBack_airPollution, DataType.Air_Pollution);
                    break;
                case Humidity:
                    FirebaseDB.CallBack_Humidity callBack_humidity = new FirebaseDB.CallBack_Humidity() {
                        @Override
                        public void dataReady(HashMap<FirebaseDB.IntensityEnum, ArrayList<WeightedLatLng>> weights, HashMap<FirebaseDB.IntensityEnum, ArrayList<MarkerOptions>> markers) {
                            try {
                                removeActiveMarkers();
                                removeHeatMap();
                            } catch (Exception ex) {}
                            for (int i=FirebaseDB.IntensityEnum.LOW.ordinal(); i <= FirebaseDB.IntensityEnum.HIGHEST.ordinal(); i++) {
                                if (weights.get(FirebaseDB.IntensityEnum.fromInteger(i)).size() == 0) {
                                    continue; // Skip empty entries
                                }
                                Gradient gradient = new Gradient(Utils.translateIntensityToColors(FirebaseDB.IntensityEnum.fromInteger(i)), startpoints, 500);
                                HeatmapTileProvider provider = new HeatmapTileProvider.Builder().weightedData(weights.get(FirebaseDB.IntensityEnum.fromInteger(i))).radius(30).gradient(gradient).build();
                                m_tilesOverlay.add(m_googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider)));
                            }

                            m_markers = markers;
                            updateMarkers((int)map_SLIDER_markers.getValue());
                        }
                    };
                    new FirebaseDB().getData(callBack_humidity, DataType.Humidity);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Unable to read heatmap locations from database!", Toast.LENGTH_LONG).show();
        }
    }

    private void removeHeatMap() {
        for (TileOverlay overlay : m_tilesOverlay) {
            overlay.remove();
        }
        m_tilesOverlay.clear();
    }

    private void showSnackbarHeatMapLoaded(int id) {
        if (toastMessage != null) {
            toastMessage.cancel();
        }
        toastMessage = Toast.makeText(getActivity(), getText(id) + " HeatMap loaded!", Toast.LENGTH_LONG);
        toastMessage.show();
    }

    private void showSnackbarHeatMapAlreadyLoaded(int id) {
        if (toastMessage != null) {
            toastMessage.cancel();
        }
        toastMessage = Toast.makeText(getActivity(), getText(id) + " HeatMap already loaded!", Toast.LENGTH_LONG);
        toastMessage.show();
    }
}