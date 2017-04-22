package rkr.binatestation.maketroll.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import rkr.binatestation.maketroll.R;
import rkr.binatestation.maketroll.fragments.dialogs.TextEditorFragment;
import rkr.binatestation.maketroll.models.ItemModel;
import rkr.binatestation.maketroll.models.ViewType;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.view.MotionEvent.ACTION_DOWN;
import static rkr.binatestation.maketroll.utils.Utils.setTextStyle;
import static rkr.binatestation.maketroll.web.WebServiceConstants.URL_LIVE_IMAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CellFragment extends Fragment implements View.OnTouchListener, View.OnClickListener {

    private static final String TAG = "CellFragment";
    private float mLastY = Float.MIN_VALUE;
    private float mLastX = Float.MIN_VALUE;
    private int mXDelta;
    private int mYDelta;
    private CellFragmentListener mCellFragmentListener;
    private FrameLayout.LayoutParams mLayoutParams;
    private ItemModel mItemModel;
    private boolean mHideUnwantedViews = false;
    private int mPosition;
    private ImageView mActionAddImage;
    private ImageButton mActionRemoveFrame;
    private PhotoViewAttacher mAttache;
    private View mMaskView;
    private View mItemView;
    private View mLabelToolbarView;
    private TextView mLabelTextView;
    private String mLabelText = "";
    private int mTextColor = Color.WHITE;
    private int mBgColor = Color.GRAY;
    private float mTextSize = 20;
    private boolean mIsBold;
    private boolean mIsItalic;
    private boolean mIsUnderLine;

    public CellFragment() {
        // Required empty public constructor
    }

    public static CellFragment newInstance(FrameLayout.LayoutParams layoutParams, ViewType viewType, int position) {

        Bundle args = new Bundle();
        CellFragment fragment = new CellFragment();
        fragment.setArguments(args);
        fragment.mLayoutParams = layoutParams;
        fragment.mItemModel = new ItemModel(viewType);
        fragment.mPosition = position;
        return fragment;
    }

    public static CellFragment newInstance(FrameLayout.LayoutParams layoutParams, ItemModel itemModel, int position) {

        Bundle args = new Bundle();
        CellFragment fragment = new CellFragment();
        fragment.setArguments(args);
        fragment.mLayoutParams = layoutParams;
        fragment.mItemModel = itemModel;
        fragment.mPosition = position;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mItemModel != null && mItemModel.getViewType() != ViewType.TEXT) {
            return inflater.inflate(R.layout.adapter_item_frame, container, false);
        } else {
            return inflater.inflate(R.layout.cell_label, container, false);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.mItemView = view;
        super.onViewCreated(view, savedInstanceState);
        if (mLayoutParams != null) {
            view.setLayoutParams(mLayoutParams);
        }
        view.setOnTouchListener(this);
        view.setOnClickListener(this);

        if (mItemModel != null && mItemModel.getViewType() != ViewType.TEXT) {
            mActionAddImage = (ImageView) view.findViewById(R.id.AIF_action_add_image);
            mActionRemoveFrame = (ImageButton) view.findViewById(R.id.AIF_action_remove_frame);
            mMaskView = view.findViewById(R.id.AIF_mask_view);
            ImageButton actionResizeImageButton = (ImageButton) view.findViewById(R.id.AIF_action_resize);
            ImageButton dragHelper = (ImageButton) view.findViewById(R.id.AIF_drag_button);
            dragHelper.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    CellFragment.this.onTouch(mItemView, event);
                    return true;
                }
            });
            actionResizeImageButton.setOnTouchListener(this);

            mActionRemoveFrame.setOnClickListener(this);
            mActionAddImage.setOnClickListener(this);
            ImageButton actionBringToFrontImageButton = (ImageButton) view.findViewById(R.id.AIF_action_bring_to_front);
            actionBringToFrontImageButton.setOnClickListener(this);

            mAttache = new PhotoViewAttacher(mActionAddImage);
            mAttache.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mMaskView.setVisibility(View.VISIBLE);
                    return true;
                }
            });
            mMaskView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaskView.setVisibility(View.GONE);
                }
            });
            setView();
        } else {
            mLabelText = getString(R.string.title_activity_home);
            mBgColor = ContextCompat.getColor(getContext(), R.color.colorMask);
            mLabelTextView = (TextView) view.findViewById(R.id.CL_label);
            mLabelToolbarView = view.findViewById(R.id.CL_toolbar_layout);
            mLabelTextView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    CellFragment.this.onTouch(mItemView, event);
                    return true;
                }
            });
            ImageButton editLabelImageButton = (ImageButton) view.findViewById(R.id.CL_action_edit);
            editLabelImageButton.setOnClickListener(this);

            ImageButton removeFrameImageButton = (ImageButton) view.findViewById(R.id.CL_action_remove_frame);
            removeFrameImageButton.setOnClickListener(this);

            ImageButton actionBringToFront = (ImageButton) view.findViewById(R.id.CL_action_bring_to_front);
            actionBringToFront.setOnClickListener(this);

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.AIF_action_resize) {
            if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
                if (mLastY != Float.MIN_VALUE && mLastX != Float.MAX_VALUE) {
                    final float height = event.getRawY() - mLastY;
                    final float width = event.getRawX() - mLastX;

                    mItemView.post(new Runnable() {
                        @Override
                        public void run() {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mItemView.getLayoutParams();
                            layoutParams.height += height;
                            layoutParams.width += width;
                            int finalHeight = layoutParams.height;
                            int finalWidth = layoutParams.width;
                            if (finalHeight > 150 && finalWidth > 150) {
                                mItemView.setLayoutParams(layoutParams);
                            }
                        }
                    });
                }
                mLastY = event.getRawY();
                mLastX = event.getRawX();
            }

            if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                mLastY = Float.MIN_VALUE;
                mLastX = Float.MIN_VALUE;
            }

            return true;
        } else {
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case ACTION_DOWN:
                    FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) v.getLayoutParams();
                    mXDelta = X - lParams.leftMargin;
                    mYDelta = Y - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v.getLayoutParams();
                    layoutParams.leftMargin = X - mXDelta;
                    layoutParams.topMargin = Y - mYDelta;
                    layoutParams.rightMargin = 0;
                    layoutParams.bottomMargin = 0;
                    v.setLayoutParams(layoutParams);
                    break;
            }
            if (mCellFragmentListener != null) {
                mCellFragmentListener.invalidate();
            }
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CellFragmentListener) {
            mCellFragmentListener = (CellFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCellFragmentListener = null;
    }

    public void setHideUnwantedViews(boolean hideUnwantedViews) {
        Log.d(TAG, "setHideUnwantedViews() called with: mHideUnwantedViews = [" + hideUnwantedViews + "]");
        this.mHideUnwantedViews = hideUnwantedViews;
        setView();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.AIF_action_remove_frame || i == R.id.CL_action_remove_frame) {
            if (mCellFragmentListener != null) {
                mCellFragmentListener.remove(mPosition, this);
            }

        } else if (i == R.id.CL_action_edit) {
            showTextEditorFragment();

        } else if (i == R.id.AIF_action_bring_to_front || i == R.id.CL_action_bring_to_front) {
            bringToFrontCell();
        }
    }

    private void showTextEditorFragment() {
        Log.d(TAG, "showTextEditorFragment() called");
        TextEditorFragment textEditorFragment = TextEditorFragment.newInstance(
                mLabelText,
                mTextColor,
                mBgColor,
                mTextSize,
                mIsBold,
                mIsItalic,
                mIsUnderLine,
                new TextEditorFragment.TextEditorListener() {
                    @Override
                    public void onDone(String text, boolean isBold, boolean isItalic, boolean isUnderLine, float textSize, int textColor, int bgColor) {
                        mIsBold = isBold;
                        mIsItalic = isItalic;
                        mIsUnderLine = isUnderLine;
                        mLabelText = text;
                        mTextColor = textColor;
                        mBgColor = bgColor;
                        mTextSize = textSize;

                        if (mLabelTextView != null) {
                            mLabelTextView.setTextColor(textColor);
                            mLabelTextView.setBackgroundColor(bgColor);
                            mLabelTextView.setTextSize(textSize);
                            mLabelTextView.setText(text);
                            setTextStyle(mLabelTextView, isBold, isItalic);
                            setTextPaintFlags(mLabelTextView, isUnderLine);
                        }
                    }
                });
        textEditorFragment.show(getChildFragmentManager(), textEditorFragment.getTag());
    }

    private void setTextPaintFlags(TextView labelTextView, boolean isUnderLine) {
        if (isUnderLine) {
            labelTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        }
    }

    private void setView() {
        Log.d(TAG, "setView() called");
        if (mItemModel != null && !mHideUnwantedViews) {
            Bitmap bitmap = mItemModel.getBitmap();
            if (bitmap != null) {
                mActionAddImage.setImageBitmap(bitmap);
            } else if (mItemModel.getEndUrl() != null) {
                String url;
                if (mItemModel.isFromDevice()) {
                    url = mItemModel.getEndUrl();
                } else {
                    url = URL_LIVE_IMAGE + mItemModel.getEndUrl();
                }
                Context context = mActionAddImage.getContext();
                Glide.with(context)
                        .load(url)
                        .asBitmap()
                        .placeholder(R.drawable.ic_image_black_24dp)
                        .error(R.drawable.ic_image_black_24dp)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                mActionAddImage.setImageBitmap(resource);
                                mItemModel.setBitmap(resource);
                                mAttache.update();
                            }
                        })
                ;
            } else {
                mActionAddImage.setImageDrawable(ContextCompat.getDrawable(
                        mActionAddImage.getContext(),
                        R.drawable.ic_add_a_photo_black_24dp
                ));
            }
            mAttache.update();
        }
        if (mItemModel != null && mItemModel.getViewType() != ViewType.TEXT && mHideUnwantedViews) {
            mActionRemoveFrame.setVisibility(View.GONE);
            mMaskView.setVisibility(View.GONE);
        } else if (mItemModel != null && mItemModel.getViewType() == ViewType.TEXT && mHideUnwantedViews) {
            mLabelToolbarView.setVisibility(View.INVISIBLE);
        }
    }

    private void bringToFrontCell() {
        if (mItemView != null) {
            mItemView.bringToFront();
        }
    }


    public interface CellFragmentListener {
        void invalidate();

        void remove(int position, CellFragment cellFragment);
    }
}
