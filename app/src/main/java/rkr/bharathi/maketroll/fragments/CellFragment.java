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

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import jp.wasabeef.richeditor.RichEditor;
import rkr.bharathi.maketroll.R;
import rkr.bharathi.maketroll.fragments.dialogs.ScaleFragment;
import rkr.bharathi.maketroll.fragments.dialogs.TextEditorFragment;
import rkr.bharathi.maketroll.models.ItemModel;
import rkr.bharathi.maketroll.models.ViewType;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.view.MotionEvent.ACTION_DOWN;

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
    private RichEditor mLabelRichEditor;
    private ImageButton editLabelImageButton;
    private ImageButton removeFrameImageButton;
    private ImageButton actionBringToFront;

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
        } else {
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
            mLabelRichEditor.setEditorFontColor(Color.BLACK);
            mLabelRichEditor.setEditorBackgroundColor(Color.TRANSPARENT);
            mLabelRichEditor.setHtml(getString(R.string.message));

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
        if (mItemModel.getViewType() != ViewType.TEXT && mItemModel.getBitmap() == null && mCellFragmentListener != null) {
            mCellFragmentListener.remove(mPosition);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.AIF_action_remove_frame:
            case R.id.CL_action_remove_frame:
                if (mCellFragmentListener != null) {
                    mCellFragmentListener.remove(mPosition);
                }
                break;
            case R.id.AIF_action_add_image:
                showImagePicker();
                break;
            case R.id.CL_action_edit:
                showTextEditorFragment();
                break;
            case R.id.AIF_action_bring_to_front:
            case R.id.CL_action_bring_to_front:
                bringToFrontCell();
        }
    }

    private void showTextEditorFragment() {
        Log.d(TAG, "showTextEditorFragment() called");
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
