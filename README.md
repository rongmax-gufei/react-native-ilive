# README
| Author        |     E-mail      |
| ------------- |:---------------:|
| gufei         | 799170694@qq.com|

*  不断更新优化中<br>
iOS编译过程中若出现错误，请联系我发ILiveSDK.framework的最新库，此库腾讯官方可能尚未更新上去。

# react-native-ilive
*  基于腾讯互动直播封装成react-native组件
*  封装android、iOS两大平台
*  目前实现功能：创建房间、加入房间、切换角色、上下麦、切换摄像头、开关摄像头、开关声麦、录视频流、录屏幕、基础美颜
*  待实现功能：屏幕分享、连麦窗口可定制化

### Android

* 将android/react-native-ilive拷贝到自己项目的android目录下

* 在android/settings.gradle文件中新增：':react-native-ilive'依赖

* 在android/app/build.gradle文件的dependencies中添加：compile project(path: ':react-native-ilive')

* 在android/app/src/main/AndroidMainfest.xml中添加权限：
```
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
```
*  application下添加：
```
    <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="com.tencent.qcloud.suixinbo.fileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
    </provider>
```
* MainApplication.java文件：
  *  extends QavsdkApplication
  *  getPackages()方法中添加new ILivePackage()

### iOS

* 将ios/RCTILive拷贝到自己项目中

* 运行ios/RCTILive/Frameworks/LoadSDK.sh，下载工程需要的资源库，仅保留AVSDK、ILiveSDK、IMSDK三个文件夹（！！多余的文件/文件夹删除）
* 下载美颜插件：http://dldir1.qq.com/hudongzhibo/ILiveSDK/TXMVideoPreprocessor_3.3.0.zip
  解压至ios/RCTILive/Frameworks/文件夹下，保留basic文件夹（基础美颜，免费），删除advance文件夹（高级美颜需付费）。
* 修改工程配置

  *  将下载好的SDK复制到工程目录下，工程目录右键，Add Files to " you projectname"

  *  Build Settings/Linking/Other Linker Flags，增加 -ObjC 配置

  *  Build Settings/Linking/Bitcode，增加 Bitcode 配置，设置为NO

  *  iOS10及以上系统，需在Info.plist中增加设备访问权限配置
  ![](http://mc.qcloudimg.com/static/img/e7b7897cb79a5cb9a984938dd4b3fda3/image.png)


* 添加系统库
    需要增加的系统库
    *  libc++.tbd
    *  libstdc++.tbd
    *  libstdc++.6.tbd
    *  libz.tbd
    *  libbz2.tbd
    *  libiconv.tbd
    *  libresolv.tbd
    *  libsqlite3.tbd
    *  libprotobuf.tbd
    *  UIKit.framework
    *  CoreVideo.framework
    *  CoreMedia.framework
    *  Accelerate.framework
    *  Foundation.framework
    *  AVFoundation.framework
    *  VideoToolbox.framework
    *  CoreGraphics.framework
    *  CoreTelephony.framework
    *  SystemConfiguration.framework
    *  OpenAL.framework

* 点击项目->TARGETS/Build Settings/Prefix Header->添加your_projectname/RCTILive/TILLiveSDKShow-Prefix.pch

* ReactNativeILive/AppDelegate.m文件修改：
 ```
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
  // start 腾讯互动直播环境初始化
  TIMManager *manager = [[ILiveSDK getInstance] getTIMManager];
  NSNumber *evn = [[NSUserDefaults standardUserDefaults] objectForKey:kEnvParam];
  [manager setEnv:[evn intValue]];
  NSNumber *logLevel = [[NSUserDefaults standardUserDefaults] objectForKey:kLogLevel];
  if (!logLevel) {
    [[NSUserDefaults standardUserDefaults] setObject:@(TIM_LOG_DEBUG) forKey:kLogLevel];
    logLevel = @(TIM_LOG_DEBUG);
  }
  [manager initLogSettings:YES logPath:[manager getLogPath]];
  [manager setLogLevel:(TIMLogLevel)[logLevel integerValue]];
  // end
  ........
}
 ```

### react-native代码配置如下：

*   将react-native-ilive.git/src下的ILiveView、index、RtcEngine三个文件拷贝到你的项目相应目录下

*   在你项目的直播功能js文件中：

  *  import {RtcEngine, ILiveView} from './src/index';

  *  componentWillMount中初始化直播引擎：
   ```
  // 初始化iLive
  const options = {
      appid: '1400027849',
      accountType: '11656'
  };
  RtcEngine.init(options);
  // 添加AVListener，此方法必须在rn的componentWillMount()方法中执行，render()之前执行
  RtcEngine.iLiveSetAVListener();
 ```
*  componentDidMount中先登录腾讯的TLS系统,use id&&sig（自己服务器端生成）
 ```
RtcEngine.iLiveLogin('learnta111', 'eJxlj8tOg0AARfd8BWGLMTPA8HBHoBGsQNqipitC59GOtTBOB4Ua-70Vm0ji3Z6Te3O-NF3XjfJxdVtj3HaNqtQgqKHf6QYwbv6gEJxUtapsSf5B2gsuaVUzReUIIULIAmDqcEIbxRm-Gm*0lo2qIYQT50j21Tj0W*JcGizPd4KpwrcjzGbrKF1Ekjmv73M-jEN0wsn88HQ-K1lcgLIzdxvVnfIMic-ewy3bpruwiJNscPmQPjsvy1zYyWogOe43idq766I000XsRmYg8UMxmVT8QK*vALI9Pwi8Cf2g8sjbZhQsABG0bPATQ-vWzhSzXpU_');
 ```
*  所有的原生通知统一管理
 ```
RtcEngine.eventEmitter({
    onLoginTLS: (data) => {
        var result = data.code === '1000';
        this.setState({isLoginSuccess: result});
        // TLS登录成功
        if (result) {
            // 自己创建直播间 hostId=自己的id,roomNum=自己的房间号,清晰度
            // 加入别人的房间 hostId=主播的id,roomNum=主播的房间号,userRole=0,清晰度
            RtcEngine.iLiveCreateRoom('learnta111', 662556, "HD");
            // RtcEngine.iLiveJoinRoom('learnta111', 662556, 0, "Guest");
        }
    },
    onLogoutTLS: (data) => {
        console.log(data);
    },
    // 创建房间
    onCreateRoom: (data) => {
        console.log(data);
    },
    // 加入房间
    onJoinRoom: (data) => {
        console.log(data);
    },
    // 离开房间
    onLeaveRoom: (data) => {
        console.log(data);
    },
    // 退出房间
    onExitRoom: (data) => {
        console.log(data);
    },
    // 切换角色
    onChangeRole: (data) => {
        console.log(data);
    },
    // 上麦
    onUpVideo: (data) => {
        console.log(data);
    },
    // 下麦
    onDownVideo: (data) => {
        console.log(data);
    },
    // 开始录制视频
    onStartVideoRecord: (data) => {
        console.log(data);
    },
    // 停止录制视频
    onStopVideoRecord: (data) => {
         console.log(data);
    },
    // 开始录屏幕
    onStartScreenRecord: (data) => {
       console.log(data);
    },
    // 停止录屏幕
    onStopScreenRecord: (data) => {
        console.log(data);
    },
    // 与房间断开连接
    onRoomDisconnect: (data) => {
        console.log(data);
    },
    // 切换摄像头
    onSwitchCamera: (data) => {
        console.log(data);
    },
    // 开关摄像头
    onToggleCamera: (data) => {
        console.log(data);
        var result = data.code === '1000';
        this.setState({
            bCameraOn: result
        });
    },
    // 开关声麦
    onToggleMic: (data) => {
        console.log(data);
        var result = data.code === '1000';
        this.setState({
            bMicOn: result
        });
    },
    onError: (data) => {
        console.log(data);
        // 错误!
    }
})
  ```
*  页面销毁退出直播间，移除回调
 ```
componentWillUnmount() {
        RtcEngine.iLiveLeaveRoom();
        RtcEngine.removeEmitter()
    }
 ```
*  退出房间、上麦、下麦、录制视频流、录制屏幕、切换摄像头、开关摄像头、开关声麦方法如下
 ```
handerLeavelRoom() {
    console.log('handerLeavelRoom');
    // 通知腾讯TLS服务器
    RtcEngine.iLiveLeaveChannel();
    // 移除监听事件
    RtcEngine.removeEmitter();
};

handlerUpVideo (uid) {
    console.log('handlerUpVideo');
    RtcEngine.iLiveUpVideo(uid);
};

handlerDownVideo(uid) {
    console.log('handlerDownVideo');
    RtcEngine.iLiveDownVideo(uid);
};

handlerStartRecord (fileName, recordType) {
    console.log('handlerStartRecord');
    RtcEngine.iLiveStartRecord(fileName, recordType);
};

handlerStopRecord() {
    console.log('handlerStopRecord');
    RtcEngine.iLiveStopRecord();
};

handlerStartScreenRecord () {
    console.log('handlerStartScreenRecord');
    RtcEngine.iLiveStartScreenRecord();
};

handlerStopScreenRecord() {
    console.log('handlerStopScreenRecord');
    RtcEngine.iLiveStopScreenRecord();
};

handlerSwitchCamera =() => {
    RtcEngine.iLiveSwitchCamera();
};

handlerToggleCamera =(bCameraOn) => {
    RtcEngine.iLiveToggleCamera(bCameraOn);
};

handlerToggleMic =(bMicOn) => {
    RtcEngine.iLiveToggleMic(bMicOn);
};
 ```
*  render()中添加直播component
 ```
  <ILiveView style={styles.localView} showVideoView={true}/>
 ```
