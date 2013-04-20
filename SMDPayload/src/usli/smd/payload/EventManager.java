package usli.smd.payload;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class EventManager extends Service 
{	
	private boolean launchDetected;
	private boolean apogeeDetected;
	private boolean landingDetected;
	
	private long numUpdates;
	
	public void onCreate()
	{		
		//dataPacket = new Intent(this, SDWriter.class);
		numUpdates;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{	
				
		
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
