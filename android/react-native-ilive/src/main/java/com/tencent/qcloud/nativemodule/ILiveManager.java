package com.tencent.qcloud.nativemodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.Toast;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMMessage;
import com.tencent.av.TIMAvManager;
import com.tencent.av.opengl.ui.GLView;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.av.sdk.AVView;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRecordOption;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;
import com.tencent.liteav.basic.enums.TXEFrameFormat;
import com.tencent.liteav.beauty.TXCVideoPreprocessor;
import com.tencent.livesdk.ILVChangeRoleRes;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConstants;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;
import com.tencent.qcloud.R;
import com.tencent.qcloud.interfacev1.IRtcEngineEventHandler;
import com.tencent.qcloud.screenrecorder.ScreenRecordService;
import com.tencent.qcloud.utils.Constants;
import com.tencent.qcloud.utils.LogConstants;
import com.tencent.qcloud.utils.MessageEvent;
import com.tencent.qcloud.utils.SxbLog;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;
import static com.tencent.qcloud.utils.Constants.HD_GUEST_ROLE;
import static com.tencent.qcloud.utils.Constants.HOST_AUTH;
import static com.tencent.qcloud.utils.Constants.KEY_ACCOUNT_TYPE;
import static com.tencent.qcloud.utils.Constants.KEY_APPID;
import static com.tencent.qcloud.utils.Constants.NORMAL_MEMBER_AUTH;
import static com.tencent.qcloud.utils.Constants.SD_GUEST_ROLE;

public class ILiveManager implements ILiveRoomOption.onRoomDisconnectListener, Observer, ActivityEventListener {

    private static final String TAG = "ILiveManager";
    private static final String SUCCESS_CODE = "1000";
    private static final String FAIL_CODE = "1001";
    private static final int TIMEOUT_INVITE = 1;
    private static final int REQUEST_CODE = 1000;

    private MediaProjectionManager mediaProjectionManager;

    private static ILiveManager sILiveManager = new ILiveManager();

    private Activity activity;
    private Context context;
    private IRtcEngineEventHandler rtcEventHandler;

    private AVRootView rootView;
    private TXCVideoPreprocessor mTxcFilter;

    private String loginId;
    private String hostId;
    private int roomId;
    private int userRole;
    private String quality;
    private int inviteViewCount = 0;

    private final String[] roles = new String[]{"高清(960*540,25fps)", "标清(640*368,20fps)", "流畅(640*368,15fps)"};
    private final String[] values = new String[]{Constants.HD_ROLE, Constants.SD_ROLE, Constants.LD_ROLE};
    private final String[] guestValues = new String[]{HD_GUEST_ROLE, SD_GUEST_ROLE, Constants.LD_GUEST_ROLE};

    private int screenWidth;
    private int screenHeight;
    private int screenDensity;

    public static ILiveManager getInstance() {
        return sILiveManager;
    }

    /**
     * 初始化
     */
    public void init(ReactApplicationContext context, Activity activity, IRtcEngineEventHandler rtcEventHandler, ReadableMap options) {
        this.context = context;
        this.activity = activity;
        this.rtcEventHandler = rtcEventHandler;
        context.addActivityEventListener(this);
        mediaProjectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (options.hasKey(KEY_APPID)) {
            Constants.SDK_APPID = Integer.valueOf(options.getString(KEY_APPID));
        }
        if (options.hasKey(KEY_ACCOUNT_TYPE)) {
            Constants.ACCOUNT_TYPE = Integer.valueOf(options.getString(KEY_ACCOUNT_TYPE));
        }
    }

    /**
     * 消息监听
     */
    public void addObserver() {
        MessageEvent.getInstance().addObserver(this);
        boolean bGLContext = false;     // 在 AVSDk 场景下，设置当前为无 OpenGL 环境
        mTxcFilter = new TXCVideoPreprocessor(activity, bGLContext);
        // 根据自己的需要，在适当的地方设置参数
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mTxcFilter.setBeautyStyle(0);           // 设置美颜风格，0: 光滑 1: 自然 2: 朦胧
            mTxcFilter.setBeautyLevel(5);           // 设置美颜级别,范围 0～10
            mTxcFilter.setWhitenessLevel(8);        // 设置美白级别,范围 0～10
            mTxcFilter.setRuddyLevel(8);            // 设置红润级别,范围 0～10
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
        this.loginId = id;
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
     * 创建房间
     */
    public void createRoom(String hostId, int roomId, String quality) {
        this.hostId = hostId;
        this.roomId = roomId;
        this.userRole = 1;
        this.quality = quality;
        ILVLiveRoomOption hostOption = new ILVLiveRoomOption(hostId)
                .roomDisconnectListener(this)
                .videoMode(ILiveConstants.VIDEOMODE_BSUPPORT)
                .controlRole(quality)
                .autoFocus(true)
                .authBits(HOST_AUTH)
                .cameraId(ILiveConstants.FRONT_CAMERA)
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);
        int ret = ILVLiveManager.getInstance().createRoom(roomId, hostOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                rootView.getViewByIndex(0).setVisibility(GLView.VISIBLE);
                boolean bRet = ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(new AVVideoCtrl.LocalVideoPreProcessCallback() {
                    @Override
                    public void onFrameReceive(AVVideoCtrl.VideoFrame var1) {
                        // 回调的数据，传递给 ilivefilter processFrame 接口处理;
                        // avsdk回调函数，默认为 I420 格式
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            mTxcFilter.processFrame(var1.data, var1.width, var1.height, var1.rotate, TXEFrameFormat.I420, TXEFrameFormat.I420);
                        }
                    }
                });
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
    public void joinRoom(final String hostId, int roomId, int userRole, String quality) {
        this.hostId = hostId;
        this.roomId = roomId;
        this.userRole = userRole;
        this.quality = quality;
        ILVLiveRoomOption memberOption = new ILVLiveRoomOption(hostId)
                .autoCamera(false)
                .roomDisconnectListener(this)
                .videoMode(ILiveConstants.VIDEOMODE_BSUPPORT)
                .controlRole(quality)
                .authBits(NORMAL_MEMBER_AUTH)
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO)
                .autoMic(false);
        int ret = ILVLiveManager.getInstance().joinRoom(roomId, memberOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                rootView.getViewByIndex(0).setVisibility(GLView.VISIBLE);
                if (hostId.equals(loginId)) {
                    // 主播方式加入房间
                    //注册一个音频回调为变声用
                    ILiveSDK.getInstance().getAvAudioCtrl().registAudioDataCallbackWithByteBuffer(AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_VOICEDISPOSE, new AVAudioCtrl.RegistAudioDataCompleteCallbackWithByteBuffer() {
                        @Override
                        public int onComplete(AVAudioCtrl.AudioFrameWithByteBuffer audioFrameWithByteBuffer, int i) {
                            return 0;
                        }
                    });
                } else {
                    sendGroupCmd(Constants.AVIMCMD_ENTERLIVE, "");
                }
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
    public void leaveRoom() {
        // 主播退出直播间
        ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(null);
        ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                rtcEventHandler.onLeaveRoom(SUCCESS_CODE, "退出房间成功");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                rtcEventHandler.onLeaveRoom(String.valueOf(errCode), errMsg);
            }
        });
        if (!loginId.equals(hostId)) {
            // 观众退出直播间
            cancelMemberView(hostId);
        }
        // 取消 AVSDK 相机数据回调（参数传null）
        boolean bRet = ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(null);
        // 退出房间后，一定要销毁filter 资源；否则下次进入房间，setFilter将不生效或其他异常
        mTxcFilter.release();
    }

    /**
     * 主播暂时离开房间
     */
    public void hostLeave(String message) {
        rtcEventHandler.onHostLeave(SUCCESS_CODE, message);
    }

    /**
     * 强制退出房间
     * 1、主播已离开房间，是否退出?
     * 2、管理员已将房间解散或将您踢出房间!
     *
     * @param message
     */
    public void forceQuitRoom(String message) {
        rtcEventHandler.onForceQuitRoom(SUCCESS_CODE, message);
    }

    /**
     * 上麦
     *
     * @param uid
     */
    public void upVideo(String uid) {
        int index = rootView.findValidViewIndex();
        if (index == -1) {
            showToast("the invitation's upper limit is 3");
            return;
        }
        int requetCount = index + inviteViewCount;
        if (requetCount > 3) {
            showToast("the invitation's upper limit is 3");
            return;
        }
        sendC2CCmd(Constants.AVIMCMD_MUlTI_HOST_INVITE, "", uid);
        inviteViewCount++;
        //30s超时取消
        Message msg = new Message();
        msg.what = TIMEOUT_INVITE;
        msg.obj = uid;
        mHandler.sendMessageDelayed(msg, 30 * 1000);
    }

    /**
     * 下麦
     *
     * @param uid
     */
    public void downVideo(String uid) {
        cancelMemberView(uid);
    }

    /**
     * 开始录视频
     *
     * @param fileName
     * @param recordType
     */
    public void startVideoRecord(String fileName, int recordType) {
        ILiveRecordOption option = new ILiveRecordOption();
        option.fileName("learnta_" + ILiveLoginManager.getInstance().getMyUserId() + "_" + fileName);
//        option.classId(123);
        option.recordType(recordType == 0 ? TIMAvManager.RecordType.VIDEO : TIMAvManager.RecordType.AUDIO);
        ILiveRoomManager.getInstance().startRecordVideo(option, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                SxbLog.d(TAG, "已经开始录制");
                rtcEventHandler.onStartVideoRecord(SUCCESS_CODE, "已经开始录制：" + data.toString());
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                String errInfo = "start record error " + errCode + "  " + errMsg;
                SxbLog.e(TAG, errInfo);
                rtcEventHandler.onStartVideoRecord(String.valueOf(errCode), errInfo);
            }
        });
    }

    /**
     * 结束录制视频
     */
    public void stopVideoRecord() {
        ILiveRoomManager.getInstance().stopRecordVideo(new ILiveCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> data) {
                for (String url : data) {
                    SxbLog.d(TAG, "stopRecord->url:" + url);
                }
                rtcEventHandler.onStartVideoRecord(SUCCESS_CODE, "已经结束录制：" + data.toString());
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                String errInfo = "stopRecord->failed:" + module + "|" + errCode + "|" + errMsg;
                SxbLog.e(TAG, errInfo);
                rtcEventHandler.onStartVideoRecord(String.valueOf(errCode), errInfo);
            }
        });
    }

    /**
     * 开始屏幕录制
     *
     * @param currentActivity
     */
    public void startScreenRecord(Activity currentActivity) {
        SxbLog.d(TAG, "startScreenRecord");
        if (currentActivity == null) {
            rtcEventHandler.onStartScreenRecord(FAIL_CODE, "Activity doesn't exist");
            return;
        }
        getScreenBaseInfo();
        /**
         * 获取屏幕录制的权限
         */
        Intent captureIntent = mediaProjectionManager.createScreenCaptureIntent();
        currentActivity.startActivityForResult(captureIntent, REQUEST_CODE);
    }

    /**
     * 结束屏幕录制
     */
    public void stopScreenRecord() {
        SxbLog.d(TAG, "stopScreenRecord");
        Intent service = new Intent(context, ScreenRecordService.class);
        context.stopService(service);
        rtcEventHandler.onStartScreenRecord(SUCCESS_CODE, "已经结束录制屏幕");
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
    public void toggleCamera(boolean bCameraOn) {
        ILiveRoomManager.getInstance().enableCamera(ILiveRoomManager.getInstance().getCurCameraId(), bCameraOn);
        rtcEventHandler.onToggleCamera(SUCCESS_CODE, "打开/关闭摄像头，操作成功");
    }

    /**
     * 打开/关闭声麦
     */
    public void toggleMic(boolean bMicOn) {
        ILiveRoomManager.getInstance().enableMic(bMicOn);
        rtcEventHandler.onToggleMic(SUCCESS_CODE, "打开/关闭声麦，操作成功");
    }

    @Override
    public void onRoomDisconnect(int errCode, String errMsg) {
        rtcEventHandler.onRoomDisconnect(String.valueOf(errCode), errMsg);
    }

    public void onDestory() {
        MessageEvent.getInstance().deleteObserver(this);
        ILVLiveManager.getInstance().quitRoom(null);
        stopScreenRecord();
    }

    /**
     * 主播/观众退出房间后的操作
     *
     * @param id
     */
    public void cancelMemberView(String id) {
        if (loginId.equals(hostId)) {
            // 主播退出房间
            sendGroupCmd(Constants.AVIMCMD_MULTI_CANCEL_INTERACT, id);
            rootView.closeUserView(id, AVView.VIDEO_SRC_TYPE_CAMERA, true);
        } else {
            // 观众退出房间
            //TODO 主动下麦 下麦；
            SxbLog.d(TAG, LogConstants.ACTION_VIEWER_UNSHOW + LogConstants.DIV + loginId + LogConstants.DIV + "start unShow" +
                    LogConstants.DIV + "id " + id);
            downMemberVideo();
        }
    }

    private void upMemberVideo() {
        if (!ILiveRoomManager.getInstance().isEnterRoom()) {
            SxbLog.e(TAG, "upMemberVideo->with not in room");
        }
        ILVLiveManager.getInstance().upToVideoMember(Constants.HD_GUEST_ROLE, true, true, new ILiveCallBack<ILVChangeRoleRes>() {
            @Override
            public void onSuccess(ILVChangeRoleRes data) {
                SxbLog.d(TAG, "upToVideoMember->success");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "upToVideoMember->failed:" + module + "|" + errCode + "|" + errMsg);
            }
        });
    }

    private void downMemberVideo() {
        if (!ILiveRoomManager.getInstance().isEnterRoom()) {
            SxbLog.e(TAG, "downMemberVideo->with not in room");
            rtcEventHandler.onDownVideo(FAIL_CODE, "连麦用户不在房间");
        }
        ILVLiveManager.getInstance().downToNorMember(Constants.HD_GUEST_ROLE, new ILiveCallBack<ILVChangeRoleRes>() {
            @Override
            public void onSuccess(ILVChangeRoleRes data) {
                SxbLog.e(TAG, "downMemberVideo->onSuccess");
                rtcEventHandler.onDownVideo(SUCCESS_CODE, "下麦成功");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "downMemberVideo->failed:" + module + "|" + errCode + "|" + errMsg);
                rtcEventHandler.onDownVideo(String.valueOf(errCode), errMsg);
            }
        });
    }

    /**
     * 发送信令
     */
    public int sendGroupCmd(int cmd, String param) {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setCmd(cmd);
        customCmd.setParam(param);
        customCmd.setType(ILVText.ILVTextType.eGroupMsg);
        return sendCmd(customCmd);
    }

    public int sendC2CCmd(final int cmd, String param, String destId) {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setDestId(destId);
        customCmd.setCmd(cmd);
        customCmd.setParam(param);
        customCmd.setType(ILVText.ILVTextType.eC2CMsg);
        return sendCmd(customCmd);
    }

    private int sendCmd(final ILVCustomCmd cmd) {
        return ILVLiveManager.getInstance().sendCustomCmd(cmd, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                SxbLog.i(TAG, "sendCmd->success:" + cmd.getCmd() + "|" + cmd.getParam());
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.i(TAG, "sendCmd->failed:" + module + "|" + errCode + "|" + errMsg);
            }
        });
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case TIMEOUT_INVITE:
                    String id = "" + msg.obj;
                    rtcEventHandler.onUpVideo(FAIL_CODE, "上麦请求超时");
                    sendGroupCmd(Constants.AVIMCMD_MULTI_HOST_CANCELINVITE, id);
                    inviteViewCount--;
                    if (inviteViewCount < 0)
                        inviteViewCount = 0;
                    break;
            }
            return false;
        }
    });

    @Override
    public void update(Observable observable, Object o) {
        MessageEvent.SxbMsgInfo info = (MessageEvent.SxbMsgInfo) o;
        SxbLog.w(TAG, "update->MessageEvent.SxbMsgInfo:" + info.toString());
        switch (info.msgType) {
            case MessageEvent.MSGTYPE_TEXT:
                // 文字消息接收，忽略，项目自己实现
                break;
            case MessageEvent.MSGTYPE_CMD:
                processCmdMsg(info);
                break;
            case MessageEvent.MSGTYPE_OTHER:
                processOtherMsg(info);
                break;
        }
    }

    // 解析自定义信令
    private void processCmdMsg(MessageEvent.SxbMsgInfo info) {
        if (null == info.data || !(info.data instanceof ILVCustomCmd)) {
            SxbLog.w(TAG, "processCmdMsg->wrong object:" + info.data);
            return;
        }
        ILVCustomCmd cmd = (ILVCustomCmd) info.data;
        if (cmd.getType() == ILVText.ILVTextType.eGroupMsg
                && !String.valueOf(roomId).equals(cmd.getDestId())) {
            SxbLog.d(TAG, "processCmdMsg->ignore message from: " + cmd.getDestId() + "/" + hostId);
            return;
        }

        String name = info.senderId;
        if (null != info.profile && !TextUtils.isEmpty(info.profile.getNickName())) {
            name = info.profile.getNickName();
        }

        handleCustomMsg(cmd.getCmd(), cmd.getParam(), info.senderId, name);
    }

    private void handleCustomMsg(int action, String param, String identifier, String nickname) {
        SxbLog.d(TAG, "handleCustomMsg->action: " + action + " ->param：" + param + " identifier:" + identifier + " nickname：" + nickname);
        switch (action) {
            case Constants.AVIMCMD_MUlTI_HOST_INVITE:
                SxbLog.d(TAG, LogConstants.ACTION_VIEWER_SHOW + LogConstants.DIV + loginId + LogConstants.DIV + "receive invite message" +
                        LogConstants.DIV + "id " + identifier);
                sendC2CCmd(Constants.AVIMCMD_MUlTI_JOIN, "", String.valueOf(hostId));
                upMemberVideo();
//              changeRole(SD_GUEST_ROLE);
                break;
            case Constants.AVIMCMD_MUlTI_JOIN:
                SxbLog.i(TAG, "handleCustomMsg " + identifier);
                break;
            case Constants.AVIMCMD_MUlTI_REFUSE:
                showToast(identifier + " refuse !");
                break;
            case Constants.AVIMCMD_MULTI_CANCEL_INTERACT:
                //主播关闭摄像头命令，关闭Camera和Mic
                if (param.equals(loginId)) {
                    SxbLog.i(TAG, "被动下麦 下麦 下麦");
                    //TODO 被动下麦 下麦 下麦
                    downMemberVideo();
                }
                //其他人关闭小窗口
                ILiveRoomManager.getInstance().getRoomView().closeUserView(param, AVView.VIDEO_SRC_TYPE_CAMERA, true);
                break;
            case Constants.AVIMCMD_MULTI_HOST_CANCELINVITE:
                break;
            case Constants.AVIMCMD_EXITLIVE:
                forceQuitRoom(context.getString(R.string.str_room_discuss));
                break;
            case ILVLiveConstants.ILVLIVE_CMD_LINKROOM_REQ:     // 跨房邀请

                break;
            case ILVLiveConstants.ILVLIVE_CMD_LINKROOM_ACCEPT:  // 接听

                break;
            case ILVLiveConstants.ILVLIVE_CMD_LINKROOM_REFUSE:  // 拒绝

                break;
            case ILVLiveConstants.ILVLIVE_CMD_LINKROOM_LIMIT:   // 达到上限

                break;
            case Constants.AVIMCMD_HOST_BACK:
                String message = identifier + "is back";
                rtcEventHandler.onHostBack(SUCCESS_CODE, message);
            default:
                break;
        }
    }

    private void processOtherMsg(MessageEvent.SxbMsgInfo info) {
        if (null == info.data || !(info.data instanceof TIMMessage)) {
            SxbLog.w(TAG, "processOtherMsg->wrong object:" + info.data);
            return;
        }
        TIMMessage currMsg = (TIMMessage) info.data;

        // 过滤非当前群组消息
        if (currMsg.getConversation() != null && currMsg.getConversation().getPeer() != null) {
            if (currMsg.getConversation().getType() == TIMConversationType.Group
                    && !String.valueOf(roomId).equals(currMsg.getConversation().getPeer())) {
                return;
            }
        }

        for (int j = 0; j < currMsg.getElementCount(); j++) {
            if (currMsg.getElement(j) == null)
                continue;
            TIMElem elem = currMsg.getElement(j);
            TIMElemType type = elem.getType();

            SxbLog.d(TAG, "LiveHelper->otherMsg type:" + type);

            //系统消息
            if (type == TIMElemType.GroupSystem) {
                // 群组解散消息
                if (TIMGroupSystemElemType.TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE == ((TIMGroupSystemElem) elem).getSubtype()) {
                    hostLeave("群组解散");
                }
            } else if (type == TIMElemType.Custom) {
                try {
                    final String strMagic = "__ACTION__";
                    String customText = new String(((TIMCustomElem) elem).getData(), "UTF-8");
                    if (!customText.startsWith(strMagic))   // 检测前缀
                        continue;
                    JSONTokener jsonParser = new JSONTokener(customText.substring(strMagic.length() + 1));
                    JSONObject json = (JSONObject) jsonParser.nextValue();
                    String action = json.optString("action", "");
                    if (action.equals("force_exit_room") || action.equals("force_disband_room")) {
                        JSONObject objData = json.getJSONObject("data");
                        String strRoomNum = objData.optString("room_num", "");
                        SxbLog.d(TAG, "processOtherMsg->action:" + action + ", room_num:" + strRoomNum);
                        if (strRoomNum.equals(String.valueOf(ILiveRoomManager.getInstance().getRoomId()))) {
                            forceQuitRoom(context.getString(R.string.str_tips_force_exit));
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 修改房间角色
     *
     * @param role 角色(请确保在腾讯云SPEAR上已配置该角色)
     */
    public void changeRole(final String role) {
        ILiveRoomManager.getInstance().changeRole(role, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                String message = "change " + role + " success !";
                SxbLog.d(TAG, message);
                rtcEventHandler.onChangeRole(SUCCESS_CODE, message);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                String message = "change " + role + "   failed  : " + errCode + " msg " + errMsg;
                SxbLog.d(TAG, message);
                rtcEventHandler.onChangeRole(SUCCESS_CODE, message);
            }
        });
    }

    private void showToast(final String strMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != context) {
                    Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // 获得权限，启动Service开始录制
                Intent service = new Intent(activity, ScreenRecordService.class);
                service.putExtra("code", resultCode);
                service.putExtra("data", data);
                service.putExtra("audio", true);
                service.putExtra("width", screenWidth);
                service.putExtra("height", screenHeight);
                service.putExtra("density", screenDensity);
                service.putExtra("quality", false);
                activity.startService(service);
                SxbLog.i(TAG, "Started screen recording");
                rtcEventHandler.onStartScreenRecord(SUCCESS_CODE, "已经开始录制屏幕");
            } else {
                SxbLog.i(TAG, "User cancelled");
                rtcEventHandler.onStartScreenRecord(SUCCESS_CODE, "取消录制屏幕");
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    /**
     * 获取屏幕相关数据
     */
    private void getScreenBaseInfo() {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        screenDensity = metrics.densityDpi;
    }
}
