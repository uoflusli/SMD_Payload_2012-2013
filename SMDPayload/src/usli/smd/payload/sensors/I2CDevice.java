package usli.smd.payload.sensors;

import ioio.lib.api.*;

public abstract class I2CDevice
{
	protected final int ADDR;
	protected final boolean ADDR_FMT;
	protected TwiMaster i2cbus;
	
	public I2CDevice(int addr, boolean addr_fmt)
	{
		ADDR = addr;
		ADDR_FMT = addr_fmt;
	}
	
	public void attach(TwiMaster i2cbus)
	{
		this.i2cbus = i2cbus;
	}
}
