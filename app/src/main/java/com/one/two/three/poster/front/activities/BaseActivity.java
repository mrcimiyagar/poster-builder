package com.one.two.three.poster.front.activities;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.one.two.three.poster.back.callbacks.OnPermissionGrantListener;
import com.one.two.three.poster.back.core.Core;

public class BaseActivity extends AppCompatActivity {

    private boolean isDownloadingPoster = false;

    public boolean isDownloadingPoster() {
        return isDownloadingPoster;
    }

    public void setDownloadingPoster(boolean downloadingPoster) {
        isDownloadingPoster = downloadingPoster;
    }

    private OnPermissionGrantListener permissionGrantListener;

    public void askPermission(OnPermissionGrantListener callback, String[] permissions) {

        this.permissionGrantListener = callback;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permissionCheck = PackageManager.PERMISSION_GRANTED;
            for (String permission : permissions) {
                permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(Core.getInstance(), permission);
            }

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions( permissions, 1);
            }
            else {
                this.permissionGrantListener.onPermissionGranted();
            }
        }
        else {
            this.permissionGrantListener.onPermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int permissionCheck = PackageManager.PERMISSION_GRANTED;

        for(int permission : grantResults) permissionCheck = permissionCheck + permission;

        if (((grantResults.length > 0) && PackageManager.PERMISSION_GRANTED == permissionCheck)) {
            this.permissionGrantListener.onPermissionGranted();
        }
        else {
            this.permissionGrantListener.onPermissionFailure();
        }
    }

    public static float dpToPixels(float dpNum) {
        return Core.getInstance().getResources().getDisplayMetrics().density * dpNum;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Core.getInstance().setCurrentActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

}