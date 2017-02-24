package rkr.binatestation.maketroll.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
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

/**
 * Created by RKR on 22-02-2017.
 * MyCreationsRecyclerViewAdapter.
 */

public class MyCreationsRecyclerViewAdapter extends RecyclerView.Adapter<MyCreationsRecyclerViewAdapter.ViewHolder> {
    private List<File> fileList = new ArrayList<>();

    public MyCreationsRecyclerViewAdapter() {
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
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.AIMC_whatsAppShare) {
                shareToWhatsApp();
            } else if (v.getId() == R.id.AIMC_action_remove_file) {
                deleteFile();
            }
        }

        private void deleteFile() {
            File file = fileList.get(getAdapterPosition());
            if (file != null && file.exists() && file.isFile()) {
                if (file.delete()) {
                    fileList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
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
    }
}
