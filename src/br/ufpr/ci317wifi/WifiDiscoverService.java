package br.ufpr.ci317wifi;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

public class WifiDiscoverService extends Service {
	private final int TIMER_TRIGGER = 1;		// time in minutes	
	private WifiManager wifiManager = null;
	private WifiDiscoverReceiver wifiDiscoverReceiver = null;
	private NotificationManager notificationManager = null; 

	public static final int NOTIFICATION_ID = R.string.app_name;
	public static int threshold_signal = 10;
	
	@Override
	public void onCreate() {
		Log.d(MainWifi.TAG, "WifiDiscoverService.onCreate threadId=" + String.valueOf(Thread.currentThread().getId()));
		
		wifiDiscoverReceiver = new WifiDiscoverReceiver();
		wifiManager = (WifiManager)getSystemService(Service.WIFI_SERVICE);
		notificationManager = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);

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
		Log.d(MainWifi.TAG, "WifiDiscoverService.onStartCommand ThreadId="+String.valueOf(Thread.currentThread().getId()));
		
		if( updateTimer != null ) return Service.START_STICKY;
		
		/*
		 * Register receiver in a different thread, since is asynchronous.
		 */
		HandlerThread ht = new HandlerThread("wifiScan");
		ht.start();
		Looper looper = ht.getLooper();
		handler = new Handler(looper);
		registerReceiver(wifiDiscoverReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), null, handler);
					
		// Create timer to trigger in TIMER_TRIGGER in minutes.
		updateTimer = new Timer("wifidiscover");
		updateTimer.scheduleAtFixedRate(wifidiscover, 0, TIMER_TRIGGER*60*1000);		
		
		/*
		 * START_STICK - restart automatically even if android kill it.
		 * 				 in the later case intent will be null.
		 */
	    return Service.START_STICKY;
	}
	
	private TimerTask wifidiscover = new TimerTask() {
		public void run() {
			Log.d(MainWifi.TAG, "WifiDiscoverService.wifidiscover ThreadId="+String.valueOf(Thread.currentThread().getId()));
			Log.d(MainWifi.TAG, "WifiDiscoverService.wifidiscover threshold=" + threshold_signal);

			wifiManager.startScan(); 
		}
	};
		
    class WifiDiscoverReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
			// Lod.d(MainWifi.TAG, "WifiDiscoverService$WifiDiscoverReceiver.onReceive ThreadId="+String.valueOf(Thread.currentThread().getId()));
            List<ScanResult> wifiList;
            WifiInfo connected;
            ScanResult bestSignal, next;
            int netId = -1;
            int diff;
            boolean connect;
    
            if( !wifiManager.isWifiEnabled() )
            	return;
            
            wifiList = wifiManager.getScanResults();
            if( wifiList.size() == 0 )
            	return;
            
            /*
             * Find the best signal that is known by the user.
             */
            bestSignal = wifiList.get(0);
            for( int i = 1; i < wifiList.size(); i++ ) { 
                next = wifiList.get(i);
                if( WifiManager.compareSignalLevel(next.level, bestSignal.level) > 0 ) {
                	if( (netId = getNetworkId(next.SSID)) != -1 ) 
                		bestSignal = next;
                }
            }

            /*
             * If netId is equal -1 means the bestSinal object
             * does not contain a valid instance of best signal.
             */
            if( netId != -1 ) {
            	Log.i("dbg", "netId = " + String.valueOf(netId));
                connected = wifiManager.getConnectionInfo();
                diff = WifiManager.compareSignalLevel(bestSignal.level, connected.getRssi());
                
                Log.d("dbg", "BestSSID: " + bestSignal.SSID + " rssi=" + bestSignal.level + ", BSSID=" + bestSignal.BSSID +
                		", ConneSSID: " + connected.getSSID() + " rssi=" + connected.getRssi() + ", BSSID=" + connected.getBSSID() + 
                		"; diff=" + String.valueOf(diff));
                                
                /*
                 * Connect to a network only if:
                 *  if is not connected
                 * 	If ssid are not the same, with different ap (mac address).
                 * 	Best signal has bigger difference than threshold_signal var
                 */
                connect = (connected.getSSID().equals("") || connected.getRssi() < -200);
                connect = connect || 
                	( (!connected.getSSID().equalsIgnoreCase(bestSignal.SSID) ||
                	!connected.getBSSID().equalsIgnoreCase(bestSignal.BSSID)) ) &&
                	(WifiManager.compareSignalLevel(bestSignal.level, connected.getRssi()) > 0 && 
                	Math.abs(diff) > threshold_signal);
                if( connect ) {
	                	Log.i("dbg", "Connecting to: " + bestSignal.SSID + " mac: " + bestSignal.BSSID);
	                	// Toast.makeText(getApplicationContext(), "Connecting to: " + bestSignal.SSID + " mac: " + bestSignal.BSSID, Toast.LENGTH_LONG).show();
	                	// connect to the network with the best signal.
	                	if( wifiManager.enableNetwork(netId, true) ) {
		        			Intent intentNotif = new Intent(WifiDiscoverService.this, br.ufpr.ci317wifi.WifiInfo.class);
		        			PendingIntent pendingIntent = PendingIntent.getActivity(WifiDiscoverService.this, 0, intentNotif, PendingIntent.FLAG_CANCEL_CURRENT);
		        			Notification.Builder builder = new Notification.Builder(WifiDiscoverService.this);
		        			builder.setSmallIcon(R.drawable.ic_launcher)
		        				.setTicker("Wifi conexão")
		        				.setContentTitle("Nova conexão")
		        				.setContentText("Conectado")
		        				.setContentInfo(bestSignal.SSID)
		        				.setContentIntent(pendingIntent)
		        				.setAutoCancel(true);
		        			
		        			@SuppressWarnings("deprecation")
							Notification notification = builder.getNotification();
		        			notificationManager.notify(R.string.app_name, notification);
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
