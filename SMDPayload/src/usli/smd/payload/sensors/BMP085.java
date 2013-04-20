package usli.smd.payload.sensors;

import java.nio.ByteBuffer;

import usli.smd.payload.SMSSender;

import android.content.Intent;
import android.widget.Toast;

import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;

// BMP085 Definition
// Reference: https://groups.google.com/forum/#!msg/ioio-users/CAhCMhOo2RI/X1cJNVXc4ScJ
public class BMP085 extends I2CDevice
{
	// Commands
	private static final byte[] GET_CALIB_VALS = { (byte)0xAA };
	private static final byte[] STRT_TEMP_CNV = { (byte)0xF4, (byte)0x2E };
	private static final byte[] STRT_PRES_CNV = { (byte)0xF4, (byte)0xF4 }; // Oversampling: osrs=0 : 0x34, osrs=1 : 0x74, osrs=2 : 0xB4, osrs=3 : 0xF4
	private static final byte[] GET_VALUE = { (byte)0xF6 };
	
	// Constants
	private static final int OSRS = 3;
	
	// Returned values
	private int pressure;
	private double temperature;
	
	// EEPROM Values
	private int AC1;
	private int AC2;
	private int AC3;
	private int AC4;
	private int AC5;
	private int AC6;
	private int B1;
	private int B2;
	private int MC;
	private int MD;
	
	// Derived values
	private int X1;
	private int X2;
	private int X3;
	private int B5;
	private int T;
	private int B6;
	private int B3;
	private int B4;	//u
	private int B7; //u
	
	public BMP085() 
	{
		super(0x77, false); //Datasheet says 0xEE, but to work with IOIO need to >> 1, 7 bit addressing used
	}
	
	public void calibrate() throws ConnectionLostException, InterruptedException
	{
		byte[] calibValues = new byte[22];
		i2cbus.writeRead(ADDR, ADDR_FMT, GET_CALIB_VALS, GET_CALIB_VALS.length, calibValues, calibValues.length);
		
		AC1 = (short)(((calibValues[0] & 0xFF) << 8) | (calibValues[1] & 0xFF));		//u
		AC2 = (short)(((calibValues[2] & 0xFF) << 8) | (calibValues[3] & 0xFF));		//u
		AC3 = (short)(((calibValues[4] & 0xFF) << 8) | (calibValues[5] & 0xFF));		//u
		AC4 = ((calibValues[6] & 0xFF) << 8) | (calibValues[7] & 0xFF);					//s
		AC5 = ((calibValues[8] & 0xFF) << 8) | (calibValues[9] & 0xFF);					//s
		AC6 = ((calibValues[10] & 0xFF) << 8) | (calibValues[11] & 0xFF);				//s
		B1 = (short)(((calibValues[12] & 0xFF) << 8) | (calibValues[13] & 0xFF));		//u
		B2 = (short)(((calibValues[14] & 0xFF) << 8) | (calibValues[15] & 0xFF));		//u
		MC = (short)(((calibValues[18] & 0xFF) << 8) | (calibValues[19] & 0xFF));
		MD = (short)(((calibValues[20] & 0xFF) << 8) | (calibValues[21] & 0xFF));
	}
	
	public double readTemperature() throws ConnectionLostException, InterruptedException
	{
		//writeRead is a blocking call. probably needs to be replaced with non-blocking writeReadAsync
		byte[] rawTemperature = {(byte) 0x00, (byte) 0x00};	// Made to length 2 b/c expecting 3 bytes according to datasheet
		i2cbus.writeRead(ADDR, ADDR_FMT, STRT_TEMP_CNV, STRT_TEMP_CNV.length, null, 0);
		Thread.sleep(5); // wait for conversion (4.5 ms, datasheet, pg 17)
		i2cbus.writeRead(ADDR, ADDR_FMT, GET_VALUE, GET_VALUE.length, rawTemperature, rawTemperature.length);
		int UT = (rawTemperature[0] & 0xFF) << 8 | (rawTemperature[1] & 0xFF);
		
		X1 = ((UT - AC6) * AC5) >> 15;
		X2 = (MC << 11) / (X1 + MD);
		B5 = X1 + X2;
		T = (B5 + 8) >> 4;
		
		temperature = T * 0.1;
		return temperature; // [C] temperature
	}
	
	public int readPressure() throws ConnectionLostException, InterruptedException
	{
		//writeRead is a blocking call. probably needs to be replaced with non-blocking writeReadAsync
		//ByteBuffer stuff = ByteBuffer.allocate(4).put((byte) 0x00);
		byte[] rawPressure = {(byte) 0x00, (byte) 0x00, (byte) 0x00};	// Made to length 3 b/c expecting 3 bytes according to datasheet
		i2cbus.writeRead(ADDR, ADDR_FMT, STRT_PRES_CNV, STRT_PRES_CNV.length, null, 0);
		Thread.sleep(26); // wait for conversion (25.5 ms, datasheet, pg 17)
		i2cbus.writeRead(ADDR, ADDR_FMT, GET_VALUE, GET_VALUE.length, rawPressure, rawPressure.length);
		int UP = ((rawPressure[0] & 0xFF) << 16 | (rawPressure[1] & 0xFF) << 8 | (rawPressure[2] & 0xFF)) >> (8 - OSRS);
		
		// Define B6
		B6 = B5 - 4000;
		
		// Define B3
		X1 = (((B6 * B6) >> 12) * B2) >> 11;
		X2 = (AC2 * B6) >> 11;
		X3 = X1 + X2;
		B3 = (((AC1 * 4 + X3) << OSRS) + 2) >> 2;
		
		// Define B4
		X1 = (AC3 * B6) >> 13;
		X2 = (B1 * ((B6 * B6) >> 12)) >> 16;
		X3 = ((X1 + X2) + 2) >> 2;
		B4 = (AC4 * (X3 + 32768)) >> 15;
		
		// Define B7
		B7 = ((UP - B3) * (50000 >> OSRS));
		
		// Math
		if (B7 < 0x80000000)
		{
			pressure = (B7 << 1) / B4;
		}
		else
		{
			pressure = (B7 / B4) << 1;
		}
		
		X1 = (pressure >> 8) * (pressure >> 8);	
		X1 = (X1 * 3038) >> 16;
		X2 = (-7357 * pressure) >> 16;
		pressure += (X1 + X2 + 3791) >> 4;
		return pressure; // [Pa] pressure	
	}
	
	public double getAltitude()
	{
		return 44330 * (1 - Math.pow((pressure / 101325.0), 1/5.255));
	}
}
