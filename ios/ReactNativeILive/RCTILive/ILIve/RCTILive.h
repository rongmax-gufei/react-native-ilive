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
// 美颜
#import "TXCVideoPreprocessor.h"

@interface RCTILive : RCTEventEmitter<RCTBridgeModule> {
  BOOL _isHost; //自己是不是主播
}

@property (nonatomic, strong) UILabel  *noCameraDatatalabel;//对方没有打开相机时的提示
@property (nonatomic, assign) BOOL  isCameraEvent;//noCameraDatatalabel需要延迟显示，isNoCameraEvent用来判断是否收到了camera事件

@property (nonatomic, strong) NSMutableArray *upVideoMembers;
@property (nonatomic, assign) NSInteger count;
//business data
@property (nonatomic, assign) NSInteger videoCount;
@property (nonatomic, assign) ILiveFrameDispatcher *frameDispatcher;

//美颜
@property (nonatomic, strong) TXCVideoPreprocessor *preProcessor;
@property (nonatomic, assign) Byte  *processorBytes;
- (void)onClose;
@end
