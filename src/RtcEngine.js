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
    iLiveLogin(id, sig) {
        ILive.iLiveLogin(id, sig);
    },
    iLiveJoinChannle(){
        console.log("iLiveJoinChannle>>>>>>");
        ILive.startEnterRoom();
    },
    iLiveLeaveChannle(){
        console.log("iLiveLeaveChannle>>>>>>");
        ILive.startExitRoom();
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
