package by.remsens.spectrometer;


import java.io.File;

import android.content.Intent;
import android.os.Bundle;


import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;

public class ViewOneActivity_bak extends FragmentActivity implements OnMenuItemClickListener {

	private FragmentTabHost mTabHost;	
	String PhotoFileName;
	String SpectrumFileName;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {	    
	    super.onCreateOptionsMenu(menu);
	    menu.add("Отправить")
        .setIcon(R.drawable.send_envelope_diskette)
        .setOnMenuItemClickListener(this)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

	
	    menu.add("Удалить")
        .setIcon(R.drawable.trash)
        .setOnMenuItemClickListener(this)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	    

	    menu.add("Назад")
        .setIcon(R.drawable.arrow_left)
        .setOnMenuItemClickListener(this)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	    
    
	    return true;
	    
	}
	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	 
	    
	 
	    setContentView(R.layout.activity_view_one_bak);
	    

	    Bundle bundle = getIntent().getExtras();
	    PhotoFileName = bundle.getString("PhotoFileName");
	    SpectrumFileName = bundle.getString("SpectrumFileName");
	
	    
		
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		 
        // Create the tabs in main_fragment.xml
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tabcontent);
 
        // Create Tab1 with a custom image in res folder
       mTabHost.addTab(mTabHost.newTabSpec("photoView").setIndicator("Фотография"),
    		   ViewPhotoFragment.class,bundle);

       mTabHost.addTab(mTabHost.newTabSpec("spectrView").setIndicator("Спектр"),
    		   ViewSpectrumFragment.class,bundle);
       
 

	/*    Log.d("SpecFileName", getIntent().getStringExtra("SpeFileName"));
	    File imgFile = new File(getIntent().getStringExtra("FileName"));
	    if(imgFile.exists()){

	        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

	        ImageView myImage = (ImageView) findViewById(R.id.PhotoView);
	        myImage.setImageBitmap(myBitmap);

	    }
	    
	    
	    int[] SpectrumArray = new Spectrum(getIntent().getStringExtra("SpeFileName")).toArray();;// = getIntent().getIntArrayExtra("SpectrumArray");
	     GraphView.GraphViewData sp[] = new GraphView.GraphViewData[SpectrumArray.length];
	      for (int i =0;  i <SpectrumArray.length; i++) {	    	  
	    	  sp[i] = new  GraphView.GraphViewData(i, SpectrumArray[i]);
	      }
	    		  
	      GraphViewSeries spectrumSeries = new GraphViewSeries(sp);
		
		
			 
			GraphView graphView = new LineGraphView(this,"Спектрум");
			graphView.addSeries(spectrumSeries); // data
			 
			LinearLayout layout = (LinearLayout) findViewById(R.id.SpectrumView);
			layout.addView(graphView);*/
	    
	    
	  }
	  
	  public void SaveBtn_Click(View v) {
		  	this.finish();
	  }

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		if (item.getTitle().toString().contains("Удалить")) {
				File specFile= new File(SpectrumFileName);
				if (specFile.exists()) specFile.delete();
				File photoFile= new File(PhotoFileName);
				if (photoFile.exists()) photoFile.delete();
				Intent intent = new Intent();
				intent.putExtra("Delete",true);
				setResult(RESULT_OK,intent);
				finish();
		}
		if (item.getTitle().toString().contains("Назад")) {
				finish();
		}
		// TODO Auto-generated method stub
		return false;
	}

}
