package br.ufpr.ci317wifi;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.NoCopySpan.Concrete;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Lists extends Activity {
	
	WifiManager wifiManager;
	WifiScanReceiver wifiScanReceiver;
	ListView listView;
	String wifis[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lists);
		
		listView = (ListView) findViewById(R.id.wifiList);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiScanReceiver = new WifiScanReceiver();
		wifiManager.startScan();
		Log.d("DBG", "after scanReceiver");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lists, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void onPouse () {
		unregisterReceiver(wifiScanReceiver);
		Log.d("DBG", "onPouse");
		super.onPause();
	}
	
	@SuppressWarnings("static-access")
	protected void onResume () {
		registerReceiver(wifiScanReceiver, new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		Log.d("DBG", "onResume");
		super.onResume();
	}
	
	class WifiScanReceiver extends BroadcastReceiver {
		public void onReceive (Context c, Intent intent) {
			List<ScanResult> wifiScanList = wifiManager.getScanResults();
			
			wifis = new String[wifiScanList.size()];
			
			for (int i = 0; i < wifiScanList.size(); i++) {
				wifis[i] = "SSID: " + wifiScanList.get(i).SSID + "\n" + ((wifiScanList.get(i)).toString());
			}
			
			Log.d("DBG", "red = " + wifis[0]);
			
			listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, wifis));
		}
	}
}
