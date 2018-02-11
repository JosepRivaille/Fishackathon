package com.fishhackathon.hackathon.fishhackathon;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.fishhackathon.hackathon.fishhackathon.controllers.APIController;
import com.fishhackathon.hackathon.fishhackathon.controllers.VolleyController;
import com.fishhackathon.hackathon.fishhackathon.models.Law;
import com.fishhackathon.hackathon.fishhackathon.views.CustomInfoWindow;
import com.fishhackathon.hackathon.fishhackathon.models.MapGeoPoint;
import com.fishhackathon.hackathon.fishhackathon.models.MapPolygon;
import com.fishhackathon.hackathon.fishhackathon.models.Zone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.TilesOverlay;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {
    public static final String TAG = MapFragment.class.getSimpleName();
    private View rootView;
    private TextView zoomInView;
    private TextView zoomOutView;
    private TextView legendView;
    private MapView osmMap;

    private Location currentLocation;
    private ArrayList<Zone> zones;

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

        if (((MainActivity) getActivity()).existsLastLocation()) {
            updateLatestLocation(((MainActivity) getActivity()).getLastLocation());
        }
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

    public void updateMapInformation(String fromDate, String toDate) {
        updateMapInformationPrivate(currentLocation, fromDate, toDate);
    }

    public void updateMapInformation() {
        updateMapInformationPrivate(currentLocation, null, null);
    }

    public void updateMapInformation(Location location) {
        updateMapInformationPrivate(location, null, null);
    }

    private void updateMapInformationPrivate(Location location, String fromDate, String toDate) {
        zones = new ArrayList<>();

        String url = APIController.getURLForNearZones(location.getLatitude(), location.getLongitude(), fromDate, toDate);
        Log.e(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude()
                + ", from: " + fromDate + ", to: " + toDate);
        Log.e(TAG, url);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, response.toString());
                        for (int i = 0; i < response.length(); ++i) {
                            try {
                                JSONObject zoneJson = response.getJSONObject(i);

                                String zoneId = zoneJson.getString("id");
                                //ArrayList<Law> laws = zoneJson.getString("laws");
                                ArrayList<Law> laws = new ArrayList<>();
                                String code = zoneJson.getString("code");
                                String level = zoneJson.getString("level");
                                String ocean = zoneJson.getString("ocean");
                                JSONArray allPolygonsJsonArray = zoneJson.getJSONArray("polygon");
                                ArrayList<MapPolygon> mapPolygons = new ArrayList<>();
                                for (int j = 0; j < allPolygonsJsonArray.length(); ++j) {
                                    JSONArray polygonJsonArray = allPolygonsJsonArray.getJSONArray(j);

                                    ArrayList<MapGeoPoint> mapGeoPoints = new ArrayList<>();
                                    for (int k = 0; k < polygonJsonArray.length(); ++k) {
                                        JSONObject pointJson = polygonJsonArray.getJSONObject(k);
                                        double pointLat = pointJson.getDouble("lat");
                                        double pointLng = pointJson.getDouble("lng");
                                        mapGeoPoints.add(new MapGeoPoint(pointLat, pointLng));
                                    }
                                    mapPolygons.add(new MapPolygon(mapGeoPoints));
                                }

                                zones.add(new Zone(zoneId, code, laws, level, ocean, mapPolygons));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (zones != null) {
                            osmMap.getOverlays().clear();

                            for (int i = 0; i < zones.size(); ++i) {
                                Zone currentZone = zones.get(i);
                                if (currentZone.getCode().contains(".")) {
                                    //continue;
                                }
                                for (int j = 0; j < currentZone.getPolygons().size(); ++j) {
                                    MapPolygon currentMapPolygon = currentZone.getPolygons().get(j);

                                    Polygon polygon = new Polygon();
                                    polygon.setTitle("Zona: " + currentZone.getCode());
                                    polygon.setSubDescription("Level: " + currentZone.getLevel() + ", Ocean: " + currentZone.getOcean());
                                    polygon.setVisible(true);
                                    polygon.setFillColor(Color.parseColor("#82e53935"));
                                    polygon.setStrokeColor(Color.RED);
                                    polygon.setStrokeWidth(10);
                                    polygon.setInfoWindow(new CustomInfoWindow(getContext(), osmMap, currentZone));
                                    List<GeoPoint> geoPoints = new ArrayList<>();
                                    for (int k = 0; k < currentMapPolygon.getPointsArrayList().size(); ++k) {
                                        MapGeoPoint mapGeoPoint = currentMapPolygon.getPointsArrayList().get(k);
                                        geoPoints.add(new GeoPoint(mapGeoPoint.getLat(), mapGeoPoint.getLng()));
                                    }
                                    polygon.setPoints(geoPoints);
                                    osmMap.getOverlayManager().add(polygon);
                                }
                            }
                            addMarker(new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            osmMap.invalidate();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "ErrorResponse: " + error.getLocalizedMessage());
            }
        });
        VolleyController.getInstance(getContext()).addToQueue(jsonArrayRequest);
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
        if (getContext() != null) {
            marker.setIcon(getContext().getDrawable(R.drawable.map_marker));
        }
        //marker.setImage(drawable);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Current Location");
        //marker.showInfoWindow();
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
        if (currentLocation == null) {
            //Geopoint located in current user position
            GeoPoint geoPointCenter = new GeoPoint(location.getLatitude(), location.getLongitude());
            addMarker(geoPointCenter);
            centerMap(geoPointCenter);
        }

        currentLocation = location;
    }
}