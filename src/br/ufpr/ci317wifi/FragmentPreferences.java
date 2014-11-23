package br.ufpr.ci317wifi;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;


public class FragmentPreferences extends Activity {
	public static final String PREF_SIGNAL_DIFF = "PREF_SIGNAL_DIFF";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Display the main fragment as the main content
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
	}
	
	public static class PrefsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			// Load preferences from an XML resource
			addPreferencesFromResource(R.xml.userpreferences);
		}
	}
}
