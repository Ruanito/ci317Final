package br.ufpr.ci317wifi;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class KnownWifis extends ListActivity {
	private static final int CURRENT = 0;
	private static final int DISABLED = 1;
	private static final int ENABLED = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> wifisconfig = wifiManager.getConfiguredNetworks();
		
		if( wifisconfig.size() > 0 ) {
			MyArrayAdapter adapter = new MyArrayAdapter(this, wifisconfig);
			setListAdapter(adapter);
		}
		
		super.onResume();
	}
	
	@SuppressWarnings("rawtypes")
	@SuppressLint("ViewHolder")
	class MyArrayAdapter extends ArrayAdapter<List> {
		@SuppressWarnings("rawtypes")
		private List wifisConfig;
		
		public MyArrayAdapter(Context c, List wifisconfig) {
			super(c, R.layout.rowlayout_known, wifisconfig);
			// TODO Auto-generated constructor stub
			this.wifisConfig = wifisconfig;
		}
		
		@Override
		public View getView(int position, View convView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.rowlayout_known, parent, false);
			
			WifiConfiguration wc = (WifiConfiguration)wifisConfig.get(position);
			TextView ssid = (TextView)row.findViewById(R.id.ssid);
			CheckBox hidden = (CheckBox)row.findViewById(R.id.hidden);
			ImageView img = (ImageView)row.findViewById(R.id.icon);
			
			if( wc.status == DISABLED )
				ssid.setTextColor(Color.RED);
			ssid.setText(wc.SSID.replaceAll("\"", ""));
			if( wc.status == CURRENT )
				img.setImageResource(R.drawable.yes);
			
			hidden.setChecked(wc.hiddenSSID);
			
			return row;
		}
	}

}
