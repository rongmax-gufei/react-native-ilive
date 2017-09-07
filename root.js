/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    TouchableOpacity,
    Image,
} from 'react-native';

import {RtcEngine, ILiveView} from './src/index';

export default class ReactNativeILive extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isLoginSuccess: false,
            isJoinSuccess: false,
            bMicOn: true,
            userRole: 1
        };
    }

    // 自己创建直播间 hostId=自己的id,roomNum=自己的房间号,userRole=1
    // 加入别人的房间 hostId=主播的id,roomNum=主播的房间号,userRole=0
    componentWillMount() {
        //初始化iLive
        const options = {
            appid: '1400027849',
            accountType: '11656',
            hostId: 'learnta01',//test0258//63072
            roomNum: '63072',
            userRole: '1'
        };
        RtcEngine.init(options);
    }

    componentDidMount() {
        // 先登录腾讯的TLS系统,use id&&sig
        RtcEngine.iLiveLogin('learnta01', 'eJxlj1FrwjAYRd-7K0JfHeNLamor*JCWbsqcQyeKTyVr0hrbxi5GcY79921VWGH39Rzu5X46CCF3OX2951m2P2qb2o9GumiIXHDv-mDTKJFym3pG-IPy3CgjU55baVqIKaUEoOsoIbVVuboZleRGWw64oxxEmbY7147*TwEZBP2wq6iihc-JPJ5Ep-wNs22xrh4f7CJcDWJ1Ee961hASllmPLYrC7Hrr3XzlM8Wsp7mZPtHsxZIZw8toXAfHOAmToN56EE-OwWZMywqiDRuNOpNW1fJ2Cjzq*wBBh56kOai9bgUCmGLiwW9c58v5Blc5XRk_');
        // RtcEngine.iLiveLogin('ruby', 'eJxlj0tPg0AUhff8CsLamJnh2SYuFIkU0dJSWuOGIHOhI*HhMLVQ439XsYmTeLffd3LO-VBUVdU2YXyZ5Xl7aEQqxg40da5qSLv4g13HaJqJVOf0H4ShYxzSrBDAJ4hN0yQIyQ6j0AhWsLPBDy*jRHtapVPFb9z4zhLbMWaywsoJPniJu1i5fe5u9pT621V-8rtwGR4HBHEdjgP1o92SWF7VR-r9-nVdLkqKb*3Ho3XD34qKr*PEAD*IbGzVJxeCp*cg2e4o96rru6G9kioFq*H8D9JnDsKOPOgdeM-aZhIIwiYmOvo5TflUvgApXF2f')
        //所有的原生通知统一管理
        RtcEngine.eventEmitter({
            onLoginTLS: (data) => {
                var result = data.code === '1000';
                this.setState({isLoginSuccess: result});
                // TLS登录成功
                if (result) {
                    console.log("登录腾讯TLS系统成功 iLiveJoinChannle>>>>>>");
                    RtcEngine.iLiveJoinChannle();
                }
            },
            onLogoutTLS: (data) => {
                console.log(data);
            },
            onCreateRoom: (data) => {
                console.log(data);
                // 创建房间
                var result = (data.code === '1000' || data.code === '1003');
                console.log("创建房间>>>>>>:" + result);
                this.setState({
                    isJoinSuccess: result,
                    userRole: 1,
                });
            },
            onJoinRoom: (data) => {
                console.log(data);
                // 加入房间1000不在房间内，1003已经在房间里面
                var result = (data.code === '1000' || data.code === '1003');
                console.log("加入房间>>>>>>:" + result);
                this.setState({
                    isJoinSuccess: result,
                    userRole: 0,
                });
            },
            onExitRoom: (data) => {
                console.log(data);
            },
            onRoomDisconnect: (data) => {
                console.log(data);
            },
            onSwitchCamera: (data) => {
                console.log(data);
            },
            onToggleCamera: (data) => {
                console.log(data);
            },
            onToggleMic: (data) => {
                console.log(data);
                var result = data.result === 'true';
                this.setState({
                    bMicOn: result
                });
            },
            onError: (data) => {
                console.log(data);
                // 错误!
            }
        })
    }

    componentWillUnmount() {
        RtcEngine.startExitRoom();
        RtcEngine.removeEmitter()
    }

    handlerCancel = () => {
        RtcEngine.startExitRoom();
    };

    handlerSwitchCamera = () => {
        RtcEngine.switchCamera();
    };

    handlerToggleMic = () => {
        RtcEngine.toggleMic();
    };

    render() {
        const {bMicOn, isJoinSuccess, userRole} = this.state;
            return (
                   <ILiveView style={styles.localView} showVideoView={true}/>
            );
    }
}

const VideoOperateButton = ({onPress, source, style, imgStyle = {width: 50, height: 50}}) => {
    return (
        <TouchableOpacity
            style={style}
            onPress={onPress}
            activeOpacity={.7}
        >
            <Image
                style={imgStyle}
                source={source}
            />
        </TouchableOpacity>
    )
};

const styles = StyleSheet.create({
    localView: {
        flex: 1,
        backgroundColor: '#F4F4F4'
    }
});
