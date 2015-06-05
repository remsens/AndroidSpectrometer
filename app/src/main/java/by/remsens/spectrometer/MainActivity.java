package by.remsens.spectrometer;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import by.remsens.spectrometer.LiveSpectrumView.Type;
import by.remsens.spectrometer.R.id;


public class MainActivity extends Activity implements OnMenuItemClickListener, SensorEventListener {

      public static final int MEDIA_TYPE_IMAGE = 1;
	  
	  private Camera mCamera;
	  private CameraPreview mPreview;
	   
	  private String PhotoFileName;
	  private String SpecFileName;

      private SensorManager mSensorManager;



      private float[] rotationMatrix;
      private float[] accelData;
      private float[] magnetData;
      private float[] OrientationData;

      public TextView rotateView;




	  int[] SpectrumArray;
	  double lat = -1;
	  double lon = -1;
	  
	  ProgressDialog dialog;
	  private GraphView graphView;
	   
	  final int CAMERA_ID = 0;
	  final boolean FULL_SCREEN = true;
	  
	  public static String timeStamp;
	  private int currentZoomLevel = 0;
	  boolean StopRealTimeSpectrumCapture = false;

	private boolean IsSaveSpectrum = false;


    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            Log.d("Devide connect", "Device connect: " + device);
                            //call method to set up device communication
                        }
                    }
                    else {
                        Log.d("permissioin denied", "permission denied for device " + device);
                    }
                }
            }
        }
    };
    //private LiveSpectrumView LiveSpeView;

	  
	  @Override
		public boolean onCreateOptionsMenu(Menu menu) {	    
		    super.onCreateOptionsMenu(menu);


		    menu.add("Снять")
		    .setIcon(R.drawable.camera)
	        
	        .setOnMenuItemClickListener(this)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM  | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		    menu.add("Просмотреть")
		    .setIcon(R.drawable.audio_wave_diskette)
	        
	        .setOnMenuItemClickListener(this)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM  | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		    
		    menu.add("Настройки")
	        .setIcon(R.drawable.audio_equalizer)
	        .setOnMenuItemClickListener(this)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM  | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	        

		    
	    
		    return true;
		    
		}
	  

      protected void RegistDevice() {
          UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
          //mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
          IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
          registerReceiver(mUsbReceiver, filter);
          Log.d("Device","connected");
      }

	  

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);	 	 
	    setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);


        rotationMatrix = new float[16];
        accelData = new float[3];
        magnetData = new float[3];
        OrientationData = new float[3];

	    this.setTitle("Мобильный спектрометр");
	    this.getActionBar().setSubtitle(" http://remsens.by");
	    this.findViewById(R.id.captureBtn).setEnabled(false);

        RegistDevice();
	    new CheckDeviceConnection(this).execute();
	    
	  
	  }  	  	  	  
	  
	  public void startProgressDialog() {
		  	dialog = new ProgressDialog(this);
		    dialog.setMessage("Съемка...");
		    dialog.setIndeterminate(true);
		    dialog.setCanceledOnTouchOutside(false);
	
		    dialog.show();
	  }
	  
	  public void setFileName(String FileName) {
		  
		  		PhotoFileName = FileName;
		  		viewPhoto();
	  }
	  
	  public void setSpecFileName(String FileName) {
		  SpecFileName = FileName;
		  viewPhoto();
	  }
	  
	  public void saveLocationFile() {
		  String LocationFileName;
		  LocationFileName = SpecFileName.replace("SPE", "LOC").replace(".spe", ".loc");
          SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
          int FrameX1 = Integer.valueOf(sharedPref.getString("FrameX1","100"));
          int FrameY1 = Integer.valueOf(sharedPref.getString("FrameY1","100"));
          int FrameX2 = Integer.valueOf(sharedPref.getString("FrameX2","200"));
          int FrameY2 = Integer.valueOf(sharedPref.getString("FrameY2","200"));
			FileOutputStream fos;
		    try {
				fos = new FileOutputStream(LocationFileName);
				OutputStreamWriter dos = new OutputStreamWriter(fos);
				dos.write(String.valueOf(this.lat)+"\n");
				dos.write(String.valueOf(this.lon)+"\n");
                dos.write(rotateView.getText().toString());
                dos.write(String.valueOf(FrameX1)+"\n");
                dos.write(String.valueOf(FrameY1)+"\n");
                dos.write(String.valueOf(FrameX2)+"\n");
                dos.write(String.valueOf(FrameY2));
				dos.close();
			} catch (FileNotFoundException e1) {
				
				e1.printStackTrace();
			} catch (IOException e) {
	
				e.printStackTrace();
			}
			
			
		  
	  }
	  
	  public void setSpectrum(int[] spectrumArray) {
		  		SpectrumArray = spectrumArray;
		  		viewPhoto();
	  }
	  
	  public void  viewPhoto() {
		  if (SpecFileName!=null) Log.d("SpecFileName",SpecFileName);
		  //if ((PhotoFileName!=null) && (SpecFileName!=null) && (lat>0) && (lon>0)) {
		  if ((PhotoFileName!=null) && (SpecFileName!=null)) {
			  dialog.dismiss();
			  IsSaveSpectrum = false;
			  Intent intent = new Intent(this, ViewOneActivity.class);
			  Bundle b = new Bundle();
			  b.putString("PhotoFileName", PhotoFileName); //Your id
			  b.putString("SpectrumFileName", SpecFileName);
			  saveLocationFile();
		
			  intent.putExtras(b); //Put your id to your next Intent
			  startActivityForResult(intent, 0);
			  
		
		  }
	  }
	  
	    public static Camera getCameraInstance(){
	        Camera c = null;
	        try {
	            c = Camera.open(0); // attempt to get a Camera instance
	            c.setDisplayOrientation(90);

	            
	        }
	        catch (Exception e){
	            // Camera is not available (in use or does not exist)
	        }
	        return c; // returns null if camera is unavailable
	    	    	   
	    }
	    
	    
	 
	    
	  @Override
	    protected void onResume()
	    {
	    	super.onResume();



	    	setContentView(R.layout.activity_main);
	    	  // Create an instance of Camera
	        mCamera = getCameraInstance();

            this.rotateView = (TextView)findViewById(id.RotateLabel);
	               
	        // Create our Preview view and set it as the content of our activity.
	        mPreview = new CameraPreview(this, mCamera);
	        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);                
	        preview.addView(mPreview);
	        
	 
	        RelativeLayout relativeLayoutControls = (RelativeLayout) findViewById(R.id.controls_layout);
	        relativeLayoutControls.bringToFront();

            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI );
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI );


            final MainActivity mainActivity = this;
	        
	        final PictureCallback mPicture = new PictureCallback() {

	            @Override
	            public void onPictureTaken(byte[] data, Camera camera) {
	            	
	                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
	                if (pictureFile == null){
	                    Log.d("1", "Error creating media file, check storage permissions: ");
	                    return;
	                }

	                try {
	                    FileOutputStream fos = new FileOutputStream(pictureFile);
	                    fos.write(data);
	                    fos.close();
	                    
	                    mainActivity.setFileName(pictureFile.getAbsolutePath());
	                    //mCamera.setDisplayOrientation(180);
	                  //  mCamera.startPreview();
	                    
	                } catch (FileNotFoundException e) {
	                    Log.d("1", "File not found: " + e.getMessage());
	                } catch (IOException e) {
	                    Log.d("!", "Error accessing file: " + e.getMessage());
	                }
	            }
	        };
	        
	        Button captureButton = (Button) findViewById(R.id.captureBtn);
	        captureButton.setOnClickListener(
	            new View.OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                  	PhotoFileName = null;
	                	SpectrumArray = null;
	                	SpecFileName = null;
	                	timeStamp = getTimeStamp();
	                	startProgressDialog();
	                    mCamera.takePicture(null, null, mPicture);
	                    IsSaveSpectrum=true;
	            		//new CaptureSpectrumTask(mainActivity,20).execute();
	                    
	                }
	            }
	        );
	        
	  
	     LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	     RelativeLayout relativeLayout = (RelativeLayout) mainActivity.findViewById(R.id.controls_layout);

	     // Define a listener that responds to location updates
	     LocationListener locationListener = new LocationListener() {
	         public void onLocationChanged(Location location) {
	           // Called when a new location is found by the network location provider.
	        	 lat = location.getLatitude();
	        	 lon = location.getLongitude();
                 String coordinateString=" "+lat+" " + lon;/*  = String.format("N: %5.3f, E: %5.3f",lat,lon);*/



                 android.widget.TextView CoordinateLabel = (android.widget.TextView)mainActivity.findViewById(R.id.CoordinateLabel);
                 CoordinateLabel.setText(coordinateString);


	           
	         }

	         public void onStatusChanged(String provider, int status, Bundle extras) {}

	         public void onProviderEnabled(String provider) {}

	         public void onProviderDisabled(String provider) {}
	       };

	     // Register the listener with the Location Manager to receive location updates
	     locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	     locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	     
	        
	       
	        relativeLayout.performClick();
	        
	        
	        //Cameta zoom
	        ZoomControls zoomControls = (ZoomControls) findViewById(R.id.CAMERA_ZOOM_CONTROLS);

	        final Parameters params = mCamera.getParameters();
	        
	        

	        final int maxZoomLevel = params.getMaxZoom();

	        zoomControls.setIsZoomInEnabled(true);
	            zoomControls.setIsZoomOutEnabled(true);

	            zoomControls.setOnZoomInClickListener(new OnClickListener(){
	                public void onClick(View v){
	                	
	                        if(currentZoomLevel < maxZoomLevel){	                        	
	                            currentZoomLevel++;
	                            params.setZoom(currentZoomLevel);
	                            mCamera.setParameters(params);
	                            /*mCamera.stopSmoothZoom();
	                            mCamera.startSmoothZoom(currentZoomLevel);*/
	                            plotFrame();
	                        }
	                }
	            });

	        zoomControls.setOnZoomOutClickListener(new OnClickListener(){
	                public void onClick(View v){
	                        if(currentZoomLevel > 0){
	                            currentZoomLevel--;
	                            params.setZoom(currentZoomLevel);
	                            mCamera.setParameters(params);
	                            /*mCamera.stopSmoothZoom();
	                            mCamera.startSmoothZoom(currentZoomLevel);*/


	                            plotFrame();
	                        }
	                }
	            });    
	      
	  
	        
	        //GraphView Real Time
	        LinearLayout layout = (LinearLayout) this.findViewById(R.id.LiveSpectrumWidget);
	        graphView = new LineGraphView(this,"");	        
	        layout.addView(graphView);
	        StopRealTimeSpectrumCapture= false;
	        new LiveSpectrumView(this,15,Type.REALTIME).execute();
	        
	    }
	  
	  
	  public void plotSpectrum(Spectrum spectrum, int ET) {
		  if (spectrum!=null) {
			  
			  
		  int[] SpectrumArray =spectrum.toArray();
		     GraphView.GraphViewData sp[] = new GraphView.GraphViewData[SpectrumArray.length];
		      for (int i =0;  i <SpectrumArray.length; i++) {	    	  
		    	  sp[i] = new  GraphView.GraphViewData(i, SpectrumArray[i]);
		      }
		      
		      if (ET<3) {
		    	  ET =3;
		    	//  Toast.makeText(this, "Слишком малеcькое время экспозиции", Toast.LENGTH_LONG).show();
		      }
		     if (ET>1000) {
		    	 ET=1000;
		    	// Toast.makeText(this, "Слишком большое время экспозиции", Toast.LENGTH_LONG).show();
		     }
		    		  
		      GraphViewSeries spectrumSeries = new GraphViewSeries(sp);
		      	graphView.removeAllSeries();
				graphView.addSeries(spectrumSeries); // data								 										
	  }
		  if (!StopRealTimeSpectrumCapture) {
			  if (this.IsSaveSpectrum)  new LiveSpectrumView(this,ET,Type.SAVE).execute();
			  else new LiveSpectrumView(this,ET,Type.REALTIME).execute();
		  }
	  }
	  
	  
	  private void plotFrame() {
		  if (mCamera!=null) {
		  Parameters camParams = mCamera.getParameters();
		  List<Integer> ZoomRation = camParams.getZoomRatios();
		  
	/*	  RelativeLayout relativeLayoutControls = (RelativeLayout) findViewById(R.id.controls_layout);
		    final int width = relativeLayoutControls.getWidth();
	        final int height = relativeLayoutControls.getHeight();*/
	        
	     
	        //relativeLayoutControls.notify();
			 SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			  

		  
		  
		   final View left_face = this.findViewById(id.rect_left_face);
	        final View right_face = this.findViewById(id.rect_right_face);
	        final View top_face = this.findViewById(id.rect_top_face);
	        final View bottom_face = this.findViewById(id.rect_bottom_face);
		  
	      final double zoomLevel = ZoomRation.get(currentZoomLevel)/100.0-1;  
	      //Log.d("zoomLevel1",""+zoomLevel);
	      int FrameX1 = Integer.valueOf(sharedPref.getString("FrameX1","252"));
	      int FrameY1 = Integer.valueOf(sharedPref.getString("FrameY1","255"));
	      int FrameX2 = Integer.valueOf(sharedPref.getString("FrameX2","450"));
	      int FrameY2 = Integer.valueOf(sharedPref.getString("FrameY2","418"));

		  final int x1 = (int) (FrameX1 - (FrameX2-FrameX1)*zoomLevel);
		  final int y1 =  (int) (FrameY1 - (FrameY2-FrameY1)*zoomLevel);
		  final int x2 =  (int) (FrameX2 + (FrameX2-FrameX1)*zoomLevel);
		  final int y2 =  (int) (FrameY2 + (FrameY2-FrameY1)*zoomLevel);
	      
		  left_face.setLeft(x1);
	      left_face.setTop(y1);
	      left_face.setRight(x1+4);
	      left_face.setBottom(y2);
	      
	      right_face.setLeft(x2);
	      right_face.setTop(y1);
	      right_face.setRight(x2+4);
	      right_face.setBottom(y2+4);
	      
	      top_face.setLeft(x1);
		  top_face.setTop(y1);
		  top_face.setRight(x2+4);
		  top_face.setBottom(y1+4);
		  
		  bottom_face.setLeft(x1);
		  bottom_face.setTop(y2);
		  bottom_face.setRight(x2+4);
		  bottom_face.setBottom(y2+4);

		  
      left_face.addOnLayoutChangeListener(new OnLayoutChangeListener() {
        
			@Override
			public void onLayoutChange(View arg0, int arg1, int arg2,
					int arg3, int arg4, int arg5, int arg6, int arg7,
					int arg8) {
			     //  Log.d("height",""+height);
				
				  left_face.setLeft(x1);
			      left_face.setTop(y1);
			      left_face.setRight(x1+4);
			      left_face.setBottom(y2);
			      				
				
			}} );
       
       right_face.addOnLayoutChangeListener(new OnLayoutChangeListener() {	        
			@Override
			public void onLayoutChange(View arg0, int arg1, int arg2,
					int arg3, int arg4, int arg5, int arg6, int arg7,
					int arg8) {

			      right_face.setLeft(x2);
			      right_face.setTop(y1);
			      right_face.setRight(x2+4);
			      right_face.setBottom(y2+4);
			      					
				
			}} );
       
      top_face.addOnLayoutChangeListener(new OnLayoutChangeListener() {	        
			@Override
			public void onLayoutChange(View arg0, int arg1, int arg2,
					int arg3, int arg4, int arg5, int arg6, int arg7,
					int arg8) {

				  top_face.setLeft(x1);
				  top_face.setTop(y1);
				  top_face.setRight(x2);
				  top_face.setBottom(y1+4);
			      				
				
			}} );

       bottom_face.addOnLayoutChangeListener(new OnLayoutChangeListener() {	        
			@Override
			public void onLayoutChange(View arg0, int arg1, int arg2,
					int arg3, int arg4, int arg5, int arg6, int arg7,
					int arg8) {

				  bottom_face.setLeft(x1);
				  bottom_face.setTop(y2);
				  bottom_face.setRight(x2);
				  bottom_face.setBottom(y2+4);
			      					
				
			}} );	           
		  }
	  }
	  
	  public void onWindowFocusChanged (boolean hasFocus){
		    super.onWindowFocusChanged(hasFocus);
		   
		  
			  plotFrame();

		}

	    
	    @Override
	    protected void onPause()
	    {
	    	super.onPause();
	    	StopRealTimeSpectrumCapture=true;

            mSensorManager.unregisterListener(this);

	    	releaseCamera();
	    	
	    }
	    
	    private void releaseCamera(){
	        if (mCamera != null){
	            mCamera.release();        // release the camera for other applications
	            mCamera = null;
	        }
	    }


    public void onSensorChanged(SensorEvent event) {
        loadNewSensorData(event);
        SensorManager.getRotationMatrix(rotationMatrix, null, accelData, magnetData);
        SensorManager.getOrientation (rotationMatrix, OrientationData);
        int xy= (int) Math.round(Math.toDegrees(OrientationData[0]));
        int xz= (int) Math.round(Math.toDegrees(OrientationData[1]));
        int zy= (int) Math.round(Math.toDegrees(OrientationData[2]));

        rotateView.setText("xy="+xy+" xz="+xz+" zy="+zy);

    /*    if((xyView==null)||(xzView==null)||(zyView==null)){
            xyView = (TextView) findViewById(R.id.xyValue);  //
            xzView = (TextView) findViewById(R.id.xzValue);  // Íàøè òåêñòîâûå ïîëÿ äëÿ âûâîäà ïîêàçàíèé
            zyView = (TextView) findViewById(R.id.zyValue);  //
        }

        xyView.setText(String.valueOf();
        xzView.setText(String.valueOf(Math.round(Math.toDegrees(OrientationData[1]))));
        zyView.setText(String.valueOf(Math.round(Math.toDegrees(OrientationData[2]))));*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void loadNewSensorData(SensorEvent event) {

        final int type = event.sensor.getType();

        if (type == Sensor.TYPE_ACCELEROMETER) {
            accelData = event.values.clone();
        }

        if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetData = event.values.clone();
        }
    }

	    
	    private static String getTimeStamp() {
	    	 return new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US).format(new Date());
	    }

	    /** Create a File for saving an image or video */
	    private static File getOutputMediaFile(int type){
	      

	        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	                  Environment.DIRECTORY_PICTURES), "SpecApp");
	        
	     
	        if (! mediaStorageDir.exists()){
	            if (! mediaStorageDir.mkdirs()){
	                Log.d("MyCameraApp", "failed to create directory");
	                return null;
	            }
	        }

	        File mediaFile;
	        if (type == MEDIA_TYPE_IMAGE){
	            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	            "IMG_"+ timeStamp + ".jpg");	     
	        } else {
	            return null;
	        }

	        return mediaFile;
	    }


		@Override
		public boolean onMenuItemClick(MenuItem item) {
			if (item.getTitle().toString().contains("Настройки")) {
				  Intent intent = new Intent(this, SettingsActivity.class);

			
				  startActivityForResult(intent, 0);
			}
			else if (item.getTitle().toString().contains("Просмотреть")) {
				  Intent intent = new Intent(this, ViewAllActivity.class);

					
				  startActivityForResult(intent, 0);
			}
		
			return false;
		}


	}



