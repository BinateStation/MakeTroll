package rkr.bharathi.maketroll.adapters;

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

import rkr.bharathi.maketroll.R;

import static rkr.bharathi.maketroll.web.WebServiceConstants.URL_LIVE_IMAGE;

/**
 * Created by RKR on 22-02-2017.
 * ImageListRecyclerViewAdapter.
 */

public class ImageListRecyclerViewAdapter extends RecyclerView.Adapter<ImageListRecyclerViewAdapter.ViewHolder> {
    private JSONArray jsonArray;
    private Set<String> selectedImages = new HashSet<>();

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
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_image_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (jsonArray != null) {
            String url = URL_LIVE_IMAGE + jsonArray.optString(position);
            Context context = holder.appCompatImageView.getContext();
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.gallery)
                    .crossFade()
                    .into(holder.appCompatImageView);
        }
    }

    @Override
    public int getItemCount() {
        if (jsonArray != null) {
            return jsonArray.length();
        } else {
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            final String key = jsonArray.optString(getAdapterPosition());
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
