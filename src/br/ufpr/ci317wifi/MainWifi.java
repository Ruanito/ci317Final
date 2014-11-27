package br.ufpr.ci317wifi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainWifi extends Activity {
	public static final String TAG = "dbg";
	
	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_wifi);
		
		updateFromPreferences();
		CreateService();
	}

	private void CreateService() {
		Log.d(TAG, "MainWifi.CreateService ThreadId="+String.valueOf(Thread.currentThread().getId()));
		Intent serviceIntent = new Intent(this, WifiDiscoverService.class);
		startService(serviceIntent);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	private final int SHOW_PREFERENCES = 1;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch( item.getItemId() ) {
			case R.id.action_info: {
				intent = new Intent(this, Info.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			}
			case R.id.action_settings: {
				intent = new Intent(this, FragmentPreferences.class);
				startActivityForResult(intent, SHOW_PREFERENCES);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if( requestCode == SHOW_PREFERENCES )
			updateFromPreferences();
	}
	
	private void updateFromPreferences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		int signal_diff = -1;

		signal_diff = Integer.valueOf(prefs.getString(FragmentPreferences.PREF_SIGNAL_DIFF, "10"));
		if( signal_diff == -1 )
			signal_diff = 10;
		
		Log.i(TAG, "MainWifi.updateFromPreferences signal_diff=" + String.valueOf(signal_diff));
		WifiDiscoverService.threshold_signal = signal_diff;  
	}
	
	public void wifiInfo (View view) {
		intent = new Intent(this, WifiInfo.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public void wifiLists (View view) {
		intent = new Intent(this, AvailableWifis.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public void wifiListSave (View view) {
		intent = new Intent(this, KnownWifis.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
