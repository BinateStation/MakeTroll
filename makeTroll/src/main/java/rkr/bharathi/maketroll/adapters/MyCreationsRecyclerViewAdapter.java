package rkr.bharathi.maketroll.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileFilter;

import rkr.bharathi.maketroll.R;

/**
 * Created by RKR on 22-02-2017.
 * MyCreationsRecyclerViewAdapter.
 */

public class MyCreationsRecyclerViewAdapter extends RecyclerView.Adapter<MyCreationsRecyclerViewAdapter.ViewHolder> {
    private File[] fileList;

    public MyCreationsRecyclerViewAdapter() {
    }

    public void setFileList(Context context) {
        File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), context.getString(R.string.created));
        if (directory.exists() && directory.isDirectory()) {
            fileList = directory.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile();
                }
            });
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
        File file = fileList[position];
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
        return fileList == null ? 0 : fileList.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView appCompatImageView;

        ViewHolder(View itemView) {
            super(itemView);
            appCompatImageView = (AppCompatImageView) itemView.findViewById(R.id.AIMC_image_view);
        }
    }
}
