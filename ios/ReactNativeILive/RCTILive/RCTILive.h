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

@interface RCTILive : RCTEventEmitter<RCTBridgeModule>

@property (nonatomic, assign) BOOL bCameraOn;
@property (nonatomic, assign) BOOL bMicOn;

@property (nonatomic, strong) UILabel  *noCameraDatatalabel;//对方没有打开相机时的提示
@property (nonatomic, assign) BOOL  isCameraEvent;//noCameraDatatalabel需要延迟显示，isNoCameraEvent用来判断是否收到了camera事件

//business data
@property (nonatomic, assign) ILiveFrameDispatcher *frameDispatcher;
- (void)onClose;
@end
