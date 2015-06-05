package com.mfedele.lamp.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.mfedele.lamp.R;
import com.mfedele.lamp.layouts.CustomColorPicker;
import com.mfedele.lamp.layouts.CustomSeekBar;
import com.mfedele.lamp.objects.ExtendedLampStatus;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements
        CompoundButton.OnCheckedChangeListener {


    private CustomSeekBar brightnessSeekBar;
    private CustomColorPicker colorPicker;
    private Switch aSwitch;

    private int colorPickerTransitionTime = 1;

    private ExtendedLampStatus lampStatus;

    private SharedPreferences sharedPreferences;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    private void loadParams(Context context) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        lampStatus.setSaveEveryUpdate(
                sp.getBoolean(
                        context.getString(R.string.sp_save_lamp_values),
                        context.getResources().getBoolean(R.bool.default_save_lamp_status_value)
                )
        );

        lampStatus.setInterval(
                sp.getInt(
                        context.getString(R.string.sp_update_frequency_ms),
                        context.getResources().getInteger(R.integer.min_interval_ms)
                )
        );

        lampStatus.setUpdateContinuously(
                sp.getBoolean(
                        context.getString(R.string.sp_enable_continuous_update),
                        context.getResources().getBoolean(R.bool.default_switch_value)
                )
        );
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().show();

        colorPickerTransitionTime = getActivity().getResources().getInteger(R.integer.main_colorpicker_transition);

        brightnessSeekBar = (CustomSeekBar) view.findViewById(R.id.fragmentSeekBar);

        colorPicker = (CustomColorPicker) view.findViewById(R.id.fragmentColorPicker);

        aSwitch = (Switch) view.findViewById(R.id.fragmentSwitch);

        lampStatus = ExtendedLampStatus.loadFromSp(getActivity(), (ExtendedLampStatus.OnStatusUpdate) getActivity());
        loadParams(getActivity());

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (colorPicker.getHeight() != 0) {

                    brightnessSeekBar.setProgress(lampStatus.getBrightness());
                    colorPicker.setColor(lampStatus.getColor());
                    colorPicker.setBrightness(lampStatus.getColorBrightness());
                    aSwitch.setChecked(lampStatus.isColorEnabled());

                    if (!lampStatus.isColorEnabled())
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
        brightnessSeekBar.setOnChangeListener(lampStatus);
        colorPicker.setOnUserActionListenerListener(lampStatus);
        aSwitch.setOnCheckedChangeListener(this);
    }




    // BUTTON //////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        lampStatus.setIsColorEnabled(b);
        animateColorPicker(colorPickerTransitionTime, b);
    }


    private void animateColorPicker(int time, boolean show) {

        // calculating transition
        float[] transition = {
                colorPicker.getTop() + (!show ? 0 : colorPicker.getHeight()),
                colorPicker.getTop() + (show ? 0 : colorPicker.getHeight()),
        };


        ObjectAnimator anim = ObjectAnimator.ofFloat(colorPicker, "y", transition);

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                // disable controls to avoid problems during animation
                brightnessSeekBar.setEnabled(false);
                colorPicker.setEnabled(false);
                aSwitch.setEnabled(false);
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

    @Override
    public void onResume() {
        super.onResume();
        loadParams(getActivity());
    }
}
