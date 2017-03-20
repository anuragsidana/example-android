package com.hypertrack.example_android;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.HyperTrackConstants;
import com.hypertrack.lib.internal.transmitter.models.HyperTrackLocation;

/**
 * Created by piyush on 04/11/16.
 */
public class UserMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = UserMapActivity.class.getSimpleName();
    private GoogleMap mMap;
    private Marker userLocationMarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);

        // Initialize Activity Toolbar
        initToolbar(getString(R.string.user_map_screen_title), true);

        // Initialize Map Fragment added in Activity Layout to getMapAsync
        // Once map is created onMapReady callback will be fire with GoogleMap object
        MapFragment mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(userCurrentLocationReceiver,
                new IntentFilter(HyperTrackConstants.HT_USER_CURRENT_LOCATION_INTENT));
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(userCurrentLocationReceiver);
    }

    BroadcastReceiver userCurrentLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {
                Log.d(TAG, "User's Current Location Changed");

                HyperTrackLocation location = (HyperTrackLocation) intent.getSerializableExtra(
                        HyperTrackConstants.HT_USER_CURRENT_LOCATION_KEY);
                updateUserLocation(location);
            }
        }
    };

    private void updateUserLocation(HyperTrackLocation location) {
        // Disable Google Map's currentLocation as User's Location is available
        enableCurrentLocationOnMap(false);

        if (mMap != null) {
            LatLng latLng = location.getGeoJSONLocation().getLatLng();

            // Add currentLocation Marker, if not present
            if (userLocationMarker == null) {
                userLocationMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_marker)));
            } else {
                // Update currentLocation Marker's position
                userLocationMarker.setPosition(latLng);
            }

            // Animate Google Map to updated user location
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.0f);
            mMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Enable CurrentLocation if User is INACTIVE
        if (!HyperTrack.isTracking()) {
            enableCurrentLocationOnMap(true);
        }
    }

    private void enableCurrentLocationOnMap(boolean enable) {
        if (mMap == null || enable == mMap.isMyLocationEnabled())
            return;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        mMap.setMyLocationEnabled(enable);
        mMap.getUiSettings().setMyLocationButtonEnabled(enable);
    }

    private void initToolbar(String title, boolean homeButtonEnabled) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null)
            return;

        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() == null)
            return;

        getSupportActionBar().setDisplayHomeAsUpEnabled(homeButtonEnabled);
        getSupportActionBar().setHomeButtonEnabled(homeButtonEnabled);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
