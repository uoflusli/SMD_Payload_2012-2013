package usli.smd.payload;

import java.io.UnsupportedEncodingException;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SMSSender extends IntentService
{
	private static final String GOOGLE_VOICE = "+16265028754";
	private static final String GROUND_STATION = "+15028211142";
	private static final String TWITTER = "40404";
	
	private SmsManager smsmgr;
	private String phoneNumber;
    private String message;
	
	public SMSSender()
	{
		super("SMSSender");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	protected void onHandleIntent(Intent intent)
	{
		Bundle data = intent.getExtras();
		switch(data.getInt("destination"))
		{
		case 0:
			phoneNumber = GOOGLE_VOICE;
			break;
		case 1:
			phoneNumber = TWITTER;
			break;
		case 2:
			phoneNumber = GROUND_STATION;
			break;
		default:
			phoneNumber = GOOGLE_VOICE;
			break;			
		}
		message = data.getString("message");

		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, SMSSender.class), 0);                
        smsmgr = SmsManager.getDefault();
        smsmgr.sendTextMessage(phoneNumber, null, message, pi, null);		
	}
}
