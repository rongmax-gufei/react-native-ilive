//
//  RCTILiveViewManager.m
//
//  Created by ruby on 2017/8/31.
//  Copyright © 2017年 Learnta Inc. All rights reserved.
//

#import "RCTILiveViewManager.h"
#import "RCTILiveVideoView.h"

@implementation RCTILiveViewManager

RCT_EXPORT_MODULE()

RCT_EXPORT_VIEW_PROPERTY(showVideoView, BOOL)

- (UIView *)view {
    return [RCTILiveVideoView new];
}
@end
