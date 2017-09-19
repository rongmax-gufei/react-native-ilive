//
//  MyILiveRtcEngineKit.h
//
//  Created by ruby on 2017/8/31.
//  Copyright © 2017年 Learnta Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ILiveConst : NSObject

@property (nonatomic, copy) NSString *sdkAppid;
@property (nonatomic, copy) NSString *sdkAccountType;
@property (nonatomic, copy) NSString *hostId;
@property (nonatomic, assign) int roomId;
@property (nonatomic, assign) int userRole;
/** 进房间的成员所属角色名，web端音视频参数配置工具所设置的角色名 */
@property (nonatomic, copy) NSString *controlRole;

+ (instancetype)share;

@end
