package rkr.bharathi.maketroll.web;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import static rkr.bharathi.maketroll.web.WebServiceConstants.KEY_SEARCH;
import static rkr.bharathi.maketroll.web.WebServiceConstants.URL_SEARCH;

public class WebServiceUtils {
    private static final String TAG = "WebServiceUtils";

    public static void search(Context context, String query, ServerResponseReceiver receiver) {
        Log.d(TAG, "search() called with: context = [" + context + "], query = [" + query + "], receiver = [" + receiver + "]");
        ContentValues params = new ContentValues();
        params.put(KEY_SEARCH, query);

        WebService.startActionApiCall(context, URL_SEARCH, params, receiver);
    }
}