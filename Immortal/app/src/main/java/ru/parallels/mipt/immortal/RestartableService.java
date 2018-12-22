package ru.parallels.mipt.immortal;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Handler;
import 	android.app.admin.DeviceAdminService;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RestartableService extends Service {
	private int interval = 30000;
	private MailSender mailSender;
	private LocationTracker locationTracker;
	private Handler handler;
	private Runnable runnable;
	private static final String TAG = "MyTag";

	ArrayList<String> fname;

	public RestartableService() {}



	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TAG, "RestartableServiceOnCreate");
		mailSender = new MailSender();

		locationTracker = new LocationTracker(this);
		locationTracker.requestLocationUpdates(interval);

		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				try {
					mailSender.setSubject("Gps: " + locationTracker.isGpsEnabled() +
							" Net: " + locationTracker.isNetworkEnabled());
					mailSender.setTextMessage("Gps: " + locationTracker.formatLocation(locationTracker.getGpsLocation()) + "; " +
							"Net: " + locationTracker.formatLocation(locationTracker.getNetworkLocation())+ "; " +
							"Wifi: " + fname.toString());
					mailSender.sendMail();
				} finally {
					handler.postDelayed(runnable, interval);
					Log.v(TAG, "sendMail");
				}
			}
		};
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(runnable);
		locationTracker.removeUpdates();
		super.onDestroy();
	}

	@android.support.annotation.Nullable
	@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		handleStart(intent);
		return START_STICKY;
	}

	public void handleStart(final Intent intent) {
		fname = intent.getStringArrayListExtra("fname");
		runnable.run();
	}
}
