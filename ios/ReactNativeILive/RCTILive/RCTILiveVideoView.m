//
//  RCTILiveVideoView.m
//
//  Created by ruby on 2017/8/31.
//  Copyright © 2017年 Learnta Inc. All rights reserved.
//

#import "RCTILiveVideoView.h"

@implementation RCTILiveVideoView

- (instancetype)init {
  if (self == [super init]) {
  }
  return self;
}

- (void)setShowVideoView {
  [[TILLiveManager getInstance] setAVRootView:self];
}

@end
