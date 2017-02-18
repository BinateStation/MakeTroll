package rkr.bharathi.maketroll.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by RKR on 17-02-2017.
 * FrameModel.
 */

public class FrameModel implements Parcelable {

    public static final Parcelable.Creator<FrameModel> CREATOR
            = new Parcelable.Creator<FrameModel>() {
        public FrameModel createFromParcel(Parcel in) {
            return new FrameModel(in);
        }

        public FrameModel[] newArray(int size) {
            return new FrameModel[size];
        }
    };

    private int width;
    private int height;
    private int index;
    private int xDiff;
    private int yDiff;
    private boolean isDragging;

    public FrameModel(int width, int height, int index, boolean isDragging) {
        this.width = width;
        this.height = height;
        this.index = index;
        this.isDragging = isDragging;
    }

    private FrameModel(Parcel in) {
        width = in.readInt();
        height = in.readInt();
        index = in.readInt();
        xDiff = in.readInt();
        yDiff = in.readInt();
        isDragging = in.readInt() == 1;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(width);
        out.writeInt(height);
        out.writeInt(index);
        out.writeInt(xDiff);
        out.writeInt(yDiff);
        out.writeInt(isDragging ? 1 : 0);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    public int getxDiff() {
        return xDiff;
    }

    public void setxDiff(int xDiff) {
        this.xDiff = xDiff;
    }

    public int getyDiff() {
        return yDiff;
    }

    public void setyDiff(int yDiff) {
        this.yDiff = yDiff;
    }
}
