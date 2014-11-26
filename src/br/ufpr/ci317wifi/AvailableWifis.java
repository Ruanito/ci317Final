package br.ufpr.ci317wifi;


import java.util.List;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ViewHolder")
public class AvailableWifis extends ListActivity {
	private WifiManager wifiManager;
	private WifiScanReceiver wifiScanReceiver;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiScanReceiver = new WifiScanReceiver();
		dialog = new ProgressDialog(this);
		if( wifiManager.isWifiEnabled() ) {
			dialog.setMessage("Carregando...");
			dialog.show();
		}else
			Toast.makeText(this, "Wifi desabilitado.", Toast.LENGTH_SHORT).show();
	}
	
	@SuppressWarnings("rawtypes")
	class MyArrayAdapter extends ArrayAdapter< List > {
		private final Context context;
		private final List values;
		
		@SuppressWarnings("unchecked")
		public MyArrayAdapter(Context c, List v) {
			super(c, R.layout.rowlayout, v);
			// TODO Auto-generated constructor stub
			this.context = c;
			this.values = v;
		}
		
		@Override
		public View getView(int position, View viewConvert, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)context.
					getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
			
			TextView ssid = (TextView)rowView.findViewById(R.id.ssid);
			TextView frequency = (TextView)rowView.findViewById(R.id.frequency);
			ProgressBar pbLevel = (ProgressBar)rowView.findViewById(R.id.level);
			ScanResult wifi = (ScanResult)values.get(position);
			ssid.setText(wifi.SSID);
			frequency.setText(String.valueOf(wifi.frequency) + " Mhz");
			pbLevel.setProgress(WifiManager.calculateSignalLevel(wifi.level, 101));
			
			return rowView;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		
		Intent intent;
		switch( item.getItemId() ) {
			case R.id.action_info:
				intent = new Intent(this, Info.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
		}
		
		return false;
	}
	
	protected void onPause () {
		Log.d("DBG", "onPause");
		super.onPause();
		
		unregisterReceiver(wifiScanReceiver);
	}
	
	protected void onResume () {
		Log.d("DBG", "onResume");
		registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wifiManager.startScan();
		
		super.onResume();
	}
	
	class WifiScanReceiver extends BroadcastReceiver {
		public void onReceive (Context c, Intent intent) {
			List<ScanResult> wifiScanList = wifiManager.getScanResults();
						
			if( wifiScanList.size() > 0 ) {
				MyArrayAdapter adapter = new MyArrayAdapter(AvailableWifis.this, wifiScanList);
				setListAdapter(adapter);
			}
			dialog.cancel();
		}
	}
}
