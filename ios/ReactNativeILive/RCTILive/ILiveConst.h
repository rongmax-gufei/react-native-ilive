//
//  MyILiveRtcEngineKit.h
//
//  Created by ruby on 2017/8/31.
//  Copyright © 2017年 Learnta Inc. All rights reserved.
//

//#import <AgoraRtcEngineKit/AgoraRtcEngineKit.h>
#import <UIKit/UIKit.h>

@interface ILiveConst : NSObject

@property (nonatomic, copy) NSString *appid;
@property (nonatomic, copy) NSString *accountType;
@property (nonatomic, copy) NSString *hostid;
@property (nonatomic, copy) NSString *roomid;
@property (nonatomic, copy) NSString *userRole;
@property (nonatomic, assign) BOOL bCameraOn;
@property (nonatomic, assign) BOOL bMicOn;

+ (instancetype)share;

@end
