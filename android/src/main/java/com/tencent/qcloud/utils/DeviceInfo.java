package com.tencent.qcloud.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * DeviceInfo
 *
 * @author learnta
 * @version 1.0
 * @createDate 2017/12/28
 * @lastUpdate 2017/12/28
 */
public class DeviceInfo {

    public static int screenWidth;
    public static int screenHeight;
    public static int screenDensity;

    /**
     * 获取屏幕相关数据
     */
    public static void getScreenBaseInfo(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        screenDensity = metrics.densityDpi;
    }
}
