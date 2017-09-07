//
//  LiveViewController+AVListener.h
//  TILLiveSDKShow
//
//  Created by wilderliao on 17/1/7.
//  Copyright © 2017年 Tencent. All rights reserved.
//

#import "RCTILive.h"

@interface MyTapGesture : UITapGestureRecognizer

@property (nonatomic, copy) NSString *codeId;
@end

@interface RCTILive (AVListener)<ILVLiveAVListener>

@end
