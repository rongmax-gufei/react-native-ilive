/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

#import "AppDelegate.h"
#import <React/RCTBundleURLProvider.h>
#import <React/RCTRootView.h>

@implementation AppDelegate

+ (instancetype)sharedAppDelegate {
  return (AppDelegate *)[UIApplication sharedApplication].delegate;
}

+ (UIAlertController *)showAlert:(UIViewController *)rootVC title:(NSString *)title message:(NSString *)msg okTitle:(NSString *)okTitle cancelTitle:(NSString *)cancelTitle ok:(ActionHandle)succ cancel:(ActionHandle)fail {
  UIAlertController *alert = [UIAlertController alertControllerWithTitle:title message:msg preferredStyle:UIAlertControllerStyleAlert];
  if (cancelTitle) {
    [alert addAction:[UIAlertAction actionWithTitle:cancelTitle style:UIAlertActionStyleCancel handler:fail]];
  }
  if (okTitle) {
    [alert addAction:[UIAlertAction actionWithTitle:okTitle style:UIAlertActionStyleDefault handler:succ]];
  }
  [rootVC presentViewController:alert animated:YES completion:nil];
  return alert;
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  TIMManager *manager = [[ILiveSDK getInstance] getTIMManager];
  NSNumber *evn = [[NSUserDefaults standardUserDefaults] objectForKey:kEnvParam];
  [manager setEnv:[evn intValue]];//环境
  NSNumber *logLevel = [[NSUserDefaults standardUserDefaults] objectForKey:kLogLevel];//log 等级(默认debug)
  if (!logLevel) {
    [[NSUserDefaults standardUserDefaults] setObject:@(TIM_LOG_DEBUG) forKey:kLogLevel];
    logLevel = @(TIM_LOG_DEBUG);
  }
  [manager initLogSettings:YES logPath:[manager getLogPath]];
  [manager setLogLevel:(TIMLogLevel)[logLevel integerValue]];
  
  NSURL *jsCodeLocation;

  jsCodeLocation = [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index.ios" fallbackResource:nil];
 
  RCTRootView *rootView = [[RCTRootView alloc] initWithBundleURL:jsCodeLocation
                                                      moduleName:@"ReactNativeILive"
                                               initialProperties:nil
                                                   launchOptions:launchOptions];
  rootView.backgroundColor = [[UIColor alloc] initWithRed:1.0f green:1.0f blue:1.0f alpha:1];

  self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
  UIViewController *rootViewController = [UIViewController new];
  rootViewController.view = rootView;
  self.window.rootViewController = rootViewController;
  [self.window makeKeyAndVisible];
  return YES;
}

@end
