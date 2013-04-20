package usli.smd.payload;

import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

public class AccelerometerMonitor extends Service implements SensorEventListener
{
	Sensor accelerometer;
  	SensorManager smgr;
  	float accelX, accelY, accelZ;
  	boolean launchDetected;
  	boolean apogeeDetected;
  	private Calendar cal;

	public void onCreate()
	{
		smgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		launchDetected = false;
		apogeeDetected = false;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		accelerometer = smgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		if (accelerometer != null)
		{
			smgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL); //200,000 us delay
		}
		else
		{
			// Accelerometer not available
		}
		return START_STICKY;
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void onSensorChanged(SensorEvent event) 
	{
		float accelX = event.values[0];
		float accelY = event.values[1];
		float accelZ = event.values[2];
		
		Intent dataReady = new Intent(this, DataAggregator.class);
		dataReady.setAction("update");
		dataReady.putExtra("usli.smd.payload.accelX", accelX);
		dataReady.putExtra("usli.smd.payload.accelY", accelY);
		dataReady.putExtra("usli.smd.payload.accelZ", accelZ);
		startService(dataReady);
		
		if (accelY > 7.5 && !launchDetected)
		{
			cal = Calendar.getInstance();
			Intent SMSDataPacket = new Intent(this, SMSSender.class);
			String message = "Launch detected at " + cal.get(Calendar.HOUR) 
					+ ":" + cal.get(Calendar.MINUTE) 
					+ ":" + cal.get(Calendar.SECOND);
			SMSDataPacket.putExtra("destination", 1);
			SMSDataPacket.putExtra("message", message);
			startService(SMSDataPacket);
			launchDetected = true;
		}
		
		if (accelX < 0.05 && accelY < 0.05 && accelZ < 0.05 && launchDetected && !apogeeDetected)
		{
			Intent SMSDataPacket = new Intent(this, SMSSender.class);
			String message = "Apogee detected at " + cal.get(Calendar.HOUR) 
					+ ":" + cal.get(Calendar.MINUTE) 
					+ ":" + cal.get(Calendar.SECOND);
			SMSDataPacket.putExtra("destination", 1);
			SMSDataPacket.putExtra("message", message);
			startService(SMSDataPacket);
			apogeeDetected = true;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
