package usli.smd.payload;

import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class DataAggregator extends Service 
{	
	// Class working fields
	private Intent dataPacket;
	private Calendar cal;
	
	public void onCreate()
	{		
		dataPacket = new Intent(this, SDWriter.class);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{	
		Bundle extras = intent.getExtras();
		
		if (intent.getAction().equals("update"))
		{
			dataPacket.putExtras(extras);
		}
		else
		{
			// Get time stamp
			cal = Calendar.getInstance();
			
			// Add time stamp to dataPacket
			dataPacket.putExtra("usli.smd.payload.second", cal.get(Calendar.SECOND));
			dataPacket.putExtra("usli.smd.payload.minute", cal.get(Calendar.MINUTE));
			dataPacket.putExtra("usli.smd.payload.hour", cal.get(Calendar.HOUR));
			
			// Change target class to SDWriter and start service
			dataPacket.setClass(this, SDWriter.class);
			startService(dataPacket);
			
			// Change target class to SMSSender and start service
			dataPacket.setClass(this, SMSAggregator.class);
			startService(dataPacket);
			
			// Change target class to EventManager and start service
			dataPacket.setClass(this, EventManager.class);
			startService(dataPacket);
		}
			
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}

