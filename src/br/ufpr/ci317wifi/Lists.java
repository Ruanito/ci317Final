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
	Intent intent;

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
		int id = item.getItemId();
		
		switch (id) {
			case R.id.action_info:
				intent = new Intent(this, Info.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	protected void onPause () {
		unregisterReceiver(wifiScanReceiver);
		Log.d("DBG", "onPause");
		super.onPause();
	}
	
	protected void onResume () {
		super.onResume();
		registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		Log.d("DBG", "onResume");
	}
	
	class WifiScanReceiver extends BroadcastReceiver {
		public void onReceive (Context c, Intent intent) {
			List<ScanResult> wifiScanList = wifiManager.getScanResults();
			
			wifis = new String[wifiScanList.size()];
			
			for (int i = 0; i < wifiScanList.size(); i++) {
				String value = String.valueOf(wifiScanList.get(i).level);
				wifis[i] = "Nome: " + wifiScanList.get(i).SSID + "\nQualidade: " + value;
			}
			
			listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, wifis));
		}
	}
}
