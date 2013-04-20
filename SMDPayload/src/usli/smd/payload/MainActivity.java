package usli.smd.payload;

//import ioio.lib.api.*;
//import ioio.lib.api.exception.ConnectionLostException;
//import ioio.lib.api.exception.IncompatibilityException;

import java.util.Calendar;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity 
{
	private static Context cxt;
	private static AlarmManager amgr;
	private static Intent locationMonitor, accelerometerMonitor, telephonyMonitor, ioiossMonitor;
	private static PendingIntent sendDataPacket;
	private static PowerManager pmgr;
	private static PowerManager.WakeLock w;
	private static boolean activated;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		/*
		 * Called when the activity is first created. This is where you should do all of your normal static set up: 
		 * create views, bind data to lists, etc. This method also provides you with a Bundle containing the activity's 
		 * previously frozen state, if there was one.
		 * Always followed by onStart().
		 */	
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		cxt = getApplicationContext();

	}
	
	protected void onRestart()
	{
		super.onRestart();
		/*
		 * Called after your activity has been stopped, prior to it being started again.
		 * Always followed by onStart()
		 */
	}
	
	protected void onStart()
	{
		/*
		 * Called when the activity is becoming visible to the user.
		 * Followed by onResume() if the activity comes to the foreground, or onStop() if it becomes hidden.
		 */
		
		super.onStart();
		
		// Acquire WakeLock to keep device on, screen will be allowed to go to sleep
		pmgr = (PowerManager)getSystemService(Context.POWER_SERVICE);
		w = pmgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Main Activity");
		w.acquire();	
		
		// Initialize alarm manager
		amgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		// Create monitor service intents
		locationMonitor = new Intent(cxt, LocationMonitor.class);
		accelerometerMonitor = new Intent(cxt, AccelerometerMonitor.class);
		telephonyMonitor = new Intent(cxt, TelephonyMonitor.class);
		ioiossMonitor = new Intent(cxt, IOIOSSMonitor.class);
		
		activated = false;
	}
	
	protected void onResume()
	{
		/*
		 * Called when the activity will start interacting with the user. At this point your activity is at the top of the 
		 * activity stack, with user input going to it.
		 * Always followed by onPause().
		 */
		super.onResume();
	}
	
	protected void onPause()
	{
		/*
		 * Called when the system is about to start resuming a previous activity. This is typically used to commit unsaved 
		 * changes to persistent data, stop animations and other things that may be consuming CPU, etc. Implementations of 
		 * this method must be very quick because the next activity will not be resumed until this method returns.
		 * Followed by either onResume() if the activity returns back to the front, or onStop() if it becomes invisible to the user.
		 */
		super.onPause();
	}
	
	protected void onStop()
	{
		/*
		 * Called when the activity is no longer visible to the user, because another activity has been resumed and is 
		 * covering this one. This may happen either because a new activity is being started, an existing one is being 
		 * brought in front of this one, or this one is being destroyed.
		 * Followed by either onRestart() if this activity is coming back to interact with the user, or onDestroy() 
		 * if this activity is going away.
		 */
		
		super.onStop();
		
		// Cancel all alarms
		amgr.cancel(sendDataPacket);
		
		// Cancel monitor services
		stopService(locationMonitor);
		stopService(accelerometerMonitor);
		stopService(telephonyMonitor);
		stopService(ioiossMonitor);

		// Release WakeLock if app is destroyed
		w.release();
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		/*
		 * The final call you receive before your activity is destroyed. This can happen either because the activity 
		 * is finishing (someone called finish() on it, or because the system is temporarily destroying this instance 
		 * of the activity to save space. You can distinguish between these two scenarios with the isFinishing() method.
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public static void activate(int delay, int refreshRate)
	{	
		if (!activated)
		{
			//start LocationService
			cxt.startService(locationMonitor);
			// start accelerometer service
			cxt.startService(accelerometerMonitor);
			// start telephony service
			cxt.startService(telephonyMonitor);
			// start IOIO service
			cxt.startService(ioiossMonitor);
			
			sendDataPacket = PendingIntent.getService(cxt, 1, new Intent(cxt, DataAggregator.class).setAction("send"), PendingIntent.FLAG_UPDATE_CURRENT);
			amgr.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis() + delay, refreshRate, sendDataPacket);
			
			// Need to calculate two offset constants for altitude
			
			Intent SMSDataPacket = new Intent(cxt, SMSSender.class);
			SMSDataPacket.putExtra("destination", 2);
			SMSDataPacket.putExtra("message", "ACTIVATED");
			cxt.startService(SMSDataPacket);
			
			
			
			activated = true;
			
			
		}
		else
		{
			// Payload has already been activated
		}
	}
	
	public static void sendLocation()
	{
		Intent SMSDataPacket = new Intent(cxt, SMSSender.class);
		
		LocationManager lmgr = (LocationManager) cxt.getSystemService(Context.LOCATION_SERVICE);
		if (lmgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Location location = lmgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null)
			{
				String message = "Lat: " + Double.toString(location.getLatitude()) + "\r\nLong: " + Double.toString(location.getLongitude());
				SMSDataPacket.putExtra("destination", 2);
				SMSDataPacket.putExtra("message", message);
				cxt.startService(SMSDataPacket);
			}
			else
			{
				String message = "null";
				SMSDataPacket.putExtra("message", message);
				cxt.startService(SMSDataPacket);
			}
		}
		else
		{
			String message = "disabled";
			SMSDataPacket.putExtra("message", message);
			cxt.startService(SMSDataPacket);
		}
		
	}
	
	public static void sendTweet()
	{
		Intent SMSDataPacket = new Intent(cxt, SMSSender.class);
		String message = "Hello from the SMD Payload!";
		SMSDataPacket.putExtra("destination", 1);
		SMSDataPacket.putExtra("message", message);
		cxt.startService(SMSDataPacket);
	}

	public static void deactivate()
	{
		if (activated)
		{
			// Cancel all alarms
			amgr.cancel(sendDataPacket);
			
			// Cancel monitor services
			cxt.stopService(locationMonitor);
			cxt.stopService(accelerometerMonitor);
			cxt.stopService(telephonyMonitor);
			cxt.stopService(ioiossMonitor);
			
			activated = false;
			
			Intent SMSDataPacket = new Intent(cxt, SMSSender.class);
			SMSDataPacket.putExtra("destination", 2);
			SMSDataPacket.putExtra("message", "DEACTIVATED");
			cxt.startService(SMSDataPacket);
		}
	}
}
