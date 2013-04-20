package usli.smd.payload.sensors;

import java.nio.ByteBuffer;

import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;

public class L3G4200D extends I2CDevice
{	
	private static final byte AUTO_INCR = (byte) 0x80;
	private static final byte CTRL_REG1 = (byte) 0xA0;
	private static final byte OUT_X_L = (byte) 0x28;
	private static final byte OUT_X_H = (byte) 0x29;
	private static final byte OUT_Y_L = (byte) 0x2A;
	private static final byte OUT_Y_H = (byte) 0x2B;
	private static final byte OUT_Z_L = (byte) 0x2C;
	private static final byte OUT_Z_H = (byte) 0x2D;
	
	private static final byte[] CONFIGURE = { CTRL_REG1 /*| AUTO_INCR*/, (byte) 0x0F, (byte) 0x00, (byte) 0x00, (byte) 0x50, (byte) 0x00 };
	private static final byte[] GET_X_SPIN = {OUT_X_L | AUTO_INCR};
	private static final byte[] GET_Y_SPIN = {OUT_Y_L | AUTO_INCR};
	private static final byte[] GET_Z_SPIN = {OUT_Z_L | AUTO_INCR};
	

	public L3G4200D() 
	{
		super(0x69, false);
	}
	
	public void configure() throws ConnectionLostException, InterruptedException
	{
		i2cbus.writeRead(ADDR, ADDR_FMT, CONFIGURE, CONFIGURE.length, null, 0);	
	}
	
	public int getXSpin() throws ConnectionLostException, InterruptedException
	{
		byte[] value = {(byte) 0x00, (byte) 0x00};
		i2cbus.writeRead(ADDR, ADDR_FMT, GET_X_SPIN, GET_X_SPIN.length, value, value.length);
		return ByteBuffer.wrap(value).getShort();
	}
	
	public int getYSpin() throws ConnectionLostException, InterruptedException
	{
		byte[] value = {(byte) 0x00, (byte) 0x00};
		i2cbus.writeRead(ADDR, ADDR_FMT, GET_Y_SPIN, GET_Y_SPIN.length, value, value.length);
		return ByteBuffer.wrap(value).getShort();
	}
	
	public int getZSpin() throws ConnectionLostException, InterruptedException
	{
		byte[] value = {(byte) 0x00, (byte) 0x00};
		i2cbus.writeRead(ADDR, ADDR_FMT, GET_Z_SPIN, GET_Z_SPIN.length, value, value.length);
		return ByteBuffer.wrap(value).getShort();
	}
}
