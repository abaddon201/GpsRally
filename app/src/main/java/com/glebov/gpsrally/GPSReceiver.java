package com.glebov.gpsrally;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

/**
 * Created by glebov on 03.11.15.
 */
public class GPSReceiver implements GpsStatus.Listener, LocationListener {
    private final Context mContext;
    private LocationManager locationManager;

    private boolean hasFix = false;

    public GPSReceiver(Context context) {
        mContext = context;
    }

    @SuppressWarnings("ResourceType")// defined in manifest
    public void start() {
        hasFix = false;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates("gps", 0, 0, this);
        locationManager.addGpsStatusListener(this);
    }

    @SuppressWarnings("ResourceType")
    public void stop() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager.removeGpsStatusListener(this);
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        if (locationManager == null) {
            return;
        }
        switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                hasFix = true;
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                int all = 0;
                Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
                for (GpsSatellite ignored : satellites) {
                    all++;
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        hasFix = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                hasFix = true;
                break;
            case LocationProvider.OUT_OF_SERVICE:
                hasFix = false;
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                hasFix = false;
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        // ignore, no information about fix
    }

    @Override
    public void onProviderDisabled(String provider) {
        hasFix = false;
    }

    @SuppressWarnings("ResourceType")
    public Location getLastLocation() {
        if (locationManager != null) {
            return locationManager.getLastKnownLocation("gps");
        }
        return null;
    }
}
