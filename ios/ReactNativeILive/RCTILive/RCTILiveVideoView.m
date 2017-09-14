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

static RCTILiveVideoView *instance = nil;

+ (RCTILiveVideoView *)getInstance{
  @synchronized(self) {
    if (instance == nil){
      instance = [[RCTILiveVideoView alloc]init];
    }
  }
  return instance;
}

- (instancetype) init {
  if (self == [super init]) {
  }
  return self;
}

- (void)setShowVideoView:(BOOL)showVideoView {
  if (showVideoView) {
    self.rootView = self;
    [[TILLiveManager getInstance] setAVRootView:self];
  }
}

@end
