package by.remsens.spectrometer;




import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewPhotoFragment extends Fragment {
	
	
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		    View view =  inflater.inflate(R.layout.view_image_bak, container, false);
		    File imgFile = new File(this.getArguments().getString("PhotoFileName"));
		    if(imgFile.exists()){

		        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

		        ImageView myImage = (ImageView) view.findViewById(R.id.imageView1);
		        myImage.setImageBitmap(myBitmap);

		    }
	     
	        return view;
	    }

}
