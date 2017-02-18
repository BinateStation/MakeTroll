package rkr.bharathi.maketroll.models;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RKR on 07-02-2017.
 * ItemModel.
 */

public class ItemModel {


    private ViewType viewType;
    private Bitmap bitmap;


    public ItemModel(ViewType viewType) {
        this.viewType = viewType;
    }


    public static List<ItemModel> getDummyList(int size) {
        List<ItemModel> itemModels = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            itemModels.add(new ItemModel(ViewType.RECTANGLE));
        }
        return itemModels;
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


}
