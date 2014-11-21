package br.ufpr.ci317wifi;

import java.util.List;

import android.preference.PreferenceActivity;

public class FragmentPreferences extends PreferenceActivity {
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_header, target);
	}
}
