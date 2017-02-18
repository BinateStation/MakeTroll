package rkr.bharathi.maketroll.adapters;

import android.graphics.Bitmap;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.vansuita.pickimage.PickImageDialog;
import com.vansuita.pickimage.PickSetup;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rkr.bharathi.maketroll.BuildConfig;
import rkr.bharathi.maketroll.R;
import rkr.bharathi.maketroll.fragments.dialogs.ScaleFragment;
import rkr.bharathi.maketroll.models.ItemModel;
import rkr.bharathi.maketroll.models.ViewType;
import rkr.bharathi.maketroll.utils.ItemTouchHelperAdapter;
import rkr.bharathi.maketroll.utils.ItemTouchHelperViewHolder;
import rkr.bharathi.maketroll.utils.OnStartDragListener;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.content.ContentValues.TAG;

/**
 * Created by RKR on 07-02-2017.
 * RecyclerViewAdapter.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private List<ItemModel> mItemModelList = new ArrayList<>();
    private OnStartDragListener mDragStartListener;
    private int mMaxWidth = 400;
    private int mMaxHeight = 600;
    private boolean hideUnwantedViews = false;
    private FragmentManager mSupportFragmentManager;

    public RecyclerViewAdapter(FragmentManager supportFragmentManager, OnStartDragListener dragListener) {
        mSupportFragmentManager = supportFragmentManager;
        mDragStartListener = dragListener;
    }

    public void setMeasures(int maxHeight, int maxWidth) {
        this.mMaxHeight = maxHeight;
        this.mMaxWidth = maxWidth;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_frame, parent, false)
        );
    }

    public void setItemModelList(List<ItemModel> itemModelList) {
        this.mItemModelList = itemModelList;
        notifyDataSetChanged();
    }

    public void addItem(ViewType viewType) {
        int insertedItemPosition = getItemCount();
        mItemModelList.add(new ItemModel(viewType));
        notifyItemInserted(insertedItemPosition);
    }

    public void setHideUnwantedViews(boolean hideUnwantedViews) {
        this.hideUnwantedViews = hideUnwantedViews;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mItemModelList.get(position).getViewType().getViewType();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemModel itemModel = mItemModelList.get(position);
        if (itemModel != null && !hideUnwantedViews) {
            Bitmap bitmap = itemModel.getBitmap();
            if (bitmap != null) {
                holder.actionAddImage.setImageBitmap(bitmap);
            } else {
                holder.actionAddImage.setImageDrawable(ContextCompat.getDrawable(
                        holder.actionAddImage.getContext(),
                        R.drawable.ic_add_a_photo_black_24dp
                ));
            }
            holder.mAttache.update();
        }
        if (hideUnwantedViews) {
            holder.actionRemoveFrame.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mItemModelList.size();
    }

    private void removeItem(int position) {
        mItemModelList.remove(position);
        notifyItemRemoved(position);
    }

    private void showImagePicker(final int adapterPosition) {
        if (mSupportFragmentManager != null) {
            PickImageDialog.on(getSupportFragmentManager(), new PickSetup(BuildConfig.APPLICATION_ID))
                    .setOnPickResult(new IPickResult() {
                        @Override
                        public void onPickResult(PickResult r) {
                            Exception exception = r.getError();
                            if (exception == null) {
                                mItemModelList.get(adapterPosition).setBitmap(r.getBitmap());
                                notifyItemChanged(adapterPosition);
                            } else {
                                Log.e(TAG, "onPickResult: ", exception);
                            }
                        }
                    });
        }
    }

    private FragmentManager getSupportFragmentManager() {
        return mSupportFragmentManager;
    }

    /**
     * Called when an item has been dragged far enough to trigger a move. This is called every time
     * an item is shifted, and not at the end of a "drop" event.
     *
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then end position of the moved item.
     */
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItemModelList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * Called when an item has been dismissed by a swipe.
     *
     * @param position The position of the item dismissed.
     */
    @Override
    public void onItemDismiss(int position) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {
        private ImageView actionAddImage;
        private ImageButton actionRemoveFrame;
        private PhotoViewAttacher mAttache;
        private View maskView;
        private ImageButton dragHelper;
        private ImageButton scaleHelper;

        ViewHolder(View itemView) {
            super(itemView);
            actionAddImage = (ImageView) itemView.findViewById(R.id.AIF_action_add_image);
            actionRemoveFrame = (ImageButton) itemView.findViewById(R.id.AIF_action_remove_frame);
            maskView = itemView.findViewById(R.id.AIF_mask_view);
            dragHelper = (ImageButton) itemView.findViewById(R.id.AIF_drag_button);
            scaleHelper = (ImageButton) itemView.findViewById(R.id.AIF_action_scale);

            actionRemoveFrame.setOnClickListener(this);
            actionAddImage.setOnClickListener(this);

            mAttache = new PhotoViewAttacher(actionAddImage);
            mAttache.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    showImagePicker(getAdapterPosition());
                }
            });

            mAttache.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    maskView.setVisibility(View.VISIBLE);
                    return true;
                }
            });
            maskView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    maskView.setVisibility(View.GONE);
                }
            });
            dragHelper.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mDragStartListener.onStartDrag(ViewHolder.this);
                    return true;
                }
            });
            scaleHelper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionScale(mMaxHeight, mMaxWidth, actionAddImage.getMeasuredHeight(), actionAddImage.getMeasuredWidth());
                }
            });
        }

        private void actionScale(int height, int width, int measuredHeight, int measuredWidth) {
            ScaleFragment scaleFragment = ScaleFragment.newInstance(height, width, measuredHeight, measuredWidth, new ScaleFragment.ScaleListener() {
                @Override
                public void onChangeHeight(int height) {
                    if (itemView != null) {
                        int width = itemView.getMeasuredWidth();
                        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
                        layoutParams.width = width;
                        layoutParams.height = height;
                        itemView.setLayoutParams(layoutParams);
                        itemView.requestLayout();
                    }
                }

                @Override
                public void onChangeWidth(int width) {
                    int height = itemView.getMeasuredHeight();
                    ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
                    layoutParams.width = width;
                    layoutParams.height = height;
                    itemView.setLayoutParams(layoutParams);
                    itemView.requestLayout();
                }

                @Override
                public void onDone(int maxWidth) {
                    int position = getAdapterPosition();
                    if (position >= 0 && position < getItemCount()) {
                        int width = itemView.getMeasuredWidth();
                        if (width < maxWidth - 100) {
                            mItemModelList.get(position).setViewType(ViewType.SQUARE);
                        } else {
                            mItemModelList.get(position).setViewType(ViewType.RECTANGLE);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
            scaleFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog_Scale);
            scaleFragment.show(getSupportFragmentManager(), scaleFragment.getTag());
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.AIF_action_remove_frame:
                    removeItem(getAdapterPosition());
                    break;
                case R.id.AIF_action_add_image:
                    showImagePicker(getAdapterPosition());
                    break;
            }
        }

        /**
         * Implementations should update the item view to indicate it's active state.
         */
        @Override
        public void onItemSelected() {
        }

        /**
         * state should be cleared.
         */
        @Override
        public void onItemClear() {
        }
    }
}
