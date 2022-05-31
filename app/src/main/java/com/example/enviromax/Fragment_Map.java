package com.example.enviromax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class Fragment_Map extends Fragment implements OnMapReadyCallback {

    // Consts
    private final int DECREASE = -1;
    private final int INCREASE = 1;

    private final int[] colors = {
            MyColor.GREEN,
            MyColor.YELLOW,
            MyColor.Orange,
            MyColor.RED,
            MyColor.DarkOrchid,
            MyColor.Brown
    };

    private final float[] startpoints = {
            0.1F, 0.2F, 0.3F, 0.4F, 0.6F, 1.0F
    };

    // Map vars
    private GoogleMap m_googleMap;
    private HeatmapTileProvider m_mapProvider;
    private FusedLocationProviderClient fusedLocationClient;
    private ArrayList<TileOverlay> m_tilesOverlay;
    private ArrayList<MarkerOptions> m_markers;
    private final ArrayList<Marker> m_activeMarkers = new ArrayList<Marker>();

    // Layout vars
    private FloatingActionButton map_BTN_temp;
    private FloatingActionButton map_BTN_barPressure;
    private FloatingActionButton map_BTN_airPollution;
    private FloatingActionButton map_BTN_Humidity;
    private MaterialButton map_BTN_left;
    private MaterialButton map_BTN_right;
    private TextView map_LBL_date;
    private TextView map_LBL_currentData;
    private Switch map_SWITCH_markers;

    // UI vars
    private Toast m_toastMessage;

    // State vars
    private float m_previousZoomLevel = -1.0f;
    private DataType m_dataType;
    private FloatingActionButton m_currentButton;
    private int m_currentDate = 0;

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

        m_tilesOverlay = new ArrayList<>();
        m_markers = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // Find all UI views
    private void findViews() {
        map_BTN_temp = getView().findViewById(R.id.map_BTN_temp);
        map_BTN_barPressure = getView().findViewById(R.id.map_BTN_barPressure);
        map_BTN_airPollution = getView().findViewById(R.id.map_BTN_airPollution);
        map_BTN_Humidity = getView().findViewById(R.id.map_BTN_Humidity);
        map_SWITCH_markers = getView().findViewById(R.id.map_SWITCH_markers);
        map_LBL_date = getView().findViewById(R.id.map_LBL_date);
        map_LBL_currentData = getView().findViewById(R.id.map_LBL_currentData);
        map_BTN_left = getView().findViewById(R.id.map_BTN_left);
        map_BTN_right = getView().findViewById(R.id.map_BTN_right);
    }

    // Init all UI views
    private void initViews() {
        map_BTN_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnClickForBtn(DataType.Temperature, map_BTN_temp);
            }
        });

        map_BTN_barPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnClickForBtn(DataType.Pressure, map_BTN_barPressure);
            }
        });

        map_BTN_airPollution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnClickForBtn(DataType.Air_Pollution, map_BTN_airPollution);
            }
        });

        map_BTN_Humidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnClickForBtn(DataType.Humidity, map_BTN_Humidity);
            }
        });


        shouldEnableMaterialButton(false, map_BTN_left, Color.LTGRAY);

        map_BTN_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeDate(DECREASE);
                leftRightButtonsEnablingManager();
                removeHeatMapMarkers();
                addHeatMapWeighted(false);
            }
        });

        shouldEnableMaterialButton(false, map_BTN_right, Color.LTGRAY);

        map_BTN_right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeDate(INCREASE);
                leftRightButtonsEnablingManager();
                removeHeatMapMarkers();
                addHeatMapWeighted(false);
            }
        });

        map_SWITCH_markers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showMarkers(true);
                } else {
                    showMarkers(false);
                }
            }
        });

        map_SWITCH_markers.setEnabled(false);
    }

    // Function helper for setting up different datatype buttons from UI
    public void setOnClickForBtn(DataType type, FloatingActionButton button) {
        Context context;
        Resources.Theme theme;
        if (m_dataType == type) {
            showToastHeatMapAlreadyLoaded(type);
            return;
        }
        else if (m_dataType != null) { // It is something else, should remove the current tile.
            removeHeatMapMarkers();
            context = getContext();
            if (context != null) {
                theme = context.getTheme();
                m_currentButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.teal_200, theme)));
            }
        }
        setCurrentDataType(type);
        setCurrentText();
        updateDateLabel();
        setCurrentButton(button);

        addHeatMapWeighted(true);

        if (getContext() != null) {
            if (getContext().getTheme() != null) {
                m_currentButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple_200, getContext().getTheme())));
            }
        }
    }

    private void setCurrentText() {
        map_LBL_currentData.setText(m_dataType.toString());
    }

    private void setCurrentButton(FloatingActionButton button) {
        m_currentButton = button;
    }

    private void setCurrentDataType(DataType type) {
        m_dataType = type;
    }

    // Function gets called when fetching data from database.
    // Inside - A callback is being registered to the FirebaseDb class to call
    // when the data is ready to get loaded to the heatmap
    private void addHeatMapWeighted(boolean shouldShowToastModeChanged) {
        leftRightButtonsEnablingManager();
        FirebaseDB.CallBack_Data callBack = new FirebaseDB.CallBack_Data() {
            @Override
            public void dataReady(ArrayList<WeightedLatLngAddress> weights, ArrayList<MarkerOptions> markers, boolean shouldShowToastModeChanged) {
                if (weights.size() == 0 && markers.size() == 0) {
                    setMarkers(false);
                    showToast("There are no available data for that date and time on the system.", Toast.LENGTH_SHORT);
                    return;
                }
                Gradient gradient = new Gradient(colors, startpoints, 1000);
                ArrayList<WeightedLatLng> weightedLatLngsArray = new ArrayList<WeightedLatLng>();
                for (WeightedLatLngAddress address : weights) {
                    weightedLatLngsArray.add(address.getWeightedLatLng());
                }
                m_mapProvider = new HeatmapTileProvider.Builder().weightedData(weightedLatLngsArray).gradient(gradient).build();
                changeRadiusBasedOnZoomLevel(m_googleMap.getCameraPosition().zoom);
                m_tilesOverlay.add(m_googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(m_mapProvider)));
                m_markers = markers;
                initMarkers();
                if (map_SWITCH_markers.isChecked()) {
                    showMarkers(true);
                }
                setMarkers(true);
                if (shouldShowToastModeChanged) {
                    showToastHeatMapLoaded(m_dataType);
                }
                else {
                    showToast("HeatMap loaded!", Toast.LENGTH_SHORT);
                }
            }
        };
        try {
            new FirebaseDB(getActivity()).getData(callBack, m_dataType, m_currentDate, shouldShowToastModeChanged);
        } catch (Exception e) {
            showToast("Unable to read heatmap locations from database!", Toast.LENGTH_LONG);
        }
    }

    // Set markers based on user choice from UI
    private void setMarkers(boolean shouldSet) {
        if (!shouldSet)
            removeMarkers();
        map_SWITCH_markers.setEnabled(shouldSet);
    }

    // Set m_googleMap member when map is ready (API function call)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_googleMap = googleMap;
        m_googleMap.getUiSettings().setZoomControlsEnabled(true);
        try {
            focusCurrentLocation();
        } catch (Exception ignored) { }
    }

    // Focus on current location after map is ready.
    @SuppressLint("MissingPermission")
    public void focusCurrentLocation() {
        m_googleMap.setMyLocationEnabled(true);
        // Focus on current location.
        if (m_googleMap != null) {
            FragmentActivity activity = getActivity();
            if (activity != null)
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                m_googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13f));
                            }
                        });
        }
        setInfoWindowAdapter();
        m_googleMap.setOnCameraChangeListener(getCameraChangeListener());
    }

    // Set markers visible/invisible
    private void showMarkers(boolean shouldShow) {
        for (Marker marker : m_activeMarkers) {
            marker.setVisible(shouldShow);
        }
    }

    // Init loaded markers to the heat map
    private void initMarkers() {
        for (MarkerOptions _marker : m_markers) {
            Marker marker = m_googleMap.addMarker(_marker);
            marker.setVisible(false);
            m_activeMarkers.add(marker);
        }
    }

    private void removeHeatMapMarkers() {
        try {
            removeHeatMap();
            removeMarkers();
        } catch (Exception ex) {
            showToast("Unable to remove HeatMap and Markers", Toast.LENGTH_SHORT);
        }
    }

    // Remove heatmap from tile overlay
    private void removeHeatMap() {
        for (TileOverlay overlay : m_tilesOverlay) {
            overlay.remove();
        }
        m_tilesOverlay.clear();
    }

    // Remove all active markers from the heatmap
    private void removeMarkers() {
        for (Marker marker : m_activeMarkers) {
            marker.remove();
        }
        m_activeMarkers.clear();
        m_markers.clear();
    }

    // Window adapter for google map UI
    private void setInfoWindowAdapter() {
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

    // Listener on the map zoom level to dynamically change the radius of the heatmap
    // points based on zoom level.
    private GoogleMap.OnCameraChangeListener getCameraChangeListener()
    {
        return new GoogleMap.OnCameraChangeListener()
        {
            @Override
            public void onCameraChange(CameraPosition position)
            {
                if(m_previousZoomLevel != position.zoom)
                { // min zoom = 3, max zoom = 21
                    changeRadiusBasedOnZoomLevel(position.zoom);
                }

                m_previousZoomLevel = position.zoom;
            }
        };
    }

    // Dynamically change radius of heatmap points based on user choosing of zoom level
    private void changeRadiusBasedOnZoomLevel(float zoom) {
        final int MIN_RADIUS = 5;
        final int MAX_RADIUS = 50;
        final int MIN_ZOOM = 3;
        final int MAX_ZOOM = 21;
        final int DIVIDER = 2;

        if (m_mapProvider != null)
            if (zoom >= 13f) {
                int newRadius = (MAX_RADIUS * ((int)zoom - MIN_ZOOM) / (MAX_ZOOM - MIN_ZOOM))/DIVIDER + MIN_RADIUS;
                m_mapProvider.setRadius(newRadius);
            }
            else {
                m_mapProvider.setRadius(MIN_RADIUS);
        }

    }

    private void leftRightButtonsEnablingManager() {
        if (m_currentDate > 0) {
            shouldEnableMaterialButton(true, map_BTN_left, Color.BLACK);
        }
        if (m_currentDate == 0) {
            shouldEnableMaterialButton(false, map_BTN_left, Color.LTGRAY);
        }
        int MAX_HOURS = 72;
        if (m_currentDate < MAX_HOURS -1) {
            shouldEnableMaterialButton(true, map_BTN_right, Color.BLACK);
        }
        if (m_currentDate == MAX_HOURS -1) {
            shouldEnableMaterialButton(false, map_BTN_right, Color.LTGRAY);
        }

    }

    // Generic class to enable/disable material button and set its color
    private void shouldEnableMaterialButton(boolean enable, MaterialButton button, int color) {
        button.setRippleColor(ColorStateList.valueOf(color));
        button.setStrokeColor(ColorStateList.valueOf(color));
        button.setTextColor(ColorStateList.valueOf(color));
        button.setEnabled(enable);
    }

    // Update date label by the user choosing.
    private void updateDateLabel()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:00");
        LocalDateTime now = LocalDateTime.now().minusHours(m_currentDate);
        map_LBL_date.setText(dtf.format(now));
    }

    // Toast helper wrapper
    private void showToastHeatMapLoaded(DataType type) {
        showToastForTypeName(type.toString(), " HeatMap successfully loaded!");
    }

    // Toast helper wrapper
    private void showToastHeatMapAlreadyLoaded(DataType type) {
        showToastForTypeName(type.toString(), " HeatMap already loaded!");
    }

    // Toast helper wrapper
    private void showToastForTypeName(String typeName, String addedMessage) {
        showToast(typeName + addedMessage, Toast.LENGTH_SHORT);
    }

    // Wrapper fucntion to show toasts of the current data loaded to the map.
    // We work with a single toast object so the current view will get run
    // over by the most current toast.
    private void showToast(String addedMessage, int length) {
        if (m_toastMessage != null) {
            m_toastMessage.cancel();
        }
        m_toastMessage = Toast.makeText(getActivity(), addedMessage, length);
        m_toastMessage.show();
    }

    // Change the date for the UI view of current date's data
    private void changeDate(int numHours) {
        m_currentDate += numHours;
        updateDateLabel();
    }
}