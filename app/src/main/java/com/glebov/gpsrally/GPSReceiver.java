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
    public interface GPSUI {
        void onFixChanged(boolean fixed);

        void onDeviceStatusChanged(int state);

        void onSatelitesChanged(int all, int good);

        void onLocation(Location location);
    }

    private final Context mContext;
    private LocationManager locationManager;

    private GPSUI ui;
    private boolean hasFix = false;

    public GPSReceiver(Context context) {
        mContext = context;
    }

    @SuppressWarnings("ResourceType")// defined in manifest
    public void start(GPSUI _ui) throws IllegalArgumentException {
        if (_ui == null)
            throw new IllegalArgumentException("_ui can't be null");

        ui = _ui;
        hasFix = false;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.addGpsStatusListener(this);
    }

    @SuppressWarnings("ResourceType")
    public void stop() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager.removeGpsStatusListener(this);
        }
        ui = null;
    }

    @Override
    public void onGpsStatusChanged(int event) {
        if (locationManager == null) {
            return;
        }
        switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                hasFix = true;
                if (ui != null)
                    ui.onFixChanged(true);
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                int all = 0;
                int good = 0;
                Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
                for (GpsSatellite ignored : satellites) {
                    all++;
                    if (ignored.usedInFix())
                        good++;
                }
                if (ui != null)
                    ui.onSatelitesChanged(all, good);
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!hasFix) {
            hasFix = true;
            if (ui != null)
                ui.onFixChanged(true);
        }
        if (ui != null)
            ui.onLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (ui != null)
            ui.onDeviceStatusChanged(status);
        switch (status) {
            case LocationProvider.AVAILABLE:
                if (!hasFix && (ui != null))
                    ui.onFixChanged(true);
                hasFix = true;
                break;
            case LocationProvider.OUT_OF_SERVICE:
                if (hasFix && (ui != null))
                    ui.onFixChanged(false);
                hasFix = false;
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                if (hasFix && (ui != null))
                    ui.onFixChanged(false);
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
        if (ui != null)
            ui.onFixChanged(false);
    }

    @SuppressWarnings("ResourceType")
    public Location getLastLocation() {
        if (locationManager != null) {
            return locationManager.getLastKnownLocation("gps");
        }
        return null;
    }
}
