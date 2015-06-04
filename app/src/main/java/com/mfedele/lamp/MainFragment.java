package com.mfedele.lamp;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.mfedele.lamp.layouts.CustomColorPicker;
import com.mfedele.lamp.layouts.CustomSeekBar;
import com.mfedele.lamp.objects.ExtendedLampStatus;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements
        CustomSeekBar.OnSeekBarChangeListener,
        CustomColorPicker.OnUserActionListener,
        CompoundButton.OnCheckedChangeListener {


    private static final boolean SAVE_LAMP_STATUS = true;
    private static final boolean CONTINUOUSLY_UPDATE = false;
    private static final int UPDATE_INTERVAL = 40;


    private CustomSeekBar brightnessSeekBar;
    private CustomColorPicker colorPicker;
    private Switch aSwitch;

    private ExtendedLampStatus lampStatus;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    private void dispatchTouchevent(MotionEvent event, ViewGroup view) {
        // Find the child view that was touched (perform a hit test)
        Rect rect = new Rect();
        int childCount = view.getChildCount();
        int[] listViewCoords = new int[2];
        view.getLocationOnScreen(listViewCoords);
        int x = (int) event.getRawX() - listViewCoords[0];
        int y = (int) event.getRawY() - listViewCoords[1];
        View child;
        for (int i = 0; i < childCount; i++) {
            child = view.getChildAt(i);
            child.getHitRect(rect);
            if (rect.contains(x, y)) {
                child.dispatchTouchEvent(event);
                break;
            }
        }
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        brightnessSeekBar = (CustomSeekBar) view.findViewById(R.id.fragmentSeekBar);

        colorPicker = (CustomColorPicker) view.findViewById(R.id.fragmentColorPicker);

        aSwitch = (Switch) view.findViewById(R.id.fragmentSwitch);

        lampStatus = ExtendedLampStatus.loadFromSp(getActivity(), (ExtendedLampStatus.OnStatusUpdate) getActivity());
        lampStatus.setSaveEveryUpdate(SAVE_LAMP_STATUS);
        lampStatus.setInterval(UPDATE_INTERVAL);
        lampStatus.setUpdateContinuously(CONTINUOUSLY_UPDATE);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (colorPicker.getHeight() != 0) {

                    brightnessSeekBar.setProgress(lampStatus.getBrightness());
                    colorPicker.setColor(lampStatus.getColor());
                    colorPicker.setBrightness(lampStatus.getColorBrightness());

                    if (lampStatus.isColorEnabled()) {
                        aSwitch.setChecked(lampStatus.isColorEnabled());
                    } else
                        animateColorPicker(1, false);

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                    enableListeners();
                }
            }
        });
    }

    private void enableListeners() {
        brightnessSeekBar.setOnChangeListener(this);
        colorPicker.setOnUserActionListenerListener(this);
        aSwitch.setOnCheckedChangeListener(this);
    }


    // COLOR PICKER /////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onColorChanged(int i) {
        if (CONTINUOUSLY_UPDATE)
            lampStatus.setColor(i);
    }

    @Override
    public void onUserInteractionEnded(int color, int brightness) {
        if (!CONTINUOUSLY_UPDATE) {
            lampStatus.setColorAndBrightness(color, brightness);
        }
    }

    @Override
    public void onBrightnessChanged(int value) {
        if (CONTINUOUSLY_UPDATE)
            lampStatus.setColorBrightness(value);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    // BRIGHTNESS //////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onProgressChanged(int value) {
        if (CONTINUOUSLY_UPDATE)
            lampStatus.setBrightness(value);
    }

    @Override
    public void onDragEnded(int value) {
        if (!CONTINUOUSLY_UPDATE)
            lampStatus.setBrightness(value);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    // BUTTON //////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        lampStatus.setIsColorEnabled(b);
        animateColorPicker(1000, b);
    }


    private void animateColorPicker(int time, boolean show) {

        // disable controls to avoid problems during animation
        brightnessSeekBar.setEnabled(false);
        colorPicker.setEnabled(false);
        aSwitch.setEnabled(false);

        // calculating transition
        float[] transition = {
                colorPicker.getTop() + (!show ? 0 : colorPicker.getHeight()),
                colorPicker.getTop() + (show ? 0 : colorPicker.getHeight()),
        };


        ObjectAnimator anim = ObjectAnimator.ofFloat(colorPicker, "y", transition);

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                // enabling controls
                brightnessSeekBar.setEnabled(true);
                colorPicker.setEnabled(true);
                aSwitch.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // not used
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                // not used
            }
        });

        // duration of the animation in ms
        anim.setDuration(time);

        anim.start();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


}
