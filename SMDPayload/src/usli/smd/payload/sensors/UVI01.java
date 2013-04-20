package usli.smd.payload.sensors;

import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;

public class UVI01 extends ADC121C027 
{
	private short uvRadiation;
	
	public UVI01() 
	{
		super(0x02);	// Denotes pin configuration; see datasheet
	}
	
	public short readRadiation() throws ConnectionLostException, InterruptedException
	{
		uvRadiation = super.readValue();
		return uvRadiation;
	}
}
