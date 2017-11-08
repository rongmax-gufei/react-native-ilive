package com.tencent.qcloud.interfacev1;

public interface IRtcEngineEventHandler {
    void onLoginTLS(String code, String msg);
    void onLogoutTLS(String code, String msg);
    void onCreateRoom(String code, String msg);
    void onJoinRoom(String code, String msg);
    void onLeaveRoom(String code, String msg);
    void onHostLeave(String code, String msg);
    void onHostBack(String code, String msg);
    void onForceQuitRoom(String code, String msg);
    void onRoomDisconnect(String code, String msg);
    void onStartVideoRecord(String code, String msg);
    void onStopVideoRecord(String code, String msg);
    void onStartScreenRecord(String code, String msg);
    void onStopScreenRecord(String code, String msg);
    void onSwitchCamera(String code, String msg);
    void onToggleCamera(String code, String msg);
    void onToggleMic(String code, String msg);
    void onUpVideo(String code, String msg);
    void onDownVideo(String code, String msg);
    void onChangeRole(String code, String msg);
}
