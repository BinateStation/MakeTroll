package rkr.binatestation.maketroll.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import rkr.binatestation.maketroll.R;

public class Utils {
    private static final String TAG = "Utils";

    public static File createImageFile(Context context) {
        Log.d(TAG, "createImageFile() called with: context = [" + context + "]");
        String imageFileName = "created_image_" + Calendar.getInstance().getTimeInMillis();
        File storageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), context.getString(R.string.created));
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                return null;
            }
        }
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpeg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "createImageFile() returned: " + image);
        return image;
    }

    public static void showAlert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void showAlert(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, onClickListener);
        builder.setNegativeButton(android.R.string.cancel, onClickListener);
        builder.show();
    }

    public static void setTextStyle(TextView textView, boolean isBold, boolean isItalic) {
        if (isBold && isItalic) {
            textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
        } else if (isBold) {
            textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        } else if (isItalic) {
            textView.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
        } else {
            textView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        }
    }

}