/**
 * Created by ruby on 2017/8/28.
 * @description 腾讯互动直播引擎
 * @copyright learnta inc.
 */
import {
    NativeModules,
    findNodeHandle,
    NativeEventEmitter,
    NativeAppEventEmitter
} from 'react-native';

const { ILive } = NativeModules;
const iLiveEmitter = new NativeEventEmitter(ILive);

export default {
    ...ILive,
    init(options = {}) {
        this.listener && this.listener.remove();
        ILive.init(options);
    },
    // 登录腾讯互动直播TLS系统
    iLiveLogin(id, sig) {
        ILive.iLiveLogin(id, sig);
    },
    // 登出腾讯互动直播TLS系统
    iLiveLogout() {
        ILive.iLiveLogout();
    },
    // 创建房间,画质"HD"、"SD"、"LD"
    iLiveCreateRoom(hostId, roomId, quality) {
        ILive.createRoom(hostId, roomId, quality);
    },
    // 加入房间,画质，清晰"Guest"、流畅"Guest2"
    iLiveJoinRoom(hostId, roomId, userRole, quality) {
        ILive.joinRoom(hostId, roomId, userRole, quality);
    },
    // 离开房间
    iLiveLeaveRoom() {
        ILive.leaveRoom();
    },
    // 添加互动直播监听器
    iLiveSetAVListener() {
        ILive.doAVListener();
    },
    // 上麦
    iLiveUpVideo(uid) {
        ILive.upVideo(uid);
    },
    // 下麦
    iLiveDownVideo(uid) {
        ILive.downVideo(uid);
    },
    // 开始录视频(腾讯云服务提供，仅能录制视频流，不能录制节目操作),recordType,0：录制视频，1：录制纯音频
    iLiveStartVideoRecord(fileName, recordType) {
        ILive.startVideoRecord(fileName, recordType);
    },
    // 停止录视频
    iLiveStopVideoRecord() {
        ILive.stopVideoRecord();
    },
    // 开始录屏幕（iOS官方ReplayKit提供）
    iLiveStartScreenRecord() {
        ILive.startScreenRecord();
    },
    // 停止录屏幕
    iLiveStopScreenRecord() {
        ILive.stopScreenRecord();
    },
    // 切换摄像头
    iLiveSwitchCamera() {
        ILive.switchCamera();
    },
    // 打开or关闭摄像头
    iLiveToggleCamera() {
        ILive.toggleCamera();
    },
    // 打卡or关闭声麦
    iLiveToggleMic() {
        ILive.toggleMic();
    },
    eventEmitter(fnConf) {
        //there are no `removeListener` for NativeAppEventEmitter & DeviceEventEmitter
        this.listener && this.listener.remove();
        // this.listener = NativeAppEventEmitter.addListener('iLiveEvent', event => {
        //     fnConf[event['type']] && fnConf[event['type']](event);
        // });
        this.listener = iLiveEmitter.addListener(
          'iLiveEvent',
          (event) => {
            fnConf[event['type']] && fnConf[event['type']](event);
          }
        );
    },
    removeEmitter() {
        this.listener && this.listener.remove();
    }
};
