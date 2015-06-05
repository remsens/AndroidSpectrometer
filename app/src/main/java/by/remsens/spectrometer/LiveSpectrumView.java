package by.remsens.spectrometer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.FT_Device;

import java.io.File;

public class LiveSpectrumView extends AsyncTask<Void,Void, Void> {
	private Activity activity;
	private int devCount = 0;
	int ET;
		 	 
	public static D2xxManager ftD2xx= null; 
	private FT_Device ftDev = null; 
	private Spectrum spectrum;
	
	public enum Type {REALTIME,   SAVE};
	private Type type;
	int SplitNumber = 10;
	
	String FileName;


	LiveSpectrumView(Activity activity, int ET, Type type) {
		 try {
				ftD2xx = D2xxManager.getInstance(activity);
				
			} catch (D2xxException e) {
				e.printStackTrace();
			} 
			 devCount = ftD2xx.createDeviceInfoList(activity); 
			 this.activity = activity;
			 this.ET=ET;
			 this.type = type;
			 
	}
	
	private void LiveView() {
		if  (devCount== 1) {				
			ftDev = ftD2xx.openByIndex(activity, 0);
	
			ftDev.setBaudRate(921600);
			ftDev.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8, 
			D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE); 			
			ftDev.setFlowControl(D2xxManager.FT_FLOW_NONE, (byte) 0x0b, (byte) 
					0x0d);
            ftDev.setBitMode((byte)0,(byte)0x0);
            FtdiPacket ExpPacket2 = new FtdiPacket(FtdiPacket.SET_EXP, ET,0,0);
			ftDev.write(ExpPacket2.getByteArray());
            FtdiPacket ExpPacket = new FtdiPacket(ftDev);
			spectrum = new Spectrum();			
			ftDev.write(new FtdiPacket(FtdiPacket.GET_SP,0,0,0).getByteArray());
			DataPacket mDataPacket = new DataPacket(ftDev);
			spectrum.addSpectrum(mDataPacket.getSpectrum());	
			int MaxSignal = spectrum.getMaxSignal();
			if (MaxSignal<1000) ET*=2;
			else if (MaxSignal>3000) ET/=2;
            if (ET < 15) ET = 15;
			ftDev.close();
			}
	}


    private void autoExposure() {
        int MaxSignal = 0;
        if  (devCount== 1) {
            do {
                ftDev = ftD2xx.openByIndex(activity, 0);

                ftDev.setBaudRate(921600);
                ftDev.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8,
                        D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE);
                ftDev.setFlowControl(D2xxManager.FT_FLOW_NONE, (byte) 0x0b, (byte)
                        0x0d);
                ftDev.setBitMode((byte) 0, (byte) 0x0);
                FtdiPacket ExpPacket2 = new FtdiPacket(FtdiPacket.SET_EXP, ET, 0, 0);
                ftDev.write(ExpPacket2.getByteArray());
                FtdiPacket ExpPacket = new FtdiPacket(ftDev);
                spectrum = new Spectrum();
                ftDev.write(new FtdiPacket(FtdiPacket.GET_SP, 0, 0, 0).getByteArray());
                DataPacket mDataPacket = new DataPacket(ftDev);
                spectrum.addSpectrum(mDataPacket.getSpectrum());
                MaxSignal = spectrum.getMaxSignal();
                if (MaxSignal < 1000) ET *= 2;
                else if (MaxSignal > 3000) ET /= 2;
                if (ET < 15) ET = 15;
                ftDev.close();
            } while((MaxSignal<1000) || (MaxSignal>3000));
        }
    }

	private void SaveSpectrum() {
		
		if  (devCount== 1) {
		
			/*ftDev = ftD2xx.openByIndex(activity, 0);
	
			ftDev.setBaudRate(921600);
			ftDev.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8, 
			D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE); 			
			ftDev.setFlowControl(D2xxManager.FT_FLOW_NONE, (byte) 0x0b, (byte) 
					0x0d);
            ftDev.setBitMode((byte)0,(byte)0x0);

            FtdiPacket ExpPacket2 = new FtdiPacket(FtdiPacket.SET_EXP, ET,0,0);
            ftDev.write(ExpPacket2.getByteArray());
            FtdiPacket ExpPacket = new FtdiPacket(ftDev);

			ftDev.close();*/

			
			//Для двух 
			
		
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
			}
		
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		if (type == Type.REALTIME) LiveView();
		else SaveSpectrum();
			
		

		
		return null;
	}
	
	 @Override
	    protected void onPostExecute(Void result) {
	      super.onPostExecute(result);	
	      if (type == Type.REALTIME) ((MainActivity)activity).plotSpectrum(spectrum,ET);
	      else   ((MainActivity)activity).setSpecFileName(FileName);
	      
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
