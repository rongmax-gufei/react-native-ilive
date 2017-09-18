//
//  RCTILiveVideoView.m
//
//  Created by ruby on 2017/8/31.
//  Copyright © 2017年 Learnta Inc. All rights reserved.
//

#import "RCTILiveVideoView.h"

@interface RCTILiveVideoView ()
@end

@implementation RCTILiveVideoView

static RCTILiveVideoView *instance = nil;
+ (RCTILiveVideoView *)getInstance {
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    instance = [[RCTILiveVideoView alloc] init];
    // 默认不隐藏
    instance.hidden = NO;
    instance.tag = 100117;
  });
  return instance;
}

- (instancetype)init {
  if (self == [super init]) {
  }
  return self;
}

- (void)setShowVideoView:(BOOL)showVideoView {
  [[TILLiveManager getInstance] setAVRootView:self];
}

@end
