package usli.smd.payload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver
{
	private static final String authorizedSender = "+15028211142";//+16265028754";"+15028211142";
	private static final byte[] CMD_ACTIVATE_HEADER = { 0x31, 0x30, 0x30, 0x31 };
	private static final byte[] CMD_WHERE_AM_I_HEADER = {0x31, 0x30, 0x30, 0x32 };
	private static final byte[] CMD_TWEET = {0x31, 0x30, 0x30, 0x33 };
	private static final byte[] CMD_DEACTIVATE = {0x31, 0x30, 0x30, 0x34};
	
	@Override
	public void onReceive(Context cxt, Intent intent) 
	{
        Bundle bundle = intent.getExtras();        
        SmsMessage message = null;
        String sender = "";
        String command = "";
        byte[] commandBytes;
        boolean activate = true;
        boolean whereAmI = true;
        boolean tweet = true;
        boolean deactivate = true;
        
        if (bundle != null)
        {
            Object[] pdus = (Object[]) bundle.get("pdus");
            message = SmsMessage.createFromPdu((byte[])pdus[0]);                
            sender = message.getOriginatingAddress();
            command = message.getMessageBody();  
            
            if (sender.equals(authorizedSender))
            {
            	commandBytes = command.getBytes();
        		for (int i = 0; i < 4; i++)
        		{
        			activate &= commandBytes[i] == CMD_ACTIVATE_HEADER[i];
        			whereAmI &= commandBytes[i] == CMD_WHERE_AM_I_HEADER[i];
        			tweet &= commandBytes[i] == CMD_TWEET[i];
        			deactivate &= commandBytes[i] == CMD_DEACTIVATE[i];
        		}
        		if (activate)
        		{
        			int refreshRate = Integer.parseInt(command.substring(4));
        			MainActivity.activate(5000, refreshRate);
        		}
        		else if (whereAmI)
        		{
        			MainActivity.sendLocation();
        		}
        		else if (tweet)
        		{
        			MainActivity.sendTweet();
        		}
        		else if (deactivate)
        		{
        			MainActivity.deactivate();
        		}
        		else
        		{
        			// We got something else. Ignore it!
        		}
            }
            else
            {
            	// Ignore messages sent from other phones
            }
        }
	}
}
