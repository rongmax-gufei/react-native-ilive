# react-native-ilive

[![npm version](https://badge.fury.io/js/react-native-ilive.svg)](https://badge.fury.io/js/react-native-ilive)
[![npm](https://img.shields.io/npm/dt/react-native-ilive.svg)](https://www.npmjs.com/package/react-native-ilive)
![Platform - Android and iOS](https://img.shields.io/badge/platform-Android%20%7C%20iOS-yellow.svg)
![MIT](https://img.shields.io/dub/l/vibe-d.svg)

| Author        |     E-mail      |
| ------------- |:---------------:|
| gufei         | 799170694@qq.com|


## 功能介绍

- 支持 iOS Android  腾讯互动直播iLive SDK
- 支持 创建房间、加入房间、切换角色、上下麦、切换摄像头、开关摄像头、开关声麦、录视频流、录屏幕、基础美颜、获取房间信息、测网速功能

## 安装使用

 `npm install --save react-native-ilive`

Then link with:

 `react-native link react-native-ilive`

#### Android

Add following to `AndroidManifest.xml`
    
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
* 将项目的MainApplication.java类继承QavsdkApplication.java类

#### iOS

* 进入"../node_modules/react-native-ilive/ios/RCTILive/Frameworks/"文件夹，在控制台执行:sh LoadSDK.sh 下载工程需要的资源库

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
## Documentation

[腾讯互动直播API文档](https://cloud.tencent.com/document/product/268/8424)

 ##### 原生通知事件

```
RtcEngine.eventEmitter({
  onLoginTLS: data => {},
  onLogoutTLS: data => {},
  onCreateRoom: data => {},
  onJoinRoom: data => {},
  onLeaveRoom: data => {},
  onExitRoom: data => {},
  onChangeRole: data => {},
  onUpVideo: data => {},
  onDownVideo: data => {},
  onStartVideoRecord: data => {},
  onStopVideoRecord: data => {},
  onStartScreenRecord: data => {},
  onStopScreenRecord: data => {},
  onSwitchCamera: data => {},
  onToggleCamera: data => {},
  onToggleMic: data => {},
  onRoomDisconnect: data => {},
  onNetSpeedTest: data => {},
  onParOn: data => {},
  onParOff: data => {},
  onError: data => {}
})
```

| Name                      | Description  |
| ------------------------- | ------------ |
| onLoginTLS | 腾讯TLS服务器登录回调 |
| onLogoutTLS      | 腾讯TLS服务器登出回调    |
| onCreateRoom             | 创建房间回调 |
| onJoinRoom              | 加入房间回调   |
| onLeaveRoom                   | 离开房间回调 |
| onExitRoom                 | 退出房间回调  |
| onChangeRole            | 改变角色回调  |
| onUpVideo            | 上麦回调  |
| onDownVideo | 下麦回调 |
| onStartVideoRecord      | 开始录制视频流回调    |
| onStopVideoRecord             | 停止录制视频流回调 |
| onStartScreenRecord              | 开始录屏幕回调 |
| onStopScreenRecord                   | 停止录屏幕回调 |
| onSwitchCamera                 | 切换摄像头回调 |
| onToggleCamera            | 开关摄像头回调  |
| onToggleMic            | 开关声麦回调  |
| onRoomDisconnect            | 房间失去联系回调 |
| onNetSpeedTest            | 网络测速结果回调 |
| onParOn            | 实时采集房间信息回调 |
| onParOff            | 结束采集房间信息回调 |
| onError            | 错误提示|

##### ILiveView 组件

| Name           | Description          |
| -------------- | -------------------- |
| showLocalVideo | 是否显示本地视频（bool）       |


## 运行示例

[Example](https://github.com/midas-gufei/RNILiveExample)


## 更新信息

- [腾讯Android更新日志](https://github.com/zhaoyang21cn/iLiveSDK_Android_Suixinbo/blob/master/doc/ILiveSDK/release%20note.md)
- [腾讯iOS更新日志](https://github.com/zhaoyang21cn/iLiveSDK_iOS_Suixinbo/blob/master/doc/ILiveSDK_ChangeList.md)


#### 2018-01-23
- 主播给观众上麦后给自己一个回调，并通知给ReactNative页面

#### 2018-01-04
- 更新iOS iLiveSDK 版本号至 V1.8.1.12629(2017-12-18)
- 更新Android iLiveSDK 版本号至 V1.8.0

#### 2017-12-27
- 新增获取视频房间信息数据
- 新增网络测速功能

