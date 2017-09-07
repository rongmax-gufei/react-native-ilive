//
//  MyILiveRtcEngineKit.h
//
//  Created by ruby on 2017/8/31.
//  Copyright © 2017年 Learnta Inc. All rights reserved.
//

//#import <AgoraRtcEngineKit/AgoraRtcEngineKit.h>
#import <UIKit/UIKit.h>

@interface ILiveConst : NSObject

@property (nonatomic, copy) NSString *sdkAppid;
@property (nonatomic, copy) NSString *sdkAccountType;
@property (nonatomic, copy) NSString *hostId;
@property (nonatomic, copy) NSString *roomId;
@property (nonatomic, copy) NSString *userRole;
@property (nonatomic, assign) BOOL bCameraOn;
@property (nonatomic, assign) BOOL bMicOn;

+ (instancetype)share;

@end
