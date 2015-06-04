package com.mfedele.lamp.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.mfedele.lamp.R;

/**
 * Created by Marco Fedele on 04/06/15.
 * <p/>
 * It's a container for the colorPicker and its brightness bar
 */
public class CustomColorPicker extends LinearLayout implements
        CustomSeekBar.OnSeekBarChangeListener,
        ColorPicker.OnColorSelectedListener,
        ColorPicker.OnColorChangedListener {

    private ColorPicker picker;
    private CustomSeekBar brightnessSeekBar;

    private OnUserActionListener onUserActionListenerListener;

    private boolean isInteractingWithColor = false;
    private boolean isInteractingWithBrightness = false;


    public CustomColorPicker(Context context) {
        super(context);
        initialize(context);
    }

    public CustomColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public CustomColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    /**
     * Initialize method used to inflate the layout
     *
     * @param context Context
     */
    private void initialize(Context context) {

        LayoutInflater.from(context).inflate(R.layout.custom_colorpicker, this, true);

        brightnessSeekBar = (CustomSeekBar) findViewById(R.id.colorBrightnessSeekBar);
        brightnessSeekBar.setOnChangeListener(this);

        picker = (ColorPicker) findViewById(R.id.colorPicker);
        picker.setShowOldCenterColor(false);

        picker.setOnColorSelectedListener(this);
        picker.setOnColorChangedListener(this);
    }

    public int getColorSelected() {
        return picker.getColor();
    }

    /**
     * @return int, the brightness of the color
     */
    public int getBrightness() {
        return brightnessSeekBar.getProgress();
    }

    /**
     * Sets the passed brightness
     *
     * @param brightness int
     */
    public void setBrightness(int brightness) {
        brightnessSeekBar.setProgress(brightness);
    }

    /**
     * Sets the color of the color picker
     *
     * @param color int, the color to set
     */
    public void setColor(int color) {
        picker.setColor(color);
    }

    public void setOnUserActionListenerListener(OnUserActionListener onUserActionListenerListener) {
        this.onUserActionListenerListener = onUserActionListenerListener;
    }

    public void resetOnUserActionListener() {
        this.onUserActionListenerListener = null;
    }

    @Override
    public void onColorChanged(int i) {
        isInteractingWithColor = true;

        if (onUserActionListenerListener != null)
            onUserActionListenerListener.onColorChanged(i);
    }

    @Override
    public void onColorSelected(int i) {
        isInteractingWithColor = false;

        if (onUserActionListenerListener != null && !isInteractingWithBrightness) {
            onUserActionListenerListener.onUserInteractionEnded(i, brightnessSeekBar.getProgress());
        }
    }

    @Override
    public void onProgressChanged(int value) {
        isInteractingWithBrightness = true;

        if (onUserActionListenerListener != null)
            onUserActionListenerListener.onBrightnessChanged(value);
    }

    @Override
    public void onDragEnded(int value) {
        isInteractingWithBrightness = false;

        if (onUserActionListenerListener != null && !isInteractingWithColor) {
            onUserActionListenerListener.onUserInteractionEnded(picker.getColor(), value);
        }
    }


    /**
     * Interface called when something change
     */
    public interface OnUserActionListener {

        /**
         * Called every time there is a change of the color
         *
         * @param i int, the color
         */
        void onColorChanged(int i);

        /**
         * Called when the user has chosen a color
         *
         * @param color      int, the color
         * @param brightness int, the brightness
         */
        void onUserInteractionEnded(int color, int brightness);

        /**
         * Called when the user change the brightness
         *
         * @param value int, the progress of the seekbar
         */
        void onBrightnessChanged(int value);
    }
}
