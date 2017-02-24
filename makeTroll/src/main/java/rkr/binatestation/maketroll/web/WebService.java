package rkr.binatestation.maketroll.web;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static rkr.binatestation.maketroll.web.ServerResponseReceiver.KEY_SERVER_RESPONSE_STRING;
import static rkr.binatestation.maketroll.web.ServerResponseReceiver.KEY_UPDATE_PROGRESS;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WebService extends IntentService {
    private static final String TAG = "WebService";
    private static final String POST = "POST";
    private static final String HEADER_KEY_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_CONTENT_TYPE = "application/x-www-form-urlencoded;charset=UTF-8";
    private static final String HEADER_KEY_CONTENT_LENGTH = "Content-Length";
    /**
     * Http request timeout
     */
    private static final int READ_TIMEOUT = 30000 /* milliseconds */;
    private static final int CONNECT_TIMEOUT = 30000 /* milliseconds */;

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_API_CALL = "rkr.bharathi.kineticinsight.webservices.action.API_CALL";

    private static final String EXTRA_PARAM1 = "rkr.bharathi.kineticinsight.webservices.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "rkr.bharathi.kineticinsight.webservices.extra.PARAM2";
    private static final String EXTRA_PARAM3 = "rkr.bharathi.kineticinsight.webservices.extra.PARAM3";

    private ResultReceiver mResultReceiver;

    public WebService() {
        super("WebService");
    }

    /**
     * Starts this service to perform action Api Call with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionApiCall(Context context, String url, ContentValues param, ServerResponseReceiver receiver) {
        Intent intent = new Intent(context, WebService.class);
        intent.setAction(ACTION_API_CALL);
        intent.putExtra(EXTRA_PARAM1, url);
        intent.putExtra(EXTRA_PARAM2, param);
        intent.putExtra(EXTRA_PARAM3, receiver);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_API_CALL.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final ContentValues param2 = intent.getParcelableExtra(EXTRA_PARAM2);
                mResultReceiver = intent.getParcelableExtra(EXTRA_PARAM3);
                handleActionApiCall(param1, param2);
            }
        }
    }

    /**
     * Handle action Api Call in the provided background thread with the provided
     * parameters.
     */
    private void handleActionApiCall(String urlString, ContentValues params) {
        Log.d(TAG, "handleActionApiCall() called with: urlString = [" + urlString + "], params = [" + params + "]");
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            String body;
            if (params != null) {
                conn.setDoOutput(true);
                conn.setUseCaches(false);

                body = writeBody(params);
                conn.setFixedLengthStreamingMode(body.length());

                conn.setRequestMethod(POST);
                conn.setRequestProperty(HEADER_KEY_CONTENT_TYPE, HEADER_CONTENT_TYPE);
                conn.setRequestProperty(HEADER_KEY_CONTENT_LENGTH, Integer.toString(body.length()));

                // post the request
                OutputStream out = conn.getOutputStream();
                byte[] payload = body.getBytes();
                int totalSize = payload.length;
                int bytesTransferred = 0;
                int chunkSize = 2000;

                while (bytesTransferred < totalSize) {
                    int nextChunkSize = totalSize - bytesTransferred;
                    if (nextChunkSize > chunkSize) {
                        nextChunkSize = chunkSize;
                    }
                    out.write(payload, bytesTransferred, nextChunkSize);
                    bytesTransferred += nextChunkSize;

                    // Here you can call the method which updates progress
                    // be sure to wrap it so UI-updates are done on the main thread!
                    int progress = (100 * bytesTransferred / totalSize);
                    Log.i(TAG, "handleActionApiCall: Progress :- " + progress);
                    if (mResultReceiver != null) {
                        Bundle progressData = new Bundle();
                        progressData.putInt(KEY_UPDATE_PROGRESS, progress);
                        mResultReceiver.send(199, progressData);
                    }
                }
                out.close();
            }
            conn.connect();

            int response = conn.getResponseCode();
            Log.d(TAG, "The response is: " + response);

            if (200 == response) {
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is);
                Log.d(TAG, "handleActionApiCall: Response String :- " + contentAsString);
                Bundle resultData = new Bundle();
                resultData.putString(KEY_SERVER_RESPONSE_STRING, contentAsString);
                if (mResultReceiver != null) {
                    mResultReceiver.send(response, resultData);
                }
            } else {
                is = conn.getErrorStream();
                Log.e(TAG, "handleActionApiCall: Error:- " + conn.getResponseMessage() + "  " + readIt(is));
                if (mResultReceiver != null) {
                    mResultReceiver.send(response, null);
                }
            }

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (ProtocolException e) {
            e.printStackTrace();
            if (mResultReceiver != null) {
                mResultReceiver.send(499, null);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            if (mResultReceiver != null) {
                mResultReceiver.send(498, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (mResultReceiver != null) {
                mResultReceiver.send(497, null);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String writeBody(ContentValues values) {
        StringBuilder bodyBuilder = new StringBuilder();
        for (String key : values.keySet()) {
            if (bodyBuilder.length() != 0) {
                bodyBuilder.append('&');
            }
            bodyBuilder.append(key).append('=')
                    .append(values.getAsString(key));
        }
        return bodyBuilder.toString();
    }

    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream stream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
