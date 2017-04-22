package rkr.binatestation.maketroll.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rkr.binatestation.maketroll.R;

import static rkr.binatestation.maketroll.web.WebServiceConstants.URL_LIVE_IMAGE;

/**
 * Created by RKR on 22-02-2017.
 * ImageListRecyclerViewAdapter.
 */

public class ImageListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> mImageEndUrls = new ArrayList<>();
    private Set<String> selectedImages = new HashSet<>();
    private boolean mShowsDialog;
    private View.OnClickListener mOnClickListener;

    public ImageListRecyclerViewAdapter(boolean showsDialog, View.OnClickListener onClickListener) {
        mShowsDialog = showsDialog;
        mOnClickListener = onClickListener;
    }

    public void setImageEndUrls(List<String> imageEndUrls) {
        this.mImageEndUrls = imageEndUrls;
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedItemModel() {
        ArrayList<String> itemModels = new ArrayList<>();
        for (String imageUrl : selectedImages) {
            itemModels.add(imageUrl);
        }
        return itemModels;
    }

    public void clearSelection() {
        selectedImages.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 2) {
            return new ViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_image_list, parent, false)
            );
        } else {
            return new DevicePickerViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_pick_frame_type, parent, false)
            );
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mShowsDialog && position == 0) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 2 && holder instanceof ViewHolder) {
            ViewHolder viewHolderImageList = (ViewHolder) holder;
            String item = getItem(position);
            if (item != null) {
                if (selectedImages.contains(item)) {
                    viewHolderImageList.maskView.setVisibility(View.VISIBLE);
                } else {
                    viewHolderImageList.maskView.setVisibility(View.INVISIBLE);
                }

                String url = URL_LIVE_IMAGE + item;
                Context context = viewHolderImageList.appCompatImageView.getContext();
                Glide.with(context)
                        .load(url)
                        .centerCrop()
                        .placeholder(R.drawable.ic_image_black_24dp)
                        .crossFade()
                        .into(viewHolderImageList.appCompatImageView);
            }
        }
    }

    private String getItem(int position) {
        if (mShowsDialog) {
            return mImageEndUrls.get(position - 1);
        } else {
            return mImageEndUrls.get(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mShowsDialog) {
            return mImageEndUrls.size() + 1;
        } else {
            return mImageEndUrls.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatImageView appCompatImageView;
        private View maskView;

        ViewHolder(View itemView) {
            super(itemView);
            appCompatImageView = (AppCompatImageView) itemView.findViewById(R.id.AIIL_image);
            maskView = itemView.findViewById(R.id.AIIL_mask_view);
            appCompatImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final String key = getItem(getAdapterPosition());
            if (selectedImages.contains(key)) {
                selectedImages.remove(key);
                maskView.setVisibility(View.GONE);
            } else {
                final int height = v.getHeight();
                final int width = v.getWidth();
                selectedImages.add(key);
                maskView.setLayoutParams(new CardView.LayoutParams(width, height));
                maskView.setVisibility(View.VISIBLE);
            }
        }
    }

    private class DevicePickerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View pickFromDeviceView;

        DevicePickerViewHolder(View itemView) {
            super(itemView);
            pickFromDeviceView = itemView.findViewById(R.id.FPFT_frame_square);
            pickFromDeviceView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(v);
            }
        }
    }
}
