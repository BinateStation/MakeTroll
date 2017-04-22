package rkr.binatestation.maketroll.fragments.dialogs;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import rkr.binatestation.maketroll.R;
import rkr.binatestation.maketroll.interfaces.MyCreationsListener;

/**
 * Dialog fragment to preview the cell
 */
public class PreviewFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = "PreviewFragment";

    private File mFile;
    private MyCreationsListener mMyCreationsListener;
    private int mPosition;

    public PreviewFragment() {
        // Required empty public constructor
    }

    public static PreviewFragment newInstance(File file, int position, MyCreationsListener previewListener) {
        Log.d(TAG, "newInstance() called with: mFile = [" + file + "]");
        Bundle args = new Bundle();

        PreviewFragment fragment = new PreviewFragment();
        fragment.setArguments(args);
        fragment.mFile = file;
        fragment.mPosition = position;
        fragment.mMyCreationsListener = previewListener;
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
        ImageView whatsAppShareAppCompatImageButton = (ImageView) view.findViewById(R.id.AIMC_whatsAppShare);
        ImageView facebookShareAppCompatImageButton = (ImageView) view.findViewById(R.id.AIMC_fbShare);
        ImageView actionRemoveFileAppCompatImageButton = (ImageView) view.findViewById(R.id.AIMC_action_remove_file);
        ImageView actionShareAppCompatImageButton = (ImageView) view.findViewById(R.id.AIMC_share);

        if (whatsAppShareAppCompatImageButton != null) {
            whatsAppShareAppCompatImageButton.setOnClickListener(this);
        }
        if (facebookShareAppCompatImageButton != null) {
            facebookShareAppCompatImageButton.setOnClickListener(this);
        }
        if (actionRemoveFileAppCompatImageButton != null) {
            actionRemoveFileAppCompatImageButton.setOnClickListener(this);
        }
        if (actionShareAppCompatImageButton != null) {
            actionShareAppCompatImageButton.setVisibility(View.VISIBLE);
            actionShareAppCompatImageButton.setOnClickListener(this);
        }

        if (mFile != null) {
            Uri uri = Uri.fromFile(mFile);
            appCompatImageView.setImageURI(uri);
        } else {
            appCompatImageView.setImageDrawable(ContextCompat.getDrawable(
                    appCompatImageView.getContext(),
                    R.drawable.ic_image_black_24dp
            ));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.AIMC_whatsAppShare) {
            if (mMyCreationsListener != null) {
                mMyCreationsListener.shareToWhatsApp(mFile);
            }
        } else if (v.getId() == R.id.AIMC_fbShare) {
            if (mMyCreationsListener != null) {
                Uri uri = Uri.fromFile(mFile);
                mMyCreationsListener.shareToFacebook(uri);
            }
        } else if (v.getId() == R.id.AIMC_share) {
            if (mMyCreationsListener != null) {
                mMyCreationsListener.share(mFile);
            }
        } else if (v.getId() == R.id.AIMC_action_remove_file) {
            if (mMyCreationsListener != null) {
                mMyCreationsListener.deleteFile(mFile, mPosition);
                dismiss();
            }
        }
    }

}
