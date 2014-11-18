package br.ufpr.ci317wifi;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class WifiDiscoverService extends Service {
	private final int TIMER_TRIGGER = 1;		// time in minutes
	private Context context = null;
	private WifiDiscoverReceiver wifiDiscoverReceiver = null;
	private WifiManager wifiManager = null;
	
	@Override
	public void onCreate() {
		super.onCreate();		
		updateTimer = new Timer("wifidiscover");
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private Timer updateTimer;
	private Handler handler;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("dbg", "WifiDiscoverService.onStartCommand ThreadId="+String.valueOf(Thread.currentThread().getId()));
		updateTimer.cancel();
		updateTimer = new Timer("wifidiscover");
		updateTimer.scheduleAtFixedRate(wifidiscover, 0, TIMER_TRIGGER*8*1000);
		
		context = getApplicationContext();
		if( wifiManager == null )
			wifiManager = (WifiManager)context.getSystemService(Service.WIFI_SERVICE);
		if( wifiDiscoverReceiver == null )
			wifiDiscoverReceiver = new WifiDiscoverReceiver();
		
		HandlerThread ht = new HandlerThread("mythread");
		ht.start();
		Looper looper = ht.getLooper();
		handler = new Handler(looper);
		context.registerReceiver(wifiDiscoverReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), null, handler);
		
		/*
		 * START_NOT_STICK - Expects service to call stopSelf().
		 *                   Note this type of return will set
		 * the service just to restart if there are pending call's
		 * to startService(... otherwise service will not start.
		 * This avoid to force android restart the service in the moment
		 * where there is a contention of resources.
		 */
	    return Service.START_STICKY;
	}
	
	private TimerTask wifidiscover = new TimerTask() {
		public void run() {
			Log.d("dbg", "WifiDiscoverService.wifidiscover ThreadId="+String.valueOf(Thread.currentThread().getId()));
			wifiManager.startScan();
		}
	};
		
    class WifiDiscoverReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
			Log.d("dbg", "WifiDiscoverService$WifiDiscoverReceiver.onReceive ThreadId="+String.valueOf(Thread.currentThread().getId()));
            List<ScanResult> wifiList = wifiManager.getScanResults();
            ScanResult bestSignal, next;
            int netId;
    
            if( wifiList.size() > 0 ) { 
                bestSignal = wifiList.get(0);
                for( int i = 1; i < wifiList.size(); i++ ) { 
                    next = wifiList.get(i);
                    if( WifiManager.compareSignalLevel(bestSignal.level, next.level) < 0 ) 
                        bestSignal = next;
                }
                Log.d("dbg", "ssid: " + bestSignal.SSID + ", signal: " + bestSignal.level);
                
                netId = getNetworkId(bestSignal.SSID);
                Log.d("dbg", "netId = " + String.valueOf(netId));
                if( netId == -1 )
                	//Toast.makeText(context, "Wifi '" + bestSignal.SSID + "' nao salvo.", Toast.LENGTH_SHORT).show();
                	Log.d("dbg", "Wifi '" + bestSignal.SSID + "' nao salvo.");
                else
                	wifiManager.enableNetwork(netId, true);			// connect to the network with best signal.
            }
        }
    }
    
    public int getNetworkId(String ssid) {
    	List<WifiConfiguration> wifisConfig = wifiManager.getConfiguredNetworks();
    	WifiConfiguration wc;
    	String curSSID;
    	int ret = -1;
    	    	
    	for( int i = 0; i < wifisConfig.size() && ret == -1; i++) {
    		wc = wifisConfig.get(i);
    		/*
    		 * WARNING: this is a workaround since getConfiguredNetworks()
    		 *          fill up the name in double quotes.
    		 */
    		curSSID = wc.SSID.replaceAll("\"", "");
    		if( curSSID != null && curSSID.equalsIgnoreCase(ssid) ) ret = wc.networkId;
    	}
    	
    	return ret;
    }
}
