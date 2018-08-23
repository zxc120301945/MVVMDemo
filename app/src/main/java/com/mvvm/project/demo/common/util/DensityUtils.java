package com.mvvm.project.demo.common.util;

import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.mvvm.project.demo.DemoApplication;

/**
 * 常用单位转换的辅助类
 */
public class DensityUtils {

    private static final DisplayMetrics RESOURCESDisplayMetrics =
            DemoApplication.Companion.getApp().getResources().getDisplayMetrics();

    private DensityUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * dp转px
     *
     * @param dpVal
     * @return
     */
    public static float dp2px(int dpVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, RESOURCESDisplayMetrics);
    }
    /**
     * dp转px
     *
     * @param dpVal
     * @return
     */
    public static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, RESOURCESDisplayMetrics);
    }
    /**
     * sp转px
     *
     * @param spVal
     * @return
     */
    public static int sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, RESOURCESDisplayMetrics);
    }

    /**
     * px转dp
     *
     * @param pxVal
     * @return
     */
    public static int px2dp(float pxVal) {
        final float scale = RESOURCESDisplayMetrics.density;
        return (int) (pxVal/scale + 0.5F);
    }

    /**
     * px转sp
     *
     * @param pxVal
     * @return
     */
    public static float px2sp(float pxVal) {
        return (pxVal / RESOURCESDisplayMetrics.scaledDensity);
    }

}
