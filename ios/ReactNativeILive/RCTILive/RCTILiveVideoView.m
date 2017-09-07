//
//  RCTAgoraVideoView.m
//
//  Created by ruby on 2017/8/31.
//  Copyright © 2017年 Learnta Inc. All rights reserved.
//

#import "RCTILiveVideoView.h"

@interface RCTILiveVideoView ()
@end

@implementation RCTILiveVideoView

- (instancetype) init {
    if (self == [super init]) {
    }
    return self;
}

- (void)setShowVideoView:(BOOL)showVideoView {
    if (showVideoView) {
      [[TILLiveManager getInstance] setAVRootView:self];
    }
}

@end
