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
import com.tencent.qcloud.interfacev1.IRtcEngineEventHandler;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class ILiveModule extends ReactContextBaseJavaModule {

    private static final String TYPE = "type";
    private static final String CODE = "code";
    private static final String MSG = "msg";

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
        public void onLeaveRoom(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onLeaveRoom");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onHostLeave(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onHostLeave");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onHostBack(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onHostBack");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onForceQuitRoom(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onForceQuitRoom");
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
        public void onToggleCamera(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onToggleCamera");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onToggleMic(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onToggleMic");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onUpVideo(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onUpVideo");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onDownVideo(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onDownVideo");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
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
    public void doAVListener() {
        ILiveManager.getInstance().addObserver();
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
    public void createRoom(final String hostId, final int roomId, final String quality) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().createRoom(hostId, roomId, quality);
            }
        });
    }

    @ReactMethod
    public void joinRoom(final String hostId, final int roomId, final int userRole, final String quality) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().joinRoom(hostId, roomId, userRole, quality);
            }
        });
    }

    @ReactMethod
    public void leaveRoom() {
        ILiveManager.getInstance().leaveRoom();
    }

    @ReactMethod
    public void upVideo(String uid) {
        ILiveManager.getInstance().upVideo(uid);
    }

    @ReactMethod
    public void downVideo(String uid) {
        ILiveManager.getInstance().downVideo(uid);
    }

    @ReactMethod
    public void switchCamera() {
        ILiveManager.getInstance().switchCamera();
    }

    @ReactMethod
    public void toggleCamera(boolean bCameraOn) {
        ILiveManager.getInstance().toggleCamera(bCameraOn);
    }

    @ReactMethod
    public void toggleMic(boolean bMicOn) {
        ILiveManager.getInstance().toggleMic(bMicOn);
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
        ILiveManager.getInstance().onDestory();
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
