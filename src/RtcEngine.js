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
    // 进入房间
    iLiveJoinChannle() {
        ILive.startEnterRoom();
    },
    // 离开房间
    iLiveLeaveChannle() {
        ILive.startExitRoom();
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
