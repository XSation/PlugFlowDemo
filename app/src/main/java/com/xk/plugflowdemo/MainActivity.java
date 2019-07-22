package com.xk.plugflowdemo;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;

import com.xk.plugflowdemo.live.LivePusher;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

public class MainActivity extends AppCompatActivity {

    private LivePusher livePusher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
//        test();
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.CAMERA)
                .onGranted(permissions -> {
                    // Storage permission are allowed.
                    livePusher = new LivePusher(this, 800, 800, 800_000, 10, Camera.CameraInfo.CAMERA_FACING_FRONT);
                    livePusher.setPreviewDisplay(surfaceView.getHolder());
                })
                .onDenied(permissions -> {
                    // Storage permission are not allowed.
                })
                .start();
    }


    public void open(View view) {
        livePusher.startLive("rtmp://192.168.0.4/myapp/abb");
    }

    public void switchCamera(View view) {
        livePusher.switchCamera();
    }
}
