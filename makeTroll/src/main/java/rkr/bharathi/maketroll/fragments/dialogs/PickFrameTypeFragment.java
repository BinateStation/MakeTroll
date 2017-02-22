package rkr.bharathi.maketroll.fragments.dialogs;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import rkr.bharathi.maketroll.R;

/**
 * Dialog fragment for picking the frame type
 */
public class PickFrameTypeFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "PickFrameTypeFragment";

    private View.OnClickListener mOnClickListener;

    public PickFrameTypeFragment() {
        // Required empty public constructor
    }

    public static PickFrameTypeFragment newInstance(View.OnClickListener onClickListener) {
        Log.d(TAG, "newInstance() called");
        Bundle args = new Bundle();

        PickFrameTypeFragment fragment = new PickFrameTypeFragment();
        fragment.mOnClickListener = onClickListener;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pick_frame_type, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View squareFrame = view.findViewById(R.id.FPFT_frame_square);
        View rectangleFrame = view.findViewById(R.id.FPFT_frame_rectangle);
        View addLabel = view.findViewById(R.id.FPFT_frame_label);
        squareFrame.setOnClickListener(this);
        rectangleFrame.setOnClickListener(this);
        addLabel.setOnClickListener(this);

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
    public void onClick(View v) {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(v);
        }
        dismiss();
    }
}
