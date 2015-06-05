package by.remsens.spectrometer;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ViewSpectrumFragment extends Fragment {
	Spectrum spectrum;
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		    View view =  inflater.inflate(R.layout.view_spectrum_bak, container, false);
		    
		    spectrum = new Spectrum();
		    spectrum.LoadFromFile(this.getArguments().getString("SpectrumFileName"));		    
		    int[] SpectrumArray =spectrum.toArray();
		     GraphView.GraphViewData sp[] = new GraphView.GraphViewData[SpectrumArray.length];
		      for (int i =0;  i <SpectrumArray.length; i++) {	    	  
		    	  sp[i] = new  GraphView.GraphViewData(i, SpectrumArray[i]);
		      }
		    		  
		      GraphViewSeries spectrumSeries = new GraphViewSeries(sp);
			
			
				 
				GraphView graphView = new LineGraphView(this.getActivity(),"");
				graphView.addSeries(spectrumSeries); // data

				 
				LinearLayout layout = (LinearLayout) view.findViewById(R.id.spectrumView);
				layout.addView(graphView);
	     
	        return view;
	    }

}
