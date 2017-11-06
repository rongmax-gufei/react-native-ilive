//
//  RCTILive+Audio.m
//  ReactNativeILive
//
//  Created by Apple on 2017/9/19.
//  Copyright © 2017年 Facebook. All rights reserved.
//

#import "RCTILive+Audio.h"

@implementation RCTILive (Audio)
- (void)initAudio {
  [[[ILiveSDK getInstance] getAVContext].audioCtrl registerAudioDataCallback:QAVAudioDataSource_VoiceDispose];
}
@end
