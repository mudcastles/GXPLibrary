package com.example.imagecompresslibrary.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.provider.MediaStore

object CameraUtils {
    /**
     * 判断android设备是否有摄像头
     * 有些扫码枪等设备没有摄像头
     */
    fun hasCamera(context: Context): Boolean {
        var packageManager = context.packageManager
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
                || Camera.getNumberOfCameras() > 0
    }

    /**
     * 获取拍照意图
     */
    fun getCameraIntent(uri: Uri): Intent {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        return intent
    }

    /**
     * 获取打开相册的意图
     */
    fun getAlbumIntent(): Intent {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        return intent
    }
}