package com.liessu.andex.widget.recycler.listener;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;

/**
 *
 */
public abstract class OnItemClickListenerEx extends OnItemTouchListenerEx implements AbsListView.OnItemClickListener {

    public OnItemClickListenerEx(RecyclerView recyclerView, boolean disallowIntercept) {
        super(recyclerView, disallowIntercept, null);
        gestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new ItemClickHelperGestureListener());
    }

    public OnItemClickListenerEx(RecyclerView recyclerView, boolean disallowIntercept,
                                 OnItemTouchListenerEx onItemTouchListenerEx) {
        super(recyclerView, disallowIntercept, onItemTouchListenerEx);
        gestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new ItemClickHelperGestureListener());
    }

    private class ItemClickHelperGestureListener extends OnItemTouchListenerEx.ItemTouchHelperGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //TODO : 如果确定是否拦截
            RecyclerView recyclerView = recyclerViewSoftReference.get();
            if (recyclerView != null) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                int position = recyclerView.getChildAdapterPosition(child);
                if (child != null) {
                    RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                    //TODO : instead null
                    onItemClick(null, child, position, child.getId());
                }
                return true;
            }
            return false;
        }
    }
}
