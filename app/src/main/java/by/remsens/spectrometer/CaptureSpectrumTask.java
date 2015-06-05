package by.remsens.spectrometer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.FT_Device;

import java.io.File;

public class CaptureSpectrumTask extends AsyncTask<Void, Void, Void> {
	private Activity activity;
	private int devCount = 0;
	//private Spectrum spectrum;	 	 
	public static D2xxManager ftD2xx= null; 
	private FT_Device ftDev = null; 
	private int ExposureTime;  
	private int SplitNumber=10;
	String FileName;
	boolean isAutoExposureMode;
	//public enum Type {REALTIME,   SAVE};
	
	 
	 CaptureSpectrumTask(Activity activity,int ExposureTime) {
		 try {
			ftD2xx = D2xxManager.getInstance(activity);
			
		} catch (D2xxException e) {
			e.printStackTrace();
		} 
		 devCount = ftD2xx.createDeviceInfoList(activity); 

		 this.activity = activity;
		 SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
		  
		 this.ExposureTime = sharedPref.getInt("ExposureTime", 50);
		 this.SplitNumber = Integer.valueOf(sharedPref.getString("SpectrumSplitNumber", "10"));
		 this.isAutoExposureMode = sharedPref.getBoolean("IsAutoExposureTime", true);

			
			
	 }
	 
	private int exposureFind() {
		int k=0;
		int ET = 10;
		while (k<=20 ) {
		ftDev = ftD2xx.openByIndex(activity, 0);
		
		ftDev.setBaudRate(921600);
		ftDev.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8, 
			D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE); 			
			ftDev.setFlowControl(D2xxManager.FT_FLOW_NONE, (byte) 0x0b, (byte) 
					0x0d); 		
		double tick = 256/41.78;
					
		ftDev.write(new FtdiPacket(FtdiPacket.SET_EXP,ET,0,0).getByteArray());
		new FtdiPacket(ftDev).readParam();	    								
		Spectrum spe = new Spectrum();			
		ftDev.write(new FtdiPacket(FtdiPacket.GET_SP,0,0,0).getByteArray());
		DataPacket mDataPacket = new DataPacket(ftDev);
		spe.addSpectrum(mDataPacket.getSpectrum());	
		int MaxSignal = spe.getMaxSignal();
		if (MaxSignal<1000) ET*=2;
		else if (MaxSignal>4000) ET/=2;
		else {ftDev.close(); break;}
		Log.d("MaxSignal-2",k+" "+ET+" "+MaxSignal);
		ftDev.close();
		k++;
		}
		return ET; 
		
	}
	 
	@Override
	protected Void doInBackground(Void... arg0) {
		
		if  (devCount== 1) {
		    int ET = 	ExposureTime;
		    if (isAutoExposureMode) ET = exposureFind();
		
			ftDev = ftD2xx.openByIndex(activity, 0);
	
			ftDev.setBaudRate(921600);
			ftDev.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8, 
			D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE); 			
			ftDev.setFlowControl(D2xxManager.FT_FLOW_NONE, (byte) 0x0b, (byte) 
					0x0d); 		
			
						
			double tick = 256/41.78;
			

			
			ftDev.write(new FtdiPacket(FtdiPacket.SET_EXP, ET, 0, 0).getByteArray());
			new FtdiPacket(ftDev).readParam();
		    
			ftDev.close();
			
			
			
		
			Spectrum spectrum = new Spectrum();
			
			
			
			for (int i  = 0 ; i < SplitNumber; i++ ) {
				ftDev = ftD2xx.openByIndex(activity, 0);
				
				ftDev.setBaudRate(921600);
				ftDev.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8, 
				D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE); 			
				ftDev.setFlowControl(D2xxManager.FT_FLOW_NONE, (byte) 0x0b, (byte) 
						0x0d); 			
			
			ftDev.write(new FtdiPacket(FtdiPacket.GET_SP,0,0,0).getByteArray());
			DataPacket mDataPacket = new DataPacket(ftDev);
			spectrum.addSpectrum(mDataPacket.getSpectrum());
			ftDev.close();
			}
	
			spectrum.devideOnValue(SplitNumber);
			FileName = getOutputSpectrumFile().getPath();
			spectrum.SaveToFIle(FileName);
			int MaxSignal = spectrum.getMaxSignal();
			Log.d("MaxSignal",ExposureTime+" "+MaxSignal);
																																										
			}
			
			
			
		
		
		return null;
	}
	
	
	 @Override
	    protected void onPostExecute(Void result) {
	      super.onPostExecute(result);	
	      ((MainActivity)activity).setSpecFileName(FileName);
	      
	    //  ((MainActivity)activity).setSpectrum(spectrum.toArray());	      	 
	    }
	 
	    /** Create a File for saving an image or video */
	    private static File getOutputSpectrumFile(){
	        // To be safe, you should check that the SDCard is mounted
	        // using Environment.getExternalStorageState() before doing this.

	        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	                  Environment.DIRECTORY_PICTURES), "SpecApp");
	        
	        // This location works best if you want the created images to be shared
	        // between applications and persist after your app has been uninstalled.

	        // Create the storage directory if it does not exist
	        if (! mediaStorageDir.exists()){
	            if (! mediaStorageDir.mkdirs()){
	                Log.d("MyCameraApp", "failed to create directory");
	                return null;
	            }
	        }

	        // Create a media file name
	        String timeStamp = MainActivity.timeStamp;
	        File speFile;	     
	        speFile = new File(mediaStorageDir.getPath() + File.separator +
	            "SPE_"+ timeStamp + ".spe");	     
	        return speFile;
	    }

	

}
