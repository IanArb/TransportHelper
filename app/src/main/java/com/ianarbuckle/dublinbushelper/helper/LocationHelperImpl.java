package com.ianarbuckle.dublinbushelper.helper;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.ianarbuckle.dublinbushelper.R;
import com.ianarbuckle.dublinbushelper.utils.Constants;
import com.ianarbuckle.dublinbushelper.utils.PermissionsManager;

/**
 * Created by Ian Arbuckle on 19/02/2017.
 *
 */

public class LocationHelperImpl implements LocationHelper, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

  private Context context;
  private GoogleMap map;
  private GoogleApiClient googleApiClient;
  LocationRequest locationRequest;
  Location lastLocation;
  private Marker currentLocation;

  public LocationHelperImpl(Context context) {
    this.context = context;
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public boolean checkLocationPermission(Fragment fragment) {
    String[] accessPermissions = {Manifest.permission.ACCESS_FINE_LOCATION};
    if(PermissionsManager.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
      PermissionsManager.requestPermissions(fragment, Constants.PERMISSION_REQUEST_ACCESS_LOCATION, accessPermissions);
      return false;
    } else {
      return true;
    }
  }

  @Override
  public void initMap(GoogleMap googleMap) {
    map = googleMap;

    if (isLocationPermissionGranted()) {
      buildGoogleApiClient();
      map.setMyLocationEnabled(true);
    } else {
      if(isLocationPermissionGranted()) {
        buildGoogleApiClient();
        map.setMyLocationEnabled(true);
      }
    }
  }

  protected synchronized void buildGoogleApiClient() {
    googleApiClient = new GoogleApiClient.Builder(context)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();

    googleApiClient.connect();
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    locationRequest = new LocationRequest();
    locationRequest.setInterval(1000);
    locationRequest.setFastestInterval(1000);
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    if (isLocationPermissionGranted()) {
      LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }
  }

  private boolean isLocationPermissionGranted() {
    return PermissionsManager.isLocationPermissionGranted(context);
  }

  @Override
  public void onConnectionSuspended(int interval) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  @Override
  public void onLocationChanged(Location location) {
    lastLocation = location;
    if (currentLocation != null) {
      currentLocation.remove();
    }

    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(latLng);
    currentLocation = map.addMarker(markerOptions);

    IconGenerator iconGenerator = new IconGenerator(context);

    iconGenerator.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_location_on));

    BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon());
    currentLocation.setIcon(icon);

    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
    map.animateCamera(CameraUpdateFactory.zoomTo(16));

    if (googleApiClient != null) {
      LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }
  }

  @Override
  public void onRequestPermission() {
    if (googleApiClient == null) {
      buildGoogleApiClient();
    }
    if (isLocationPermissionGranted()) {
      map.setMyLocationEnabled(true);
    }
  }
}