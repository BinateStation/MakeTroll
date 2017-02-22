package rkr.bharathi.maketroll.fragments.dialogs;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import rkr.bharathi.maketroll.R;

/**
 * Dialog fragment to scale the photo frame.
 */
public class ScaleFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "ScaleFragment";

    private static final String KEY_MAX_HEIGHT = "max_height";
    private static final String KEY_MAX_WIDTH = "max_width";
    private static final String KEY_CURRENT_HEIGHT = "current_height";
    private static final String KEY_CURRENT_WIDTH = "current_width";

    private ScaleListener mScaleListener;
    private int maxHeight;
    private int maxWidth;
    private int currentHeight;
    private int currentWidth;

    public ScaleFragment() {
        // Required empty public constructor
    }

    public static ScaleFragment newInstance(int maxHeight, int maxWidth, int measuredHeight, int measuredWidth, ScaleListener scaleListener) {
        Log.d(TAG, "newInstance() called with: scaleListener = [" + scaleListener + "]");
        Bundle args = new Bundle();
        args.putInt(KEY_MAX_HEIGHT, maxHeight);
        args.putInt(KEY_MAX_WIDTH, maxWidth);
        args.putInt(KEY_CURRENT_HEIGHT, measuredHeight);
        args.putInt(KEY_CURRENT_WIDTH, measuredWidth);
        ScaleFragment fragment = new ScaleFragment();
        fragment.setArguments(args);
        fragment.mScaleListener = scaleListener;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            maxHeight = bundle.getInt(KEY_MAX_HEIGHT, 600);
            maxWidth = bundle.getInt(KEY_MAX_WIDTH, 400);
            currentHeight = bundle.getInt(KEY_CURRENT_HEIGHT, 100);
            currentWidth = bundle.getInt(KEY_CURRENT_WIDTH, 100);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scale, container, false);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatSeekBar width = (AppCompatSeekBar) view.findViewById(R.id.FS_width);
        AppCompatSeekBar height = (AppCompatSeekBar) view.findViewById(R.id.FS_height);
        Button done = (Button) view.findViewById(R.id.FS_done);

        if (maxHeight > 0) {
            height.setMax(maxHeight);
        }
        if (maxWidth > 0) {
            width.setMax(maxWidth);
        }
        height.setProgress(currentHeight);
        width.setProgress(currentWidth);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mScaleListener != null) {
                    mScaleListener.onDone(maxWidth);
                }
            }
        });

        width.setOnSeekBarChangeListener(this);
        height.setOnSeekBarChangeListener(this);
    }

    /**
     * Notification that the progress level has changed. Clients can use the fromUser parameter
     * to distinguish user-initiated changes from those that occurred programmatically.
     *
     * @param seekBar  The SeekBar whose progress has changed
     * @param progress The current progress level. This will be in the range 0..max where max
     *                 was set by {@link ProgressBar#setMax(int)}. (The default value for max is 100.)
     * @param fromUser True if the progress change was initiated by the user.
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG, "onProgressChanged() called with: seekBar = [" + seekBar + "], progress = [" + progress + "], fromUser = [" + fromUser + "]");
        if (mScaleListener != null && fromUser) {
            if (progress > 100) {
                if (seekBar.getId() == R.id.FS_width) {
                    if (progress <= maxWidth) {
                        mScaleListener.onChangeWidth(progress);
                    }
                } else {
                    if (progress <= maxHeight) {
                        mScaleListener.onChangeHeight(progress);
                    }
                }
            }
        }
    }

    /**
     * Notification that the user has started a touch gesture. Clients may want to use this
     * to disable advancing the seekbar.
     *
     * @param seekBar The SeekBar in which the touch gesture began
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    /**
     * Notification that the user has finished a touch gesture. Clients may want to use this
     * to re-enable advancing the seek bar.
     *
     * @param seekBar The SeekBar in which the touch gesture began
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface ScaleListener {
        void onChangeHeight(int height);

        void onChangeWidth(int width);

        void onDone(int maxWidth);
    }
}
