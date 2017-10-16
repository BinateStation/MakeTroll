package rkr.binatestation.maketroll.models;

import android.support.annotation.DrawableRes;

import rkr.binatestation.maketroll.R;

/**
 * Created by RKR on 16-10-2017.
 * ErrorModel
 */

public class ErrorModel {
    private int icon;
    private String message;

    public ErrorModel(@DrawableRes int icon, String message) {
        this.icon = icon;
        this.message = message;
    }

    public static ErrorModel getEmptyModel() {
        return new ErrorModel(R.drawable.ic_sentiment_dissatisfied_black_24dp, "No Matching memes!");
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
