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
package com.liessu.andex.span;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;

/**
 *  A extensional class for SpannableStringBuilder , ignore the Android sdk version .
 */
public class SpannableStringBuilderEx extends SpannableStringBuilder{
    private final static String TAG = "SpanStringBuilderEx";

    /**
     * Create a new SpannableStringBuilderEx with empty contents
     */
    public SpannableStringBuilderEx() {
        super("");
    }

    /**
     * Create a new SpannableStringBuilderEx containing a copy of the
     * specified text, including its spans if any.
     */
    public SpannableStringBuilderEx(@NonNull final CharSequence text) {
        super(text, 0, text.length());
    }

    /**
     * Create a new SpannableStringBuilderEx containing a copy of the
     * specified slice of the specified text, including its spans if any.
     */
    public SpannableStringBuilderEx(final CharSequence text,final int start,final int end) {
       super(text , start , end);
    }


    @Override
    public SpannableStringBuilderEx append(final char text) {
        append(String.valueOf(text));
        return this;
    }

    @Override
    public SpannableStringBuilderEx append(@NonNull final CharSequence text) {
        int length = length();
        replace(length, length, text, 0, text.length());
        return this;
    }

    @Override
    public SpannableStringBuilder append(@NonNull final CharSequence text, int start, int end) {
        int length = length();
        replace(length, length, text, start, end);
        return this;
    }


    /**
     * Appends the character sequence {@code text} and spans {@code what} over the appended part.
     * See {@link Spanned} for an explanation of what the flags mean.
     * @param text the character sequence to append.
     * @param what the object to be spanned over the appended text.
     * @param flags see {@link Spanned}.
     * @return this {@code SpannableStringBuilder}.
     */
    @Override
    public SpannableStringBuilderEx append(CharSequence text, Object what, int flags) {
            int start = length();
            append(text);
            setSpan(what, start, length(), flags);
            return this;
    }

    /**
     * Appends the character sequence {@code text} and spans {@code what} over the appended part.
     * See {@link Spanned} for an explanation of what the flags mean.
     * @param text the character sequence to append.
     * @param flags see {@link Spanned}.
     * @param whats the objects to be spanned over the appended text.
     * @return this {@code SpannableStringBuilderEx}.
     */
    public SpannableStringBuilderEx append(CharSequence text,  int flags , Object... whats) {
        int start = length();
        append(text);

        for(Object object : whats){
            setSpan(object, start, length(), flags);
        }
        return this;
    }


    /**
     * Append ImageSpan easily .
     * @param drawable  drawable instance to insert
     * @param imageSpanFlags one of {@link DynamicDrawableSpan#ALIGN_BOTTOM} or
     * {@link DynamicDrawableSpan#ALIGN_BASELINE}.
     * @param spanFlags determine how the span will behave. see {@link Spanned}.
     * @return SpannableStringBuilderEx instance
     */
    public SpannableStringBuilderEx appendImageSpan(Drawable drawable , int imageSpanFlags , int spanFlags ){
        int start = length();
        append("ImageSpan");
        setSpan(new ImageSpan(drawable , imageSpanFlags), start, length(), spanFlags);
        return this;
    }

    @Override
    public SpannableStringBuilderEx replace(int start, int end, CharSequence tb) {
        super.replace(start, end, tb);
        return this;
    }

    @Override
    public SpannableStringBuilderEx replace(int start, int end, CharSequence tb, int tbstart, int tbend) {
        super.replace(start, end, tb, tbstart, tbend);
        return this;
    }

    /**
     * Replace first matched string with ImageSpan.
     * @param str  replaced string
     * @param start replace start index
     * @param drawable drawable replaces the matched string content .
     * @param imgFlag image span flag . one of {@link DynamicDrawableSpan#ALIGN_BOTTOM} or
     * {@link DynamicDrawableSpan#ALIGN_BASELINE}.
     * @param spanFlag determine how the span will behave. see {@link Spanned}.
     */
    public SpannableStringBuilderEx replace(@NonNull String str , int start , @NonNull Drawable drawable ,int imgFlag , int spanFlag){
        int index = start + toString().substring(start).indexOf(str);
        setSpan(new ImageSpan(drawable,imgFlag) , index ,index + str.length() , spanFlag);
        return this;
    }

    /**
     * Replace all matched string with ImageSpan.
     * @param str  replaced string
     * @param drawable drawable replaces the matched string content .
     * @param imgFlag image span flag . one of {@link DynamicDrawableSpan#ALIGN_BOTTOM} or
     * {@link DynamicDrawableSpan#ALIGN_BASELINE}.
     * @param spanFlag determine how the span will behave. see {@link Spanned}.
     */
    public SpannableStringBuilderEx replaceAll(@NonNull String str , @NonNull Drawable drawable ,int imgFlag , int spanFlag) {
        String content = toString();
        int index = 0;
        while((index =content.indexOf(str,index)) != -1){
            setSpan(new ImageSpan(drawable,imgFlag) , index ,index + str.length() , spanFlag);
            index += str.length();
        }
        return this;
    }

    @Override
    public SpannableStringBuilderEx insert(int where, CharSequence tb) {
        super.insert(where, tb);
        return this;
    }

    @Override
    public SpannableStringBuilderEx insert(int where, CharSequence tb, int start, int end) {
        super.insert(where, tb, start, end);
        return this;
    }

    @Override
    public SpannableStringBuilderEx delete(int start, int end) {
        super.delete(start, end);
        return this;
    }

    /**
     *  Cat off  the SpannableStringBuilderEx's content , then build new one .
     * @param start cat off start index
     * @param end cat off end index
     * @return a new SpannableStringBuilderEx instance .
     */
    public SpannableStringBuilderEx rebuild(int start , int end){
        return new SpannableStringBuilderEx(this, start, end);
    }


    public static SpannableStringBuilderEx valueOf(CharSequence source) {
        if (source instanceof SpannableStringBuilderEx) {
            return (SpannableStringBuilderEx) source;
        } else {
            return new SpannableStringBuilderEx(source);
        }
    }


    /**
     * Returns the depth of TextWatcher callbacks. Returns 0 when the object is not handling
     * TextWatchers. A return value greater than 1 implies that a TextWatcher caused a change that
     * recursively triggered a TextWatcher.
     * @return -1 if Android sdk version is too low .
     */
    @Override
    public int getTextWatcherDepth() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return super.getTextWatcherDepth();
        else {
            Log.e(TAG , "Android sdk version number must be greater than or equal to 23. ");
            return -1;
        }
    }
}
