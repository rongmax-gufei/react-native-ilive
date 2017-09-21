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

    public static final int AVIMCMD_MULTI = 0x800;             // 多人互动消息类型

    public static final int AVIMCMD_MUlTI_HOST_INVITE = AVIMCMD_MULTI + 1;         // 邀请互动,
    public static final int AVIMCMD_MULTI_CANCEL_INTERACT = AVIMCMD_MUlTI_HOST_INVITE + 1;       // 断开互动，
    public static final int AVIMCMD_MUlTI_JOIN = AVIMCMD_MULTI_CANCEL_INTERACT + 1;       // 同意互动，
    public static final int AVIMCMD_MUlTI_REFUSE = AVIMCMD_MUlTI_JOIN + 1;      // 拒绝互动，

    public static final int AVIMCMD_MULTI_HOST_ENABLEINTERACTMIC = AVIMCMD_MUlTI_REFUSE + 1;  // 主播打开互动者Mic，
    public static final int AVIMCMD_MULTI_HOST_DISABLEINTERACTMIC = AVIMCMD_MULTI_HOST_ENABLEINTERACTMIC + 1;// 主播关闭互动者Mic，
    public static final int AVIMCMD_MULTI_HOST_ENABLEINTERACTCAMERA = AVIMCMD_MULTI_HOST_DISABLEINTERACTMIC + 1; // 主播打开互动者Camera，
    public static final int AVIMCMD_MULTI_HOST_DISABLEINTERACTCAMERA = AVIMCMD_MULTI_HOST_ENABLEINTERACTCAMERA + 1; // 主播打开互动者Camera
    public static final int AVIMCMD_MULTI_HOST_CANCELINVITE = AVIMCMD_MULTI_HOST_DISABLEINTERACTCAMERA + 1; //主播让某个互动者下麦
    public static final int AVIMCMD_MULTI_HOST_CONTROLL_CAMERA = AVIMCMD_MULTI_HOST_CANCELINVITE + 1; //主播控制某个上麦成员摄像头
    public static final int AVIMCMD_MULTI_HOST_CONTROLL_MIC = AVIMCMD_MULTI_HOST_CONTROLL_CAMERA + 1; //主播控制某个上麦成员MIC
    public static final int AVIMCMD_MULTI_HOST_SWITCH_CAMERA = AVIMCMD_MULTI_HOST_CONTROLL_MIC+1; ////主播切换某个上麦成员MIC

    public static final int AVIMCMD_TEXT = -1;         // 普通的聊天消息

    public static final int AVIMCMD_NONE = AVIMCMD_TEXT + 1;               // 无事件

    // 以下事件为TCAdapter内部处理的通用事件
    public static final int AVIMCMD_ENTERLIVE = AVIMCMD_NONE + 1;          // 用户加入直播,
    public static final int AVIMCMD_EXITLIVE = AVIMCMD_ENTERLIVE + 1;         // 用户退出直播,
    public static final int AVIMCMD_PRAISE = AVIMCMD_EXITLIVE + 1;           // 点赞消息,
    public static final int AVIMCMD_HOST_LEAVE = AVIMCMD_PRAISE + 1;         // 主播离开,
    public static final int AVIMCMD_HOST_BACK = AVIMCMD_HOST_LEAVE + 1;      // 主播回来,

    public static final int HOST = 1;
    public static final int MEMBER = 0;

//    public static final String HOST_ROLE = "LiveMaster";
//    public static final String VIDEO_MEMBER_ROLE = "LiveGuest";
//    public static final String NORMAL_MEMBER_ROLE = "Guest";

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
