package usli.smd.payload;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class TelephonyMonitor extends Service
{
	private TelephonyManager tmgr;
  	private MyPhoneStateListener listener;

	public void onCreate()
	{
		tmgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		super.onCreate();	
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		super.onStartCommand(intent, flags, startId);
		tmgr.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		return START_NOT_STICKY;
	}

	private class MyPhoneStateListener extends PhoneStateListener 
	{
		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength)
		{
			Intent dataReady = new Intent(getApplicationContext(), DataAggregator.class);
			dataReady.setAction("update");
			dataReady.putExtra("usli.smd.payload.signal", signalStrength.getGsmSignalStrength());
			startService(dataReady);	
			super.onSignalStrengthsChanged(signalStrength);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	};

}
