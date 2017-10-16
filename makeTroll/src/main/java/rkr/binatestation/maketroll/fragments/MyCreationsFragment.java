package rkr.binatestation.maketroll.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.File;

import rkr.binatestation.maketroll.BuildConfig;
import rkr.binatestation.maketroll.R;
import rkr.binatestation.maketroll.adapters.MyCreationsRecyclerViewAdapter;
import rkr.binatestation.maketroll.fragments.dialogs.PreviewFragment;
import rkr.binatestation.maketroll.interfaces.MyCreationsListener;
import rkr.binatestation.maketroll.utils.Utils;

import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCreationsFragment extends Fragment implements MyCreationsListener {

    private static final String TAG = "MyCreationsFragment";

    private MyCreationsRecyclerViewAdapter mMyCreationsRecyclerViewAdapter;

    public MyCreationsFragment() {
        // Required empty public constructor
    }

    public static MyCreationsFragment newInstance() {
        Log.d(TAG, "newInstance() called");
        Bundle args = new Bundle();

        MyCreationsFragment fragment = new MyCreationsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_creations, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.FMC_recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, VERTICAL));
        recyclerView.setAdapter(mMyCreationsRecyclerViewAdapter = new MyCreationsRecyclerViewAdapter(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMyCreationsRecyclerViewAdapter != null && getContext() != null) {
            mMyCreationsRecyclerViewAdapter.setFileList(getContext());
        }
    }

    @Override
    public void shareToFacebook(Uri uri) {
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            SharePhoto photo = new SharePhoto.Builder()
                    .setImageUrl(uri)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            ShareDialog.show(this, content);
        }
    }

    @Override
    public void loadPreviewDialog(File file, int position) {
        PreviewFragment previewFragment = PreviewFragment.newInstance(file, position, this);
        previewFragment.show(getChildFragmentManager(), previewFragment.getTag());
    }

    private boolean isPackageInstalled(String packageName, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    @Override
    public void shareToWhatsApp(File file) {
        Context context = getContext();
        if (isPackageInstalled("com.whatsapp", context)) {
            if (file != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID + ".imagepicker.provider",
                        file);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
                sendIntent.setType("image/jpeg");
                sendIntent.setPackage("com.whatsapp");
                context.startActivity(sendIntent);
            }
        }
    }

    @Override
    public void deleteFile(final File file, final int position) {
        Context context = getContext();
        if (context != null) {
            Utils.showAlert(
                    context,
                    context.getString(android.R.string.dialog_alert_title),
                    context.getString(R.string.delete_image_confirmation_msg),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (DialogInterface.BUTTON_POSITIVE == which) {
                                if (file != null && file.exists() && file.isFile()) {
                                    if (file.delete()) {
                                        if (mMyCreationsRecyclerViewAdapter != null) {
                                            mMyCreationsRecyclerViewAdapter.removeFile(position);
                                        }
                                    }
                                }
                            }
                        }
                    }
            );
        }

    }

    @Override
    public void share(File file) {
        Context context = getContext();
        if (file != null) {
            Uri photoURI = FileProvider.getUriForFile(getContext(),
                    BuildConfig.APPLICATION_ID + ".imagepicker.provider",
                    file);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
            sendIntent.setType("image/jpeg");
            context.startActivity(sendIntent);
        }
    }

}
