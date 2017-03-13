package rkr.binatestation.maketroll.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rkr.binatestation.maketroll.R;
import rkr.binatestation.maketroll.interfaces.MyCreationsListener;

/**
 * Created by RKR on 22-02-2017.
 * MyCreationsRecyclerViewAdapter.
 */

public class MyCreationsRecyclerViewAdapter extends RecyclerView.Adapter<MyCreationsRecyclerViewAdapter.ViewHolder> {
    private List<File> mFileList = new ArrayList<>();
    private MyCreationsListener mMyCreationsListener;

    public MyCreationsRecyclerViewAdapter(MyCreationsListener myCreationsListener) {
        mMyCreationsListener = myCreationsListener;
    }

    public void setFileList(Context context) {
        try {
            File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), context.getString(R.string.created));
            if (directory.exists() && directory.isDirectory()) {
                File[] fileList = directory.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isFile();
                    }
                });
                this.mFileList.clear();
                if (fileList != null && fileList.length > 1) {
                    Arrays.sort(fileList, new Comparator<File>() {
                        @Override
                        public int compare(File object1, File object2) {
                            return (object1.lastModified() > object2.lastModified()) ? -1 : 1;
                        }
                    });
                }
                if (fileList != null) {
                    Collections.addAll(this.mFileList, fileList);
                }
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFile(int position) {
        if (position >= 0) {
            mFileList.remove(position);
            notifyItemRemoved(position);
        }
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
        File file = mFileList.get(position);
        if (file != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Bitmap resized = ThumbnailUtils.extractThumbnail(bitmap, width / 2, height / 2);
            holder.appCompatImageView.setImageBitmap(resized);
        } else {
            holder.appCompatImageView.setImageDrawable(ContextCompat.getDrawable(
                    holder.appCompatImageView.getContext(),
                    R.drawable.gallery
            ));
        }
    }

    @Override
    public int getItemCount() {
        return mFileList == null ? 0 : mFileList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatImageView appCompatImageView;
        private AppCompatImageButton whatsAppShareAppCompatImageButton;
        private AppCompatImageButton facebookShareAppCompatImageButton;
        private AppCompatImageButton actionRemoveFileAppCompatImageButton;
        private AppCompatImageButton actionShareAppCompatImageButton;

        ViewHolder(View itemView) {
            super(itemView);
            appCompatImageView = (AppCompatImageView) itemView.findViewById(R.id.AIMC_image_view);
            whatsAppShareAppCompatImageButton = (AppCompatImageButton) itemView.findViewById(R.id.AIMC_whatsAppShare);
            facebookShareAppCompatImageButton = (AppCompatImageButton) itemView.findViewById(R.id.AIMC_fbShare);
            actionRemoveFileAppCompatImageButton = (AppCompatImageButton) itemView.findViewById(R.id.AIMC_action_remove_file);
            actionShareAppCompatImageButton = (AppCompatImageButton) itemView.findViewById(R.id.AIMC_share);
            actionShareAppCompatImageButton.setVisibility(View.GONE);

            whatsAppShareAppCompatImageButton.setOnClickListener(this);
            facebookShareAppCompatImageButton.setOnClickListener(this);
            actionRemoveFileAppCompatImageButton.setOnClickListener(this);
            actionShareAppCompatImageButton.setOnClickListener(this);
            appCompatImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.AIMC_whatsAppShare) {
                int position = getAdapterPosition();
                if (mMyCreationsListener != null && position >= 0) {
                    File file = mFileList.get(position);
                    mMyCreationsListener.shareToWhatsApp(file);
                }
            } else if (v.getId() == R.id.AIMC_fbShare) {
                int position = getAdapterPosition();
                if (mMyCreationsListener != null && position >= 0) {
                    File file = mFileList.get(position);
                    Uri uri = Uri.fromFile(file);
                    mMyCreationsListener.shareToFacebook(uri);
                }
            } else if (v.getId() == R.id.AIMC_share) {
                int position = getAdapterPosition();
                if (mMyCreationsListener != null && position >= 0) {
                    File file = mFileList.get(position);
                    mMyCreationsListener.share(file);
                }
            } else if (v.getId() == R.id.AIMC_action_remove_file) {
                int position = getAdapterPosition();
                if (mMyCreationsListener != null && position >= 0) {
                    mMyCreationsListener.deleteFile(mFileList.get(position), position);
                }
            } else if (v.getId() == R.id.AIMC_image_view) {
                int position = getAdapterPosition();
                if (mMyCreationsListener != null && position >= 0) {
                    mMyCreationsListener.loadPreviewDialog(mFileList.get(position), position);
                }
            }
        }
    }
}
