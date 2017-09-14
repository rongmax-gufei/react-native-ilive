//
//  RCTILive.h
//
//  Created by ruby on 2017/8/31.
//  Copyright © 2017年 Learnta Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

//是否需要直播前预览
#define kIsPreview 1

@interface RCTILive : RCTEventEmitter<RCTBridgeModule> {
  UIView *_rootView;
  NSMutableArray      *_members;          //房间成员
  NSMutableArray      *_upVideoMembers;   //连麦列表
  BOOL _isHost; //自己是不是主播
  RoomOptionType _roomOptionType;
  TCShowLiveListItem *_liveItem;
  NSInteger _count;
}
@property (nonatomic, assign) BOOL bCameraOn;
@property (nonatomic, assign) BOOL bMicOn;

@property (nonatomic, strong) UILabel  *noCameraDatatalabel;//对方没有打开相机时的提示
@property (nonatomic, assign) BOOL  isCameraEvent;//noCameraDatatalabel需要延迟显示，isNoCameraEvent用来判断是否收到了camera事件

@property (nonatomic, strong) TCShowLiveListItem  *liveItem;
@property (nonatomic, strong) NSMutableArray *upVideoMembers;
@property (nonatomic, assign) NSInteger count;
//business data
@property (nonatomic, assign) ILiveFrameDispatcher *frameDispatcher;
- (void)onClose;
@end
