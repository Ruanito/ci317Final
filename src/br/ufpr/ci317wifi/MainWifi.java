package br.ufpr.ci317wifi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainWifi extends Activity {

	Intent intent;
	int two = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_wifi);
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		
		display.getSize(size);
		
		int width = size.x;
		int height = size.y;
		/*
		Button buttonInfo = (Button) findViewById(R.id.wifiInfo);
		Button buttonList = (Button) findViewById(R.id.wifiList);
		Button buttonSave = (Button) findViewById(R.id.wifiListSave);
		
		buttonInfo.getLayoutParams().width = (width);
		buttonList.getLayoutParams().width = (width / two);
		buttonSave.getLayoutParams().width = (width / two);
		*/
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
		if (id == R.id.action_settings) {
			return true;
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
