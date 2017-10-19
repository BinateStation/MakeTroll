package rkr.binatestation.maketroll.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rkr.binatestation.maketroll.R;
import rkr.binatestation.maketroll.models.ErrorModel;
import rkr.binatestation.maketroll.models.ImagePickerModel;

import static rkr.binatestation.maketroll.utils.Constants.URL_LIVE_IMAGE;

/**
 * Created by RKR on 22-02-2017.
 * ImageListRecyclerViewAdapter.
 */

public class ImageListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> mImageEndUrls = new ArrayList<>();
    private Set<String> selectedImages = new HashSet<>();
    private View.OnClickListener mOnClickListener;

    public ImageListRecyclerViewAdapter(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void setImageEndUrls(List<Object> imageEndUrls) {
        this.mImageEndUrls = imageEndUrls;
        if (imageEndUrls.size() < 1) {
            this.mImageEndUrls.add(ErrorModel.getEmptyModel());
        }
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
        } else if (viewType == 1) {
            return new DevicePickerViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_pick_frame_type, parent, false)
            );
        } else {
            return new ErrorViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.error_item, parent, false)
            );
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object object = getItem(position);
        if (object instanceof ImagePickerModel) {
            return 1;
        } else if (object instanceof String) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ErrorViewHolder) {
            ErrorViewHolder errorViewHolder = (ErrorViewHolder) holder;
            errorViewHolder.bindView(getItem(position));
        } else {
            if (getItemViewType(position) == 2 && holder instanceof ViewHolder) {
                ViewHolder viewHolderImageList = (ViewHolder) holder;
                Object object = getItem(position);
                if (object instanceof String) {
                    String item = (String) object;

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
    }

    private Object getItem(int position) {
        return mImageEndUrls.get(position);
    }

    @Override
    public int getItemCount() {
        return mImageEndUrls.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatImageView appCompatImageView;
        private View maskView;

        ViewHolder(View itemView) {
            super(itemView);
            appCompatImageView = itemView.findViewById(R.id.AIIL_image);
            maskView = itemView.findViewById(R.id.AIIL_mask_view);
            appCompatImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                Object object = getItem(getAdapterPosition());
                if (object instanceof String) {
                    String key = (String) object;
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
        }
    }

    private class ErrorViewHolder extends RecyclerView.ViewHolder {
        private ImageView appCompatImageView;
        private TextView message;

        ErrorViewHolder(View itemView) {
            super(itemView);
            appCompatImageView = itemView.findViewById(R.id.imageView);
            message = itemView.findViewById(R.id.message);
        }

        void bindView(Object object) {
            if (object instanceof ErrorModel) {
                ErrorModel errorModel = (ErrorModel) object;
                message.setText(errorModel.getMessage());
                appCompatImageView.setImageResource(errorModel.getIcon());
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
