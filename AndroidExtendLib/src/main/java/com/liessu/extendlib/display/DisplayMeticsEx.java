package com.liessu.extendlib.display;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.ArrayMap;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class DisplayMeticsEx extends DisplayMetrics{
    private static Map<String , Integer> dpiRange = new HashMap<>();
    private Context context;


    static {
        dpiRange.put("ldpi",DENSITY_LOW);
        dpiRange.put("mdpi",DENSITY_MEDIUM);
        dpiRange.put("hdpi",DENSITY_HIGH);
        dpiRange.put("xdpi",DENSITY_XHIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            dpiRange.put("xxdpi",DENSITY_XXHIGH);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            dpiRange.put("xxxdpi",DENSITY_XXXHIGH);
        }
    }


    public DisplayMeticsEx(Activity activity){
        super();
        context = activity;
        activity.getWindowManager().getDefaultDisplay().getMetrics(this);
    }

    /**获取屏幕分辨率**/
    public String getResolution(){
        return heightPixels + "x" + widthPixels;
    }

    public String getNonCompatResolution(){
        return getNonCompat("noncompatHeightPixels") + "x" + getNonCompat("noncompatWidthPixels");
    }

    @Override
    public String toString() {
        String stringBuilder = ("屏幕分辨率：" + getResolution()) +
                "\n逻辑屏幕密度/dp缩放比例：" + density +
                "\nsp缩放比例：" + scaledDensity +
                "\n每英寸像素数(dpi)：" + densityDpi +
                "\n实际水平dpi：" + xdpi + "，实际垂直dpi" + ydpi +
                "\n实际对角dpi：" + Math.sqrt(xdpi * xdpi + ydpi * ydpi) +
                "\n换算：1dp = " + dip2px(context, 1) +"px";
        ;
        return stringBuilder;
    }

    public String getNonCompatString(){
        float x = getNonCompatFloat("noncompatXdpi") , y = getNonCompatFloat("noncompatYdpi");
        String stringBuilder = ("屏幕分辨率：" + getNonCompatResolution()) +
                "\n逻辑屏幕密度/dp缩放比例：" + getNonCompatFloat("noncompatDensity") +
                "\nsp缩放比例：" + getNonCompatFloat("noncompatScaledDensity") +
                "\n每英寸像素数(dpi)：" + getNonCompat("noncompatDensityDpi")+
                "\n实际水平dpi：" + x + "，实际垂直dpi" + y +
                "\n实际对角dpi：" + Math.sqrt(x * x + y * y) +
                "\n换算：1dp = " + dip2px(context, 1) +"px";

        return stringBuilder;
    }

    public static float dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale;
    }

    public static float px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale;
    }

    public static String getDipRange(int dpi){
        return "";
    }

    public int getNonCompat(String fieleName){
        try {
            Field field = DisplayMetrics.class.getDeclaredField(fieleName);
            field.setAccessible(true);
            return  field.getInt(this);
        } catch (Exception e) {
            e.printStackTrace();
            return  -1;
        }
    }

    public float getNonCompatFloat(String filedName){
        try {
            Field field = DisplayMetrics.class.getDeclaredField(filedName);
            field.setAccessible(true);
            return  field.getFloat(this);
        } catch (Exception e) {
            e.printStackTrace();
            return  -1.0F;
        }
    }
}
