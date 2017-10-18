package rkr.binatestation.maketroll.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONObject;

/**
 * Created by RKR on 18-10-2017.
 * DataModel
 */

public class DataModel implements Parcelable {
    public static final Creator<DataModel> CREATOR = new Creator<DataModel>() {
        @Override
        public DataModel createFromParcel(Parcel in) {
            return new DataModel(in);
        }

        @Override
        public DataModel[] newArray(int size) {
            return new DataModel[size];
        }
    };
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private String filePath;
    private String description;

    public DataModel(String filePath, String description) {
        this.filePath = filePath;
        this.description = description;
    }

    public DataModel(@NonNull JSONObject jsonObject) {
        this.filePath = jsonObject.optString(KEY_NAME);
        this.description = jsonObject.optString(KEY_DESCRIPTION);
    }

    protected DataModel(Parcel in) {
        filePath = in.readString();
        description = in.readString();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(filePath);
        parcel.writeString(description);
    }
}
