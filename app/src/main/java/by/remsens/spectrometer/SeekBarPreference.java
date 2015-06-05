package by.remsens.spectrometer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SeekBarPreference extends DialogPreference implements
		OnSeekBarChangeListener {


    // Namespaces to read attributes
    private static final String PREFERENCE_NS = "http://schemas.android.com/apk/res/by.remsens.spectrometer";
 //   private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    // Attribute names
 //   private static final String ATTR_DEFAULT_VALUE = "defaultValue";
    private static final String ATTR_MIN_VALUE = "minValue";
    private static final String ATTR_MAX_VALUE = "maxValue";
//    private static final String ATTR_AUTO_VALUE = "isAuto";

    // Default values for defaults
    private static final int DEFAULT_CURRENT_VALUE = 50;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final boolean DEFAULT_AUTO_VALUE = true;

    // Real defaults
  //  private final int mDefaultValue;
    private final int mMaxValue;
    private final int mMinValue;
  //  private final boolean mDefaultAuto;
    
    
    // Current value
    private int mCurrentValue;
    private boolean mAutoExposure;
    
    // View elements
    private SeekBar mSeekBar;
    private TextView mValueText;
    private ToggleButton mToggleButton;

    public SeekBarPreference(Context context, AttributeSet attrs) {
	super(context, attrs);

	// Read parameters from attributes
	mMinValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIN_VALUE, DEFAULT_MIN_VALUE);
	mMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MAX_VALUE, DEFAULT_MAX_VALUE);
//	mDefaultValue = attrs.getAttributeIntValue(ANDROID_NS, ATTR_DEFAULT_VALUE, DEFAULT_CURRENT_VALUE);
//	mDefaultAuto = attrs.getAttributeBooleanValue(PREFERENCE_NS, ATTR_AUTO_VALUE, DEFAULT_AUTO_VALUE);
    }

    @Override
    protected View onCreateDialogView() {
	// Get current value from preferences
    	
    SharedPreferences sharedPreferences = getSharedPreferences();
    mCurrentValue= sharedPreferences.getInt("ExposureTime", DEFAULT_CURRENT_VALUE);
   
    mAutoExposure = sharedPreferences.getBoolean("IsAutoExposureTime", DEFAULT_AUTO_VALUE);    	
  
	
	

	// Inflate layout
	LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View view = inflater.inflate(R.layout.dialog_slider, null);

	// Setup minimum and maximum text labels
	((TextView) view.findViewById(R.id.min_value)).setText(Integer.toString(mMinValue));
	((TextView) view.findViewById(R.id.max_value)).setText(Integer.toString(mMaxValue));

	// Setup SeekBar
	mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);
	mSeekBar.setMax(mMaxValue-mMinValue);
	mSeekBar.setProgress(mCurrentValue );
	mSeekBar.setOnSeekBarChangeListener(this);

	// Setup text label for current value
	mValueText = (TextView) view.findViewById(R.id.current_value);
	mValueText.setText(Integer.toString(mCurrentValue));
	
	mToggleButton = (ToggleButton)view.findViewById(R.id.is_auto_expossure);
	mToggleButton.setChecked(mAutoExposure);
	if (mAutoExposure) mSeekBar.setEnabled(false);
	else mSeekBar.setEnabled(true);
	
	
	mToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean checked) {
			if (checked) {
				mSeekBar.setEnabled(false);
				mAutoExposure= true;
				
			}
			else {
				mSeekBar.setEnabled(true);
				mAutoExposure=false;
			}
		
			
		}
		
	});

	return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
	super.onDialogClosed(positiveResult);

	// Return if change was cancelled
	if (!positiveResult) {
	    return;
	}
	
	Editor editor = getEditor();
    editor.putInt("ExposureTime", mCurrentValue);
    editor.putBoolean("IsAutoExposureTime", mAutoExposure);
    editor.commit();
	

	if (mAutoExposure) 	super.setSummary("Автоматически");
	else super.setSummary(Integer.toString(mCurrentValue));
	// Notify activity about changes (to update preference summary line)
	notifyChanged();
    }

/*    @Override
    public CharSequence getSummary() {
	// Format summary string with current value
	String summary = super.getSummary().toString();		
//	int value = Integer.valueOf(getPersistedString(String.valueOf(mDefaultValue)));
	return String.format(summary, 60);
    }*/
    
    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
	// Update current value
	mCurrentValue = value + mMinValue;
	// Update label with current value
	mValueText.setText(Integer.toString(mCurrentValue));
    }

    public void onStartTrackingTouch(SeekBar seek) {
	// Not used
    }

    public void onStopTrackingTouch(SeekBar seek) {
	// Not used
    }
}
