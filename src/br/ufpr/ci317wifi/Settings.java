package br.ufpr.ci317wifi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Settings extends Activity {
	
	Intent intent;
	public static final String PREFS_NAME = "MyDiff";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
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
	
	public void saveDiff (View view) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    TextView textView = (TextView)findViewById(R.id.editDiff);
	    
	    int diff = Integer.valueOf(textView.getText().toString());
	    
	    editor.putInt("diff", diff);
	    
	    editor.commit();
	    
	    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	    alertDialog.setTitle("Salvo");
	    alertDialog.setMessage("Configurações salvas!");
	    alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
	    });
	    
	    alertDialog.show();
	    textView.setText("");

		return;
	}
}
