package rkr.binatestation.maketroll.models;

/**
 * Created by RKR on 07-02-2017.
 * ViewType.
 */

public enum ViewType {
    TEXT(3), RECTANGLE(2), SQUARE(1);
    private int viewType;

    ViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }
}
