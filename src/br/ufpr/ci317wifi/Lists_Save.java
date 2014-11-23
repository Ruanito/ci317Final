package br.ufpr.ci317wifi;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Lists_Save extends Activity {
	WifiManager wifiManager;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lists_save);
		
		wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
	}

	@Override
	public void onResume(){
		List<WifiConfiguration> wConfigs = wifiManager.getConfiguredNetworks();
		if( wConfigs != null ) {
			String[] ssids = new String[wConfigs.size()];
			
			int i = 0;
			for( WifiConfiguration wc : wConfigs ) {
			   /*
	    		* WARNING: this is a workaround since getConfiguredNetworks()
	    		*          fill up the name in double quotes.
	    		*/
				ssids[i++] = wc.SSID.replaceAll("\"", "");
			}
			
			ListView lv = (ListView)findViewById(R.id.wifiListSave);
			lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, ssids));
		}

		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lists__save, menu);
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
}
