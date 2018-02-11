package com.fishhackathon.hackathon.fishhackathon;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fishhackathon.hackathon.fishhackathon.controllers.APIController;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RESULT_FOR_FILTER = 288;
    private BottomNavigationView navigation;
    private Location lastLocation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            String tag = null;
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    selectedFragment = MapFragment.newInstance();
                    tag = MapFragment.TAG;
                    break;
                case R.id.navigation_dashboard:
                    selectedFragment = ARFragment.newInstance();
                    tag = ARFragment.TAG;
                    break;
                case R.id.navigation_profile:
                    selectedFragment = ProfileFragment.newInstance();
                    tag = ProfileFragment.TAG;
                    break;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, tag).commit();
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.navigation_map);

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    getLocation();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
            }
        }).check();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                startActivityForResult(new Intent(MainActivity.this, FilterActivity.class), RESULT_FOR_FILTER);
                break;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        SmartLocation.with(getApplicationContext()).location().stop();
    }

    private void getLocation() {
        SmartLocation.with(getApplicationContext()).location()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        Log.e(TAG, "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
                        lastLocation = location;

                        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(MapFragment.TAG);
                        if (mapFragment != null) {
                            mapFragment.updateLatestLocation(location);
                            mapFragment.updateMapInformation(location);
                        }

                        ARFragment arFragment = (ARFragment) getSupportFragmentManager().findFragmentByTag(ARFragment.TAG);
                        if (arFragment != null) {
                            arFragment.updateLatestLocation(location);
                        }
                    }
                });
    }

    public boolean existsLastLocation() {
        return lastLocation != null;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == RESULT_FOR_FILTER) {
            Bundle bundle = data.getExtras();
            assert bundle != null;
            String fromDate = bundle.getString(FilterActivity.KEY_DATE_FROM);
            String toDate = bundle.getString(FilterActivity.KEY_DATE_TO);

            MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(MapFragment.TAG);
            if (mapFragment != null) {
                mapFragment.updateMapInformation(fromDate, toDate);
            }
        }
    }
}
