//
//  RCTILive.m
//
//  Created by ruby on 2017/8/31.
//  Copyright © 2017年 Learnta Inc. All rights reserved.
//
#import "RCTILive.h"
#import "RCTILive+AVListener.h"
#import "RCTILive+ImListener.h"
#import "RCTILive+Audio.h"
#import "RCTILiveVideoView.h"

#import <ReplayKit/ReplayKit.h>

#import <React/RCTEventDispatcher.h>
#import <React/RCTBridge.h>
#import <React/RCTUIManager.h>
#import <React/RCTView.h>

@interface RCTILive ()<QAVLocalVideoDelegate, ILiveRoomDisconnectListener, RPPreviewViewControllerDelegate, TXIVideoPreprocessorDelegate>
@end

@implementation RCTILive

RCT_EXPORT_MODULE();

@synthesize bridge = _bridge;

/**
 * 初始化腾讯互动直播SDK
 *
 * @param appId           用户标识接入SDK的应用ID
 * @param accountType 用户的账号类型
 */
RCT_EXPORT_METHOD(init:(NSDictionary *)options) {
  [ILiveConst share].sdkAppid = options[@"appid"];
  [ILiveConst share].sdkAccountType = options[@"accountType"];
  // 初始化iLive模块
  [[ILiveSDK getInstance] initSdk:[[ILiveConst share].sdkAppid intValue] accountType:[[ILiveConst share].sdkAccountType intValue]];
}

/**
 * 登录腾讯互动直播TLS服务器
 *
 * @param uid 用户ID
 * @param sig 用户token
 */
RCT_EXPORT_METHOD(iLiveLogin:(NSString *)uid sig:(NSString *)sig) {
  [[ILiveLoginManager getInstance] iLiveLogin:uid sig:sig succ:^{
      [self commentEvent:@"onLoginTLS" code:kSuccess msg:@"登录腾讯TLS系统成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
      [self commentEvent:@"onLoginTLS" code:errId msg:errMsg];
  }];
}

/**
 * 登出腾讯互动直播TLS服务器
 */
RCT_EXPORT_METHOD(iLiveLogout) {
  [[ILiveLoginManager getInstance] iLiveLogout:^{
    [self commentEvent:@"onLogoutTLS" code:kSuccess msg:@"登出腾讯TLS系统成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
     [self commentEvent:@"onLogoutTLS" code:errId msg:errMsg];
  }];
}

/**
 * 摄像头与屏幕的渲染层发生改变时的监听器
 * 建议此方法在componentWillMount方法中执行，确保render()之前执行
 */
RCT_EXPORT_METHOD(doAVListener) {
  TILLiveManager *manager = [TILLiveManager getInstance];
  // 音视频监听
  [manager setAVListener:self];
  // 消息监听
  [manager setIMListener:self];
  //创建变量
  self.preProcessor = [[TXCVideoPreprocessor alloc] init];
  [self.preProcessor setDelegate:self];
  // 进房前设置数据帧回调
  [[ILiveRoomManager getInstance] setLocalVideoDelegate:self];
  // 注册全局消息回调（主播主动退出房间、取消连麦）
  [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onGotupDelete:) name:kGroupDelete_Notification object:nil];
  [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(connectVideoCancel:) name:kCancelConnect_Notification object:nil];
  [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(upVideoCallback:) name:kUserUpVideo_Notification object:nil];
}

/**
 * 创建房间
 *
 * @param hostId  主播ID
 * @param roomId 房间号
 * @param quality  画质"HD"、"SD"、"LD"
 * HD：高清(1280x720,25fps)
 * SD：标清(960x540,20fps)
 * LD：流畅(640x480,15fps)
 */
RCT_EXPORT_METHOD(createRoom:(NSString *)hostId roomId:(int)roomId quality:(NSString *)quality) {
  [ILiveConst share].hostId = hostId;
  [ILiveConst share].roomId = roomId;
  [ILiveConst share].userRole = 1;
  [ILiveConst share].controlRole = quality;
  _isHost = true;
  [self createRoom: quality];
}

/**
 * 加入房间
 *
 * @param hostId      主播ID
 * @param roomId     房间号
 * @param userRole   角色（主播二次进入直播间or观众进入直播间）1:主播0:观众
 * @param quality      画质，清晰"Guest"、流畅"Guest2"
 */
RCT_EXPORT_METHOD(joinRoom:(NSString *)hostId roomId:(int)roomId userRole:(int)userRole quality:(NSString *)quality) {
  [ILiveConst share].hostId = hostId;
  [ILiveConst share].roomId = roomId;
  [ILiveConst share].userRole = userRole;
  [ILiveConst share].controlRole = quality;
  _isHost = (userRole == 1);
  [self joinRoom:quality];
}

/**
 * 切换角色
 * 请确保在腾讯云SPEAR上已配置该角色
 * @param quality
 *  1、画质，主播："HD"、"SD"、"LD"
 *  2、连麦观众："HDGuest"、"SDGuest"、"LDGuest"
 *  3、普通观众：清晰"Guest"、流畅"Guest2"
 */
RCT_EXPORT_METHOD(changeRole:(NSString *)quality) {
  ILiveRoomManager *manager = [ILiveRoomManager getInstance];
  [manager changeRole:quality succ:^ {
    [self commentEvent:@"onChangeRole" code:kSuccess  msg:@"角色改变成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    [self commentEvent:@"onChangeRole" code:errId  msg:errMsg];
  }];
}

/**
 * 退出房间
 * 主播退出房间时，则发通知给群主成员
 */
RCT_EXPORT_METHOD(leaveRoom) {
  __weak typeof(self) ws = self;
  if (_isHost) {
    ILVLiveCustomMessage *customMsg = [[ILVLiveCustomMessage alloc] init];
    customMsg.type = ILVLIVE_IMTYPE_GROUP;
    customMsg.recvId = [[ILiveRoomManager getInstance] getIMGroupId];
    customMsg.cmd = (ILVLiveIMCmd)AVIMCMD_ExitLive;
    [[TILLiveManager getInstance] sendCustomMessage:customMsg succ:^{
      NSLog(@"主播退群，发送退群消息成功！");
      [ws onClose];
    } failed:^(NSString *module, int errId, NSString *errMsg) {
      NSLog(@"主播退群，发送退群消息失败！");
      [ws onClose];
    }];
  } else {
    [ws onClose];
  }
}

/**
 * 上麦
 * 1、先判断当前存在几路画面，超过规定线路，则拒绝连接
 * 2、给连麦对象发送连麦请求
 * 3、增加连麦小视图
 *
 * @param uid   上麦对象ID
 */
RCT_EXPORT_METHOD(upVideo:(NSString *)uid) {
  // 先判断当前存在几路画面，超过规定线路，则拒绝连接
  if ([UserViewManager shareInstance].total >= kMaxUserViewCount) {
    NSString *message = [NSString stringWithFormat:@"连麦画面不能超过%d路，先取消一路连麦",  kMaxUserViewCount+1];
    [self commentEvent:@"onUpVideo" code:kFail  msg:message];
    return;
  }
  // 给连麦对象发送连麦请求
  ILVLiveCustomMessage *video = [[ILVLiveCustomMessage alloc] init];
  video.recvId = uid;
  video.type = ILVLIVE_IMTYPE_C2C;
  video.cmd = (ILVLiveIMCmd)AVIMCMD_Multi_Host_Invite;
  [[TILLiveManager getInstance] sendCustomMessage:video succ:^{
    [self commentEvent:@"onUpVideo" code:kSuccess msg:@"连麦请求已发出"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    [self commentEvent:@"onUpVideo" code:errId msg:errMsg];
  }];
  // 增加连麦小视图
  LiveCallView *callView = [[UserViewManager shareInstance] addPlaceholderView:uid];
  ILiveRenderView *mainAvRenderView = [[TILLiveManager getInstance] getAVRenderView:[[ILiveConst share] hostId] srcType:QAVVIDEO_SRC_TYPE_CAMERA];
  [mainAvRenderView.superview addSubview:callView];
}

/**
 * 下麦
 *
 * @param uid    下麦对象ID
 */
RCT_EXPORT_METHOD(downVideo:(NSString *)uid) {
  ILVLiveCustomMessage *video = [[ILVLiveCustomMessage alloc] init];
  video.recvId = uid;
  video.data = [uid dataUsingEncoding:NSUTF8StringEncoding];
  video.type = ILVLIVE_IMTYPE_GROUP;
  video.cmd = (ILVLiveIMCmd)AVIMCMD_Multi_CancelInteract;
  [[TILLiveManager getInstance] sendCustomMessage:video succ:^{
    NSLog(@"下麦请求已发出");
    [self commentEvent:@"onDownVideo" code:kSuccess msg:@"下麦请求已发出"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    [self commentEvent:@"onDownVideo" code:errId msg:errMsg];
  }];
}

/**
 * 切换前置/后置摄像头
 */
RCT_EXPORT_METHOD(switchCamera) {
  [[ILiveRoomManager getInstance] switchCamera:^{
      [self commentEvent:@"onSwitchCamera" code:kSuccess msg:@"摄像头切换成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
      [self commentEvent:@"onSwitchCamera" code:errId msg:errMsg];
  }];
}

/**
 * 打开/关闭摄像头
 *
 * @param bCameraOn    true:打开/false:关闭
 */
RCT_EXPORT_METHOD(toggleCamera:(BOOL) bCameraOn) {
  [[ILiveRoomManager getInstance] enableCamera:CameraPosFront enable:bCameraOn succ:^{
    [self commentEvent:@"onToggleCamera" code:kSuccess msg:bCameraOn?@"摄像头打开成功":@"摄像头关闭成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
      [self commentEvent:@"onToggleCamera" code:errId msg:errMsg];
  }];
}

/**
 * 打开/关闭声麦
 *
 * @param bMicOn     true:打开/false:关闭
 */
RCT_EXPORT_METHOD(toggleMic:(BOOL) bMicOn) {
  [[ILiveRoomManager getInstance] enableMic:bMicOn succ:^{
    [self commentEvent:@"onToggleMic" code:kSuccess msg:bMicOn?@"声麦打开成功":@"声麦打开失败"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    [self commentEvent:@"onToggleMic" code:errId msg:errMsg];
  }];
}

/**
 * 视频录制(腾讯云服务提供，只能录制视频不能录制控件)
 * @param filename          录制的文件名
 * @param recordType      0：录制视频，1：录制纯音频
 */
RCT_EXPORT_METHOD(startLiveVideoRecord:(NSString *)filename type:(int)recordType) {
  NSString *defName = [[NSString alloc] initWithFormat:@"%3.f", [NSDate timeIntervalSinceReferenceDate]];
  NSString *recName = filename && filename.length > 0 ? filename : defName;
  ILiveRecordOption *option = [[ILiveRecordOption alloc] init];
  NSString *identifier = [[ILiveLoginManager getInstance] getLoginId];
  option.fileName = [NSString stringWithFormat:@"learnta_%@_%@",identifier,recName];
  option.recordType = (recordType == 0) ? ILive_RECORD_TYPE_VIDEO:ILive_RECORD_TYPE_AUDIO;
  [[ILiveRoomManager getInstance] startRecordVideo:option succ:^{
    [self commentEvent:@"onStartVideoRecord" code:kSuccess msg:@"已开始录制"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    NSString *errinfo = [NSString stringWithFormat:@"push stream fail.module=%@,errid=%d,errmsg=%@",module,errId,errMsg];
    [self commentEvent:@"onStartVideoRecord" code:errId msg:errinfo];
  }];
}

/**
 * 结束视频录制
 */
RCT_EXPORT_METHOD(stopLiveVideoRecord) {
  [[ILiveRoomManager getInstance] stopRecordVideo:^(id selfPtr) {
    [self commentEvent:@"onStopVideoRecord" code:kSuccess msg:@"已结束录制"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    NSString *errinfo = [NSString stringWithFormat:@"push stream fail.module=%@,errid=%d,errmsg=%@",module,errId,errMsg];
   [self commentEvent:@"onStopVideoRecord" code:errId msg:errinfo];
  }];
}

/**
 * 开始屏幕录制（iOS 9.0后官方自带ReplayKit，支持录制控件）
 */
RCT_EXPORT_METHOD(startScreenRecord) {
  //如果还没有开始录制，判断系统是否支持
  if ([RPScreenRecorder sharedRecorder].available) {
     [self commentEvent:@"onStartScreenRecord" code:kSuccess msg:@"已经开始录制屏幕"];
    //如果支持，就使用下面的方法可以启动录制回放
    [[RPScreenRecorder sharedRecorder] startRecordingWithHandler:^(NSError * _Nullable error) {
      [self commentEvent:@"onStartScreenRecord" code:kSuccess msg:error.localizedDescription];
    }];
//    [[RPScreenRecorder sharedRecorder] startRecordingWithMicrophoneEnabled:YES handler:^(NSError * _Nullable error) {
//      //处理发生的错误，如设用户权限原因无法开始录制等
//      [self commentEvent:@"onStartScreenRecord" code:kSuccess msg:error.localizedDescription];
//    }];
  } else {
    [self commentEvent:@"onStartScreenRecord" code:kSuccess msg:@"录制回放功能不可用"];
  }
}

/**
 * 结束屏幕录制
 */
RCT_EXPORT_METHOD(stopScreenRecord) {
  // 停止录制回放，并显示回放的预览，在预览中用户可以选择保存视频到相册中、放弃、或者分享出去
  [[RPScreenRecorder sharedRecorder] stopRecordingWithHandler:^(RPPreviewViewController * _Nullable previewViewController, NSError * _Nullable error) {
    if (error) {
      NSLog(@"%@", error);
      //处理发生的错误，如磁盘空间不足而停止等
      [self commentEvent:@"onStopScreenRecord" code:kSuccess msg:error.localizedDescription];
    }
    if (previewViewController) {
      //设置预览页面到代理
      previewViewController.previewControllerDelegate = self;
      [[self getCurrentVC] presentViewController:previewViewController animated:YES completion:nil];
      [self commentEvent:@"onStopScreenRecord" code:kSuccess msg:@"执行预览"];
    }
  }];
}

/**
 * 销毁引擎实例
 */
RCT_EXPORT_METHOD(destroy) {
  __weak typeof(self) ws = self;
  [ws onClose];
}

#pragma mark - createRoom or joinRoom method
/**
 *  创建房间
 */
- (void)createRoom:(NSString *)quality {
  __weak typeof(self) ws = self;
  TILLiveRoomOption *option = [TILLiveRoomOption defaultHostLiveOption];
  option.controlRole = quality;
  option.avOption.autoHdAudio = YES;//使用高音质模式，可以传背景音乐
  option.roomDisconnectListener = self;
  option.imOption.imSupport = YES;
  [[TILLiveManager getInstance] createRoom:[[ILiveConst share] roomId] option:option succ:^{
    [ws initAudio];
    [self commentEvent:@"onCreateRoom" code:kSuccess msg:@"创建房间成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    [self commentEvent:@"onCreateRoom" code:errId msg:errMsg];
  }];
}

/**
 *  加入房间
 */
- (void)joinRoom:(NSString *)quality {
  __weak typeof(self) ws = self;
  TILLiveRoomOption *option = [TILLiveRoomOption defaultGuestLiveOption];
  option.controlRole = quality;
  [[TILLiveManager getInstance] joinRoom:[[ILiveConst share] roomId] option:option succ:^{
    NSLog(@"加入房间成功");
    [ws sendJoinRoomMsg];
    [self commentEvent:@"onJoinRoom" code:kSuccess msg:@"加入房间成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    NSLog(@"加入房间失败");
    [self commentEvent:@"onJoinRoom" code:errId msg:errMsg];
  }];
}

- (void)sendJoinRoomMsg {
  ILVLiveCustomMessage *msg = [[ILVLiveCustomMessage alloc] init];
  msg.type = ILVLIVE_IMTYPE_GROUP;
  msg.cmd = (ILVLiveIMCmd)AVIMCMD_EnterLive;
  msg.recvId = [[ILiveRoomManager getInstance] getIMGroupId];
  
  [[TILLiveManager getInstance] sendCustomMessage:msg succ:^{
    NSLog(@"success");
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    NSLog(@"fail");
  }];
}

#pragma mark - local video delegate
/**
 *  需要预览才设置local delegate
 */
- (void)OnLocalVideoPreview:(QAVVideoFrame *)frameData {
  frameData.identifier = [[ILiveLoginManager getInstance] getLoginId];
  [_frameDispatcher dispatchVideoFrame:frameData];
}

- (void)OnLocalVideoPreProcess:(QAVVideoFrame *)frameData {
  //设置美颜、美白、红润等参数
  [self.preProcessor setBeautyLevel:5];
  [self.preProcessor setRuddinessLevel:8];
  [self.preProcessor setWhitenessLevel:8];
  [self.preProcessor setOutputSize:CGSizeMake(frameData.frameDesc.width, frameData.frameDesc.height)];
  //开始预处理
  [self.preProcessor processFrame:frameData.data width:frameData.frameDesc.width height:frameData.frameDesc.height orientation:TXE_ROTATION_0 inputFormat:TXE_FRAME_FORMAT_NV12 outputFormat:TXE_FRAME_FORMAT_NV12];
  //将处理完的数据拷贝到原来的地址空间，如果是同步处理，此时会先执行（4）
  if (self.processorBytes) {
    memcpy(frameData.data, self.processorBytes, frameData.frameDesc.width * frameData.frameDesc.height * 3 / 2);
  }
}

- (void)didProcessFrame:(Byte *)bytes width:(NSInteger)width height:(NSInteger)height format:(TXEFrameFormat)format timeStamp:(UInt64)timeStamp {
  self.processorBytes = bytes;
}

- (void)OnLocalVideoRawSampleBuf:(CMSampleBufferRef)buf result:(CMSampleBufferRef *)ret {
}

#pragma mark - upVideo or downVideo method
/**
 * 连麦回调事件
 */
- (void)upVideoCallback:(NSNotification *)noti {
  NSString *message = (NSString *)noti.object;
  [self commentEvent:@"onUpVideo" code:kFail msg:message];
}

/**
 * 取消连麦
 */
- (void)connectVideoCancel:(NSNotification *)noti {
  NSString *userId = (NSString *)noti.object;
  [[UserViewManager shareInstance] removePlaceholderView:userId];
  [[UserViewManager shareInstance] refreshViews];
}

/**
 *退出房间操作
 */
- (void)onClose {
  [[TILLiveManager getInstance] quitRoom:^{
    [self commentEvent:@"onLeaveRoom" code:kSuccess msg:@"退出房间成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    [self commentEvent:@"onLeaveRoom" code:errId msg:errMsg];
  }];
  [[UserViewManager shareInstance] releaseManager];
  [[NSNotificationCenter defaultCenter] removeObserver:self name:kCancelConnect_Notification object:nil];
  [[NSNotificationCenter defaultCenter] removeObserver:self name:kGroupDelete_Notification object:nil];
}

/**
 * 主播退出直播间
 */
- (void)onGotupDelete:(NSNotification *)noti  {
  [self onClose];
  [self commentEvent:@"onLeaveRoom" code:kSuccess msg:@"主播已经离开房间"];
}

/**
 * 房间失去连接
 */
- (BOOL)onRoomDisconnect:(int)reason {
  __weak typeof(self) ws = self;
  [ws onClose];
  [ws commentEvent:@"onRoomDisconnect" code:kSuccess msg:@"房间失去连接"];
  return YES;
}

#pragma mark - 回放预览界面的代理方法
- (void)previewControllerDidFinish:(RPPreviewViewController *)previewController {
  //用户操作完成后，返回之前的界面
  [previewController dismissViewControllerAnimated:YES completion:nil];
}

//获取当前屏幕显示的viewcontroller
- (UIViewController *)getCurrentVC {
  UIViewController *result = nil;
  UIWindow * window = [[UIApplication sharedApplication] keyWindow];
  if (window.windowLevel != UIWindowLevelNormal) {
    NSArray *windows = [[UIApplication sharedApplication] windows];
    for(UIWindow * tmpWin in windows) {
      if (tmpWin.windowLevel == UIWindowLevelNormal) {
        window = tmpWin;
        break;
      }
    }
  }
  UIView *frontView = [[window subviews] objectAtIndex:0];
  id nextResponder = [frontView nextResponder];
  if ([nextResponder isKindOfClass:[UIViewController class]])
    result = nextResponder;
  else
    result = window.rootViewController;
  return result;
}

#pragma mark - native to js event method
- (NSArray<NSString *> *)supportedEvents {
  return @[@"iLiveEvent"];
}

- (void)commentEvent:(NSString *)type code:(int )code msg:(NSString *)msg {
  NSMutableDictionary *params = @{}.mutableCopy;
  params[kType] = type;
  params[kCode] = [NSString stringWithFormat:@"%d", code];
  params[kMsg] = msg;
  params[kRoomId] = [NSString stringWithFormat:@"%d", [[ILiveConst share] roomId]];
  NSLog(@"返回commentEvent%@", params );
  dispatch_async(dispatch_get_main_queue(), ^{
      [self sendEventWithName:@"iLiveEvent" body:params];
  });
}

// RCT必须的方法体，不可删除，否则所有暴露的RCT_EXPORT_METHOD不在主线程执行
- (dispatch_queue_t)methodQueue {
  return dispatch_get_main_queue();
}
@end

