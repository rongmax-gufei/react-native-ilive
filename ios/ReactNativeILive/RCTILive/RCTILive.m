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

#import <React/RCTEventDispatcher.h>
#import <React/RCTBridge.h>
#import <React/RCTUIManager.h>
#import <React/RCTView.h>

@interface RCTILive ()<QAVLocalVideoDelegate, ILiveRoomDisconnectListener>
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
  // 注册全局消息回调（主播主动退出房间、取消连麦）
  [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onGotupDelete:) name:kGroupDelete_Notification object:nil];
  [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(connectVideoCancel:) name:kCancelConnect_Notification object:nil];
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
RCT_EXPORT_METHOD(createChannel:(NSString *)hostId roomId:(int)roomId quality:(NSString *)quality) {
  [ILiveConst share].hostId = hostId;
  [ILiveConst share].roomId = roomId;
  [ILiveConst share].controlRole = quality;
  _isHost = true;
  [self createRoom: quality];
}

/**
 * 加入房间
 *
 * @param hostId      主播ID
 * @param roomId     房间号
 * @param userRole   角色（主播二次进入直播间or观众进入直播间）
 * @param quality      画质，清晰"Guest"、流畅"Guest2"
 */
RCT_EXPORT_METHOD(joinChannel:(NSString *)hostId roomId:(int)roomId userRole:(int)userRole quality:(NSString *)quality) {
  [ILiveConst share].hostId = hostId;
  [ILiveConst share].roomId = roomId;
  [ILiveConst share].userRole = userRole;
  [ILiveConst share].controlRole = quality;
  _isHost = false;
  [self joinRoom:quality];
}

/**
 * 退出房间
 * 主播退出房间时，则发通知给群主成员
 */
RCT_EXPORT_METHOD(leaveChannel) {
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
   [AlertHelp alertWith:@"温馨提示" message:message cancelBtn:@"知道了" alertStyle:UIAlertControllerStyleAlert cancelAction:nil];
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
  TILLiveRoomOption *option = [TILLiveRoomOption defaultGuestLiveOption];
  option.controlRole = quality;
  [[TILLiveManager getInstance] joinRoom:[[ILiveConst share] roomId] option:option succ:^{
      NSLog(@"加入房间成功");
      [self commentEvent:@"onJoinRoom" code:kSuccess msg:@"加入房间成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
      NSLog(@"加入房间失败");
      [self commentEvent:@"onJoinRoom" code:errId msg:errMsg];
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
}

- (void)OnLocalVideoRawSampleBuf:(CMSampleBufferRef)buf result:(CMSampleBufferRef *)ret {
}

#pragma mark - upVideo or downVideo method
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
    [self commentEvent:@"onExitRoom" code:kSuccess msg:@"退出房间成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    [self commentEvent:@"onExitRoom" code:errId msg:errMsg];
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

