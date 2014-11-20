package br.ufpr.ci317wifi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainWifi extends Activity {
	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_wifi);
		
		CreateService();
	}
	
	private void CreateService() {
		Log.d("dbg", "MainWifi.CreateService ThreadId="+String.valueOf(Thread.currentThread().getId()));
		Intent serviceIntent = new Intent(this, WifiDiscoverService.class);
		startService(serviceIntent);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_wifi, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		switch (id) {
			case R.id.action_setting:
				intent = new Intent(this, Settings.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			case R.id.action_info:
				intent = new Intent(this, Info.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void wifiInfo (View view) {
		intent = new Intent(this, WifiInfo.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public void wifiLists (View view) {
		intent = new Intent(this, Lists.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public void wifiListSave (View view) {
		intent = new Intent(this, Lists_Save.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
