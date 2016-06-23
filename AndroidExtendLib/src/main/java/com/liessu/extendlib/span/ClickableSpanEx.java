/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liessu.extendlib.span;


import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * A extensional class for ClickableSpan .

 * <p/>
 * <pre>
 * ex :
 * new ClickableSpanEx(Color.BLUE,Color.GRAY) {
 *      public void onClick(View widget) {
 *          Toast.makeText(DemoApplication.getContext(),name,Toast.LENGTH_SHORT).show();
 *      }
 * }
 * </pre>
 * <pre>
 * Note : If you need to  indicate click event of  ClickableSpanEx  , you must set the TextView OnTouchListener
 * like this :
 * <b>textView.setOnTouchListener(new ClickableSpanEx.ClickableSpanSelector());</b>
 * or
 * call <b>{@link #onTouch(View, MotionEvent, boolean)} in your own onTouch method </b>.
 * </pre>
 */
public abstract class ClickableSpanEx extends ClickableSpan {
    private static final int DEFAULT_FOREGROUND_COLOR = Color.BLUE;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;
    private static final String TAG = "ClickableSpanEx";
    /**Is Intercept touch event **/
    // protected static boolean isInterceptTouchEvent = false;

    /**
     * Foreground color , protected member
     **/
    protected int mForegroundColor = DEFAULT_FOREGROUND_COLOR;
    /**
     * Background color  , protected member
     **/
    protected int mBackgroundColor = DEFAULT_BACKGROUND_COLOR;
    /**
     * Determine whether the background of ClickableSpanEx transparent
     **/
    protected boolean isVisibility = true;

    /**
     * New ClickableSpanEx instance.
     */
    public ClickableSpanEx() {
        this(DEFAULT_FOREGROUND_COLOR, DEFAULT_BACKGROUND_COLOR);
    }

    /**
     * New ClickableSpanEx instance .
     *
     * @param foregroundColor foreground color
     */
    public ClickableSpanEx(@ColorInt int foregroundColor) {
        this(foregroundColor, DEFAULT_BACKGROUND_COLOR);
    }

    /**
     * New ClickableSpanEx instance .
     *
     * @param foregroundColor foreground color
     * @param backgroundColor backgroundColor color
     */
    public ClickableSpanEx(@ColorInt int foregroundColor, @ColorInt int backgroundColor) {
        mForegroundColor = foregroundColor;
        mBackgroundColor = backgroundColor;
    }

    /**
     * If you have  implement OnTouchListener, call this one in your own
     * {@link View.OnTouchListener#onTouch(View, MotionEvent) OnTouchListener.onTouch}  method .
     *
     * @param v                The view the touch event has been dispatched to.
     * @param event            The MotionEvent object containing full information about the event.
     * @param isAllowIntercept Is allow ClickableSpanEx intercept the touch event
     * @return True if the listener has consumed the event , false otherwise.
     */
    public static boolean onTouch(View v, MotionEvent event, boolean isAllowIntercept) {
        int action = event.getAction();
        if (v instanceof TextView) {
            TextView widget = (TextView) v;
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();
            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);


            //* Return the text the TextView is displaying. If TextView.setText() was called with
            // * an argument of BufferType.SPANNABLE or BufferType.EDITABLE, you can cast
            // * the return value from this method to Spannable or Editable, respectively.
            // *
            //* Note: The content of the return value should not be modified. If you want
            //* a modifiable one, you should make your own copy first.
            Spannable buffer = (Spannable) widget.getText();
            ClickableSpanEx[] link = buffer.getSpans(off, off, ClickableSpanEx.class);
            if (link.length != 0) {
                if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
                    link[0].setBackgroundVisibility(false);
                } else {
                    if (action == MotionEvent.ACTION_UP) link[0].onClick(widget);
                    link[0].setBackgroundVisibility(true);
                    Selection.removeSelection(buffer);
                }

                return isAllowIntercept;
            }
        } else {
            Log.e(TAG, "ClickableSpanEx supports TextView only .");
        }
        return false;
    }

    /**
     * Sets the background color for this ClickableSpanEx.
     *
     * @param color the color of the background
     */
    public void setBackgroundColor(@ColorInt int color) {
        this.mBackgroundColor = color;
    }

    /**
     * Sets the foreground color for this ClickableSpanEx.
     *
     * @param foregroundColor the color of the background
     */
    public void setForegroundColor(@ColorInt int foregroundColor) {
        this.mForegroundColor = foregroundColor;
    }

    /**
     * Determine whether the background of ClickableSpanEx transparent .
     *
     * @param isVisibility disable background color if true
     */
    public void setBackgroundVisibility(boolean isVisibility) {
        this.isVisibility = isVisibility;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(mForegroundColor);
        ds.setUnderlineText(false);

        ds.bgColor = isVisibility ? Color.TRANSPARENT : mBackgroundColor;
    }

    /**
     * A inner static  class implements OnTouchListener interface . You can use it to indicate click
     * event of  ClickableSpanEx .
     */
    public static final class Selector implements View.OnTouchListener {
        private boolean mIsAllowIntercept = false;

        public Selector(boolean isAllowIntercept) {
            mIsAllowIntercept = isAllowIntercept;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return ClickableSpanEx.onTouch(v, event, mIsAllowIntercept);
        }
    }

    //Customize yourself ClickableSpanEx , enjoy it !
    //Add inner class builder , if necessary .
}
