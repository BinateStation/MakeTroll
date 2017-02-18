package rkr.bharathi.maketroll.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

public class Utils {
    private static final String TAG = "Utils";
    /**
     * static variable for saving images in external storage directory.
     */

    private static final String SAVED_IMAGE_FILE_PATH = Environment.getExternalStorageDirectory().toString() +
            File.separator + "MakeTroll" + File.separator + "SavedImages" + File.separator;

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Log.d(TAG, "getCroppedBitmap() called with: bitmap = [" + bitmap + "]");
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        Log.d(TAG, "getCroppedBitmap() returned: " + output);
        return output;
    }

    public static void putBooleansToSharedPreferences(Context context, Map<String, Boolean> values) {
        Log.d(TAG, "putBooleansToSharedPreferences() called with: context = [" + context + "], values = [" + values + "]");
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).edit();
            for (String key : values.keySet()) {
                editor.putBoolean(key, values.get(key));
            }
            editor.apply();
        }
    }

    public static void putIntsToSharedPreferences(Context context, Map<String, Integer> values) {
        Log.d(TAG, "putIntsToSharedPreferences() called with: context = [" + context + "], values = [" + values + "]");
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).edit();
            for (String key : values.keySet()) {
                editor.putInt(key, values.get(key));
            }
            editor.apply();
        }
    }

    public static String getStringFromSharedPreferences(Context context, String key) {
        Log.d(TAG, "getStringFromSharedPreferences() called with: context = [" + context + "], key = [" + key + "]");
        try {
            return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).getString(key, "");
        } catch (Exception e) {
            Log.e(TAG, "getStringFromSharedPreferences: ", e);
            e.printStackTrace();
            return "";
        }
    }

    public static Boolean getBooleanFromSharedPreferences(Context context, String key) {
        Log.d(TAG, "getBooleanFromSharedPreferences() called with: context = [" + context + "], key = [" + key + "]");
        try {
            return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).getBoolean(key, false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean emailValidator(CharSequence email) {
        Log.d(TAG, "emailValidator() called with: email = [" + email + "]");
        Boolean isMatches = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        Log.d(TAG, "emailValidator() returned: " + isMatches);
        return isMatches;
    }

    /**
     * static method used to get the External Storage path which ensures whether it exits or not
     */

    private static File getSavedImageFileDirs() {
        Log.d(TAG, "getSavedImageFileDirs() called");
        File file = new File(SAVED_IMAGE_FILE_PATH);
        if (file.exists()) {
            Log.d(TAG, "getSavedImageFileDirs() returned: " + file);
            return file;
        } else {
            if (file.mkdirs()) {
                Log.d(TAG, "getSavedImageFileDirs() returned: " + file);
                return file;
            } else {
                File defaultPathFile = new File(Environment.getExternalStorageDirectory().toString() + File.separator);
                Log.d(TAG, "getSavedImageFileDirs() returned: " + defaultPathFile);
                return defaultPathFile;
            }
        }
    }

    public static File createImageFile(Context context) {
        Log.d(TAG, "createImageFile() called with: context = [" + context + "]");
        String imageFileName = "created_image_" + Calendar.getInstance().getTimeInMillis();
        File storageDir = getSavedImageFileDirs();
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