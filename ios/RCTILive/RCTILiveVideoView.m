//
//  RCTAgoraVideoView.m
//
//  Created by ruby on 2017/8/31.
//  Copyright © 2017年 Learnta Inc. All rights reserved.
//

#import "RCTILiveVideoView.h"

@interface RCTILiveVideoView ()<ILVLiveAVListener>
@end

@implementation RCTILiveVideoView

- (instancetype) init {
    if (self == [super init]) {
    }
    return self;
}

- (void)setShowVideoView:(BOOL)showVideoView {
    NSLog(@"setShowVideoView");
    if (showVideoView) {
      NSLog(@"setShowVideoView true");
      TILLiveManager *manager = [TILLiveManager getInstance];
      [manager setAVListener:self];
      UIView *renderView = [[UIView alloc] init];
      [manager setAVRootView:renderView];
    }
}

@end
