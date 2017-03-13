package rkr.binatestation.maketroll.interfaces;

import android.net.Uri;

import java.io.File;

/**
 * Created by RKR on 28-02-2017.
 * MyCreationsListener.
 */

public interface MyCreationsListener {
    void shareToFacebook(Uri uri);

    void loadPreviewDialog(File file, int position);

    void shareToWhatsApp(File file);

    void deleteFile(File file, int position);

    void share(File file);
}
