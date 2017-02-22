package rkr.bharathi.maketroll.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import rkr.bharathi.maketroll.R;

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
                    ".png",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "createImageFile() returned: " + image);
        return image;
    }
}