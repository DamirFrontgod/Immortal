package ru.parallels.mipt.immortal;

/**
 * Created by Damir Salakhutdinov on 12.11.2017.
 */

import android.Manifest.permission;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import java.util.Date;

public class LocationTracker {
	private LocationManager locationManager;
	private Service parent;
	private boolean gpsEnabled = false;
	private boolean networkEnabled = false;
	private Location gpsLocation = null;
	private Location networkLocation = null;
	private static final String TAG = "MyTag";

	public Location getGpsLocation() {
		return gpsLocation;
	}

	public Location getNetworkLocation() {
		return networkLocation;
	}

	public LocationTracker(final Service patent) {
		this.parent = patent;
		locationManager = (LocationManager) patent.getSystemService(patent.LOCATION_SERVICE);
	}

	public void requestLocationUpdates(final int interval) {
		if (ActivityCompat.checkSelfPermission(parent, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			throw new UnsupportedOperationException("Permissions are not granted");
		}

		if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, interval, 0,
					locationListener);
		if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, interval , 0,
					locationListener);
	}

	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(final Location location) {
			checkEnabled();
			changeLocation(location);
		}

		@Override
		public void onProviderDisabled(final String provider) {
			checkEnabled();
		}

		@Override
		public void onProviderEnabled(final String provider) {
			checkEnabled();

			if (ActivityCompat.checkSelfPermission(parent, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				throw new UnsupportedOperationException("Permissions are not granted");
			}
			changeLocation(locationManager.getLastKnownLocation(provider));
		}

		@Override
		public void onStatusChanged(final String provider, final int status, final Bundle extras) {}
	};

	boolean isGpsEnabled() {
		return gpsEnabled;
	}

	boolean isNetworkEnabled() {
		return networkEnabled;
	}

	private void changeLocation(final Location location) {
		if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
			gpsLocation = location;
		} else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
			networkLocation = location;
		}
	}

	public String formatLocation(final Location location) {
		if (location == null)
			return "unknown";
		return String.format(java.util.Locale.ENGLISH,
				"%.4f, %.4f, %3$tF %3$tT",
				location.getLatitude(), location.getLongitude(), new Date(
						location.getTime()));
	}

	private void checkEnabled() {
		gpsEnabled = locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
		networkEnabled = locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
	}

	public void removeUpdates() {
		locationManager.removeUpdates(locationListener);
	}
}


