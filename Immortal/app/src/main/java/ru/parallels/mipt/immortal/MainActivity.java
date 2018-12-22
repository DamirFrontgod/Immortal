package ru.parallels.mipt.immortal;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.Intent;
import android.widget.CheckBox;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.widget.Toast;
import android.view.View;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener{
	private Button startButton;
	private Button stopButton;
	private CheckBox adminEnableCheckbox;
	private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
	private ComponentName mDeviceAdminSample;
	private DevicePolicyManager mDPM;
	private boolean adminIsActive = false;
	private static final String TAG = "MyTag";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		setContentView(R.layout.activity_main);
		startButton = (Button) findViewById(ru.parallels.mipt.immortal.R.id.start);
		stopButton = (Button) findViewById(ru.parallels.mipt.immortal.R.id.stop);
		adminEnableCheckbox = (CheckBox) findViewById(ru.parallels.mipt.immortal.R.id.adminEnable);

		startButton.setOnClickListener(this);
		stopButton.setOnClickListener(this);
		adminEnableCheckbox.setOnClickListener(this);
		mDeviceAdminSample = new ComponentName(this, DeviceAdminSampleReceiver.class);
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		restoreCheckBoxes();
	}

	public void restoreCheckBoxes() {
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		boolean defaultValue = getResources().getBoolean(R.bool.admin_default_value);
		adminIsActive = sharedPref.getBoolean(getString(R.string.admin_enabled), defaultValue);
		adminEnableCheckbox.setChecked(adminIsActive);
	}

	public void writeDownCheckBoxState(boolean state) {
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(getString(R.string.admin_enabled), state);
		editor.commit();
	}

	static final String ADMIN_STATUS = "admin_status";
	static final String TEXT = "text";

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == startButton.getId()) {

			WifiManager wmgr = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			// Get List of Available Wifi Networs
			List<ScanResult> availNetworks = wmgr.getScanResults();
			ArrayList<String> wifis = new ArrayList<String>();
			if (availNetworks.size() > 0) {
				for (int i=0; i<availNetworks.size();i++) {
					wifis.add(availNetworks.get(i).SSID);
				}
			}

			Intent serviceIntent = new Intent(this, RestartableService.class);
			serviceIntent.putStringArrayListExtra("fname", wifis);
			startService(serviceIntent);
//
//			startService(new Intent(this, RestartableService.class));

			/*final ComponentName compName =
					new ComponentName(this.getPackageName(), "ru.parallels.mipt.immortal.RestartableService");

			this.getPackageManager().setComponentEnabledSetting(
					compName,
					this.getPackageManager().COMPONENT_ENABLED_STATE_ENABLED,
					this.getPackageManager().DONT_KILL_APP);*/
		} else if (id == stopButton.getId()) {
			stopService(new Intent(this, RestartableService.class));
			/*final ComponentName compName =
					new ComponentName(this.getPackageName(), "ru.parallels.mipt.immortal.RestartableService");

			this.getPackageManager().setComponentEnabledSetting(
					compName,
					this.getPackageManager().COMPONENT_ENABLED_STATE_DISABLED,
					this.getPackageManager().DONT_KILL_APP);*/
		} else if (id == adminEnableCheckbox.getId()) {
			if(((CheckBox)view).isChecked()) {
				//adminIsActive = true;
				Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
				startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
				/*mDPM.addUserRestriction(mDeviceAdminSample, UserManager.DISALLOW_APPS_CONTROL);*/
			} else {
				//adminIsActive = false;
				/*mDPM.clearUserRestriction(mDeviceAdminSample, UserManager.DISALLOW_APPS_CONTROL);*/
				mDPM.removeActiveAdmin(mDeviceAdminSample);
			}
			/*adminEnableCheckbox.setChecked(isActiveAdmin());*/
			writeDownCheckBoxState(adminEnableCheckbox.isChecked());
		}
	}



	private boolean isActiveAdmin() {
		return mDPM.isAdminActive(mDeviceAdminSample);
	}

	public static class DeviceAdminSampleReceiver extends DeviceAdminReceiver {
		void showToast(Context context, String msg) {
			String status = context.getString(R.string.admin_receiver_status, msg);
			Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == ACTION_DEVICE_ADMIN_DISABLE_REQUESTED) {
				abortBroadcast();
			}
			super.onReceive(context, intent);
		}

		@Override
		public void onEnabled(Context context, Intent intent) {
			showToast(context, context.getString(R.string.admin_receiver_status_enabled));
		}

		@Override
		public CharSequence onDisableRequested(Context context, Intent intent) {
			return context.getString(R.string.admin_receiver_status_disable_warning);
		}

		@Override
		public void onDisabled(Context context, Intent intent) {
			showToast(context, context.getString(R.string.admin_receiver_status_disabled));
		}
	}
}
