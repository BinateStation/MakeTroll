package rkr.binatestation.maketroll.fragments.dialogs;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import rkr.binatestation.maketroll.R;
import yuku.ambilwarna.AmbilWarnaDialog;

import static rkr.binatestation.maketroll.utils.Utils.setTextStyle;

/**
 * Dialog fragment to add text
 */
public class TextEditorFragment extends DialogFragment {

    private static final String TAG = "TextEditorFragment";

    private static final String KEY_TEXT = "text";

    private TextEditorListener mTextEditorListener;
    private int mTextColor = Color.WHITE;
    private int mBgColor = Color.GRAY;
    private float mTextSize = 20;
    private boolean mIsBold;
    private boolean mIsItalic;
    private boolean mIsUnderLine;

    private EditText mEditor;
    private ImageButton mActionBoldImageButton;
    private ImageButton mActionItalicImageButton;
    private ImageButton mActionUnderLine;

    public TextEditorFragment() {
        // Required empty public constructor
    }

    public static TextEditorFragment newInstance(String text, int textColor, int bgColor, float textSize, boolean isBold, boolean isItalic, boolean isUnderLine, TextEditorListener textEditorListener) {
        Log.d(TAG, "newInstance() called");
        Bundle args = new Bundle();
        args.putString(KEY_TEXT, text);
        TextEditorFragment fragment = new TextEditorFragment();
        fragment.setArguments(args);
        fragment.mTextEditorListener = textEditorListener;
        fragment.mTextColor = textColor;
        fragment.mBgColor = bgColor;
        fragment.mTextSize = textSize;
        fragment.mIsBold = isBold;
        fragment.mIsItalic = isItalic;
        fragment.mIsUnderLine = isUnderLine;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        Window window = dialog.getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_editor, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditor = (EditText) view.findViewById(R.id.FTE_editor);
        mActionBoldImageButton = (ImageButton) view.findViewById(R.id.action_bold);
        mActionItalicImageButton = (ImageButton) view.findViewById(R.id.action_italic);
        mActionUnderLine = (ImageButton) view.findViewById(R.id.action_underline);

        mEditor.setTextColor(mTextColor);
        mEditor.setBackgroundColor(mBgColor);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(KEY_TEXT)) {
            mEditor.setText(bundle.getString(KEY_TEXT));
        } else {
            mEditor.setHint("Insert text here...");
        }
        setTextStyle(mEditor, mIsBold, mIsItalic);
        setTextUnderLine(mIsUnderLine);

        mActionBoldImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionBoldImageButton.isSelected()) {
                    mActionBoldImageButton.setSelected(false);
                    setTextStyle(mEditor, false, mActionItalicImageButton.isSelected());
                } else {
                    mActionBoldImageButton.setSelected(true);
                    setTextStyle(mEditor, true, mActionItalicImageButton.isSelected());
                }
            }
        });

        mActionItalicImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionItalicImageButton.isSelected()) {
                    mActionItalicImageButton.setSelected(false);
                    setTextStyle(mEditor, mActionBoldImageButton.isSelected(), false);
                } else {
                    mActionItalicImageButton.setSelected(true);
                    setTextStyle(mEditor, mActionBoldImageButton.isSelected(), true);
                }
            }
        });

        mActionUnderLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextUnderLine(!mActionUnderLine.isSelected());
            }
        });

        view.findViewById(R.id.action_txt_size).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSizePicker(new NumberPickerFragment.NumberPickerListener() {
                    @Override
                    public void onDone(int number) {
                        mEditor.setTextSize(number);
                        mTextSize = number;
                    }
                });
            }
        });

        view.findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPicker(Color.BLACK, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        mEditor.setTextColor(color);
                        mTextColor = color;
                    }
                });
            }
        });

        view.findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPicker(Color.GRAY, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        mEditor.setBackgroundColor(color);
                        mBgColor = color;
                    }
                });
            }
        });

        Button doneButton = (Button) view.findViewById(R.id.FTE_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTextEditorListener != null) {
                    mTextEditorListener.onDone(
                            mEditor.getText().toString(),
                            mActionBoldImageButton.isSelected(),
                            mActionItalicImageButton.isSelected(),
                            mActionUnderLine.isSelected(),
                            mTextSize,
                            mTextColor,
                            mBgColor
                    );
                }
                dismiss();
            }
        });
    }


    private void showColorPicker(int defaultColor, AmbilWarnaDialog.OnAmbilWarnaListener onAmbilWarnaListener) {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(getContext(), defaultColor, true, onAmbilWarnaListener);
        ambilWarnaDialog.show();
    }

    private void showSizePicker(NumberPickerFragment.NumberPickerListener numberPickerListener) {
        NumberPickerFragment numberPickerFragment = NumberPickerFragment.newInstance(
                12,
                100,
                (int) mTextSize,
                numberPickerListener
        );
        numberPickerFragment.show(getChildFragmentManager(), numberPickerFragment.getTag());
    }


    private void setTextUnderLine(boolean isUnderLine) {
        if (isUnderLine) {
            mActionUnderLine.setSelected(true);
            mEditor.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        } else {
            mActionUnderLine.setSelected(false);
            mEditor.setPaintFlags(mEditor.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        }
    }

    public interface TextEditorListener {
        void onDone(String text, boolean isBold, boolean isItalic, boolean isUnderLine, float textSize, int textColor, int bgColor);
    }
}
