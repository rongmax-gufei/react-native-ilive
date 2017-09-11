//
//  RCTILive.m
//
//  Created by ruby on 2017/8/31.
//  Copyright © 2017年 Learnta Inc. All rights reserved.
//
#import "RCTILive.h"
#import "ILiveConst.h"
#import "RCTILive+AVListener.h"

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

// 托管模式登录
RCT_EXPORT_METHOD(iLiveLogin:(NSString *)id sig:(NSString *)sig) {
    [[ILiveLoginManager getInstance] iLiveLogin:id sig:sig succ:^{
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

// 添加AVListener，此方法必须在rn的willAmount方法中执行，render()之前执行
RCT_EXPORT_METHOD(doAVListener) {
  TILLiveManager *manager = [TILLiveManager getInstance];
  [manager setAVListener:self];
}

// 进入房间
RCT_EXPORT_METHOD(joinChannel:(NSString *)hostId roomId:(int)roomId userRole:(int)userRole) {
    [ILiveConst share].hostId = hostId;
    [ILiveConst share].roomId = roomId;
    [ILiveConst share].userRole = userRole;
    BOOL isHost = userRole == 1;
    RoomOptionType _roomOptionType = isHost ? RoomOptionType_CrateRoom:RoomOptionType_JoinRoom;
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
  [ws onClose];
}

//切换前置/后置摄像头
RCT_EXPORT_METHOD(switchCamera) {
  [[ILiveRoomManager getInstance] switchCamera:^{
      [self commentEvent:@"switchCamera" code:kSuccess msg:@"切换摄像头成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
      [self commentEvent:@"switchCamera" code:errId msg:errMsg];
  }];
}

// 打开/关闭摄像头
RCT_EXPORT_METHOD(toggleCamera) {
  _bCameraOn = !_bCameraOn;
  [[ILiveRoomManager getInstance] enableCamera:CameraPosFront enable:_bCameraOn succ:^{
      [self commentEvent:@"toogleCamera" code:kSuccess msg:@"打开/关闭摄像头成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
      [self commentEvent:@"toogleCamera" code:errId msg:errMsg];
  }];
}

// 打开/关闭声麦
RCT_EXPORT_METHOD(toggleMic) {
  _bMicOn = !_bMicOn;
  [[ILiveRoomManager getInstance] enableMic:_bMicOn succ:^{
      [self commentEvent:@"toogleMic" code:kSuccess msg:@"打开/关闭声麦成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
      [self commentEvent:@"toogleMic" code:errId msg:errMsg];
  }];
}

//销毁引擎实例
RCT_EXPORT_METHOD(destroy) {
  __weak typeof(self) ws = self;
  [ws onClose];
}

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
        [self commentEvent:@"onCreateRoom" code:kSuccess msg:@"创建房间成功"];
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

- (void)onClose {
  [[TILLiveManager getInstance] quitRoom:^{
    [self commentEvent:@"startExitRoom" code:kSuccess msg:@"退出房间成功"];
  } failed:^(NSString *module, int errId, NSString *errMsg) {
    [self commentEvent:@"startExitRoom" code:errId msg:errMsg];
  }];
  [[UserViewManager shareInstance] releaseManager];
}

- (BOOL)onRoomDisconnect:(int)reason {
    __weak typeof(self) ws = self;
    [ws onClose];
    [ws commentEvent:@"onRoomDisconnect" code:kSuccess msg:@"房间失去连接"];
    return YES;
}

- (NSArray<NSString *> *)supportedEvents {
  return @[@"iLiveEvent"];
}

- (void)commentEvent:(NSString *)type code:(int )code msg:(NSString *)msg {
    NSMutableDictionary *params = @{}.mutableCopy;
    params[kType] = type;
    params[kCode] = [NSString stringWithFormat:@"%d", code];
    params[kMsg] = msg;
    NSLog(@"返回commentEvent%@", params );
    dispatch_async(dispatch_get_main_queue(), ^{
        [self sendEventWithName:@"iLiveEvent" body:params];
    });
}

- (dispatch_queue_t)methodQueue {
  return dispatch_get_main_queue();
}

//销毁引擎实例
- (void)dealloc {
}

//导出常量
- (NSDictionary *)constantsToExport {
  return @{};
}
@end

