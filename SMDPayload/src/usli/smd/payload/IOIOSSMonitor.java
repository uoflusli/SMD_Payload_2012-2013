package usli.smd.payload;

import java.nio.ByteBuffer;

import usli.smd.payload.sensors.*;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class IOIOSSMonitor extends IOIOService
{	
	@Override
	protected IOIOLooper createIOIOLooper() 
	{
		return new BaseIOIOLooper() 
		{
			private TwiMaster[] sensorArray = new TwiMaster[3];
			
			private BMP085 pressureSensor1, pressureSensor2;
			private HIH6130 humiditySensor1, humiditySensor2;
			private BPW34 lightSensor1, lightSensor2;
			private UVI01 uvSensor1, uvSensor2;
			//private L3G4200D spinSensor;
			
			private Intent dataReady;

			@Override
			protected void setup() throws ConnectionLostException, InterruptedException 
			{					
				// Open all I2C buses
				for (int i = 0; i < sensorArray.length; i++)
				{
					sensorArray[i] = ioio_.openTwiMaster(i, TwiMaster.Rate.RATE_100KHz, false);
				}
				
				// Create sensors
				pressureSensor1 = new BMP085();
				humiditySensor1 = new HIH6130();
				lightSensor1 = new BPW34();
				uvSensor1 = new UVI01();
				pressureSensor2 = new BMP085();
				humiditySensor2 = new HIH6130();
				lightSensor2 = new BPW34();
				uvSensor2 = new UVI01();
				//spinSensor = new L3G4200D(sensorArray[2]);
				
				// Attach sensors to their respective buses
				pressureSensor1.attach(sensorArray[0]);
				humiditySensor1.attach(sensorArray[0]);
				lightSensor1.attach(sensorArray[0]);
				uvSensor1.attach(sensorArray[0]);
				pressureSensor2.attach(sensorArray[1]);
				humiditySensor2.attach(sensorArray[1]);
				lightSensor2.attach(sensorArray[1]);
				uvSensor2.attach(sensorArray[1]);
				
				// Calibrate sensors
				pressureSensor1.calibrate();
				pressureSensor2.calibrate();
				//spinSensor.configure();
				
				// Define some calibration constants; altitude for sure
							
				// Build intent
				dataReady = new Intent(getApplicationContext(), DataAggregator.class);
				dataReady.setAction("update");			
			}

			@Override
			public void loop() throws ConnectionLostException, InterruptedException 
			{
				// Get data
				dataReady.putExtra("usli.smd.payload.temperature1", pressureSensor1.readTemperature());
				dataReady.putExtra("usli.smd.payload.pressure1", pressureSensor1.readPressure());
				dataReady.putExtra("usli.smd.payload.altitude1", pressureSensor1.getAltitude());
				dataReady.putExtra("usli.smd.payload.humidity1", humiditySensor1.readHumidity());
				dataReady.putExtra("usli.smd.payload.solarIrradiance1", lightSensor1.readIrradiance());
				dataReady.putExtra("usli.smd.payload.uvRadiation1", uvSensor1.readRadiation());
				
				dataReady.putExtra("usli.smd.payload.temperature2", pressureSensor2.readTemperature());
				dataReady.putExtra("usli.smd.payload.pressure2", pressureSensor2.readPressure());
				dataReady.putExtra("usli.smd.payload.altitude2", pressureSensor2.getAltitude());
				dataReady.putExtra("usli.smd.payload.humidity2", humiditySensor2.readHumidity());
				dataReady.putExtra("usli.smd.payload.solarIrradiance2", lightSensor2.readIrradiance());
				dataReady.putExtra("usli.smd.payload.uvRadiation2", uvSensor2.readRadiation());
			
				//dataReady.putExtra("usli.smd.payload.gyroX", spinSensor.getXSpin());
				//dataReady.putExtra("usli.smd.payload.gyroY", spinSensor.getYSpin());
				//dataReady.putExtra("usli.smd.payload.gyroZ", spinSensor.getZSpin());
				
				startService(dataReady);
			}
		};
	}

	@Override
	public void onStart(Intent intent, int startId) 
	{
		super.onStart(intent, startId);	
	}
	
	@Override
	public IBinder onBind(Intent arg0) 
	{
		return null;
	}
}
