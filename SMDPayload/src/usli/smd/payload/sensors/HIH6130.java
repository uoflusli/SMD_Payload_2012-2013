package usli.smd.payload.sensors;

import java.nio.ByteBuffer;

import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;

public class HIH6130 extends I2CDevice
{
	private static final byte[] GET_VALUE = {};
	
	// Returned values
	int humidity;
	
	public HIH6130() 
	{
		super(0x27, false);
	}
	
	public int readHumidity() throws ConnectionLostException, InterruptedException
	{
		byte[] rawHumidity = {(byte) 0x00, (byte) 0x00};
		i2cbus.writeRead(ADDR, ADDR_FMT, GET_VALUE, GET_VALUE.length, rawHumidity, rawHumidity.length);
		int UH = (rawHumidity[0] & 0x3F) << 8 | (rawHumidity[1] & 0xFF);
		//return ByteBuffer.wrap(rawHumidity).getShort();
		humidity = (int)(UH / (Math.pow(2, 14) - 2) * 100); 
		return humidity;
		//return UH;
	}
}
