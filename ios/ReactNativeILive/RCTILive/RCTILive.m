//
//  RCTILive.m
//
//  Created by ruby on 2017/8/31.
//  Copyright © 2017年 Learnta Inc. All rights reserved.
//
#import "RCTILive.h"
#import "RCTILive+AVListener.h"
#import "RCTILive+ImListener.h"
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
 *  初始化AgoraKit
 *
 *  @param appid           sdk appid
 *  @param accountType sdk type
 *  @param hostid          主播id
 *  @param roomid         房间号
 *  @param userRole       创建角色
 *  @return 0 when executed successfully. return negative value if failed.
 */
RCT_EXPORT_METHOD(init:(NSDictionary *)options) {
  [ILiveConst share].sdkAppid = options[@"appid"];
  [ILiveConst share].sdkAccountType = options[@"accountType"];
  // 初始化iLive模块
  [[ILiveSDK getInstance] initSdk:[[ILiveConst share].sdkAppid intValue] accountType:[[ILiveConst share].sdkAccountType intValue]];
}

// 独立模式登录
RCT_EXPORT_METHOD(iLiveLogin:(NSString *)uid sig:(NSString *)sig) {
  [[ILiveLoginManager getInstance] iLiveLogin:uid sig:sig succ:^{
      NSLog(@"iLiveLogin 腾讯登录成功");
      [self commentEvent:@"onLoginTLS" code:kSuccess msg:@"登录腾讯TLS系统成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
      NSLog(@"iLiveLogin 腾讯登录失败");
      [self commentEvent:@"onLoginTLS" code:errId msg:errMsg];
  }];
}

// 登出
RCT_EXPORT_METHOD(iLiveLogout) {
  [[ILiveLoginManager getInstance] iLiveLogout:^{
    [self commentEvent:@"onLogoutTLS" code:kSuccess msg:@"登出腾讯TLS系统成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
     [self commentEvent:@"onLogoutTLS" code:errId msg:errMsg];
  }];
}

// !!!添加AVListener，此方法必须在rn的willAmount方法中执行，render()之前执行
RCT_EXPORT_METHOD(doAVListener) {
  TILLiveManager *manager = [TILLiveManager getInstance];
  [manager setAVListener:self];
  [manager setIMListener:self];
}

// 进入房间
RCT_EXPORT_METHOD(joinChannel:(NSString *)hostId roomId:(int)roomId userRole:(int)userRole) {
  _rootView = [RCTILiveVideoView getInstance] ;
  //添加监听
  [self addObserver];
  [ILiveConst share].hostId = hostId;
  [ILiveConst share].roomId = roomId;
  [ILiveConst share].userRole = userRole;
  _isHost= userRole == 1;
  _roomOptionType = _isHost ? RoomOptionType_CrateRoom:RoomOptionType_JoinRoom;
  switch (_roomOptionType) {
      case RoomOptionType_CrateRoom:
              NSLog(@"开始创建房间");
              [self createRoom];
              break;
      case RoomOptionType_JoinRoom:
              NSLog(@"开始加入房间");
              [self joinRoom];
              break;
  }
}

// 退出房间
RCT_EXPORT_METHOD(leaveChannel) {
  __weak typeof(self) ws = self;
  if (_isHost) {
    //主播退群时，发送退群消息
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

// 上麦
RCT_EXPORT_METHOD(upVideo:(NSString *)hostId) {
  // 先判断当前存在几路画面，超过规定线路，则拒绝连接
  if ([UserViewManager shareInstance].total >= kMaxUserViewCount) {
    NSString *message = [NSString stringWithFormat:@"连麦画面不能超过%d路,可以先取消一路连麦",kMaxUserViewCount+1];
   [AlertHelp alertWith:@"提示" message:message cancelBtn:@"好吧" alertStyle:UIAlertControllerStyleAlert cancelAction:nil];
    return;
  }
  ILVLiveCustomMessage *video = [[ILVLiveCustomMessage alloc] init];
  video.recvId = hostId;
  video.type = ILVLIVE_IMTYPE_C2C;
  video.cmd = (ILVLiveIMCmd)AVIMCMD_Multi_Host_Invite;
  [[TILLiveManager getInstance] sendCustomMessage:video succ:^{
    [self commentEvent:@"onUpVideo" code:kSuccess msg:@"连麦请求已发出"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    NSLog(@"login fail. module=%@,errid=%d,errmsg=%@",module,errId,errMsg);
  }];
  //增加连麦小视图
  LiveCallView *callView = [[UserViewManager shareInstance] addPlaceholderView:hostId];
  ILiveRenderView *mainAvRenderView = [[TILLiveManager getInstance] getAVRenderView:[[ILiveConst share] hostId] srcType:QAVVIDEO_SRC_TYPE_CAMERA];
  [mainAvRenderView.superview addSubview:callView];
//  [_rootView addSubview:callView];
}

// 下麦
RCT_EXPORT_METHOD(downVideo:(NSString *)hostId) {
  ILVLiveCustomMessage *video = [[ILVLiveCustomMessage alloc] init];
  video.recvId = hostId;
  video.type = ILVLIVE_IMTYPE_GROUP;
  video.cmd = (ILVLiveIMCmd)AVIMCMD_Multi_CancelInteract;
  [[TILLiveManager getInstance] sendCustomMessage:video succ:^{
    NSLog(@"下麦请求已发出");
    [self commentEvent:@"onDownVideo" code:kDownVideoReqSuccess msg:@"下麦请求已发出"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    NSLog(@"login fail. module=%@,errid=%d,errmsg=%@",module,errId,errMsg);
  }];
}

//切换前置/后置摄像头
RCT_EXPORT_METHOD(switchCamera) {
  [[ILiveRoomManager getInstance] switchCamera:^{
      [self commentEvent:@"onSwitchCamera" code:kSuccess msg:@"切换摄像头成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
      [self commentEvent:@"onSwitchCamera" code:errId msg:errMsg];
  }];
}

// 打开/关闭摄像头
RCT_EXPORT_METHOD(toggleCamera) {
  _bCameraOn = !_bCameraOn;
  [[ILiveRoomManager getInstance] enableCamera:CameraPosFront enable:_bCameraOn succ:^{
      [self commentEvent:@"onToggleCamera" code:kSuccess msg:@"打开/关闭摄像头成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
      [self commentEvent:@"onToggleCamera" code:errId msg:errMsg];
  }];
}

// 打开/关闭声麦
RCT_EXPORT_METHOD(toggleMic) {
  _bMicOn = !_bMicOn;
  [[ILiveRoomManager getInstance] enableMic:_bMicOn succ:^{
    [self commentEvent:@"onToggleMic" code:kSuccess msg:@"打开/关闭声麦成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    [self commentEvent:@"onToggleMic" code:errId msg:errMsg];
  }];
}

//销毁引擎实例
RCT_EXPORT_METHOD(destroy) {
  __weak typeof(self) ws = self;
  [ws onClose];
}

#pragma mark - addObserver
- (void)addObserver {
  [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onGotupDelete:) name:kGroupDelete_Notification object:nil];
  [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(connectVideoCancel:) name:kCancelConnect_Notification object:nil];
}

#pragma mark - createRoom or joinRoom method
// 创建房间
- (void)createRoom {
  #if kIsPreview
  _frameDispatcher = [[ILiveRoomManager getInstance] getFrameDispatcher];
  [_frameDispatcher startDisplay];
  [self startPreview];
  #endif
  __weak typeof(self) ws = self;
  TILLiveRoomOption *option = [TILLiveRoomOption defaultHostLiveOption];
  option.controlRole = [[ILiveConst share] hostId];
  option.avOption.autoHdAudio = YES;//使用高音质模式，可以传背景音乐
  option.roomDisconnectListener = self;
  option.imOption.imSupport = YES;
  [[TILLiveManager getInstance] createRoom:[[ILiveConst share] roomId] option:option succ:^{
    NSLog(@"创建房间成功");
    [ws initAudio];
    [self commentEvent:@"onCreateRoom" code:kSuccess msg:@""];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    [self commentEvent:@"onCreateRoom" code:errId msg:errMsg];
  }];
}

- (void)initAudio {
  [[[ILiveSDK getInstance] getAVContext].audioCtrl registerAudioDataCallback:QAVAudioDataSource_VoiceDispose];
}

- (void)joinRoom {
  TILLiveRoomOption *option = [TILLiveRoomOption defaultGuestLiveOption];
  option.controlRole = kSxbRole_GuestHD;
  [[TILLiveManager getInstance] joinRoom:[[ILiveConst share] roomId] option:option succ:^{
      NSLog(@"加入房间成功");
      [self commentEvent:@"onJoinRoom" code:kSuccess msg:@"加入房间成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
      NSLog(@"加入房间失败");
      [self commentEvent:@"onJoinRoom" code:errId msg:errMsg];
  }];
}

#pragma mark - local video delegate
//需要预览才设置local delegate
- (void)OnLocalVideoPreview:(QAVVideoFrame *)frameData {
  frameData.identifier = [[ILiveLoginManager getInstance] getLoginId];
  [_frameDispatcher dispatchVideoFrame:frameData];
}

- (void)OnLocalVideoPreProcess:(QAVVideoFrame *)frameData {
}

- (void)OnLocalVideoRawSampleBuf:(CMSampleBufferRef)buf result:(CMSampleBufferRef *)ret {
}

#pragma mark - video preview
//开始预览
- (void)startPreview {
  QAVContext *context = [[ILiveSDK getInstance] getAVContext];
  [context.videoCtrl setLocalVideoDelegate:self];
  [[ILiveRoomManager getInstance] enableCamera:CameraPosFront enable:YES succ:^{
    NSString *loginId = [[ILiveLoginManager getInstance] getLoginId];
    [[TILLiveManager getInstance] addAVRenderView:[UIScreen mainScreen].bounds forIdentifier:loginId srcType:QAVVIDEO_SRC_TYPE_CAMERA];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    NSLog(@"enable camera fail. m=%@,errid=%d,msg=%@",module,errId,errMsg);
  }];
}

#pragma mark - upVideo or downVideo method
// 取消连麦
- (void)connectVideoCancel:(NSNotification *)noti {
  NSString *userId = (NSString *)noti.object;
  [[UserViewManager shareInstance] removePlaceholderView:userId];
  [[UserViewManager shareInstance] refreshViews];
}

// 退出房间操作
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
 *主播退出直播间
 */
- (void)onGotupDelete:(NSNotification *)noti  {
  [self onClose];
  [self commentEvent:@"onLeaveRoom" code:kSuccess msg:@"主播已经离开房间"];
}

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

