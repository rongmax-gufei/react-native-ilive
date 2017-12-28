package com.tencent.qcloud;

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
    private static final String ROOMID = "roomId";

    private ReactApplicationContext context;
    private int roomId;

    public ILiveModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    @Override
    public String getName() {
        return "RCTILive";
    }

    private IRtcEngineEventHandler rtcEventHandler = new IRtcEngineEventHandler() {

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
        public void onStartVideoRecord(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onStartVideoRecord");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onStopVideoRecord(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onStopVideoRecord");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onStartScreenRecord(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onStartScreenRecord");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onStopScreenRecord(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onStopScreenRecord");
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

        @Override
        public void onChangeRole(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onChangeRole");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onParOn(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onParOn");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onParOff(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onParOff");
                    map.putString(CODE, code);
                    map.putString(MSG, msg);
                    commonEvent(map);
                }
            });
        }

        @Override
        public void onNetSpeedTest(final String code, final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WritableMap map = Arguments.createMap();
                    map.putString(TYPE, "onNetSpeedTest");
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
    public void init(final ReadableMap options) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().init(context, getCurrentActivity(), rtcEventHandler, options);
            }
        });
    }

    @ReactMethod
    public void doAVListener() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().addObserver();
            }
        });
    }

    @ReactMethod
    public void iLiveLogin(final String id, final String sig) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().iLiveLogin(id, sig);
            }
        });
    }

    @ReactMethod
    public void iLiveLogout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().iLiveLogout();
            }
        });
    }

    @ReactMethod
    public void createRoom(final String hostId, final int roomId, final String quality) {
        this.roomId = roomId;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().createRoom(hostId, roomId, quality);
            }
        });
    }

    @ReactMethod
    public void joinRoom(final String hostId, final int roomId, final int userRole, final String quality) {
        this.roomId = roomId;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().joinRoom(hostId, roomId, userRole, quality);
            }
        });
    }

    @ReactMethod
    public void changeRole(final String role) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().changeRole(role);
            }
        });
    }

    @ReactMethod
    public void leaveRoom() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().leaveRoom();
            }
        });
    }

    @ReactMethod
    public void upVideo(final String uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().upVideo(uid);
            }
        });
    }

    @ReactMethod
    public void downVideo(final String uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().downVideo(uid);
            }
        });
    }

    @ReactMethod
    public void startVideoRecord(final String fileName, final int recordType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().startVideoRecord(fileName, recordType);
            }
        });
    }

    @ReactMethod
    public void stopVideoRecord() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().stopVideoRecord();
            }
        });
    }

    @ReactMethod
    public void startScreenRecord() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().startScreenRecord(getCurrentActivity());
            }
        });
    }

    @ReactMethod
    public void stopScreenRecord() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().stopScreenRecord();
            }
        });
    }


    @ReactMethod
    public void switchCamera() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().switchCamera();
            }
        });
    }

    @ReactMethod
    public void toggleCamera(final boolean bCameraOn) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().toggleCamera(bCameraOn);
            }
        });
    }

    @ReactMethod
    public void toggleMic(final boolean bMicOn) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().toggleMic(bMicOn);
            }
        });
    }

    @ReactMethod
    public void onParOn() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().onParOn();
            }
        });
    }

    @ReactMethod
    public void onParOff() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().onParOff();
            }
        });
    }

    @ReactMethod
    public void netSpeedTest() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().netSpeedTest();
            }
        });
    }

    @ReactMethod
    public void onPause() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveRoomManager.getInstance().onPause();
            }
        });
    }

    @ReactMethod
    public void onResume() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveRoomManager.getInstance().onResume();
            }
        });
    }

    @ReactMethod
    public void onDestory() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ILiveManager.getInstance().onDestory();
            }
        });
    }

    private void commonEvent(WritableMap map) {
        map.putString(ROOMID, String.valueOf(roomId));
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
