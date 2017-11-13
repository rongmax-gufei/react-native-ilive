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
    TouchableHighlight,
    Text,
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
            bCameraOn: true,
            userRole: 1
        };
    }

    componentWillMount() {
        //初始化iLive
        const options = {
            appid: '1400027849',
            accountType: '11656'
        };
        RtcEngine.init(options);
        // 添加AVListener，此方法必须在rn的componentWillMount()方法中执行，render()之前执行
        RtcEngine.iLiveSetAVListener();
    }

    componentDidMount() {
        // 先登录腾讯的TLS系统,use id&&sig
        // RtcEngine.iLiveLogin('learnta01', 'eJxlj1FrwjAYRd-7K0JfHeNLamor*JCWbsqcQyeKTyVr0hrbxi5GcY79921VWGH39Rzu5X46CCF3OX2951m2P2qb2o9GumiIXHDv-mDTKJFym3pG-IPy3CgjU55baVqIKaUEoOsoIbVVuboZleRGWw64oxxEmbY7147*TwEZBP2wq6iihc-JPJ5Ep-wNs22xrh4f7CJcDWJ1Ee961hASllmPLYrC7Hrr3XzlM8Wsp7mZPtHsxZIZw8toXAfHOAmToN56EE-OwWZMywqiDRuNOpNW1fJ2Cjzq*wBBh56kOai9bgUCmGLiwW9c58v5Blc5XRk_');
        RtcEngine.iLiveLogin('ruby', 'eJxlj0tPg0AUhff8CsLamJnh2SYuFIkU0dJSWuOGIHOhI*HhMLVQ439XsYmTeLffd3LO-VBUVdU2YXyZ5Xl7aEQqxg40da5qSLv4g13HaJqJVOf0H4ShYxzSrBDAJ4hN0yQIyQ6j0AhWsLPBDy*jRHtapVPFb9z4zhLbMWaywsoJPniJu1i5fe5u9pT621V-8rtwGR4HBHEdjgP1o92SWF7VR-r9-nVdLkqKb*3Ho3XD34qKr*PEAD*IbGzVJxeCp*cg2e4o96rru6G9kioFq*H8D9JnDsKOPOgdeM-aZhIIwiYmOvo5TflUvgApXF2f')
        // RtcEngine.iLiveLogin('learnta111', 'eJxlj8tOg0AARfd8BWGLMTPA8HBHoBGsQNqipitC59GOtTBOB4Ua-70Vm0ji3Z6Te3O-NF3XjfJxdVtj3HaNqtQgqKHf6QYwbv6gEJxUtapsSf5B2gsuaVUzReUIIULIAmDqcEIbxRm-Gm*0lo2qIYQT50j21Tj0W*JcGizPd4KpwrcjzGbrKF1Ekjmv73M-jEN0wsn88HQ-K1lcgLIzdxvVnfIMic-ewy3bpruwiJNscPmQPjsvy1zYyWogOe43idq766I000XsRmYg8UMxmVT8QK*vALI9Pwi8Cf2g8sjbZhQsABG0bPATQ-vWzhSzXpU_');
        //所有的原生通知统一管理
        RtcEngine.eventEmitter({
            onLoginTLS: (data) => {
                var result = data.code === '1000';
                this.setState({isLoginSuccess: result});
                // TLS登录成功
                if (result) {
                    // 自己创建直播间 hostId=自己的id,roomNum=自己的房间号,清晰度（HD、SD、LD）
                    // 加入别人的房间 hostId=主播的id,roomNum=主播的房间号,userRole=0(1:主播、0:观众),清晰度（Guest、Guest2）
                    // RtcEngine.iLiveCreateRoom('learnta111', 779999, 'HD');
                    RtcEngine.iLiveJoinRoom('learnta111', 779999, 0, 'Guest');
                }
            },
            onLogoutTLS: (data) => {
                console.log(data);
            },
            // 创建房间
            onCreateRoom: (data) => {
                console.log(data);
            },
            // 加入房间
            onJoinRoom: (data) => {
                console.log(data);
            },
            // 离开房间
            onLeaveRoom: (data) => {
                console.log(data);
            },
            // 切换角色
            onChangeRole: (data) => {
                console.log(data);
            },
            // 上麦
            onUpVideo: (data) => {
                console.log(data);
            },
            // 下麦
            onDownVideo: (data) => {
                console.log(data);
            },
            // 开始录制视频
            onStartVideoRecord: (data) => {
                console.log(data);
            },
            // 停止录制视频
            onStopVideoRecord: (data) => {
                console.log(data);
            },
            // 开始录屏幕
            onStartScreenRecord: (data) => {
                console.log(data);
            },
            // 停止录屏幕
            onStopScreenRecord: (data) => {
                console.log(data);
            },
            // 与房间断开连接
            onRoomDisconnect: (data) => {
                console.log(data);
            },
            // 切换摄像头
            onSwitchCamera: (data) => {
                console.log(data);
            },
            // 开关摄像头
            onToggleCamera: (data) => {
                console.log(data);
                var result = data.code === '1000';
                this.setState({
                    bCameraOn: result
                });
            },
            // 开关声麦
            onToggleMic: (data) => {
                console.log(data);
                var result = data.code === '1000';
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

    handerLeavelRoom() {
        console.log('handerLeavelRoom');
        // 通知腾讯TLS服务器
        RtcEngine.iLiveLeaveRoom();
        // 移除监听事件
        RtcEngine.removeEmitter();
    };

    handlerChangeRole(role) {
      console.log('handlerChangeRole');
      RtcEngine.iLiveChangeRole(role);
    };

    handlerUpVideo(uid) {
        console.log('handlerUpVideo');
        RtcEngine.iLiveUpVideo(uid);
    };

    handlerDownVideo(uid) {
        console.log('handlerDownVideo');
        RtcEngine.iLiveDownVideo(uid);
    };

    handlerStartVideoRecord (fileName, recordType) {
        console.log('handlerStartVideoRecord');
        RtcEngine.iLiveStartVideoRecord(fileName, recordType);
    };

    handlerStopVideoRecord() {
        console.log('handlerStopVideoRecord');
        RtcEngine.iLiveStopVideoRecord();
    };

    handlerStartScreenRecord () {
        console.log('handlerStartScreenRecord');
        RtcEngine.iLiveStartScreenRecord();
    };

    handlerStopScreenRecord() {
        console.log('handlerStopScreenRecord');
        RtcEngine.iLiveStopScreenRecord();
    };

    handlerSwitchCamera =() => {
        RtcEngine.iLiveSwitchCamera();
    };

    handlerToggleCamera =(bCameraOn) => {
        RtcEngine.iLiveToggleCamera(bCameraOn);
    };

    handlerToggleMic =(bMicOn) => {
        RtcEngine.iLiveToggleMic(bMicOn);
    };

    render() {
        const {bMicOn, bCameraOn, bUpVideo, isJoinSuccess, userRole} = this.state;
            return (
                <View style={styles.container}>
                    <ILiveView ref="liveView" style={styles.localView} showVideoView={true}/>
                    <View style={styles.absView}>
                        <View>
                            <VideoOperateButton
                                style={{alignSelf: 'center'}}
                                onPress={this.handerLeavelRoom.bind(this)}
                                imgStyle={{width: 60, height: 60}}
                                source={require('./images/icon_exit_live.png')}
                            />
                            <View style={styles.bottomView}>
                                <TouchableHighlight style={styles.btn} underlayColor={'#ff0000'}
                                  onPress={this.handlerUpVideo.bind(this, 'ruby')}>
                                  <Text style={styles.btnText}>连麦</Text>
                                </TouchableHighlight>
                                <TouchableHighlight style={styles.btn} underlayColor={'#ff0000'}
                                  onPress={this.handlerDownVideo.bind(this, 'ruby')}>
                                  <Text style={styles.btnText}>下麦</Text>
                                </TouchableHighlight>
                            </View>
                        </View>
                    </View>
                </View>
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
    container: {
        flex: 1,
        backgroundColor: '#F4F4F4',
    },
    absView: {
        position: 'absolute',
        top: 20,
        left: 0,
        right: 0,
        bottom: 0,
        justifyContent: 'space-between',
    },
    videoView: {
        padding: 5,
        flexWrap: 'wrap',
        flexDirection: 'row',
        zIndex: 100,
    },
    localView: {
        flex: 1
    },
    bottomView: {
        padding: 20,
        flexDirection: 'row',
        justifyContent: 'space-around',
    },
    btn: {
        width: 100,
        height: 50,
        alignItems: 'center',
        justifyContent: 'center',
        alignSelf: 'center',
    },
    btnText: {
        fontSize: 18,
    }
});
