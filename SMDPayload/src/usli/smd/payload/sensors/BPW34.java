package usli.smd.payload.sensors;

import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;

public class BPW34 extends ADC121C027
{
	private short solarIrradiance;
	
	public BPW34() 
	{
		super(0x01);	// Denotes pin configuration; see datasheet
	}
	
	public short readIrradiance() throws ConnectionLostException, InterruptedException
	{
		solarIrradiance = super.readValue();
		return solarIrradiance;
	}
}
