package com.example.enviromax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
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
    private final boolean firstLocationUpdateFlag = false;
    private FusedLocationProviderClient fusedLocationClient;
    private DataType dataType;
    private FloatingActionButton currentButton;
    private ArrayList<TileOverlay> m_tilesOverlay;
    private ArrayList<MarkerOptions> m_markers;
    private final ArrayList<Marker> m_activeMarkers = new ArrayList<Marker>();
    private final List<WeightedLatLng> m_weightedLatLngs = new ArrayList<WeightedLatLng>();
    private FloatingActionButton map_BTN_temp;
    private FloatingActionButton map_BTN_barPressure;
    private FloatingActionButton map_BTN_airPollution;
    private FloatingActionButton map_BTN_Humidity;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch map_SWITCH_markers;
    private Slider map_SLIDER_daySlider;
    private Toast toastMessage;
    private final int[] colors = {
            Color.GREEN,    // green(0-50)
            Color.YELLOW,    // yellow(51-100)
            Color.rgb(255,165,0), //Orange(101-150)
            Color.RED,              //red(151-200)
            Color.rgb(153,50,204), //dark orchid(201-300)
            Color.rgb(165,42,42) //brown(301-500)
    };
    private final float[] startpoints = {
            0.1F, 0.2F, 0.3F, 0.4F, 0.6F, 1.0F
    };

    private void findViews() {
        map_BTN_temp = (FloatingActionButton) getView().findViewById(R.id.map_BTN_temp);
        map_BTN_barPressure = (FloatingActionButton) getView().findViewById(R.id.map_BTN_barPressure);
        map_BTN_airPollution = (FloatingActionButton) getView().findViewById(R.id.map_BTN_airPollution);
        map_BTN_Humidity = (FloatingActionButton) getView().findViewById(R.id.map_BTN_Humidity);
        map_SLIDER_daySlider = (Slider) getView().findViewById(R.id.map_SLIDER_daySlider);
        map_SWITCH_markers = (Switch) getView().findViewById(R.id.map_SWITCH_markers);
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
        m_markers = new ArrayList<MarkerOptions>();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void initViews() {
        map_BTN_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnClickForBtn(DataType.Temperature, R.string.Temperature, map_BTN_temp);
            }
        });

        map_BTN_barPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnClickForBtn(DataType.Pressure, R.string.Barometer_Pressure, map_BTN_barPressure);
            }
        });

        map_BTN_airPollution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnClickForBtn(DataType.Air_Pollution, R.string.Air_Pollution, map_BTN_airPollution);
            }
        });

        map_BTN_Humidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnClickForBtn(DataType.Humidity, R.string.Humidity, map_BTN_Humidity);
            }
        });


//        map_SLIDER_markers.setLabelFormatter(new LabelFormatter() { TODO: get dates from DB
//            @NonNull
//            @Override
//            public String getFormattedValue(float value) {
//                if (value == 0) {
//                    return "No Markers";
//                }
//                return FirebaseDB.IntensityEnum.fromInteger((int)value-1).toString();
//            }
//        });

        map_SLIDER_daySlider.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                // TODO : implement
            }
        });
        map_SLIDER_daySlider.setEnabled(false);

        map_SWITCH_markers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showMarkers();
                } else {
                    setActiveMarkersInvisible();
                }
            }
        });

        map_SWITCH_markers.setEnabled(false);
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

    public void setOnClickForBtn(DataType type, int id, FloatingActionButton button) {
        if (dataType == type) {
            showSnackbarHeatMapAlreadyLoaded(id);
            return;
        }
        else if (dataType != null) { // It is something else, should remove the current tile.
            removeHeatMap();
            removeMarkers();
            currentButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.teal_200, getContext().getTheme())));
        }
        showSnackbarHeatMapLoaded(id);
        currentButton = button;
        currentButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple_200, getContext().getTheme())));
        dataType = type;
        addHeatMapWeighted();
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



//    private void updateMarkers(int value) {
//        if (m_activeMarkers.size() > 0) {
//            setActiveMarkersInvisible();
//        }
//        if (value == 0) {
//            return;
//        }
//        for (int i = value - 1; i <= FirebaseDB.IntensityEnum.HIGHEST.ordinal(); i++) {
//            for (Marker marker : m_activeMarkers.get(FirebaseDB.IntensityEnum.fromInteger(i))) {
//                Log.d("vvvv", "...");
//                marker.setVisible(true);
//            }
//        }
//    }

    private void showMarkers() {
        for (Marker marker : m_activeMarkers) {
            marker.setVisible(true);
        }
    }


    private void setActiveMarkersInvisible() {
        for (Marker marker : m_activeMarkers) {
            marker.setVisible(false);
        }
    }

    private void initMarkers() {
        for (MarkerOptions _marker : m_markers) {
            Marker marker = m_googleMap.addMarker(_marker);
            marker.setVisible(false);
            m_activeMarkers.add(marker);
        }
    }

    private void removeMarkers() {
        for (Marker marker : m_activeMarkers) {
            marker.remove();
        }
        m_activeMarkers.clear();
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
        boolean isHeatMapLoaded = true;
        map_SLIDER_daySlider.setEnabled(true);
        map_SWITCH_markers.setEnabled(true);
        m_weightedLatLngs.clear();
        FirebaseDB.CallBack_Data callBack = new FirebaseDB.CallBack_Data() {
            @Override
            public void dataReady(ArrayList<WeightedLatLngAddress> weights, ArrayList<MarkerOptions> markers) {
                try {
                    removeHeatMap();
                    removeMarkers();
                } catch (Exception ex) { Toast.makeText(getActivity(), "Unable to remove HeatMap and Markers", Toast.LENGTH_LONG).show(); }
                Gradient gradient = new Gradient(colors, startpoints, 1000);
                ArrayList<WeightedLatLng> weightedLatLngsArray = new ArrayList<WeightedLatLng>();
                for (WeightedLatLngAddress address : weights) {
                    weightedLatLngsArray.add(address.getWeightedLatLng());
                }
                HeatmapTileProvider provider = new HeatmapTileProvider.Builder().weightedData(weightedLatLngsArray).radius(30).gradient(gradient).build();
                m_tilesOverlay.add(m_googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider)));

                m_markers = markers;
                initMarkers();
                if (map_SWITCH_markers.isChecked()) {
                    showMarkers();
                }
            }
        };
        try {
            new FirebaseDB(getActivity()).getData(callBack, dataType);
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