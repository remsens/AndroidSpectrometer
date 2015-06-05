package by.remsens.spectrometer;


import java.io.File;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;


import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ViewOneActivity extends Activity implements OnMenuItemClickListener {


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
	 
	    
	 
	    setContentView(R.layout.activity_view_one);
	    

	    Bundle bundle = getIntent().getExtras();
	    PhotoFileName = bundle.getString("PhotoFileName");
	    SpectrumFileName = bundle.getString("SpectrumFileName");
	
	        
	    File imgFile = new File(PhotoFileName);
	    if(imgFile.exists()){

	        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

	        ImageView myImage = (ImageView) findViewById(R.id.imageView1);
	        myImage.setImageBitmap(myBitmap);

	    }
	    
	    Spectrum spectrum = new Spectrum();
	    spectrum.LoadFromFile(SpectrumFileName);		    
	    int[] SpectrumArray =spectrum.toArray();
	     GraphView.GraphViewData sp[] = new GraphView.GraphViewData[SpectrumArray.length];
	      for (int i =0;  i <SpectrumArray.length; i++) {	    	  
	    	  sp[i] = new  GraphView.GraphViewData(i, SpectrumArray[i]);
	      }
	    		  
	      GraphViewSeries spectrumSeries = new GraphViewSeries(sp);
		
		
			 
			GraphView graphView = new LineGraphView(this,"");
			graphView.addSeries(spectrumSeries); // data

			 
			LinearLayout layout = (LinearLayout) findViewById(R.id.spectrumView);
			layout.addView(graphView);

	    
	    
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
		return false;
	}

}
