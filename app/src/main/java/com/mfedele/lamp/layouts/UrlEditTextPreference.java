package com.mfedele.lamp.layouts;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mfedele.lamp.R;

/**
 * Created by Marco Fedele on 05/06/15.
 */
public class UrlEditTextPreference extends Preference implements View.OnFocusChangeListener {


    private static final String TAG = UrlEditTextPreference.class.getSimpleName();

    private static final int DEFAULT_INPUT_TYPE = InputType.TYPE_CLASS_TEXT;


    private EditText editText;
    private int inputType;
    private String mCurrentValue;

    private Context context;


    public UrlEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPreference(context, attrs);
    }

    public UrlEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPreference(context, attrs);
    }

    private static String getAttributeStringValue(TypedArray a, int index, String defaultValue) {
        String value = a.getString(index);
        if (TextUtils.isEmpty(value))
            value = defaultValue;

        return value;
    }

    private void initPreference(Context context, AttributeSet attrs) {

        this.context = context;
        mCurrentValue = context.getString(R.string.url_default_value);

        setValuesFromXml(context, attrs);

        editText = new EditText(context, attrs);
        editText.setInputType(inputType);
        editText.setSingleLine();
        editText.setOnFocusChangeListener(this);

        setWidgetLayoutResource(R.layout.edit_text_preference);
    }

    private void setValuesFromXml(Context context, AttributeSet attrs) {

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.UrlEditTextPreference,
                0, 0);

        try {
            inputType = a.getInt(R.styleable.UrlEditTextPreference_android_inputType, DEFAULT_INPUT_TYPE);

        } finally {
            a.recycle();
        }
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);

        // The basic preference layout puts the widget frame to the right of the title and summary,
        // so we need to change it a bit - the seekbar should be under them.
        LinearLayout layout = (LinearLayout) view;
        layout.setOrientation(LinearLayout.VERTICAL);

        return view;
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);

        try {
            // move our seekbar to the new view we've been given
            ViewParent oldContainer = editText.getParent();
            ViewGroup newContainer = (ViewGroup) view.findViewById(R.id.editTextContainer);

            if (oldContainer != newContainer) {
                // remove the seekbar from the old view
                if (oldContainer != null) {
                    ((ViewGroup) oldContainer).removeView(editText);
                }
                // remove the existing seekbar (there may not be one) and add ours
                newContainer.removeAllViews();
                newContainer.addView(editText, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error binding view: " + ex.toString());
        }

        //if dependency is false from the beginning, disable the seek bar
        if (view != null && !view.isEnabled()) {
            editText.setEnabled(false);
        }

        updateView(view);
    }

    /**
     * Update a SeekBarPreference view with our current state
     *
     * @param view
     */
    protected void updateView(View view) {

        try {
            editText.setText(mCurrentValue);
        } catch (Exception e) {
            Log.e(TAG, "Error updating seek bar preference", e);
        }

    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
/* When focus is lost check that the text field
    * has valid values.
    */
        if (!hasFocus) {
            persistString(editText.getText().toString());
            notifyChanged();
        }


    }

    @Override
    protected Object onGetDefaultValue(TypedArray ta, int index) {

        String defaultValue = getAttributeStringValue(ta, index, mCurrentValue);
        return defaultValue;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

        if (restoreValue) {
            mCurrentValue = getPersistedString(mCurrentValue);
        } else {
            persistString((String) defaultValue);
            mCurrentValue = (String) defaultValue;
        }

    }

    /**
     * make sure that the seekbar is disabled if the preference is disabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        editText.setEnabled(enabled);
    }

    @Override
    public void onDependencyChanged(Preference dependency, boolean disableDependent) {
        super.onDependencyChanged(dependency, disableDependent);

        //Disable movement of seek bar when dependency is false
        if (editText != null) {
            editText.setEnabled(!disableDependent);
        }
    }

}
