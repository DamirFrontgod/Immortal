package ru.parallels.mipt.immortal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class LockScreen extends AppCompatActivity {
	private static final String TAG = "MyTag";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "LockScreenOnCreate");
		setContentView(R.layout.activity_lock_screen);
	}
}
