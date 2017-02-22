package rkr.bharathi.maketroll.web;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by RKR on 20-12-2016.
 * ServerResponseReceiver.
 */

public class ServerResponseReceiver extends ResultReceiver {
    static final String KEY_SERVER_RESPONSE_STRING = "server_response";
    static final String KEY_UPDATE_PROGRESS = "update_progress";
    private static final String RESPONSE_MESSAGE_SUCCESS = "Success";
    private static final String RESPONSE_MESSAGE_JSON_PARSE_ERROR = "Result is not a JSONObject String";
    private static final String RESPONSE_MESSAGE_SERVER_ERROR = "Error server response";
    private static final String RESPONSE_MESSAGE_PROTOCOL_ERROR = "Using wrong Protocol";
    private static final String RESPONSE_MESSAGE_MALFORMED_URL = "Url not correct";
    private static final String RESPONSE_MESSAGE_IO_ERROR = "IO Exception";
    private static final String TAG = "ServerResponseReceiver";
    private Receiver mReceiver;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler new handler to handle this result
     */
    public ServerResponseReceiver(Handler handler, Receiver receiver) {
        super(handler);
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (200 == resultCode) {
            try {
                JSONObject jsonObject = new JSONObject(resultData.getString(KEY_SERVER_RESPONSE_STRING));
                if (mReceiver != null) {
                    mReceiver.onServerResponse(resultCode, jsonObject, RESPONSE_MESSAGE_SUCCESS);
                }
            } catch (JSONException e) {
                Log.e(TAG, "onReceiveResult: ", e);
                if (mReceiver != null) {
                    mReceiver.onServerResponse(resultCode, null, RESPONSE_MESSAGE_JSON_PARSE_ERROR);
                }
            }
        } else if (499 == resultCode) {
            if (mReceiver != null) {
                mReceiver.onServerResponse(resultCode, null, RESPONSE_MESSAGE_PROTOCOL_ERROR);
            }
        } else if (498 == resultCode) {
            if (mReceiver != null) {
                mReceiver.onServerResponse(resultCode, null, RESPONSE_MESSAGE_MALFORMED_URL);
            }
        } else if (497 == resultCode) {
            if (mReceiver != null) {
                mReceiver.onServerResponse(resultCode, null, RESPONSE_MESSAGE_IO_ERROR);
            }
        } else if (199 == resultCode) {
            if (mReceiver != null) {
                mReceiver.onUpdateProgress(resultData.getInt(KEY_UPDATE_PROGRESS));
            }
        } else {
            if (mReceiver != null) {
                mReceiver.onServerResponse(resultCode, null, RESPONSE_MESSAGE_SERVER_ERROR);
            }
        }
    }

    public interface Receiver {
        void onServerResponse(int resultCode, JSONObject resultData, String message);

        void onUpdateProgress(int progress);
    }

}
