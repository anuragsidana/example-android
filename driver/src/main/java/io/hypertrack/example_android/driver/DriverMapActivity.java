package io.hypertrack.example_android.driver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
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

import io.hypertrack.lib.transmitter.model.TransmitterConstants;
import io.hypertrack.lib.transmitter.service.HTTransmitterService;

/**
 * Created by piyush on 04/11/16.
 */
public class DriverMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = DriverMapActivity.class.getSimpleName();
    private GoogleMap mMap;
    private Marker driverLocationMarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        // Initialize Activity Toolbar
        initToolbar(getString(R.string.driver_map_screen_title), true);

        // Initialize Map Fragment added in Activity Layout to getMapAsync
        // Once map is created onMapReady callback will be fire with GoogleMap object
        MapFragment mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(driverCurrentLocationReceiver,
                new IntentFilter(TransmitterConstants.HT_DRIVER_CURRENT_LOCATION_INTENT));
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(driverCurrentLocationReceiver);
    }

    BroadcastReceiver driverCurrentLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {
                Log.d(TAG, "Driver's Current Location Changed");

                Bundle bundle = intent.getExtras();
                Location location = bundle.getParcelable(TransmitterConstants.HT_DRIVER_CURRENT_LOCATION_KEY);
                updateDriverLocation(location);
            }
        }
    };

    private void updateDriverLocation(Location location) {
        // Disable Google Map's currentLocation as Driver's Location is available
        enableCurrentLocationOnMap(false);

        if (mMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            // Add currentLocation Marker, if not present
            if (driverLocationMarker == null) {
                driverLocationMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver_marker)));
            } else {
                // Update currentLocation Marker's position
                driverLocationMarker.setPosition(latLng);
            }

            // Animate Google Map to updated driver location
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.0f);
            mMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Enable CurrentLocation if Driver is INACTIVE
        HTTransmitterService transmitterService = HTTransmitterService.getInstance(getApplicationContext());
        if (!transmitterService.isDriverLive()) {
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
