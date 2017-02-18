package rkr.bharathi.maketroll.utils;

/**
 * Created by RKR on 08-02-2017.
 * ItemTouchHelperViewHolder.
 */

public interface ItemTouchHelperViewHolder {
    /**
     * Implementations should update the item view to indicate it's active state.
     */
    void onItemSelected();


    /**
     * state should be cleared.
     */
    void onItemClear();
}
