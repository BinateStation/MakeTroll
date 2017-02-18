package rkr.bharathi.maketroll.utils;

import rkr.bharathi.maketroll.adapters.RecyclerViewAdapter;

/**
 * Created by RKR on 08-02-2017.
 * OnStartDragListener.
 */

public interface OnStartDragListener {
    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerViewAdapter.ViewHolder viewHolder);
}
