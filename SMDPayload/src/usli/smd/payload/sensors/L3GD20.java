/*
package usli.smd.payload.sensors;

public class L3GD20 extends I2CDevice
{
	private static final byte CTRL_REG_ = (byte) 0xA0;
	
	
	private static final byte[] STRT_TEMP_CNV = { (byte)0xF4, (byte)0x2E };
	private static final byte[] STRT_PRES_CNV = { (byte)0xF4, (byte)0xF4 }; // Oversampling: osrs=0 : 0x34, osrs=1 : 0x74, osrs=2 : 0xB4, osrs=3 : 0xF4
	private static final byte[] GET_VALUE = { (byte)0xF6 };
	
	public L3GD20() 
	{
		super(0x69, false);	// SDO Pin tied high
	}
	
	// First byte: Register to be written to, MSb := 1 = auto increment register, 0 = do not auto increment register; other 7 bits = register
	// Important registers:
	/*
	 * CTRL_REG1 = 0x20 => 00001111 => 0x0F "WAKE"
	 * CTRL_REG2 = 0x21 => 00000000 => 0x00 "HPFSetup"; research this operation
	 * CTRL_REG3 = 0x22 => 00000000 => 0x00 "INTSetup"
	 * CTRL_REG4 = 0x23 => 00000000 => 0x00 "ScaleSelect"
	 * CTRL_REG5 = 0x24 => 00000000 => 0x00 "?"
	 * STATUS_REG = 0x27
	 * OUT_X_L = 0x28
	 * OUT_X_H = 0x29
	 * OUT_Y_L = 0x2A
	 * OUT_Y_H = 0x2B
	 * OUT_Z_L = 0x2C
	 * OUT_Z_H = 0x2D
	 * 
	 *
}
*/