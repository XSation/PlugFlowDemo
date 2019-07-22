package com.xk.plugflowdemo.live;

import android.app.Activity;
import android.hardware.Camera;
import android.view.SurfaceHolder;

/**
 * @author xuekai1
 * @date 2019-07-21
 */
public class VideoChannel implements Camera.PreviewCallback, CameraHelper.OnChangedSizeListener
//        implements SurfaceHolder.Callback
{
    private final LivePusher livePusher;
    Activity activity;
    int width;
    int height;
    int bitrate;
    int fps;
    int cameraId;
    private final CameraHelper cameraHelper;

    public VideoChannel(LivePusher livePusher, Activity activity, int width, int height, int bitrate, int fps, int cameraId) {
        this.livePusher = livePusher;
        this.activity = activity;
        this.width = width;
        this.height = height;
        this.bitrate = bitrate;
        this.fps = fps;
        this.cameraId = cameraId;
        cameraHelper = new CameraHelper(activity, cameraId, width, height);
        cameraHelper.setPreviewCallback(this);
        cameraHelper.setOnChangedSizeListener(this);
    }

    public void setPreviewDisplay(SurfaceHolder surfaceHolder) {
        cameraHelper.setPreviewDisplay(surfaceHolder);
//        surfaceHolder.addCallback(this);
    }

    public void switchCamera() {
        cameraHelper.switchCamera();
    }

    public void startLive() {
//        Camera camera = Camera.open(this.cameraId);
//        camera.addCallbackBuffer();
    }

    public void stopLive() {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        livePusher.native_pushVideo(data);
    }

    @Override
    public void onChanged(int w, int h) {
        livePusher.native_setVideoEncInfo(w, h, fps, bitrate);
    }

//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//
//    }
}
