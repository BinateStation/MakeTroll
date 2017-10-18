package rkr.binatestation.maketroll.database;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.ArrayList;

import rkr.binatestation.maketroll.models.DataModel;


/**
 * IntentService used to do db actions in background thread.
 */
public class DbActionsIntentService extends IntentService {
    public static final int RESULT_CODE_SUCCESS = 1;
    public static final int RESULT_CODE_IN_PROGRESS = 2;
    public static final int RESULT_CODE_ERROR = 3;
    public static final String KEY_ERROR_MESSAGE = "error_message";
    public static final String KEY_IN_PROGRESS_MESSAGE = "in_progress_message";
    public static final String KEY_SUCCESS_MESSAGE = "success_message";
    public static final String KEY_INSERT_ID = "insert_id";

    private static final String ACTION_SAVE_FILES = "com.cmm.isik.database.action.SAVE_FILES";

    private static final String EXTRA_PARAM1 = "com.cmm.isik.database.extra.PARAM1";

    private static final String TAG = "DbActionsIntentService";

    private ResultReceiver mResultReceiver;

    public DbActionsIntentService() {
        super("DbActionsIntentService");
    }

    /**
     * Starts this service to perform action insert students with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSaveFiles(Context context, ArrayList<DataModel> filePaths) {
        Log.d(TAG, "startActionSaveFiles() called with: context = [" + context + "], filePaths = [" + filePaths + "]");
        if (context != null) {
            Intent intent = new Intent(context, DbActionsIntentService.class);
            intent.setAction(ACTION_SAVE_FILES);
            intent.putParcelableArrayListExtra(EXTRA_PARAM1, filePaths);
            context.startService(intent);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_SAVE_FILES: {
                    ArrayList<DataModel> filePaths = intent.getParcelableArrayListExtra(EXTRA_PARAM1);
                    handleActionSaveFiles(filePaths);
                }
                break;
            }
        }
    }

    private void sendReceiverData(int resultCode, String key, String message) {
        Log.d(TAG, "sendReceiverData() called with: resultCode = [" + resultCode + "], key = [" + key + "], message = [" + message + "]");
        if (mResultReceiver != null) {
            Bundle bundle = new Bundle();
            bundle.putString(key, message);
            mResultReceiver.send(resultCode, bundle);
        }
    }

    private void sendReceiverData(int resultCode, Bundle bundle) {
        Log.d(TAG, "sendReceiverData() called with: resultCode = [" + resultCode + "], bundle = [" + bundle + "]");
        if (mResultReceiver != null) {
            mResultReceiver.send(resultCode, bundle);
        }
    }

    /**
     * Handle action Save Student Data in the provided background thread with the provided
     * parameters.
     *
     * @param filePaths the row to add
     */
    private void handleActionSaveFiles(ArrayList<DataModel> filePaths) {
        Log.d(TAG, "handleActionSaveFiles() called with: filePaths = [" + filePaths + "]");

        if (filePaths != null) {
            int noOfRowsInserted = getContentResolver().bulkInsert(TrollMakerContract.FilePaths.CONTENT_URI, getContentValues(filePaths));
            Bundle bundle = new Bundle();
            bundle.putLong(KEY_SUCCESS_MESSAGE, noOfRowsInserted);
            sendReceiverData(RESULT_CODE_SUCCESS, bundle);
        } else {
            sendReceiverData(RESULT_CODE_ERROR, KEY_ERROR_MESSAGE, "Status false");
        }
    }

    private ContentValues[] getContentValues(ArrayList<DataModel> filePaths) {
        ContentValues[] contentValues = new ContentValues[filePaths.size()];
        for (int i = 0; i < filePaths.size(); i++) {
            ContentValues values = new ContentValues();
            DataModel dataModel = filePaths.get(i);
            values.put(TrollMakerContract.FilePaths.COLUMN_FILE_PATH, dataModel.getFilePath());
            values.put(TrollMakerContract.FilePaths.COLUMN_DESCRIPTION, dataModel.getDescription());
            contentValues[i] = values;
        }
        return contentValues;
    }

}
