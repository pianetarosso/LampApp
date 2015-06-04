package com.mfedele.lamp.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

/**
 * Created by Marco Fedele on 04/06/15.
 */
public class ExtendedLampStatus extends LampStatus {

    private static final int DEFAULT_UPDATE_TIME = 200;

    private static final String KEY_BRIGHTNESS = "brightness";
    private static final String KEY_COLORS_ENABLED = "colors_enabled";
    private static final String KEY_COLOR = "color";
    private static final String KEY_COLOR_BRIGHTNESS = "color_brightness";

    private SharedPreferences.Editor editor;

    private OnStatusUpdate onStatusUpdate;

    private boolean saveEveryUpdate = false;

    private int interval = DEFAULT_UPDATE_TIME;
    private boolean updateContinuously = false;

    private Handler handler;
    private boolean running = false;


    public ExtendedLampStatus(Context context, OnStatusUpdate onStatusUpdate, int brightness, boolean isColorEnabled, int color, int colorBrightness) {
        super(brightness, isColorEnabled, color, colorBrightness);
        editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        this.onStatusUpdate = onStatusUpdate;
        handler = new Handler(context.getMainLooper());
    }

    public static ExtendedLampStatus loadFromSp(Context context, OnStatusUpdate onStatusUpdate) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return new ExtendedLampStatus(
                context,
                onStatusUpdate,
                sp.getInt(KEY_BRIGHTNESS, 0),
                sp.getBoolean(KEY_COLORS_ENABLED, false),
                sp.getInt(KEY_COLOR, 0),
                sp.getInt(KEY_COLOR_BRIGHTNESS, 0)
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

    public void setColorAndBrightness(int color, int brightness) {
        super.setColor(color);
        super.setColorBrightness(brightness);
        getData();
    }

    public void setSaveEveryUpdate(boolean saveEveryUpdate) {
        this.saveEveryUpdate = saveEveryUpdate;
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

        LampStatus l = new LampStatus(getBrightness(), isColorEnabled(), getColor(), getColorBrightness());

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

                LampStatus l1 = new LampStatus(getBrightness(), isColorEnabled(), getColor(), getColorBrightness());

                if (running && (time == 0 || !l.equals(l1))) {

                    if (updateContinuously)
                        notifyObserver(l);

                    start(l1, interval);
                } else {
                    running = false;
                }
            }
        }, time);
    }

    public void notifyObserver(LampStatus l) {
        if (onStatusUpdate != null) {
            if (saveEveryUpdate)
                save();

            onStatusUpdate.onDataUpdate(l);
        }
    }

    public void setOnStatusUpdate(OnStatusUpdate onStatusUpdate) {
        this.onStatusUpdate = onStatusUpdate;
    }

    private void save() {

        editor.putInt(KEY_BRIGHTNESS, getBrightness());
        editor.putBoolean(KEY_COLORS_ENABLED, isColorEnabled());
        editor.putInt(KEY_COLOR, getColor());
        editor.putInt(KEY_COLOR_BRIGHTNESS, getColorBrightness());
        editor.commit();
    }


    public interface OnStatusUpdate {
        void onDataUpdate(LampStatus l);
    }
}
