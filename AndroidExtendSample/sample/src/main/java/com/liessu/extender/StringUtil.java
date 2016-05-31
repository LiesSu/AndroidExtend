package com.liessu.extender;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.view.View;
import android.widget.Toast;

import com.liessu.extendlib.span.ClickableSpanEx;
import com.liessu.extendlib.span.SpannableStringBuilderEx;


/**
 * Created by Administrator on 2016/3/24.
 */
public  class StringUtil {

    public static CharSequence getExample(){
        SpannableStringBuilderEx builder = new SpannableStringBuilderEx("[][[]][[[]]]");
        final String[] names = {"林语堂","童颜","白起","商鞅","嬴渠梁","张玉琪","林伊丽","齐博亮","段友奇","冯柏德"};

        Drawable drawable = DemoApplication.getContext().getResources().getDrawable(R.drawable.ic_star);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        for(final String name : names){
            builder.append(name + "、", Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                    new ClickableSpanEx(Color.BLUE,Color.GRAY) {
                        @Override
                        public void onClick(View widget) {
                            Toast.makeText(DemoApplication.getContext(),name,Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }

        builder.replaceAll("[]",drawable,DynamicDrawableSpan.ALIGN_BOTTOM,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder.rebuild(0,builder.length()-1).append(" 觉得很赞");
    }
}
