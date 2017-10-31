//
//  LiveViewController+ImListener.m
//  TILLiveSDKShow
//
//  Created by wilderliao on 17/1/7.
//  Copyright © 2017年 Tencent. All rights reserved.
//

#import "RCTILive+ImListener.h"

@implementation RCTILive (ImListener)

- (void)onCustomMessage:(ILVLiveCustomMessage *)msg {
  if (msg.type == ILVLIVE_IMTYPE_GROUP) {
    //非当前直播间的群消息，不处理
    if (![[[ILiveRoomManager getInstance] getIMGroupId] isEqualToString:msg.recvId]) {
      return;
    }
  }
  int cmd = msg.cmd;
  if (msg.type == ILVLIVE_IMTYPE_C2C) {
        switch (cmd) {
            case AVIMCMD_Multi_Host_Invite: {
//         kSxbRole_InteractHD（高清）/kSxbRole_InteractLD（流畅）
             [self upToVideo:nil roleName:kSxbRole_InteractSD];//默认标清
              NSString *message = [NSString stringWithFormat:@"收到%@视频邀请",msg.sendId];
              NSLog(@"%@", message);
              [[NSNotificationCenter defaultCenter] postNotificationName:kUserUpVideo_Notification object:message];
//          [self rejectToVideo:nil];//收到上麦一方有权拒绝视频（暂无此需求）
            }
                break;
            case AVIMCMD_Multi_Interact_Refuse:  {
              NSString *message = [NSString stringWithFormat:@"%@拒绝了你的邀请",msg.sendId];
              [[UserViewManager shareInstance] removePlaceholderView:msg.sendId];
              NSLog(@"%@", message);
              [[NSNotificationCenter defaultCenter] postNotificationName:kUserUpVideo_Notification object:message];
            }
                break;
            case AVIMCMD_Multi_Host_CancelInvite:  {
               NSString *message = [NSString stringWithFormat:@"%@已取消视频邀请",msg.sendId];
                  [[UserViewManager shareInstance] removePlaceholderView:msg.sendId];
                  NSLog(@"%@", message);
                  [[NSNotificationCenter defaultCenter] postNotificationName:kUserUpVideo_Notification object:message];
            }
                break;
            default:
                break;
        }
    } else if (msg.type == ILVLIVE_IMTYPE_GROUP) {
        switch (cmd) {
            case AVIMCMD_Praise:
                [[NSNotificationCenter defaultCenter] postNotificationName:kUserParise_Notification object:nil];
                break;
            case AVIMCMD_Multi_CancelInteract:
                if ([self isSendToSelf:msg]) {
                    [self downToVideo:nil];
                }
                break;
          case AVIMCMD_EnterLive: {
                NSLog(@"%@", msg);
                }
                break;
            case AVIMCMD_ExitLive:
                [[NSNotificationCenter defaultCenter] postNotificationName:kGroupDelete_Notification object:nil];
                break;
            default:
                break;
        }
    }
}

- (BOOL)isSendToSelf:(ILVLiveCustomMessage *)msg {
    NSString *recvId = [[NSString alloc] initWithData:msg.data encoding:NSUTF8StringEncoding];
    NSString *selfId = [[ILiveLoginManager getInstance] getLoginId];
    return [recvId isEqualToString:selfId];
}

//上麦
- (void)upToVideo:(id)sender roleName:(NSString *)role {
  ILVLiveCustomMessage *msg = [[ILVLiveCustomMessage alloc] init];
  msg.type = ILVLIVE_IMTYPE_C2C;
  msg.cmd = (ILVLiveIMCmd)AVIMCMD_Multi_Interact_Join;
  msg.recvId = [[ILiveConst share] hostId];
  
  [[TILLiveManager getInstance] sendCustomMessage:msg succ:^{
        ILiveRoomManager *roomManager = [ILiveRoomManager getInstance];
        [roomManager changeRole:role succ:^{
            NSLog(@"changeRole");
            [roomManager enableCamera:CameraPosFront enable:YES succ:^{
                NSLog(@"enable camera YES");
                [roomManager enableMic:YES succ:^{
                    [[NSNotificationCenter defaultCenter] postNotificationName:kUserUpVideo_Notification object:role];
                } failed:^(NSString *module, int errId, NSString *errMsg) {
                    NSLog(@"enable mic fail");
                }];
            } failed:^(NSString *module, int errId, NSString *errMsg) {
                NSLog(@"enable camera fail");
            }];
        } failed:^(NSString *module, int errId, NSString *errMsg) {
            NSLog(@"change role fail");
        }];
    } failed:^(NSString *module, int errId, NSString *errMsg) {
        NSLog(@"fail");
    }];
}

//下麦
- (void)downToVideo:(id)sender {
    ILVLiveCustomMessage *msg = [[ILVLiveCustomMessage alloc] init];
    msg.type = ILVLIVE_IMTYPE_GROUP;
    msg.cmd = (ILVLiveIMCmd)AVIMCMD_Multi_CancelInteract;
    msg.recvId = [[ILiveRoomManager getInstance] getIMGroupId];
    ILiveRoomManager *manager = [ILiveRoomManager getInstance];
    [[TILLiveManager getInstance] sendCustomMessage:msg succ:^{
        [manager changeRole:kSxbRole_GuestHD succ:^ {
            NSLog(@"down to video: change role succ");
            cameraPos pos = [[ILiveRoomManager getInstance] getCurCameraPos];
            [manager enableCamera:pos enable:NO succ:^{
                NSLog(@"down to video: disable camera succ");
                [manager enableMic:NO succ:^{
                    NSLog(@"down to video: disable mic succ");
                    [[NSNotificationCenter defaultCenter] postNotificationName:kUserDownVideo_Notification object:nil];
                } failed:^(NSString *module, int errId, NSString *errMsg) {
                    NSLog(@"down to video: disable mic fail: module=%@,errId=%d,errMsg=%@",module, errId, errMsg);
                }];
            } failed:^(NSString *module, int errId, NSString *errMsg) {
                NSLog(@"down to video: disable camera fail: module=%@,errId=%d,errMsg=%@",module, errId, errMsg);
            }];
        } failed:^(NSString *module, int errId, NSString *errMsg) {
            NSLog(@"down to video: change role fail: module=%@,errId=%d,errMsg=%@",module, errId, errMsg);
        }];
    } failed:^(NSString *module, int errId, NSString *errMsg) {
        NSLog(@"down to video: change auth fail: module=%@,errId=%d,errMsg=%@",module, errId, errMsg);
    }];
}

//拒绝上麦
- (void)rejectToVideo:(id)sender
{
    ILVLiveCustomMessage *msg = [[ILVLiveCustomMessage alloc] init];
    msg.cmd = (ILVLiveIMCmd)AVIMCMD_Multi_Interact_Refuse;
    msg.type = ILVLIVE_IMTYPE_C2C;
    msg.recvId = [[ILiveConst share] hostId];
  
    [[TILLiveManager getInstance] sendCustomMessage:msg succ:^{
        NSLog(@"refuse video succ");
    } failed:^(NSString *module, int errId, NSString *errMsg) {
        NSLog(@"refuse video  fail.module=%@,errid=%d,errmsg=%@",module,errId,errMsg);
    }];
}

@end
