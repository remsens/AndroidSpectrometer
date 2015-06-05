package by.remsens.spectrometer;

import android.widget.ImageView;
import android.content.Context;
import android.util.AttributeSet;


public class SquareImageView extends ImageView {
	 public SquareImageView(Context context)
	    {
	        super(context);
	    }

	    public SquareImageView(Context context, AttributeSet attrs)
	    {
	        super(context, attrs);
	    }

	    public SquareImageView(Context context, AttributeSet attrs, int defStyle)
	    {
	        super(context, attrs, defStyle);
	    }

	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	    {
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
	    }
}
