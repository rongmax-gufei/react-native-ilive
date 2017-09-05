package com.tencent.qcloud.nativemodule;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.facebook.react.bridge.ReadableMap;
import com.tencent.av.opengl.ui.GLView;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.qcloud.app.InitBusinessHelper;
import com.tencent.qcloud.interfacev1.IRtcEngineEventHandler;
import com.tencent.qcloud.utils.Constants;
import com.tencent.qcloud.utils.SxbLog;
import com.tencent.qcloud.utils.SxbLogImpl;

import static com.tencent.qcloud.utils.Constants.HD_ROLE;
import static com.tencent.qcloud.utils.Constants.HOST;
import static com.tencent.qcloud.utils.Constants.HOST_AUTH;
import static com.tencent.qcloud.utils.Constants.KEY_ACCOUNT_TYPE;
import static com.tencent.qcloud.utils.Constants.KEY_APPID;
import static com.tencent.qcloud.utils.Constants.KEY_HOSTID;
import static com.tencent.qcloud.utils.Constants.KEY_ROOM_NUMBER;
import static com.tencent.qcloud.utils.Constants.KEY_USER_ROLE;
import static com.tencent.qcloud.utils.Constants.MEMBER;
import static com.tencent.qcloud.utils.Constants.NORMAL_MEMBER_AUTH;
import static com.tencent.qcloud.utils.Constants.SD_GUEST;

public class ILiveManager implements ILiveRoomOption.onRoomDisconnectListener {

    private static final String TAG = "ILiveManager";
    private static final String SUCCESS_CODE = "1000";

    private static ILiveManager sILiveManager = new ILiveManager();

    private Context context;
    private IRtcEngineEventHandler rtcEventHandler;

    private AVRootView rootView;
    private String hostId;
    private int roomNumber;
    private int userRole;
    private boolean bCameraOn = false;
    private boolean bMicOn = false;

    public static ILiveManager getInstance() {
        return sILiveManager;
    }

    /**
     * 初始化
     */
    public void init(Context context, IRtcEngineEventHandler rtcEventHandler, ReadableMap options) {
        this.context = context;
        this.rtcEventHandler = rtcEventHandler;
        if (null == ILiveSDK.getInstance().getAVContext()) {
            SxbLogImpl.init(context);
            InitBusinessHelper.initApp(context);
        }
        if (options.hasKey(KEY_APPID)) {
            Constants.SDK_APPID = Integer.valueOf(options.getString(KEY_APPID));
        }
        if (options.hasKey(KEY_ACCOUNT_TYPE)) {
            Constants.ACCOUNT_TYPE = Integer.valueOf(options.getString(KEY_ACCOUNT_TYPE));
        }
        if (options.hasKey(KEY_USER_ROLE)) {
            userRole = Integer.valueOf(options.getString(KEY_USER_ROLE));
        }
        if (options.hasKey(KEY_HOSTID)) {
            hostId = options.getString(KEY_HOSTID);
        }
        if (options.hasKey(KEY_ROOM_NUMBER)) {
            roomNumber = Integer.valueOf(options.getString(KEY_ROOM_NUMBER));
        }
    }

    /**
     * 获取播放器渲染层
     *
     * @return
     */
    public SurfaceView getVideoView() {
        //TODO 获取渲染层
        if (null == rootView) {
            rootView = new AVRootView(context);
            //TODO 设置渲染层
            ILVLiveManager.getInstance().setAvVideoView(rootView);
            rootView.setGravity(AVRootView.LAYOUT_GRAVITY_RIGHT);
            rootView.setSubCreatedListener(new AVRootView.onSubViewCreatedListener() {
                @Override
                public void onSubViewCreated() {
                    for (int i = 1; i < ILiveConstants.MAX_AV_VIDEO_NUM; i++) {
                        final int index = i;
                        AVVideoView avVideoView = rootView.getViewByIndex(index);
                        avVideoView.setRotate(false);
                        avVideoView.setGestureListener(new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapConfirmed(MotionEvent e) {
                                rootView.swapVideoView(0, index);
                                hostId = rootView.getViewByIndex(0).getIdentifier();
                                return super.onSingleTapConfirmed(e);
                            }
                        });
                    }

                    rootView.getViewByIndex(0).setRotate(false);
                    rootView.getViewByIndex(0).setGestureListener(new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                            return false;
                        }
                    });
                }
            });
        }
        return rootView;
    }

    /**
     * 登录腾讯TLS系统
     *
     * @param id
     * @param sig
     */
    public void iLiveLogin(String id, String sig) {
        //登录
        ILiveLoginManager.getInstance().iLiveLogin(id, sig, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                rtcEventHandler.onLoginTLS(SUCCESS_CODE, "登录腾讯TLS系统成功");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                rtcEventHandler.onLoginTLS(String.valueOf(errCode), errMsg);
            }
        });
    }

    /**
     * 退出腾讯TLS系统
     */
    public void iLiveLogout() {
        ILiveLoginManager.getInstance().iLiveLogout(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                rtcEventHandler.onLogoutTLS(SUCCESS_CODE, "退出腾讯TLS系统成功");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
            }
        });
    }

    /**
     * 进入房间
     */
    public void startEnterRoom() {
        switch (userRole) {
            case HOST:
                createRoom();
                break;
            case MEMBER:
                joinRoom();
                break;
        }
    }

    /**
     * 创建房间
     */
    private void createRoom() {
        ILVLiveRoomOption hostOption = new ILVLiveRoomOption(hostId)
                .roomDisconnectListener(this)
                .videoMode(ILiveConstants.VIDEOMODE_BSUPPORT)
                .controlRole(HD_ROLE)
                .autoFocus(true)
                .authBits(HOST_AUTH)
                .cameraId(ILiveConstants.FRONT_CAMERA)
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);
        int ret = ILVLiveManager.getInstance().createRoom(roomNumber, hostOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                bCameraOn = true;
                bMicOn = true;
                rootView.getViewByIndex(0).setVisibility(GLView.VISIBLE);
                rtcEventHandler.onCreateRoom(SUCCESS_CODE, "创建房间成功");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                rtcEventHandler.onCreateRoom(String.valueOf(errCode), errMsg);
            }
        });
        checkEnterReturn(ret);
    }

    /**
     * 加入房间
     */
    private void joinRoom() {
        ILVLiveRoomOption memberOption = new ILVLiveRoomOption(hostId)
                .autoCamera(false)
                .roomDisconnectListener(this)
                .videoMode(ILiveConstants.VIDEOMODE_BSUPPORT)
                .controlRole(SD_GUEST)
                .authBits(NORMAL_MEMBER_AUTH)
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO)
                .autoMic(false);
        int ret = ILVLiveManager.getInstance().joinRoom(roomNumber, memberOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                rootView.getViewByIndex(0).setVisibility(GLView.VISIBLE);
                rtcEventHandler.onJoinRoom(SUCCESS_CODE, "进入房间成功");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                rtcEventHandler.onJoinRoom(String.valueOf(errCode), errMsg);
            }
        });
        checkEnterReturn(ret);
    }

    private void checkEnterReturn(int iRet) {
        if (ILiveConstants.NO_ERR != iRet) {
            SxbLog.d(TAG, "checkEnterReturn," + iRet);
            if (ILiveConstants.ERR_ALREADY_IN_ROOM == iRet) {
                // 上次房间未退出处理做退出处理
                ILiveRoomManager.getInstance().quitRoom(new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        SxbLog.d(TAG, "上次房间未退出处理做退出处理," + data.toString());
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        SxbLog.e(TAG, "上次房间未退出处理做退出处理,errCode=" + errCode + "errMsg=" + errMsg);
                    }
                });
            }
        }
    }

    /**
     * 退出房间
     */
    public void startExitRoom() {
        ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(null);
        ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                rtcEventHandler.onExitRoom(SUCCESS_CODE, "退出房间成功");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                rtcEventHandler.onExitRoom(String.valueOf(errCode), errMsg);
            }
        });
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        ILiveRoomManager.getInstance().switchCamera(1 - ILiveRoomManager.getInstance().getCurCameraId());
        rtcEventHandler.onSwitchCamera(SUCCESS_CODE, "切换成功");
    }

    /**
     * 打开/关闭摄像头
     */
    public void toggleCamera() {
        bCameraOn = !bCameraOn;
        ILiveRoomManager.getInstance().enableCamera(ILiveRoomManager.getInstance().getCurCameraId(), bCameraOn);
        rtcEventHandler.onToggleCamera(bCameraOn);
    }

    /**
     * 打开/关闭声麦
     */
    public void toggleMic() {
        bMicOn = !bMicOn;
        ILiveRoomManager.getInstance().enableMic(bMicOn);
        rtcEventHandler.onToggleMic(bMicOn);
    }

    @Override
    public void onRoomDisconnect(int errCode, String errMsg) {
        rtcEventHandler.onRoomDisconnect(String.valueOf(errCode), errMsg);
    }
}
