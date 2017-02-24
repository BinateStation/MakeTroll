package rkr.binatestation.maketroll.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import rkr.binatestation.maketroll.R;
import rkr.binatestation.maketroll.fragments.dialogs.ScaleFragment;
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
public class CellFragment extends Fragment implements View.OnTouchListener, View.OnClickListener, IPickResult {

    private static final String TAG = "CellFragment";

    private int _xDelta;
    private int _yDelta;
    private CellFragmentListener mCellFragmentListener;
    private FrameLayout.LayoutParams mLayoutParams;
    private ItemModel mItemModel;
    private int mMaxWidth = 400;
    private int mMaxHeight = 600;
    private boolean hideUnwantedViews = false;
    private int mPosition;
    private ImageView actionAddImage;
    private ImageButton actionRemoveFrame;
    private PhotoViewAttacher mAttache;
    private View maskView;
    private View itemView;
    private ImageButton editLabelImageButton;
    private ImageButton removeFrameImageButton;
    private ImageButton actionBringToFront;
    private TextView mLabelTextView;
    private String mLabelText = "";
    private int mTextColor = Color.WHITE;
    private int mBgColor = Color.GRAY;
    private float mTextSize = 12;
    private boolean mIsBold;
    private boolean mIsItalic;
    private boolean mIsUnderLine;
    private boolean mIsStrikeThru;

    public CellFragment() {
        // Required empty public constructor
    }

    public static CellFragment newInstance(FrameLayout.LayoutParams layoutParams, int width, int height, ViewType viewType, int position) {

        Bundle args = new Bundle();
        CellFragment fragment = new CellFragment();
        fragment.setArguments(args);
        fragment.mLayoutParams = layoutParams;
        fragment.mItemModel = new ItemModel(viewType);
        fragment.mMaxWidth = width;
        fragment.mMaxHeight = height;
        fragment.mPosition = position;
        return fragment;
    }

    public static CellFragment newInstance(FrameLayout.LayoutParams layoutParams, int width, int height, ItemModel itemModel, int position) {

        Bundle args = new Bundle();
        CellFragment fragment = new CellFragment();
        fragment.setArguments(args);
        fragment.mLayoutParams = layoutParams;
        fragment.mItemModel = itemModel;
        fragment.mMaxWidth = width;
        fragment.mMaxHeight = height;
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
        this.itemView = view;
        super.onViewCreated(view, savedInstanceState);
        if (mLayoutParams != null) {
            view.setLayoutParams(mLayoutParams);
        }
        view.setOnTouchListener(this);
        view.setOnClickListener(this);

        if (mItemModel != null && mItemModel.getViewType() != ViewType.TEXT) {
            actionAddImage = (ImageView) view.findViewById(R.id.AIF_action_add_image);
            actionRemoveFrame = (ImageButton) view.findViewById(R.id.AIF_action_remove_frame);
            maskView = view.findViewById(R.id.AIF_mask_view);
            ImageButton scaleHelper = (ImageButton) view.findViewById(R.id.AIF_action_scale);
            ImageButton dragHelper = (ImageButton) view.findViewById(R.id.AIF_drag_button);
            dragHelper.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    CellFragment.this.onTouch(itemView, event);
                    return true;
                }
            });

            actionRemoveFrame.setOnClickListener(this);
            actionAddImage.setOnClickListener(this);
            ImageButton actionBringToFrontImageButton = (ImageButton) view.findViewById(R.id.AIF_action_bring_to_front);
            actionBringToFrontImageButton.setOnClickListener(this);

            mAttache = new PhotoViewAttacher(actionAddImage);
            mAttache.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    showImagePicker();
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
            scaleHelper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionScale(mMaxHeight, mMaxWidth, actionAddImage.getMeasuredHeight(), actionAddImage.getMeasuredWidth());
                }
            });
            setView();
        } else {
            mLabelText = getString(R.string.title_activity_home);
            mBgColor = ContextCompat.getColor(getContext(), R.color.colorMask);
            mLabelTextView = (TextView) view.findViewById(R.id.CL_label);
            mLabelTextView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    CellFragment.this.onTouch(itemView, event);
                    return true;
                }
            });
            editLabelImageButton = (ImageButton) view.findViewById(R.id.CL_action_edit);
            editLabelImageButton.setOnClickListener(this);

            removeFrameImageButton = (ImageButton) view.findViewById(R.id.CL_action_remove_frame);
            removeFrameImageButton.setOnClickListener(this);

            actionBringToFront = (ImageButton) view.findViewById(R.id.CL_action_bring_to_front);
            actionBringToFront.setOnClickListener(this);

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case ACTION_DOWN:
                FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) v.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v.getLayoutParams();
                layoutParams.leftMargin = X - _xDelta;
                layoutParams.topMargin = Y - _yDelta;
                layoutParams.rightMargin = 0;
                layoutParams.bottomMargin = 0;
                v.setLayoutParams(layoutParams);
                break;
        }
        if (mCellFragmentListener != null) {
            mCellFragmentListener.invalidate();
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
        Log.d(TAG, "setHideUnwantedViews() called with: hideUnwantedViews = [" + hideUnwantedViews + "]");
        this.hideUnwantedViews = hideUnwantedViews;
        setView();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.AIF_action_remove_frame || i == R.id.CL_action_remove_frame) {
            if (mCellFragmentListener != null) {
                mCellFragmentListener.remove(mPosition);
            }

        } else if (i == R.id.AIF_action_add_image) {
            showImagePicker();

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
                mIsStrikeThru,
                new TextEditorFragment.TextEditorListener() {
                    @Override
                    public void onDone(String text, boolean isBold, boolean isItalic, boolean isUnderLine, boolean isStrikeThru, float textSize, int textColor, int bgColor) {
                        mIsBold = isBold;
                        mIsItalic = isItalic;
                        mIsUnderLine = isUnderLine;
                        mLabelText = text;
                        mTextColor = textColor;
                        mBgColor = bgColor;
                        mTextSize = textSize;
                        mIsStrikeThru = isStrikeThru;

                        if (mLabelTextView != null) {
                            mLabelTextView.setTextColor(textColor);
                            mLabelTextView.setBackgroundColor(bgColor);
                            mLabelTextView.setTextSize(textSize);
                            mLabelTextView.setText(text);
                            setTextStyle(mLabelTextView, isBold, isItalic);
                            setTextPaintFlags(mLabelTextView, isUnderLine, isStrikeThru);
                        }
                    }
                });
        textEditorFragment.show(getChildFragmentManager(), textEditorFragment.getTag());
    }

    private void setTextPaintFlags(TextView labelTextView, boolean isUnderLine, boolean isStrikeThru) {
        if (isUnderLine && isStrikeThru) {
            labelTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.STRIKE_THRU_TEXT_FLAG);
        } else if (isUnderLine) {
            labelTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        } else if (isStrikeThru) {
            labelTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private void actionScale(int height, int width, int measuredHeight, int measuredWidth) {
        Log.d(TAG, "actionScale() called with: height = [" + height + "], width = [" + width + "], measuredHeight = [" + measuredHeight + "], measuredWidth = [" + measuredWidth + "]");
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
                int width = itemView.getMeasuredWidth();
                if (width < maxWidth - 100) {
                    mItemModel.setViewType(ViewType.SQUARE);
                } else {
                    mItemModel.setViewType(ViewType.RECTANGLE);
                }
                setView();
            }
        });
        scaleFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog_Scale);
        scaleFragment.show(getChildFragmentManager(), scaleFragment.getTag());
    }

    private void setView() {
        Log.d(TAG, "setView() called");
        if (mItemModel != null && !hideUnwantedViews) {
            Bitmap bitmap = mItemModel.getBitmap();
            if (bitmap != null) {
                actionAddImage.setImageBitmap(bitmap);
            } else if (mItemModel.getEndUrl() != null) {
                String url = URL_LIVE_IMAGE + mItemModel.getEndUrl();
                Context context = actionAddImage.getContext();
                Glide.with(context)
                        .load(url)
                        .asBitmap()
                        .placeholder(R.drawable.gallery)
                        .error(R.drawable.gallery)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                actionAddImage.setImageBitmap(resource);
                                mItemModel.setBitmap(resource);
                                mAttache.update();
                            }
                        })
                ;
            } else {
                actionAddImage.setImageDrawable(ContextCompat.getDrawable(
                        actionAddImage.getContext(),
                        R.drawable.ic_add_a_photo_black_24dp
                ));
            }
            mAttache.update();
        }
        if (mItemModel != null && mItemModel.getViewType() != ViewType.TEXT && hideUnwantedViews) {
            actionRemoveFrame.setVisibility(View.GONE);
            maskView.setVisibility(View.GONE);
        } else if (mItemModel != null && mItemModel.getViewType() == ViewType.TEXT && hideUnwantedViews) {
            editLabelImageButton.setVisibility(View.INVISIBLE);
            removeFrameImageButton.setVisibility(View.INVISIBLE);
            actionBringToFront.setVisibility(View.INVISIBLE);
        }
    }

    private void showImagePicker() {
        Log.d(TAG, "showImagePicker() called");
        PickImageDialog.build(new PickSetup()).show(getActivity()).setOnPickResult(this);
    }

    private void bringToFrontCell() {
        if (itemView != null) {
            itemView.bringToFront();
        }
    }

    @Override
    public void onPickResult(PickResult pickResult) {
        Throwable exception = pickResult.getError();
        if (exception == null) {
            mItemModel.setBitmap(pickResult.getBitmap());
            setView();
        } else {
            Log.e(TAG, "onPickResult: ", exception);
        }

    }

    public interface CellFragmentListener {
        void invalidate();

        void remove(int position);
    }
}
