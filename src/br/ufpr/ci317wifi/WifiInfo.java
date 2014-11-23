package br.ufpr.ci317wifi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class WifiInfo extends Activity {
	
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
		
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiInfo = wifiManager.getConnectionInfo();
		wifiName = wifiInfo.getSSID();
		wifiSpeed = wifiInfo.getLinkSpeed();
		wifiAddress = wifiInfo.getBSSID();
		wifiStrength = wifiInfo.getRssi();
		
		String wifiSpeedLink = String.valueOf(wifiSpeed);
		String wifiStrengthLink = String.valueOf(wifiStrength);
		
		textViewName.setText(wifiName);
		textViewSpeed.setText(wifiSpeedLink + " Mbps");
		textViewAddress.setText(wifiAddress);
		textViewStrength.setText(wifiStrengthLink);
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
