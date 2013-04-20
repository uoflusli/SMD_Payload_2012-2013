 package usli.smd.payload;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class LocationMonitor extends Service implements LocationListener
{
	private LocationManager lmgr;
	private double latitude;
	private double longitude;
	private float bearing;
	
	@Override
	public void onCreate() 
	{
		lmgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		lmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() 
	{
		lmgr.removeUpdates(this);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// Not using binding so do nothing.
		return null;
	}

	@Override
	public void onLocationChanged(Location l) 
	{
		latitude = l.getLatitude();
		longitude = l.getLongitude();
		bearing = l.getBearing();
		
		Intent dataReady = new Intent(this, DataAggregator.class);
		dataReady.setAction("update");
		dataReady.putExtra("usli.smd.payload.latitude", latitude);
		dataReady.putExtra("usli.smd.payload.longitude", longitude);
		dataReady.putExtra("usli.smd.payload.bearing", bearing);
		startService(dataReady);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// Do nothing
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// Do nothing
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// Do nothing
	}	
}

