package com.xk.plugflowdemo.live;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

/**
 * @author xuekai1
 * @date 2019-07-21
 */
public class CameraHelper implements SurfaceHolder.Callback {
    private static final String TAG = "CameraHelper";
    private int cameraId;
    private final Activity activity;
    private int width;
    private int height;
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private Camera.PreviewCallback previewCallback;
    private OnChangedSizeListener mOnChangedSizeListener;

    public CameraHelper(Activity activity, int cameraId, int width, int height) {
        this.activity = activity;
        this.cameraId = cameraId;
        this.width = width;
        this.height = height;
    }

    public void switchCamera() {
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else {
            cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        startPreview();
    }

    private void stopPreview() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private void startPreview() {
        stopPreview();
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            Log.i("CameraHelper", "startPreview-->" + cameraInfo.facing);
        }
        //获得camera对象
        camera = Camera.open(cameraId);
        //配置camera的属性
        Camera.Parameters parameters = camera.getParameters();
        //设置预览数据格式为nv21
        parameters.setPreviewFormat(ImageFormat.NV21);
        //这是摄像头宽、高
        setPreviewSize(parameters);
        // 设置摄像头 图像传感器的角度、方向
        setCameraDisplayOrientation();
        //数据缓存区
        byte[] buffer = new byte[(int) (width * height / 3f * 2)];
        camera.addCallbackBuffer(buffer);
        camera.setPreviewCallback(previewCallback);
        //设置预览画面
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
        mOnChangedSizeListener.onChanged(width, height);
    }

    /**
     * copy自源码注释，采集回来的数据角度不对，需要转换
     */
    public void setCameraDisplayOrientation() {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


    /**
     * 摄像头支持的宽高有限，调用方传入的宽高和支持的比较，取一个最近的。
     *
     * @param parameters
     */
    private void setPreviewSize(Camera.Parameters parameters) {
        //获取摄像头支持的宽、高
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size size = supportedPreviewSizes.get(0);
        Log.d(TAG, "支持 " + size.width + "x" + size.height);
        //选择一个与设置的差距最小的支持分辨率
        // 10x10 20x20 30x30
        // 12x12
        int m = Math.abs(size.height * size.width - width * height);
        supportedPreviewSizes.remove(0);
        //遍历
        for (Camera.Size next : supportedPreviewSizes) {
            Log.d(TAG, "支持 " + next.width + "x" + next.height);
            int n = Math.abs(next.height * next.width - width * height);
            if (n < m) {
                m = n;
                size = next;
            }
        }
        width = size.width;
        height = size.height;
        parameters.setPreviewSize(width, height);
        Log.d(TAG, "设置预览分辨率 width:" + size.width + " height:" + size.height);
    }


    public void setPreviewDisplay(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        surfaceHolder.addCallback(this);
        startPreview();
    }

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        this.previewCallback = previewCallback;

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
    }

    public void setOnChangedSizeListener(OnChangedSizeListener listener) {
        mOnChangedSizeListener = listener;
    }

    public interface OnChangedSizeListener {
        void onChanged(int w, int h);
    }
}
