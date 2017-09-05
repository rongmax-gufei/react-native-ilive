package com.tencent.qcloud.app;

import android.content.Context;

import com.tencent.TIMLogLevel;
import com.tencent.TIMManager;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.qcloud.utils.Constants;
import com.tencent.qcloud.utils.CrashHandler;
import com.tencent.qcloud.utils.SxbLog;

/**
 * 初始化包括imsdk等
 */
public class InitBusinessHelper {

    /**
     * 初始化App
     */
    public static void initApp(final Context context) {
        //初始化avsdk imsdk
        TIMManager.getInstance().disableBeaconReport();
        switch(SxbLog.SxbLogLevel.INFO){
        case OFF:
            TIMManager.getInstance().setLogLevel(TIMLogLevel.OFF);
            break;
        case WARN:
            TIMManager.getInstance().setLogLevel(TIMLogLevel.WARN);
            break;
        case DEBUG:
            TIMManager.getInstance().setLogLevel(TIMLogLevel.DEBUG);
            break;
        case INFO:
            TIMManager.getInstance().setLogLevel(TIMLogLevel.INFO);
            break;
        default:
            break;
        }

        // 初始化ILiveSDK
        ILiveSDK.getInstance().initSdk(context, Constants.SDK_APPID, Constants.ACCOUNT_TYPE);
        // 初始化直播模块
        ILVLiveConfig liveConfig = new ILVLiveConfig();
        ILVLiveManager.getInstance().init(liveConfig);

        //初始化CrashReport系统
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(context);
    }
}
