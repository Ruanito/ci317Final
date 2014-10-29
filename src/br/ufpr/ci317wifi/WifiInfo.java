package br.ufpr.ci317wifi;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class WifiInfo extends Activity {
	
	private TextView textViewName, textViewAddress, textViewSpeed;
	private WifiManager wifiManager;
	private android.net.wifi.WifiInfo wifiInfo;
	private String wifiName, wifiAddress;
	int wifiSpeed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_info);
		
		textViewName = (TextView) findViewById(R.id.wifiNameResult);
		textViewSpeed = (TextView) findViewById(R.id.wifiSpeedResult);
		textViewAddress = (TextView) findViewById(R.id.wifiAddressResult);		
		
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiInfo = wifiManager.getConnectionInfo();
		wifiName = wifiInfo.getSSID();
		wifiSpeed = wifiInfo.getLinkSpeed();
		wifiAddress = wifiInfo.getBSSID();
		
		String wifiSpeedLink = String.valueOf(wifiSpeed);
		
		textViewName.setText(wifiName);
		textViewSpeed.setText(wifiSpeedLink + " Mbps");
		textViewAddress.setText(wifiAddress);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wifi_info, menu);
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
}
