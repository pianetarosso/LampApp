package com.mfedele.lamp.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.mfedele.lamp.R;
import com.mfedele.lamp.layouts.CustomColorPicker;
import com.mfedele.lamp.layouts.CustomSeekBar;

/**
 * Created by Marco Fedele on 04/06/15.
 */
public class ExtendedLampStatus extends LampStatus implements
        CustomSeekBar.OnSeekBarChangeListener,
        CustomColorPicker.OnUserActionListener {

    private static final int DEFAULT_UPDATE_TIME = 200;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private OnStatusUpdate onStatusUpdate;

    private boolean saveEveryUpdate = false;

    private int interval = DEFAULT_UPDATE_TIME;
    private boolean updateContinuously = false;

    private Handler handler;
    private boolean running = false;

    private Context context;


    public ExtendedLampStatus(Context context, OnStatusUpdate onStatusUpdate, int brightness, boolean isColorEnabled, int color, int colorBrightness) {
        super(brightness, isColorEnabled, color, colorBrightness);

        this.context = context;

        sp = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sp.edit();

        this.onStatusUpdate = onStatusUpdate;
        handler = new Handler(context.getMainLooper());
    }

    public static ExtendedLampStatus loadFromSp(Context context, OnStatusUpdate onStatusUpdate) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return new ExtendedLampStatus(
                context,
                onStatusUpdate,
                sp.getInt(context.getString(R.string.sp_brightness), context.getResources().getInteger(R.integer.def_brightness)),
                sp.getBoolean(context.getString(R.string.sp_enable_color), context.getResources().getBoolean(R.bool.default_switch_value)),
                sp.getInt(context.getString(R.string.sp_color), context.getResources().getColor(R.color.initial_color)),
                sp.getInt(context.getString(R.string.sp_color_brightness), context.getResources().getInteger(R.integer.def_brightness))
        );

    }

    @Override
    public void setBrightness(int brightness) {
        super.setBrightness(brightness);
        getData();
    }

    @Override
    public void setIsColorEnabled(boolean isColorEnabled) {
        super.setIsColorEnabled(isColorEnabled);

        if (saveEveryUpdate)
            save();

        getData();
    }

    @Override
    public void setColor(int color) {
        super.setColor(color);
        getData();
    }

    @Override
    public void setColorBrightness(int colorBrightness) {
        super.setColorBrightness(colorBrightness);
        getData();
    }

    public void setSaveEveryUpdate(boolean saveEveryUpdate) {
        this.saveEveryUpdate = saveEveryUpdate;

        if (!saveEveryUpdate) {
            editor.remove(context.getString(R.string.sp_brightness));
            editor.remove(context.getString(R.string.sp_enable_color));
            editor.remove(context.getString(R.string.sp_color));
            editor.remove(context.getString(R.string.sp_color_brightness));
            editor.commit();

            setBrightness(context.getResources().getInteger(R.integer.def_brightness));
            setIsColorEnabled(context.getResources().getBoolean(R.bool.default_switch_value));
            setColor(context.getResources().getColor(R.color.initial_color));
            setColorBrightness(context.getResources().getInteger(R.integer.def_brightness));
        }
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setUpdateContinuously(boolean updateContinuously) {
        this.updateContinuously = updateContinuously;

        if (!updateContinuously) {
            handler.removeCallbacksAndMessages(null);
            running = false;
        }
    }

    private synchronized void getData() {

        LampStatus l = copy();

        if (!updateContinuously) {
            notifyObserver(l);
        } else if (!running) {
            start(l, 0);
        }
    }

    private void start(final LampStatus l, final int time) {

        running = true;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                LampStatus l1 = copy();

                if (running && (time == 0 || !l.equals(l1))) {

                    if (updateContinuously) {
                        notifyObserver(l1);
                        start(l1, interval);
                    }
                } else {
                    running = false;
                }
            }
        }, time);
    }

    public void notifyObserver(LampStatus l) {
        if (onStatusUpdate != null) {
            onStatusUpdate.onDataUpdate(l);
        }
    }

    public void setOnStatusUpdate(OnStatusUpdate onStatusUpdate) {
        this.onStatusUpdate = onStatusUpdate;
    }

    private void save() {

        editor.putInt(context.getString(R.string.sp_brightness), getBrightness());
        editor.putBoolean(context.getString(R.string.sp_enable_color), isColorEnabled());
        editor.putInt(context.getString(R.string.sp_color), getColor());
        editor.putInt(context.getString(R.string.sp_color_brightness), getColorBrightness());
        editor.commit();
    }

    @Override
    public void onColorChanged(int i) {
        super.setColor(i);

        if (updateContinuously)
            getData();
    }


    // COLOR PICKER /////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onUserInteractionEnded(int color, int brightness) {
        super.setColor(color);
        super.setColorBrightness(brightness);

        if (saveEveryUpdate)
            save();

        getData();
    }

    @Override
    public void onBrightnessChanged(int value) {
        super.setColorBrightness(value);

        if (updateContinuously)
            getData();
    }

    @Override
    public void onProgressChanged(int value) {
        super.setBrightness(value);

        if (updateContinuously)
            getData();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    // BRIGHTNESS //////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onDragEnded(int value) {
        super.setBrightness(value);

        if (saveEveryUpdate)
            save();

        getData();
    }

    public interface OnStatusUpdate {
        void onDataUpdate(LampStatus l);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

}
