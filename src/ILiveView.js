/**
 * Created by ruby on 2017/8/28.
 * @description 腾讯互动直播渲染层
 * @copyright learnta inc.
 */
import  React, {Component, PropTypes} from 'react'
import {
    requireNativeComponent,
    View
} from 'react-native'

export default class ILiveView extends Component {

    render() {
        return (
            <RCTILiveView {...this.props}/>
        )
    }
}

ILiveView.propTypes = {
    showVideoView: PropTypes.bool,
    ...View.propTypes
};

const RCTILiveView = requireNativeComponent("RCTILiveView", ILiveView);