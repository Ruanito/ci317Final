package br.ufpr.ci317wifi;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiService extends BroadcastReceiver {
	private final String TAG = "WS";
	private WifiListReceiver wifiReceiver = null;
	private WifiManager wifiManager = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "deu");
		//if( wifiReceiver == null ) wifiReceiver = new WifiListReceiver();
		//if( wifiManager == null ) wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		
		//context.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		//wifiManager.startScan();
	}
	
	class WifiListReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//List<ScanResult> wifiList = wifiManager.getScanResults();
			Log.i(TAG, "deu");
			//context.unregisterReceiver(wifiReceiver);
		}
	}
}
