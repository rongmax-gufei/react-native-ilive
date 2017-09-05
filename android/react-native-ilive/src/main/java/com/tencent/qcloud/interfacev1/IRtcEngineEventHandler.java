package com.tencent.qcloud.interfacev1;

public interface IRtcEngineEventHandler {
    void onLoginTLS(String code, String msg);
    void onLogoutTLS(String code, String msg);
    void onCreateRoom(String code, String msg);
    void onJoinRoom(String code, String msg);
    void onExitRoom(String code, String msg);
    void onRoomDisconnect(String code, String msg);
    void onSwitchCamera(String code, String msg);
    void onToggleCamera(boolean bCameraOn);
    void onToggleMic(boolean bMicOn);
}
