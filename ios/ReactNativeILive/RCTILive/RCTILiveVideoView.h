//
//  RCTILiveVideoView.h
//
//  Created by ruby on 2017/8/31.
//  Copyright © 2017年 Learnta Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ILiveConst.h"

@interface RCTILiveVideoView : UIView

/**
 * 获取单例
 * @return 单例
 */
+ (instancetype)getInstance;

@property (nonatomic) BOOL showVideoView;
@property (nonatomic, strong) UIView  *rootView;
@end
