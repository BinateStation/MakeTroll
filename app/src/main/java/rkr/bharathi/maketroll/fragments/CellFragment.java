package rkr.bharathi.maketroll.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import com.vansuita.pickimage.PickImageDialog;
import com.vansuita.pickimage.PickSetup;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.listeners.IPickResult;

import jp.wasabeef.richeditor.RichEditor;
import rkr.bharathi.maketroll.BuildConfig;
import rkr.bharathi.maketroll.R;
import rkr.bharathi.maketroll.fragments.dialogs.ScaleFragment;
import rkr.bharathi.maketroll.fragments.dialogs.TextEditorFragment;
import rkr.bharathi.maketroll.models.ItemModel;
import rkr.bharathi.maketroll.models.ViewType;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.content.ContentValues.TAG;
import static android.view.MotionEvent.ACTION_DOWN;

/**
 * A simple {@link Fragment} subclass.
 */
public class CellFragment extends Fragment implements View.OnTouchListener, View.OnClickListener, View.OnLongClickListener {


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
    private RichEditor mLabelRichEditor;

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

        if (mItemModel.getViewType() != ViewType.TEXT) {
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
        } else {
            view.setOnLongClickListener(this);
            mLabelRichEditor = (RichEditor) view.findViewById(R.id.CL_label);
            mLabelRichEditor.setFocusable(false);
            mLabelRichEditor.setFocusableInTouchMode(false);
            mLabelRichEditor.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    CellFragment.this.onTouch(itemView, event);
                    return true;
                }
            });
            mLabelRichEditor.setEditorFontSize(22);
            mLabelRichEditor.setEditorFontColor(Color.RED);
            mLabelRichEditor.setEditorBackgroundColor(android.R.color.transparent);
            mLabelRichEditor.setHtml(getString(R.string.app_name));
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
        this.hideUnwantedViews = hideUnwantedViews;
        setView();
        if (mItemModel.getViewType() != ViewType.TEXT && mItemModel.getBitmap() == null && mCellFragmentListener != null) {
            mCellFragmentListener.remove(mPosition);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.AIF_action_remove_frame:
                if (mCellFragmentListener != null) {
                    mCellFragmentListener.remove(mPosition);
                }
                break;
            case R.id.AIF_action_add_image:
                showImagePicker();
                break;
        }
    }

    private void showTextEditorFragment() {
        String text = "";
        if (mLabelRichEditor != null) {
            text = mLabelRichEditor.getHtml();
        }
        TextEditorFragment textEditorFragment = TextEditorFragment.newInstance(text, new TextEditorFragment.TextEditorListener() {
            @Override
            public void onDone(String text) {
                if (mLabelRichEditor != null) {
                    //noinspection deprecation
                    mLabelRichEditor.setHtml(text);
                }
            }
        });
        textEditorFragment.show(getChildFragmentManager(), textEditorFragment.getTag());
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
        if (mItemModel != null && !hideUnwantedViews) {
            Bitmap bitmap = mItemModel.getBitmap();
            if (bitmap != null) {
                actionAddImage.setImageBitmap(bitmap);
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
        }
    }

    private void showImagePicker() {
        PickImageDialog.on(getChildFragmentManager(), new PickSetup(BuildConfig.APPLICATION_ID))
                .setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        Exception exception = r.getError();
                        if (exception == null) {
                            mItemModel.setBitmap(r.getBitmap());
                            setView();
                        } else {
                            Log.e(TAG, "onPickResult: ", exception);
                        }
                    }
                });
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.CL_label) {
            showTextEditorFragment();
            return true;
        }
        return false;
    }

    public interface CellFragmentListener {
        void invalidate();

        void remove(int position);
    }
}
