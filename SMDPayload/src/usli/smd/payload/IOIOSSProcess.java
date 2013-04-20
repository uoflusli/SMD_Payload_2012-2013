package usli.smd.payload;

import android.content.Context;
import android.content.Intent;
import usli.smd.payload.sensors.BMP085;
import usli.smd.payload.sensors.BPW34;
import usli.smd.payload.sensors.HIH6130;
import usli.smd.payload.sensors.UVI01;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

public class IOIOSSProcess extends BaseIOIOLooper
{
	private TwiMaster[] sensorArray;
	//private TwiMaster gyro;
	
	private BMP085 pressureSensor;
	private HIH6130 humiditySensor;
	private BPW34 lightSensor;
	private UVI01 uvSensor;
	
	private Intent dataReady;
	
	private DigitalOutput led_;
	
	private Context cxt;

	public IOIOSSProcess(Context cxt)
	{
		super();
		this.cxt = cxt;
	}
	
	/*
	@Override
	protected void setup() throws ConnectionLostException, InterruptedException 
	{
		// Open all the things
		
		pressureSensor = new BMP085();
		humiditySensor = new HIH6130();
		lightSensor = new BPW34();
		uvSensor = new UVI01();
		
		dataReady = new Intent(cxt, DataAggregator.class);
		dataReady.setAction("update");
		
		sensorArray[0] = ioio_.openTwiMaster(0, TwiMaster.Rate.RATE_100KHz, false);
		sensorArray[1] = ioio_.openTwiMaster(1, TwiMaster.Rate.RATE_100KHz, false);
		//gyro = ioio_.openTwiMaster(2, TwiMaster.Rate.RATE_100KHz, false);
		
		led_ = ioio_.openDigitalOutput(IOIO.LED_PIN);
		led_.write(true);

	}

	@Override
	public void loop() throws ConnectionLostException, InterruptedException 
	{
		// Get data
		for (int i = 0; i < 2; i++)
		{
			pressureSensor.attach(sensorArray[i]);
			humiditySensor.attach(sensorArray[i]);
			lightSensor.attach(sensorArray[i]);
			uvSensor.attach(sensorArray[i]);
			
			dataReady.putExtra("usli.smd.payload.pressure" + (i + 1), pressureSensor.readPressure());
			dataReady.putExtra("usli.smd.payload.temperature" + (i + 1), pressureSensor.readTemperature());
			dataReady.putExtra("usli.smd.payload.humidity" + (i + 1), humiditySensor.readHumidity());
			dataReady.putExtra("usli.smd.payload.irradiance" + (i + 1), lightSensor.readIrradiance());
			dataReady.putExtra("usli.smd.payload.radiation" + (i + 1), uvSensor.readRadiation());
		}
		
		cxt.startService(dataReady);
		
		Thread.sleep(100);
	}
	*/
	
	@Override
	protected void setup() throws ConnectionLostException,
			InterruptedException {
		led_ = ioio_.openDigitalOutput(IOIO.LED_PIN);
	}

	@Override
	public void loop() throws ConnectionLostException,
			InterruptedException {
		led_.write(false);
		Thread.sleep(500);
		led_.write(true);
		Thread.sleep(500);
	}
}
