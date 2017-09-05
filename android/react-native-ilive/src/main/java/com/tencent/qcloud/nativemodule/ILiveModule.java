package com.tencent.qcloud.nativemodule;

import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.qcloud.interfacev1.IRtcEngineEventHandler;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class ILiveModule extends ReactContextBaseJavaModule {

    private static final String TYPE = "type";
    private static final String CODE = "code";
    private static final String MSG = "msg";
    private static final String RESULT = "result";

    public ILiveModule(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return "RCTILive";
    }

    private IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onLoginTLS(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onLoginTLS");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onLogoutTLS(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onLogoutTLS");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onCreateRoom(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onCreateRoom");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onJoinRoom(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onJoinRoom");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onExitRoom(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onExitRoom");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onRoomDisconnect(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onRoomDisconnect");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onSwitchCamera(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onSwitchCamera");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onToggleCamera(final boolean bCameraOn) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onToggleCamera");
                    map.putString(RESULT, String.valueOf(bCameraOn));
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onToggleMic(final boolean bMicOn) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onToggleMic");
                    map.putString(RESULT, String.valueOf(bMicOn));
                    commonEvent(map);
                }
            });
        }
    };

    /**
     * 初始化
     *
     * @param options
     */
    @ReactMethod
    public void init(ReadableMap options) {
        ILiveManager.getInstance().init(getReactApplicationContext(), mRtcEventHandler, options);
    }

    @ReactMethod
    public void iLiveLogin(String id, String sig) {
        ILiveManager.getInstance().iLiveLogin(id, sig);
    }

    @ReactMethod
    public void iLiveLogout() {
        ILiveManager.getInstance().iLiveLogout();
    }

    @ReactMethod
    public void startEnterRoom() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().startEnterRoom();
            }
        });
    }

    @ReactMethod
    public void startExitRoom() {
        ILiveManager.getInstance().startExitRoom();
    }

    @ReactMethod
    public void onPause() {
        ILiveRoomManager.getInstance().onPause();
    }

    @ReactMethod
    public void onResume() {
        ILiveRoomManager.getInstance().onResume();
    }

    @ReactMethod
    public void onDestory() {
        ILVLiveManager.getInstance().quitRoom(null);
    }

    @ReactMethod
    public void switchCamera() {
        ILiveManager.getInstance().switchCamera();
    }

    @ReactMethod
    public void toggleCamera() {
        ILiveManager.getInstance().toggleCamera();
    }

    @ReactMethod
    public void toggleMic() {
        ILiveManager.getInstance().toggleMic();
    }

    private void commonEvent(WritableMap map) {
        sendEvent(getReactApplicationContext(), "iLiveEvent", map);
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

}
