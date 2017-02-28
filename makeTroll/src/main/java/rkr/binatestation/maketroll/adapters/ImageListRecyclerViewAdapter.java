package rkr.binatestation.maketroll.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import rkr.binatestation.maketroll.R;

import static rkr.binatestation.maketroll.web.WebServiceConstants.URL_LIVE_IMAGE;

/**
 * Created by RKR on 22-02-2017.
 * ImageListRecyclerViewAdapter.
 */

public class ImageListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private JSONArray jsonArray;
    private Set<String> selectedImages = new HashSet<>();
    private boolean mShowsDialog;
    private View.OnClickListener mOnClickListener;

    public ImageListRecyclerViewAdapter(boolean showsDialog, View.OnClickListener onClickListener) {
        mShowsDialog = showsDialog;
        mOnClickListener = onClickListener;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
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
                        .placeholder(R.drawable.gallery)
                        .crossFade()
                        .into(viewHolderImageList.appCompatImageView);
            }
        }
    }

    private String getItem(int position) {
        if (jsonArray != null) {
            if (mShowsDialog) {
                return jsonArray.optString(position - 1);
            } else {
                return jsonArray.optString(position);
            }
        } else {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        if (jsonArray != null) {
            if (mShowsDialog) {
                return jsonArray.length() + 1;
            } else {
                return jsonArray.length();
            }
        } else {
            if (mShowsDialog) {
                return 1;
            } else {
                return 0;
            }
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
