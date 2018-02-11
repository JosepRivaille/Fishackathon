package com.fishhackathon.hackathon.fishhackathon;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.TilesOverlay;

public class MapFragment extends Fragment {
    public static final String TAG = MapFragment.class.getSimpleName();
    private View rootView;
    private TextView zoomInView;
    private TextView zoomOutView;
    private TextView legendView;
    private MapView osmMap;

    private Location currentLocation;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();

        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        setUpElements();
        setUpListeners();

        osmMap.setMultiTouchControls(true);
        addOverlay();

        return rootView;
    }

    private void showLegend() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(getLayoutInflater().inflate(R.layout.map_legend_alert, null));
        dialogBuilder.create();
        dialogBuilder.show();
    }

    private void addMarker(GeoPoint geoPoint) {
        Marker marker = new Marker(osmMap);
        marker.setPosition(geoPoint);
        //marker.setIcon(drawable);
        //marker.setImage(drawable);
        marker.setTitle("Current Location");
        marker.showInfoWindow();
        osmMap.getOverlays().add(marker);
        osmMap.invalidate();
    }

    private void addOverlay() {
        MapTileProviderBasic mProvider = new MapTileProviderBasic(getContext());
        TilesOverlay seaMap = new TilesOverlay(mProvider, getContext());
        seaMap.setLoadingLineColor(Color.TRANSPARENT);
        seaMap.setLoadingBackgroundColor(Color.TRANSPARENT);
        seaMap.setLoadingDrawable(null);
        mProvider.setTileSource(TileSourceFactory.OPEN_SEAMAP);
        osmMap.getOverlays().add(seaMap);
        osmMap.postInvalidate();
    }

    private void centerMap(GeoPoint geoPoint) {
        IMapController mapController = osmMap.getController();
        mapController.setZoom(15);
        mapController.setCenter(geoPoint);
    }

    private void setUpElements() {
        osmMap = rootView.findViewById(R.id.osmap);
        zoomInView = rootView.findViewById(R.id.home_zoom_in);
        zoomOutView = rootView.findViewById(R.id.home_zoom_out);
        legendView = rootView.findViewById(R.id.home_legend);
    }

    private void setUpListeners() {
        zoomInView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (osmMap != null && osmMap.canZoomIn()) {
                    osmMap.getController().setZoom(osmMap.getZoomLevel() + 1);
                }
            }
        });

        zoomOutView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (osmMap != null && osmMap.canZoomOut()) {
                    osmMap.getController().setZoom(osmMap.getZoomLevel() - 1);
                }
            }
        });

        legendView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showLegend();
            }
        });
    }

    public void updateLatestLocation(Location location) {
        currentLocation = location;

        //Geopoint located in current user position
        GeoPoint geoPointCenter = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
        addMarker(geoPointCenter);
        centerMap(geoPointCenter);
    }
}