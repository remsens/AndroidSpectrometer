package by.remsens.spectrometer;

import android.content.SharedPreferences;

import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        // Load the preferences from an XML resource
	        addPreferencesFromResource(R.xml.preference);
	    }
	 
	  @Override
	public void onResume() {
	       super.onResume();
	       // Set up a listener whenever a key changes
	       getPreferenceScreen().getSharedPreferences()
	               .registerOnSharedPreferenceChangeListener(this);
	       
	       
	       
	       
		    Preference pref =getPreferenceScreen().getPreference(0);

		   
		    	EditTextPreference listPref = (EditTextPreference) pref;
		    	
		    	//listPref.setText(listPref.geD);
		    	
		        pref.setSummary(listPref.getText());
		        
		        
		        pref = getPreferenceScreen().getPreference(1);
		        
		        SharedPreferences sharedPreferences = this.getPreferenceScreen().getSharedPreferences();
		        Integer mCurrentValue = sharedPreferences.getInt("ExposureTime", 50);
		       
		        boolean mAutoExposure = sharedPreferences.getBoolean("IsAutoExposureTime", true);    	
		        if (mAutoExposure) {
		        	pref.setSummary("Автоматически");
		        }
		        else {
		        	pref.setSummary(""+mCurrentValue);
		        }
		        
		        
		        ((EditTextPreference)this.findPreference("FrameX1")).setSummary(sharedPreferences.getString("FrameX1", "100"));
		        ((EditTextPreference)this.findPreference("FrameY1")).setSummary(sharedPreferences.getString("FrameY1", "100"));		        
		        ((EditTextPreference)this.findPreference("FrameX2")).setSummary(sharedPreferences.getString("FrameX2", "200"));
		        ((EditTextPreference)this.findPreference("FrameY2")).setSummary(sharedPreferences.getString("FrameY2", "200"));
	
		        
		   
	       
	       
	       //кmCurrentValue= sharedPreferences.getInt("ExposureTime", 50);
	   }

	   @Override
	public void onPause() {
	       super.onPause();
	       // Unregister the listener whenever a key changes
	       getPreferenceScreen().getSharedPreferences()
	               .unregisterOnSharedPreferenceChangeListener(this);
	   }

	   @Override
			 public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				    Preference pref = findPreference(key);

				    if (pref instanceof EditTextPreference) {
				    	EditTextPreference listPref = (EditTextPreference) pref;
				        pref.setSummary(listPref.getText());
				        
				    }
				}
}
