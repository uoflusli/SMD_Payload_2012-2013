package usli.smd.payload;

import java.io.*;

import android.app.IntentService;
import android.content.Intent;
import android.os.*;
import android.widget.Toast;

public class SDWriter extends IntentService
{
	//private final String path =  "/uoflusli";
	private final String fname = "data.xml";
	
	public SDWriter()
	{
		super("SDWriter");
	}
	
	public void onCreate()
	{
		super.onCreate();	
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	protected void onHandleIntent(Intent intent)
	{
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
		    try 
		    {
		    	File file = new File(getExternalFilesDir(null),fname);
		    	FileOutputStream fos = new FileOutputStream(file, true);
		    	fos.write(dataToXML(intent.getExtras()).getBytes());
		    	fos.close();
		    } 
		    catch (IOException ioe) 
		    {
		        ioe.printStackTrace();
		    }
		}
		else
		{
			Toast.makeText(this, "SD card not mounted!", Toast.LENGTH_LONG).show();
		}
	}
	
	private String dataToXML(Bundle data)
	{
		String xml = "";
		
		xml += "<packet>\r\n";
		xml += "\t<hour>" + data.getInt("usli.smd.payload.hour") + "</hour>\r\n";
		xml += "\t<minute>" + data.getInt("usli.smd.payload.minute") + "</minute>\r\n";
		xml += "\t<second>" + data.getInt("usli.smd.payload.second") + "</second>\r\n";
		xml += "\t<pressure1>" + data.getInt("usli.smd.payload.pressure1") + "</pressure1>\r\n";
		xml += "\t<altitude1>" + data.getDouble("usli.smd.payload.altitude1") + "</altitude1>\r\n";
		xml += "\t<temperature1>" + data.getDouble("usli.smd.payload.temperature1") + "</temperature1>\r\n";
		xml += "\t<humidity1>" + data.getInt("usli.smd.payload.humidity1") + "</humidity1>\r\n";
		xml += "\t<solarIrradiance1>" + data.getShort("usli.smd.payload.solarIrradiance1") + "</solarIrradiance1>\r\n";
		xml += "\t<uvRadiation1>" + data.getShort("usli.smd.payload.uvRadiation1") + "</uvRadiation1>\r\n";
		xml += "\t<pressure2>" + data.getInt("usli.smd.payload.pressure2") + "</pressure2>\r\n";
		xml += "\t<altitude2>" + data.getDouble("usli.smd.payload.altitude2") + "</altitude2>\r\n";
		xml += "\t<temperature2>" + data.getDouble("usli.smd.payload.temperature2") + "</temperature2>\r\n";
		xml += "\t<humidity2>" + data.getInt("usli.smd.payload.humidity2") + "</humidity2>\r\n";
		xml += "\t<solarIrradiance2>" + data.getShort("usli.smd.payload.solarIrradiance2") + "</solarIrradiance2>\r\n";
		xml += "\t<uvRadiation2>" + data.getShort("usli.smd.payload.uvRadiation2") + "</uvRadiation2>\r\n";
		xml += "\t<latitude>" + data.getDouble("usli.smd.payload.latitude") + "</latitude>\r\n";
		xml += "\t<longitude>" + data.getDouble("usli.smd.payload.longitude") + "</longitude>\r\n";
		xml += "\t<accelX>" + data.getFloat("usli.smd.payload.accelX") + "</accelX>\r\n";
		xml += "\t<accelY>" + data.getFloat("usli.smd.payload.accelY") + "</accelY>\r\n";
		xml += "\t<accelZ>" + data.getFloat("usli.smd.payload.accelZ") + "</accelZ>\r\n";
		xml += "\t<gyroX>" + data.getFloat("usli.smd.payload.gyroX") + "</gyroX>\r\n";
		xml += "\t<gyroY>" + data.getFloat("usli.smd.payload.gyroY") + "</gyroY>\r\n";
		xml += "\t<gyroZ>" + data.getFloat("usli.smd.payload.gyroZ") + "</gyroZ>\r\n";
		xml += "\t<bearing>" + data.getFloat("usli.smd.payload.bearing") + "</bearing>\r\n";
		xml += "\t<signal>" + data.getInt("usli.smd.payload.signal") + "</signal>\r\n";
		xml += "</packet>\r\n";
		
		return xml;
	}
}

