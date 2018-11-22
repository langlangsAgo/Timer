package com.wonderelf.timer.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Author: cl
 * Time: 2018/11/21
 * Description:
 */
public class GridLayoutManagerNoBug extends GridLayoutManager {

    public GridLayoutManagerNoBug(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public GridLayoutManagerNoBug(Context context, int spanCount) {
        super(context, spanCount);
    }

    public GridLayoutManagerNoBug(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            //try catch一下
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
