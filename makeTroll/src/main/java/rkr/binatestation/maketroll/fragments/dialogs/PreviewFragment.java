package rkr.binatestation.maketroll.fragments.dialogs;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import rkr.binatestation.maketroll.R;

/**
 * Dialog fragment to preview the cell
 */
public class PreviewFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = "PreviewFragment";

    private File file;
    private PreviewListener mPreviewListener;

    public PreviewFragment() {
        // Required empty public constructor
    }

    public static PreviewFragment newInstance(File file, PreviewListener previewListener) {
        Log.d(TAG, "newInstance() called with: file = [" + file + "]");
        Bundle args = new Bundle();

        PreviewFragment fragment = new PreviewFragment();
        fragment.setArguments(args);
        fragment.file = file;
        fragment.mPreviewListener = previewListener;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.adapter_item_my_creations, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        AppCompatImageView appCompatImageView = (AppCompatImageView) view.findViewById(R.id.AIMC_image_view);
        AppCompatImageButton whatsAppShareAppCompatImageButton = (AppCompatImageButton) view.findViewById(R.id.AIMC_whatsAppShare);
        AppCompatImageButton facebookShareAppCompatImageButton = (AppCompatImageButton) view.findViewById(R.id.AIMC_fbShare);
        AppCompatImageButton actionRemoveFileAppCompatImageButton = (AppCompatImageButton) view.findViewById(R.id.AIMC_action_remove_file);

        if (whatsAppShareAppCompatImageButton != null) {
            whatsAppShareAppCompatImageButton.setOnClickListener(this);
        }
        if (facebookShareAppCompatImageButton != null) {
            facebookShareAppCompatImageButton.setOnClickListener(this);
        }
        if (actionRemoveFileAppCompatImageButton != null) {
            actionRemoveFileAppCompatImageButton.setOnClickListener(this);
        }

        if (file != null) {
            Uri uri = Uri.fromFile(file);
            appCompatImageView.setImageURI(uri);
        } else {
            appCompatImageView.setImageDrawable(ContextCompat.getDrawable(
                    appCompatImageView.getContext(),
                    R.drawable.gallery
            ));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.AIMC_whatsAppShare) {
            if (mPreviewListener != null) {
                mPreviewListener.shareToWhatsApp();
            }
        } else if (v.getId() == R.id.AIMC_action_remove_file) {
            if (mPreviewListener != null) {
                mPreviewListener.deleteFile();
            }
        }
    }

    public interface PreviewListener {
        void shareToWhatsApp();

        void deleteFile();
    }

}
