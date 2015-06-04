package com.mfedele.lamp.layouts;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mfedele.lamp.R;

/**
 * Created by Marco Fedele on 04/06/15.
 * <p/>
 * A Seekbar with an indicator of it's percentage
 */
public class CustomSeekBar extends LinearLayout implements SeekBar.OnSeekBarChangeListener {


    /**
     * Display the current seekBar's value
     */
    private TextView value;

    /**
     * The seekBar
     */
    private SeekBar seekBar;

    /**
     * Minimum value for the seekBar
     */
    private int min = 0;

    /**
     * Maximum value for the seekBar
     */
    private int max = 100;

    /**
     * Current value of the seekBar
     */
    private int currentValue = 50;

    /**
     * Listener for changes of value in the seekBar
     */
    private OnSeekBarChangeListener changeListener;


    // CONSTRUCTORS ///////////////////////////////////////////////////

    public CustomSeekBar(Context context) {
        super(context);
        init(context);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        getAttributes(context, attrs);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        getAttributes(context, attrs);
    }

    ///////////////////////////////////////////////////////////////////////

    /**
     * Get the attributes from the xml, in this case the title, and set it in the corresponding
     * TextView
     *
     * @param attrs   AttributeSet
     * @param context Context
     */
    private void getAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomSeekBar,
                0, 0);

        TextView titleTextView = (TextView) findViewById(R.id.titleSeekBar);
        titleTextView.setVisibility(GONE);

        try {
            String title = a.getString(R.styleable.CustomSeekBar_csTitle);

            if (!TextUtils.isEmpty(title)) {
                titleTextView.setVisibility(VISIBLE);
                titleTextView.setText(title);
            }

            setMaxValue(a.getInt(R.styleable.CustomSeekBar_csMaxValue, 100));
            setMinValue(a.getInt(R.styleable.CustomSeekBar_csMinValue, 0));

        } finally {
            a.recycle();
        }
    }

    /**
     * Initialize method used to inflate the layout
     *
     * @param context Context
     */
    private void init(Context context) {

        LayoutInflater.from(context).inflate(R.layout.custom_seekbar, this, true);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        // setting the listener
        seekBar.setOnSeekBarChangeListener(this);

        value = (TextView) findViewById(R.id.valueSeekBar);

        // initializing the current value
        setCurrentValue(seekBar.getProgress());
    }

    /**
     * Setting the change listener for the seekBar
     *
     * @param changeListener {@link OnSeekBarChangeListener}
     */
    public void setOnChangeListener(OnSeekBarChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    /**
     * Clearing the change listener for the seekBar
     */
    public void removeOnChangeListener() {
        this.changeListener = null;
    }

    /**
     * setting the MAX value for the seekBar
     *
     * @param value int
     */
    public void setMaxValue(int value) {
        if (value <= 0)
            throw new IllegalArgumentException("You cannot set a maximum value of 0 or less");

        if (value <= min)
            throw new IllegalArgumentException("You cannot set a maximum value smaller than the minimum");

        max = value;
        setCurrentValue(seekBar.getProgress());
    }

    /**
     * Setting the MIN value for the seekBar
     *
     * @param value int
     */
    public void setMinValue(int value) {

        if (value < 0)
            throw new IllegalArgumentException("You cannot set a minimum value of 0 or less");

        if (value >= max)
            throw new IllegalArgumentException("You cannot set a minimum value bigger than the maximum");

        min = value;
        setCurrentValue(seekBar.getProgress());
    }

    /**
     * @return current seekBar's progress
     */
    public int getProgress() {
        return currentValue;
    }

    /**
     * Set the progress of the seekBar
     *
     * @param progress int, the progress of teh seekBar to set
     */
    public void setProgress(int progress) {
        seekBar.setProgress(progress);
    }

    /**
     * Calculate the current value and sets it in the TextView
     *
     * @param i int, the value to check
     */
    private void setCurrentValue(int i) {
        currentValue = min + (i * 100) / (max - min);
        value.setText(currentValue + "%");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        setCurrentValue(i);

        // if the listener is set, we notify it
        if (changeListener != null && b)
            changeListener.onProgressChanged(currentValue);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // not used
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        setCurrentValue(seekBar.getProgress());

        if (changeListener != null)
            changeListener.onDragEnded(currentValue);
    }


    /**
     * Interface used to trace seekBar's changes
     */
    public interface OnSeekBarChangeListener {

        /**
         * When a change is detected in the seekBar, this method is called
         *
         * @param value int, the current seekBar's value
         */
        void onProgressChanged(int value);

        /**
         * Called when the user has finished with the gesture
         *
         * @param value int, the current seekBar's value
         */
        void onDragEnded(int value);
    }
}
