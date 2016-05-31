package com.liessu.extender;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

/**
 * Created by Administrator on 2016/3/24.
 */
public  class StringUtil {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static CharSequence getExample(){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String[] names = {"林语堂","童颜","白起","商鞅","嬴渠梁","张玉琪","吴伊丽","齐博亮","段友奇","冯柏德"};

        for(String name : names){
            builder.append(name+"、", new ForegroundColorSpan(Color.BLUE),0);
        }

        return builder.subSequence(0,builder.length());
    }
}
