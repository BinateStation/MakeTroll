package rkr.binatestation.maketroll.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rkr.binatestation.maketroll.R;
import rkr.binatestation.maketroll.fragments.dialogs.PreviewFragment;
import rkr.binatestation.maketroll.interfaces.FbShareListener;
import rkr.binatestation.maketroll.utils.Utils;

/**
 * Created by RKR on 22-02-2017.
 * MyCreationsRecyclerViewAdapter.
 */

public class MyCreationsRecyclerViewAdapter extends RecyclerView.Adapter<MyCreationsRecyclerViewAdapter.ViewHolder> {
    private List<File> fileList = new ArrayList<>();
    private FragmentManager mChildFragmentManager;
    private FbShareListener mFbShareListener;

    public MyCreationsRecyclerViewAdapter(FragmentManager childFragmentManager, FbShareListener fbShareListener) {
        mChildFragmentManager = childFragmentManager;
        mFbShareListener = fbShareListener;
    }

    public void setFileList(Context context) {
        File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), context.getString(R.string.created));
        if (directory.exists() && directory.isDirectory()) {
            File[] fileList = directory.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile();
                }
            });
            this.fileList.clear();
            Collections.addAll(this.fileList, fileList);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.adapter_item_my_creations,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File file = fileList.get(position);
        if (file != null) {
            Uri uri = Uri.fromFile(file);
            holder.appCompatImageView.setImageURI(uri);
        } else {
            holder.appCompatImageView.setImageDrawable(ContextCompat.getDrawable(
                    holder.appCompatImageView.getContext(),
                    R.drawable.gallery
            ));
        }
    }


    @Override
    public int getItemCount() {
        return fileList == null ? 0 : fileList.size();
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatImageView appCompatImageView;
        private AppCompatImageButton whatsAppShareAppCompatImageButton;
        private AppCompatImageButton facebookShareAppCompatImageButton;
        private AppCompatImageButton actionRemoveFileAppCompatImageButton;

        ViewHolder(View itemView) {
            super(itemView);
            appCompatImageView = (AppCompatImageView) itemView.findViewById(R.id.AIMC_image_view);
            whatsAppShareAppCompatImageButton = (AppCompatImageButton) itemView.findViewById(R.id.AIMC_whatsAppShare);
            facebookShareAppCompatImageButton = (AppCompatImageButton) itemView.findViewById(R.id.AIMC_fbShare);
            actionRemoveFileAppCompatImageButton = (AppCompatImageButton) itemView.findViewById(R.id.AIMC_action_remove_file);

            whatsAppShareAppCompatImageButton.setOnClickListener(this);
            facebookShareAppCompatImageButton.setOnClickListener(this);
            actionRemoveFileAppCompatImageButton.setOnClickListener(this);
            appCompatImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.AIMC_whatsAppShare) {
                shareToWhatsApp();
            } else if (v.getId() == R.id.AIMC_fbShare) {
                shareFb();
            } else if (v.getId() == R.id.AIMC_action_remove_file) {
                deleteFile();
            } else if (v.getId() == R.id.AIMC_image_view) {
                loadPreviewDialog();
            }
        }

        private void loadPreviewDialog() {
            PreviewFragment previewFragment = PreviewFragment.newInstance(fileList.get(getAdapterPosition()), new PreviewFragment.PreviewListener() {
                @Override
                public void shareToWhatsApp() {
                    ViewHolder.this.shareToWhatsApp();
                }

                @Override
                public void deleteFile() {
                    ViewHolder.this.deleteFile();
                }

                @Override
                public void shareToFb() {
                    ViewHolder.this.shareFb();
                }
            });
            previewFragment.show(mChildFragmentManager, previewFragment.getTag());
        }

        private void deleteFile() {
            Context context = itemView.getContext();
            if (context != null) {
                Utils.showAlert(
                        context,
                        context.getString(android.R.string.dialog_alert_title),
                        context.getString(R.string.delete_image_confirmation_msg),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (DialogInterface.BUTTON_POSITIVE == which) {
                                    File file = fileList.get(getAdapterPosition());
                                    if (file != null && file.exists() && file.isFile()) {
                                        if (file.delete()) {
                                            fileList.remove(getAdapterPosition());
                                            notifyItemRemoved(getAdapterPosition());
                                        }
                                    }
                                }
                                dialog.dismiss();
                            }
                        }
                );
            }
        }

        private void shareToWhatsApp() {
            Context context = whatsAppShareAppCompatImageButton.getContext();
            if (isPackageInstalled("com.whatsapp", context)) {
                File file = fileList.get(getAdapterPosition());
                if (file != null) {
                    Uri uri = Uri.fromFile(file);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.setType("image/jpeg");
                    sendIntent.setPackage("com.whatsapp");
                    context.startActivity(sendIntent);
                }
            }
        }

        private void shareFb() {
            if (mFbShareListener != null) {
                File file = fileList.get(getAdapterPosition());
                if (file != null) {
                    Uri uri = Uri.fromFile(file);
                    mFbShareListener.shareToFacebook(uri);
                }
            }
        }
    }
}
