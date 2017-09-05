package com.tencent.qcloud.utils;

import com.tencent.av.sdk.AVRoomMulti;

/**
 * 静态函数
 */
public class Constants {

    public static final String KEY_APPID = "appid";
    public static final String KEY_ACCOUNT_TYPE = "accountType";
    public static final String KEY_USER_ROLE = "userRole";
    public static final String KEY_HOSTID = "hostId";
    public static final String KEY_ROOM_NUMBER = "roomNum";

    public static int SDK_APPID = 1400027849;//1400028369;

    public static int ACCOUNT_TYPE = 11656;//12747;

    public static final int HOST = 1;
    public static final int MEMBER = 0;

    // 清晰度
    public static final String HD_ROLE = "HD";
    public static final String SD_ROLE = "SD";
    public static final String LD_ROLE = "LD";

    public static final String HD_GUEST_ROLE = "HDGuest";
    public static final String SD_GUEST_ROLE = "SDGuest";
    public static final String LD_GUEST_ROLE = "LDGuest";

    public static final String SD_GUEST = "Guest";
    public static final String LD_GUEST = "Guest2";

    public static final long HOST_AUTH = AVRoomMulti.AUTH_BITS_DEFAULT;//权限位；TODO：默认值是拥有所有权限。
    public static final long VIDEO_MEMBER_AUTH = AVRoomMulti.AUTH_BITS_DEFAULT;//权限位；TODO：默认值是拥有所有权限。
    public static final long NORMAL_MEMBER_AUTH = AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO;
}
