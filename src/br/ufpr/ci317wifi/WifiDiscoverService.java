package br.ufpr.ci317wifi;

import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

public class WifiDiscoverService extends Service {
	private WifiDiscoverReceiver wifiDiscoverReceiver;
	private WifiManager wifiManager;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Thread thread = new Thread(null, runThread);
	    thread.start();
	        
		/*
		 * START_NOT_STICK - Expects service to call stopSelf().
		 *                   Note this type of return will set
		 * the service just to restart if there are pending call's
		 * to startService(... otherwise service will not start.
		 * This avoid to force android restart the service in the moment
		 * where there is a contention of resources.
		 */
	    return Service.START_NOT_STICKY;
	}
	
	private Runnable runThread = new Runnable() {
		public void run() {
			Log.d("dbg", String.valueOf(this.hashCode()));
			Context context = getApplicationContext();
			wifiDiscoverReceiver = new WifiDiscoverReceiver();
			wifiManager = (WifiManager)context.getSystemService(Service.WIFI_SERVICE);
			context.registerReceiver(wifiDiscoverReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
			wifiManager.startScan();
			/*
			for( int i = 0; i < 10; i++ ) {
				try {
					Thread.sleep(1000);
			        Log.i("dbg", i + " DEU");
				} catch( Exception e ) {
					e.getLocalizedMessage();
				}
			}*/
			stopSelf();
		}
	};
	
    class WifiDiscoverReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("dbg", "WifiDiscoverReceiver=" + String.valueOf(this.hashCode()));
    
            List<ScanResult> wifiList = wifiManager.getScanResults();
            ScanResult bestSignal, next;
    
            if( wifiList.size() > 0 ) { 
                bestSignal = wifiList.get(0);
                for( int i = 1; i < wifiList.size(); i++ ) { 
                    next = wifiList.get(i);
                    if( WifiManager.compareSignalLevel(bestSignal.level, next.level) < 0 ) 
                        bestSignal = next;
                }   
    
                Log.d("dbg", "ssid: " + bestSignal.SSID + ", signal: " + bestSignal.level);
            }   
        }   
    }
}
