package rkr.binatestation.maketroll.fragments.dialogs;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.NumberPicker;

import rkr.binatestation.maketroll.R;

/**
 * Dialog fragment for number picker
 */
public class NumberPickerFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "NumberPickerFragment";
    private static final String KEY_MIN_VALUE = "min_value";
    private static final String KEY_MAX_VALUE = "max_value";
    private static final String KEY_VALUE = "value";

    private NumberPickerListener mNumberPickerListener;
    private int mMinValue = 0;
    private int mMaxValue = 0;
    private int mValue = 0;

    private NumberPicker mNumberPicker;

    public NumberPickerFragment() {
        // Required empty public constructor
    }

    public static NumberPickerFragment newInstance(int minValue, int maxValue, int value, NumberPickerListener numberPickerListener) {
        Log.d(TAG, "newInstance() called with: minValue = [" + minValue + "], maxValue = [" + maxValue + "], value = [" + value + "], numberPickerListener = [" + numberPickerListener + "]");
        Bundle args = new Bundle();
        args.putInt(KEY_MIN_VALUE, minValue);
        args.putInt(KEY_MAX_VALUE, maxValue);
        args.putInt(KEY_VALUE, value);
        NumberPickerFragment fragment = new NumberPickerFragment();
        fragment.setArguments(args);
        fragment.mNumberPickerListener = numberPickerListener;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        Window window = dialog.getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mMinValue = bundle.getInt(KEY_MIN_VALUE);
            mMaxValue = bundle.getInt(KEY_MAX_VALUE);
            mValue = bundle.getInt(KEY_VALUE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_number_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNumberPicker = (NumberPicker) view.findViewById(R.id.FNP_number_picker);
        mNumberPicker.setMinValue(mMinValue);
        mNumberPicker.setMaxValue(mMaxValue);
        mNumberPicker.setValue(mValue);

        AppCompatImageButton actionDoneAppCompatImageButton = (AppCompatImageButton) view.findViewById(R.id.FNP_action_done);
        actionDoneAppCompatImageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.FNP_action_done && mNumberPickerListener != null && mNumberPicker != null) {
            mNumberPickerListener.onDone(mNumberPicker.getValue());
            dismiss();
        }
    }

    public interface NumberPickerListener {
        void onDone(int number);
    }
}
