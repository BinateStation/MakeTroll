package rkr.binatestation.maketroll.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by RKR on 07-02-2017.
 * ItemModel.
 */

public class ItemModel implements Parcelable {
    public static final Parcelable.Creator<ItemModel> CREATOR
            = new Parcelable.Creator<ItemModel>() {
        public ItemModel createFromParcel(Parcel in) {
            return new ItemModel(in);
        }

        public ItemModel[] newArray(int size) {
            return new ItemModel[size];
        }
    };

    private ViewType viewType;
    private Bitmap bitmap;
    private String endUrl;
    private boolean isFromDevice;


    public ItemModel(ViewType viewType) {
        this.viewType = viewType;
    }

    private ItemModel(Parcel in) {
        viewType = ViewType.valueOf(in.readString());
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        isFromDevice = in.readInt() == 1;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(viewType.name());
        out.writeParcelable(bitmap, flags);
        out.writeInt(isFromDevice ? 1 : 0);
    }

    public ViewType getViewType() {
        return viewType;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getEndUrl() {
        return endUrl;
    }

    public void setEndUrl(String endUrl) {
        this.endUrl = endUrl;
    }

    public boolean isFromDevice() {
        return isFromDevice;
    }

    public void setFromDevice(boolean fromDevice) {
        isFromDevice = fromDevice;
    }
}

