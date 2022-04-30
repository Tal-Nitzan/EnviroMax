package com.example.enviromax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.os.Build;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
@SuppressLint("UseSwitchCompatOrMaterialCode")
public class Fragment_Map extends androidx.fragment.app.Fragment implements OnMapReadyCallback {

    private GoogleMap m_googleMap;
    private HeatmapTileProvider m_mapProvider;
    private float previousZoomLevel = -1.0f;
    private FusedLocationProviderClient fusedLocationClient;
    private DataType dataType;
    private FloatingActionButton currentButton;
    private ArrayList<TileOverlay> m_tilesOverlay;
    private ArrayList<MarkerOptions> m_markers;
    private final ArrayList<Marker> m_activeMarkers = new ArrayList<Marker>();
    private FloatingActionButton map_BTN_temp;
    private FloatingActionButton map_BTN_barPressure;
    private FloatingActionButton map_BTN_airPollution;
    private FloatingActionButton map_BTN_Humidity;
    private MaterialButton map_BTN_left;
    private MaterialButton map_BTN_right;
    private TextView map_LBL_date;
    private int currentDate = 0;

    private Switch map_SWITCH_markers;
    private Toast toastMessage;

    private final int[] colors = {
            MyColor.GREEN,
            MyColor.YELLOW,
            MyColor.Orange,
            Color.RED,
            MyColor.DarkOrchid,
            MyColor.Brown
    };

    private final float[] startpoints = {
            0.1F, 0.2F, 0.3F, 0.4F, 0.6F, 1.0F
    };

    private void findViews() {
        map_BTN_temp = (FloatingActionButton) getView().findViewById(R.id.map_BTN_temp);
        map_BTN_barPressure = (FloatingActionButton) getView().findViewById(R.id.map_BTN_barPressure);
        map_BTN_airPollution = (FloatingActionButton) getView().findViewById(R.id.map_BTN_airPollution);
        map_BTN_Humidity = (FloatingActionButton) getView().findViewById(R.id.map_BTN_Humidity);
        map_SWITCH_markers = (Switch) getView().findViewById(R.id.map_SWITCH_markers);
        map_LBL_date = (TextView) getView().findViewById(R.id.map_LBL_date);
        map_BTN_left = (MaterialButton) getView().findViewById(R.id.map_BTN_left);
        map_BTN_right = (MaterialButton) getView().findViewById(R.id.map_BTN_right);

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

        map_BTN_left.setEnabled(false);

        map_BTN_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentDate--;
                leftRightButtonsEnabling();
                addHeatMapWeighted();
            }
        });

        map_BTN_right.setEnabled(false);

        map_BTN_right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentDate++;
                leftRightButtonsEnabling();
                addHeatMapWeighted();
            }
        });

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
                            m_googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13f));
                        }
                    });
        }
        setInfoWindowAdapter();
        m_googleMap.setOnCameraChangeListener(getCameraChangeListener());
    }

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
        leftRightButtonsEnabling();
        map_SWITCH_markers.setEnabled(true);
        updateDateLabel();
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
                m_mapProvider = new HeatmapTileProvider.Builder().weightedData(weightedLatLngsArray).gradient(gradient).build();
                changeRadiusBasedOnZoomLevel(m_googleMap.getCameraPosition().zoom);
                m_tilesOverlay.add(m_googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(m_mapProvider)));

                m_markers = markers;
                initMarkers();
                if (map_SWITCH_markers.isChecked()) {
                    showMarkers();
                }
            }
        };
        try {
            new FirebaseDB(getActivity()).getData(callBack, dataType, currentDate);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Unable to read heatmap locations from database!", Toast.LENGTH_LONG).show();
        }
    }

    //TODO
    public GoogleMap.OnCameraChangeListener getCameraChangeListener()
    {
        return new GoogleMap.OnCameraChangeListener()
        {
            @Override
            public void onCameraChange(CameraPosition position)
            {
                if(previousZoomLevel != position.zoom)
                { // min zoom = 3, max zoom = 21
                    changeRadiusBasedOnZoomLevel(position.zoom);
                }

                previousZoomLevel = position.zoom;
            }
        };
    }

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

    private void leftRightButtonsEnabling() {
        if (currentDate > 0)
            map_BTN_left.setEnabled(true);
        if (currentDate == 0)
            map_BTN_left.setEnabled(false);
        int NUM_HOURS = 72;
        if (currentDate < NUM_HOURS -1)
            map_BTN_right.setEnabled(true);
        if (currentDate == NUM_HOURS -1)
            map_BTN_right.setEnabled(false);
    }

    private void updateDateLabel()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:00");
        LocalDateTime now = LocalDateTime.now().minusHours(currentDate);
        map_LBL_date.setText(dtf.format(now));
    }

    private void removeHeatMap() {
        for (TileOverlay overlay : m_tilesOverlay) {
            overlay.remove();
        }
        m_tilesOverlay.clear();
    }

    private void showSnackbarHeatMapLoaded(int id) {
        showSnackbar(id, " HeatMap loaded!");
    }

    private void showSnackbarHeatMapAlreadyLoaded(int id) {
        showSnackbar(id, " HeatMap already loaded!");
    }

    private void showSnackbar(int id, String addedMessage) {
        if (toastMessage != null) {
            toastMessage.cancel();
        }
        toastMessage = Toast.makeText(getActivity(), getText(id) + addedMessage, Toast.LENGTH_LONG);
        toastMessage.show();
    }
}