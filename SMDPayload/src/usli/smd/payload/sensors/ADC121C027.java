package usli.smd.payload.sensors;

import java.nio.ByteBuffer;

import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;

public class ADC121C027 extends I2CDevice
{
	public static final byte[] STRT_CNV = { (byte) 0x00 };
	public static final byte[] CONFIGURE = { (byte) 0x02, (byte) 0x00};
	
	public ADC121C027(int pinConfiguration) 
	{ 
		super(0x50 | pinConfiguration, false);
	}
	
	public void configure() throws ConnectionLostException, InterruptedException
	{
		i2cbus.writeRead(ADDR, ADDR_FMT, CONFIGURE, CONFIGURE.length, null, 0);	
	}
	
	protected short readValue() throws ConnectionLostException, InterruptedException
	{
		byte[] rawValue = {(byte) 0x00, (byte) 0x00};
		i2cbus.writeRead(ADDR, ADDR_FMT, null, 0, rawValue, rawValue.length);
		//return (rawValue[0] & 0xFF) << 8 | (rawValue[1] & 0xFF);
		return ByteBuffer.wrap(rawValue).getShort();
	}
}
