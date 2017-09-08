# react-native-ilive
1、基于腾讯互动直播封装成react-native组件
2、封装android、iOS两大平台

Get started

#Android

1、将android/react-native-ilive拷贝到自己项目的android目录下

2、在android/settings.gradle文件中新增：':react-native-ilive'依赖

3、在android/app/build.gradle文件的dependencies中添加：
compile project(path: ':react-native-ilive')

4、在android/app/src/main/AndroidMainfest.xml中：
权限添加：
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.MANAGE_ACCOUNTS" />
    <uses-permission android:name="android.permission.GET_ACCOUNTS" />
    <uses-permission android:name="android.permission.GET_TASKS" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    <uses-permission android:name="android.permission.READ_LOGS" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.BROADCAST_STICKY" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
application下添加：
<provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="com.tencent.qcloud.suixinbo.fileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
    </provider>

5、MainApplication.java文件：
  a、extends QavsdkApplication
  b、getPackages()方法中添加new ILivePackage()

#iOS

1、将ios/RCTILive拷贝到自己项目中

2、运行ios/RCTILive/Frameworks/LoadSDK.sh，下载工程需要的资源库，仅保留AVSDK、ILiveSDK、IMSDK三个文件夹（！！多余的文件/文件夹删除）

3、修改工程配置

  将下载好的SDK复制到工程目录下，工程目录右键，Add Files to " you projectname"

  Build Settings/Linking/Other Linker Flags，增加 -ObjC 配置

  Build Settings/Linking/Bitcode，增加 Bitcode 配置，设置为NO

  iOS10及以上系统，需在Info.plist中增加设备访问权限配置
  http://mc.qcloudimg.com/static/img/e7b7897cb79a5cb9a984938dd4b3fda3/image.png


4 添加系统库
    需要增加的系统库
    libc++.tbd
    libstdc++.tbd
    libstdc++.6.tbd
    libz.tbd
    libbz2.tbd
    libiconv.tbd
    libresolv.tbd
    libsqlite3.tbd
    libprotobuf.tbd
    UIKit.framework
    CoreVideo.framework
    CoreMedia.framework
    Accelerate.framework
    Foundation.framework
    AVFoundation.framework
    VideoToolbox.framework
    CoreGraphics.framework
    CoreTelephony.framework
    SystemConfiguration.framework

5、Build Settings/PrefixHeader/your_projectname/RCTILive/TILLiveSDKShow-Prefix.pch

6、ReactNativeILive/AppDelegate.m文件修改：

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  // start 腾讯互动直播环境初始化
  TIMManager *manager = [[ILiveSDK getInstance] getTIMManager];
  NSNumber *evn = [[NSUserDefaults standardUserDefaults] objectForKey:kEnvParam];
  [manager setEnv:[evn intValue]];//环境
  NSNumber *logLevel = [[NSUserDefaults standardUserDefaults] objectForKey:kLogLevel];//log 等级(默认debug)
  if (!logLevel)
  {
    [[NSUserDefaults standardUserDefaults] setObject:@(TIM_LOG_DEBUG) forKey:kLogLevel];
    logLevel = @(TIM_LOG_DEBUG);
  }
  [manager initLogSettings:YES logPath:[manager getLogPath]];
  [manager setLogLevel:(TIMLogLevel)[logLevel integerValue]];
  // end
  ........
}


#react-native代码配置如下：

1、将react-native-ilive.git/src下的ILiveView、index、RtcEngine三个文件拷贝到你的项目相应目录下

2、在你项目的直播功能js文件中：

  a、导入import {RtcEngine, ILiveView} from './src/index';

  b、componentWillMount中初始化直播引擎：
  //初始化iLive
        const options = {
            appid: '1400027849',// 腾讯后台获取
            accountType: '11656',// 腾讯后台获取
            hostId: 'ruby',
            roomNum: '6015711',//自己服务器动态分配，规则自己服务器定
            userRole: '1'//角色，1：主播、0：观众
        };
        RtcEngine.init(options);
  // 自己创建直播间 hostId=自己的id,roomNum=自己的房间号,userRole=1
    // 加入别人的房间 hostId=主播的id,roomNum=主播的房间号,userRole=0

c、componentDidMount中先登录腾讯的TLS系统,use id&&sig（自己服务器端生成）

RtcEngine.iLiveLogin('learnta01', 'eJxlj1FrwjAYRd-7K0JfHeNLamor*JCWbsqcQyeKTyVr0hrbxi5GcY79921VWGH39Rzu5X46CCF3OX2951m2P2qb2o9GumiIXHDv-mDTKJFym3pG-IPy3CgjU55baVqIKaUEoOsoIbVVuboZleRGWw64oxxEmbY7147*TwEZBP2wq6iihc-JPJ5Ep-wNs22xrh4f7CJcDWJ1Ee961hASllmPLYrC7Hrr3XzlM8Wsp7mZPtHsxZIZw8toXAfHOAmToN56EE-OwWZMywqiDRuNOpNW1fJ2Cjzq*wBBh56kOai9bgUCmGLiwW9c58v5Blc5XRk_');
//所有的原生通知统一管理
RtcEngine.eventEmitter({
            onLoginTLS: (data) => {
                var result = data.code === '1000';
                this.setState({isLoginSuccess: result});
                // TLS登录成功
                if (result) {
                    console.log("登录腾讯TLS系统成功iLiveJoinChannle>>>>>>");
                    RtcEngine.iLiveJoinChannle();
                }
            },
            onLogoutTLS: (data) => {
                console.log(data);
            },
            onCreateRoom: (data) => {
                console.log(data);
                // 创建房间
                var result = (data.code === '1000' || data.code === '1003');
                console.log("创建房间>>>>>>:" + result);
            },
            onJoinRoom: (data) => {
                console.log(data);
                // 加入房间1000不在房间内，1003已经在房间里面
                var result = (data.code === '1000' || data.code === '1003');
                console.log("加入房间>>>>>>:" + result);
            },
            onExitRoom: (data) => {
                console.log(data);
            },
            onRoomDisconnect: (data) => {
                console.log(data);
            },
            onSwitchCamera: (data) => {
                console.log(data);
            },
            onToggleCamera: (data) => {
                console.log(data);
            },
            onToggleMic: (data) => {
                console.log(data);
                var result = data.result === 'true';
                this.setState({
                    bMicOn: result
                });
            },
            onError: (data) => {
                console.log(data);
                // 错误!
            }
        })

d、页面销毁退出直播间，移除回调
componentWillUnmount() {
        RtcEngine.startExitRoom();
        RtcEngine.removeEmitter()
    }

e、退出房间、切换摄像头、开关声麦方法如下

    handlerCancel = () => {
        RtcEngine.startExitRoom();
    };

    handlerSwitchCamera = () => {
        RtcEngine.switchCamera();
    };

    handlerToggleMic = () => {
        RtcEngine.toggleMic();
    };

f、render()中添加直播component
    <ILiveView showVideoView={true}/>

