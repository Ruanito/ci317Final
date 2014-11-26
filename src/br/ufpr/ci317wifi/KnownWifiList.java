package br.ufpr.ci317wifi;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class KnownWifiList extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
		        "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
		        "Linux", "OS/2" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.ssid, values);
		setListAdapter(adapter);
	}
}
