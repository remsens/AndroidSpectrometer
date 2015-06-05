package by.remsens.spectrometer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

public class SettingsActivity extends Activity implements OnMenuItemClickListener {
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {	    
	    super.onCreateOptionsMenu(menu);

	    

	    menu.add("Назад")
        .setIcon(R.drawable.arrow_left)
        .setOnMenuItemClickListener(this)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	    
    
	    return true;
	    
	}
	
	   @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.setTitle("Настройки");
	        // Display the fragment as the main content.
	        getFragmentManager().beginTransaction()
	                .replace(android.R.id.content, new SettingsFragment())
	                .commit();
	    }

	@Override
	public boolean onMenuItemClick(MenuItem arg0) {
		finish();
		return true;
	}
	   
	 
	   
	

}
