package br.ufpr.ci317wifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class WifiInfo extends Activity {
	private IsWifiEnabled isWifiEn = null;
	private IntentFilter intentFilterWifiState = null;
	private TextView textViewName, textViewAddress, textViewSpeed, textViewStrength;
	private WifiManager wifiManager;
	private android.net.wifi.WifiInfo wifiInfo;
	private String wifiName, wifiAddress;
	int wifiSpeed, wifiStrength;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_info);

		textViewName = (TextView) findViewById(R.id.wifiNameResult);
		textViewSpeed = (TextView) findViewById(R.id.wifiSpeedResult);
		textViewAddress = (TextView) findViewById(R.id.wifiAddressResult);	
		textViewStrength = (TextView) findViewById(R.id.wifiStrengthResult);
		
		isWifiEn = new IsWifiEnabled();
		intentFilterWifiState = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if( wifiManager.isWifiEnabled() ) {
			updateConnInfo();
		}else
			((TextView)findViewById(R.id.wifidisabled)).setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(isWifiEn, intentFilterWifiState);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(isWifiEn);
	}
	
	class IsWifiEnabled extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if( wifiManager.isWifiEnabled() ) {
				((TextView)findViewById(R.id.wifidisabled)).setVisibility(View.GONE);
				/* wait for a second, so update can get correct information */
				//try { Thread.sleep(1000); } catch( Exception e) { e.getLocalizedMessage(); };
				updateConnInfo();
			}else {
				((TextView)findViewById(R.id.wifidisabled)).setVisibility(View.VISIBLE);
				textViewName.setText("");
				textViewSpeed.setText("");
				textViewAddress.setText("");
				textViewStrength.setText("");				
			}
		}
	}
	
	private void updateConnInfo() {
		wifiInfo = wifiManager.getConnectionInfo();
		wifiName = wifiInfo.getSSID();
		wifiSpeed = wifiInfo.getLinkSpeed();
		wifiAddress = wifiInfo.getBSSID();
		wifiStrength = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 10);
		
		String wifiSpeedLink = String.valueOf(wifiSpeed);
		
		/* make sure we've got a valid data */
		if( wifiName != null && 
			wifiAddress != null &&
			wifiSpeed > 0 && wifiStrength > -200 ) {
			textViewName.setText(" " + wifiName);
			textViewSpeed.setText(" " + wifiSpeedLink + " Mbps");
			textViewAddress.setText(" " + wifiAddress);
			textViewStrength.setText(" " + String.valueOf((wifiStrength/10.0) * 100) + "%");
		}else 
			Toast.makeText(this, "Tempo esgotado.", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
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
}
