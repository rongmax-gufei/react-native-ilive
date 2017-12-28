package com.tencent.qcloud;

import android.view.SurfaceView;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.tencent.qcloud.view.ILiveView;

public class ILiveViewManage extends SimpleViewManager<ILiveView> {

    public static final String REACT_CLASS = "RCTILiveView";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected ILiveView createViewInstance(ThemedReactContext reactContext) {
        return new ILiveView(reactContext);
    }

    @ReactProp(name = "showVideoView")
    public void setShowVideoView(final ILiveView iLiveView, boolean showVideoView) {
        if (showVideoView) {
            SurfaceView surfaceView = ILiveManager.getInstance().getVideoView();
            iLiveView.addView(surfaceView);
        }
    }
}
