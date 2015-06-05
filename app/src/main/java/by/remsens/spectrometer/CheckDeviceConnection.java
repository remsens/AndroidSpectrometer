package by.remsens.spectrometer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.D2xxManager.D2xxException;

public class CheckDeviceConnection extends AsyncTask<Void, Void, Void> {
	public static D2xxManager ftD2xx= null; 

	int devCount = 0;
	Activity activity;
	
	
	CheckDeviceConnection(Activity activity) {
		this.activity = activity;

	}
	
	// ProgressDialog dialog;
	 
	 
	
	
	@Override
	protected Void doInBackground(Void... arg0) {
		 try {
				ftD2xx = D2xxManager.getInstance(activity);
				
			} catch (D2xxException e) {
				e.printStackTrace();
			} 
			 devCount = ftD2xx.createDeviceInfoList(activity); 
		// TODO Auto-generated method stub
		return null;
	}
	
	
	 @Override
	    protected void onPostExecute(Void result) {
	      super.onPostExecute(result);		      

	    //  dialog.dismiss();

	      if (devCount==1) {
	    	  ((MainActivity)activity).findViewById(R.id.captureBtn).setEnabled(true);
	    }
	      else {
              Log.d("devCount",""+devCount);
	    	  AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    		builder.setTitle("Внимание!")
	    				.setMessage("Нет соединения со спектрометром! ")
	    				.setCancelable(false)
	    				.setNegativeButton("Закрыть программу",
	    						new DialogInterface.OnClickListener() {
	    							public void onClick(DialogInterface dialog, int id) {
	    								System.exit(0);
	    								
	    							}
	    						});
	    		AlertDialog alert = builder.create();
	    		alert.show();
	    	  
	      }
	 }


	

}
