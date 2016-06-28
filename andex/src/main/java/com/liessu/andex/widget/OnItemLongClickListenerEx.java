package com.liessu.andex.widget;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;

/**
 *
 */
public abstract class OnItemLongClickListenerEx extends OnItemTouchListenerEx implements AbsListView.OnItemLongClickListener{

    public OnItemLongClickListenerEx(RecyclerView recyclerView, boolean disallowIntercept) {
        super(recyclerView, disallowIntercept, null);
        gestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new ItemLongClickHelperGestureListener());
    }

    public OnItemLongClickListenerEx(RecyclerView recyclerView, boolean disallowIntercept,
                                     OnItemTouchListenerEx onItemTouchListenerEx) {
        super(recyclerView, disallowIntercept, onItemTouchListenerEx);
        gestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new ItemLongClickHelperGestureListener());
    }

    private class ItemLongClickHelperGestureListener extends OnItemTouchListenerEx.ItemTouchHelperGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            RecyclerView recyclerView = recyclerViewSoftReference.get();
            if (recyclerView != null) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                int position = recyclerView.getChildAdapterPosition(child);
                if (child != null) {
                    RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                    //TODO : instead null
                    child.performClick();//检查是否能够触发selector
                    onItemLongClick(null, child, position, child.getId());
                }
            }
        }
    }
}
