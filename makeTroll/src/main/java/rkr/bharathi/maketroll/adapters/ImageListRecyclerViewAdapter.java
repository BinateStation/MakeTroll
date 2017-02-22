package rkr.bharathi.maketroll.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import rkr.bharathi.maketroll.R;
import rkr.bharathi.maketroll.models.ItemModel;
import rkr.bharathi.maketroll.models.ViewType;

import static rkr.bharathi.maketroll.web.WebServiceConstants.URL_LIVE_IMAGE;

/**
 * Created by RKR on 22-02-2017.
 * ImageListRecyclerViewAdapter.
 */

public class ImageListRecyclerViewAdapter extends RecyclerView.Adapter<ImageListRecyclerViewAdapter.ViewHolder> {
    private JSONArray jsonArray;
    private Map<String, ItemModel> selectedItemModelMap = new LinkedHashMap<>();

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
        notifyDataSetChanged();
    }

    public ArrayList<ItemModel> getSelectedItemModel() {
        ArrayList<ItemModel> itemModels = new ArrayList<>();
        for (ItemModel itemModel : selectedItemModelMap.values()) {
            itemModels.add(itemModel);
        }
        return itemModels;
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
            if (selectedItemModelMap.containsKey(key)) {
                selectedItemModelMap.remove(key);
                maskView.setVisibility(View.GONE);
            } else {
                Context context = appCompatImageView.getContext();
                String url = URL_LIVE_IMAGE + key;
                Glide.with(context)
                        .load(url)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                // Do something with bitmap here.
                                ItemModel itemModel = new ItemModel(ViewType.SQUARE);
                                itemModel.setBitmap(bitmap);

                                selectedItemModelMap.put(key, itemModel);
                                maskView.setVisibility(View.GONE);
                            }
                        });
            }
        }
    }
}
