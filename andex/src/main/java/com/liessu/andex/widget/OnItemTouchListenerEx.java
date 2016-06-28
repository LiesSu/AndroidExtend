package com.liessu.andex.widget;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.lang.ref.SoftReference;

/**
 *
 */
public abstract class OnItemTouchListenerEx implements RecyclerView.OnItemTouchListener {
    protected GestureDetectorCompat gestureDetector;
    protected SoftReference<RecyclerView> recyclerViewSoftReference;
    protected boolean disallowIntercept;
    protected OnItemTouchListenerEx onItemTouchListenerEx;

    public OnItemTouchListenerEx(RecyclerView recyclerView, boolean disallowIntercept,
                                 OnItemTouchListenerEx onItemTouchListenerEx) {
        this.onItemTouchListenerEx = onItemTouchListenerEx;
        this.disallowIntercept = disallowIntercept;
        recyclerViewSoftReference = new SoftReference<>(recyclerView);
    }

    /**
     * Silently observe and/or take over touch events sent to the RecyclerView
     * before they are handled by either the RecyclerView itself or its child views.
     * <p>
     * <p>The onInterceptTouchEvent methods of each attached OnItemTouchListener will be run
     * in the order in which each listener was added, before any other touch processing
     * by the RecyclerView itself or child views occurs.</p>
     *
     * @param rv
     * @param e  MotionEvent describing the touch event. All coordinates are in
     *           the RecyclerView's coordinate system.
     * @return true if this OnItemTouchListener wishes to begin intercepting touch events, false
     * to continue with the current behavior and continue observing future events in
     * the gesture.
     */
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        //TODO : 确定调用内容
        if (onItemTouchListenerEx == null) {
            return disallowIntercept || gestureDetector.onTouchEvent(e);
        } else {
            return disallowIntercept || gestureDetector.onTouchEvent(e) || onItemTouchListenerEx.onInterceptTouchEvent(rv, e);
        }
    }

    /**
     * Process a touch event as part of a gesture that was claimed by returning true from
     * a previous call to {@link #onInterceptTouchEvent}.
     *
     * @param rv
     * @param e  MotionEvent describing the touch event. All coordinates are in
     */
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        //Nothing to do ,  onInterceptTouchEvent has been consume event .
    }

    /**
     * Called when a child of RecyclerView does not want RecyclerView and its ancestors to
     * intercept touch events with
     * {@link ViewGroup#onInterceptTouchEvent(MotionEvent)}.
     *
     * @param disallowIntercept True if the child does not want the parent to
     *                          intercept touch events.
     * @see ViewParent#requestDisallowInterceptTouchEvent(boolean)
     */
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        this.disallowIntercept = disallowIntercept;
    }

    protected abstract class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {
    }
}
