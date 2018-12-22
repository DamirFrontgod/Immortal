package ru.parallels.mipt.immortal;

/**
 * Created by Damir Salakhutdinov on 22.02.2018.
 */

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class AppLocker extends AccessibilityService {
	private static final String TAG = "MyTag";

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		Log.v(TAG, "onAccessibilityEvent");
		/*final int eventType = event.getEventType();
		String eventText = null;
		switch(eventType) {
			case AccessibilityEvent.TYPE_VIEW_CLICKED:
				eventText = "Clicked: ";
				break;
			case AccessibilityEvent.TYPE_VIEW_FOCUSED:
				eventText = "Focused: ";
				break;
		}*/

		/*eventText = eventText + event.getContentDescription();

		Toast.makeText(getApplicationContext(), eventText, Toast.LENGTH_LONG).show();*/
		Intent intent = new Intent(this, LockScreen.class);
		startActivity(intent);
	}

	@Override
	public void onServiceConnected() {
		Log.v(TAG, "onServiceConnected");
		// Set the type of events that this service wants to listen to.  Others
		// won't be passed to this service.
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();

		info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
				AccessibilityEvent.TYPE_VIEW_FOCUSED;

		// If you only want this service to work with specific applications, set their
		// package names here.  Otherwise, when the service is activated, it will listen
		// to events from all applications
		// Set the type of feedback your service will provide.
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;

		// Default services are invoked only if no package-specific ones are present
		// for the type of AccessibilityEvent generated.  This service *is*
		// application-specific, so the flag isn't necessary.  If this was a
		// general-purpose service, it would be worth considering setting the
		// DEFAULT flag.

		// info.flags = AccessibilityServiceInfo.DEFAULT;

		info.notificationTimeout = 30;

		this.setServiceInfo(info);

	}

	@Override
	public void onInterrupt() {
		Log.v(TAG, "onInterrupt");
	}
}
