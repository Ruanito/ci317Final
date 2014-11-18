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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class WifiDiscoverService extends Service {
	private final int THRESHOLD_SIGNAL = 10; 
	private final int TIMER_TRIGGER = 1;		// time in minutes
	private WifiManager wifiManager = null;
	private WifiDiscoverReceiver wifiDiscoverReceiver = null;
	
	@Override
	public void onCreate() {
		Log.d("dbg", "WifiDiscoverService.onCreate threadId=" + String.valueOf(Thread.currentThread().getId()));
		
		wifiDiscoverReceiver = new WifiDiscoverReceiver();
		wifiManager = (WifiManager)getSystemService(Service.WIFI_SERVICE);
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/*
	 * updateTimer - used as a flag, to indicate if we are running
	 * the service already or not.
	 */
	private Timer updateTimer = null;
	private Handler handler;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("dbg", "WifiDiscoverService.onStartCommand ThreadId="+String.valueOf(Thread.currentThread().getId()));
		
		if( updateTimer != null ) return Service.START_STICKY;
		
		HandlerThread ht = new HandlerThread("wifiScan");
		ht.start();
		Looper looper = ht.getLooper();
		handler = new Handler(looper);
		registerReceiver(wifiDiscoverReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), null, handler);
			
		updateTimer = new Timer("wifidiscover");
		updateTimer.scheduleAtFixedRate(wifidiscover, 0, TIMER_TRIGGER*30*1000);		
		
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
            List<ScanResult> wifiList;
            WifiInfo connected;
            ScanResult bestSignal, next;
            int netId;
            int diff;
    
            if( wifiManager.isWifiEnabled() ) {
	            wifiList = wifiManager.getScanResults();
	            if( wifiList.size() > 0 ) { 
	                bestSignal = wifiList.get(0);
	                for( int i = 1; i < wifiList.size(); i++ ) { 
	                    next = wifiList.get(i);
	                    if( WifiManager.compareSignalLevel(next.level, bestSignal.level) > 0 )
	                        bestSignal = next;
	                }
	                connected = wifiManager.getConnectionInfo();
	                diff = WifiManager.compareSignalLevel(bestSignal.level, connected.getRssi());
	                
	                Log.d("dbg", "BestSSID: " + bestSignal.SSID + " rssi=" + bestSignal.level + ", BSSID=" + bestSignal.BSSID +
	                		", ConneSSID: " + connected.getSSID() + " rssi=" + connected.getRssi() + ", BSSID=" + connected.getBSSID() +
	                		"; diff=" + String.valueOf(diff));
	                
	                Toast.makeText(getApplicationContext(), "BestSSID: " + bestSignal.SSID + " rssi=" + bestSignal.level + ", BSSID=" + bestSignal.BSSID +
	                		", ConneSSID: " + connected.getSSID() + " rssi=" + connected.getRssi() + ", BSSID=" + connected.getBSSID() +
	                		"; diff=" + String.valueOf(diff), Toast.LENGTH_LONG).show();
	                
	                /*
	                 * Connect to a network only if 
	                 */
	                if( (WifiManager.compareSignalLevel(bestSignal.level, connected.getRssi()) > 0 && Math.abs(diff) > THRESHOLD_SIGNAL) &&
	                	(!connected.getSSID().equalsIgnoreCase(bestSignal.SSID) ||
	                	!connected.getBSSID().equalsIgnoreCase(bestSignal.BSSID)) ) {
	                	
		                netId = getNetworkId(bestSignal.SSID);
		                Log.d("dbg", "netId = " + String.valueOf(netId));
		                if( netId == -1 ) {
		                	//Toast.makeText(context, "Wifi '" + bestSignal.SSID + "' not saved.", Toast.LENGTH_SHORT).show();
		                	Log.d("dbg", "Wifi '" + bestSignal.SSID + "' not saved.");
		                }else {
		                	// connect to the network with the best signal.
		                	Log.i("dbg", "Connecting to: " + bestSignal.SSID + " mac: " + bestSignal.BSSID);
		                	Toast.makeText(getApplicationContext(), "Connecting to: " + bestSignal.SSID + " mac: " + bestSignal.BSSID, Toast.LENGTH_LONG).show();
		                	wifiManager.enableNetwork(netId, true);
		                }
	                }
	            }
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
