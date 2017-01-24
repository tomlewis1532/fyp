package com.uob.tomoslewis.spotshare;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = MapsActivity.class.getSimpleName();

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap gMap; //Map received from Google Services API.

    private GoogleApiClient gClient;
    private LocationRequest lReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        gClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        lReq = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        gClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (gClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(gClient, this);
            gClient.disconnect();
        }
    }

    private void setUpMapIfNeeded() {
        if (gMap == null) {
            gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (gMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        LatLng latLng = new LatLng(52.482961, -1.893592);

        gMap.addMarker(new MarkerOptions()
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.direction_arrow))
                .position(latLng)
                .title("Marker"));

        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        MarkerOptions options = new MarkerOptions()
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.direction_arrow))
                .position(latLng)
                .title("I am here!");
        gMap.addMarker(options);

        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(gClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(gClient, lReq, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }
}
