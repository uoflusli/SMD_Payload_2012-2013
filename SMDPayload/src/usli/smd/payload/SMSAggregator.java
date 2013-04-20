package usli.smd.payload;

import java.nio.ByteBuffer;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class SMSAggregator extends Service
{
	// Class final fields
	private final int numPDUs = 5;
	
	// Working fields
	private int numPDUsReceived;
	private Intent SMSDataPacket;
	private ByteBuffer buffer;
	
	private int packetNum;
	
	public void onCreate()
	{
		super.onCreate();
		numPDUsReceived = 0;
		SMSDataPacket = new Intent(this, SMSSender.class);
		buffer = ByteBuffer.allocate(134);
		packetNum = 0;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Bundle data = intent.getExtras();
		
		numPDUsReceived++;
		
		if (numPDUsReceived == 1)
		{
			// Add packet type + time stamp
			buffer.put((byte)0x03);											// Type
			buffer.put((byte)data.getInt("usli.smd.payload.hour"));			// Hour
			buffer.put((byte)data.getInt("usli.smd.payload.minute"));		// Minute
			buffer.put((byte)data.getInt("usli.smd.payload.second"));		// Second
		}
		
		// Add data
		buffer.putDouble(data.getDouble("usli.smd.payload.latitude"));		// Latitude
		buffer.putDouble(data.getDouble("usli.smd.payload.longitude"));		// Longitude 
		buffer.putShort(data.getShort("usli.smd.payload.pressure1"));		// Pressure
		buffer.putShort(data.getShort("usli.smd.payload.temperature1"));	// Temperature
		buffer.putShort(data.getShort("usli.smd.payload.humidity1"));		// Humidity
		buffer.putShort(data.getShort("usli.smd.payload.solarIrradiance1"));// Solar Irradiance
		buffer.putShort(data.getShort("usli.smd.payload.uvRadiation1"));	// UV Radiation
		
		if (numPDUsReceived == numPDUs)
		{
			String message = "";
			message += data.getInt("usli.smd.payload.hour");
			message += ":" + data.getInt("usli.smd.payload.minute");
			message += ":" + data.getInt("usli.smd.payload.second");
			message += ";" + data.getDouble("usli.smd.payload.latitude");
			message += "," + data.getDouble("usli.smd.payload.longitude");
			message += "," + data.getFloat("usli.smd.payload.bearing");
			message += "," + data.getInt("usli.smd.payload.signal");
			//message += "," + data.getFloat("usli.smd.payload.accelX");
			//message += "," + data.getFloat("usli.smd.payload.accelY");
			//message += "," + data.getFloat("usli.smd.payload.accelZ");
			// Send byte[] to SMSSender
			SMSDataPacket.putExtra("destination", 0);
			SMSDataPacket.putExtra("message", message);
			startService(SMSDataPacket);
			
			// Reset service
			numPDUsReceived = 0;
			buffer.clear();
		}
		
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}


}
