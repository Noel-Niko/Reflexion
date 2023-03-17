package com.livingtechusa.reflexion.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class MediaUtil {

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val REQUEST_CAMERA = 2
    private val REQUEST_READ_VIDEO = 3

    private val permissionStorage = arrayOf<String>(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val permissionCamera = arrayOf<String>(
        Manifest.permission.CAMERA
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissionReadVideo = arrayOf<String>(
        Manifest.permission.READ_MEDIA_VIDEO
    )

    fun verifyStoragePermission(activity: Activity) {
        val permissionWrite = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permissionRead =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionWrite != PackageManager.PERMISSION_GRANTED || permissionRead != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                permissionStorage,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    fun verifyCameraPermission(activity: Activity) {
        val permissionUseCamera = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        )

        if (permissionUseCamera != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                permissionCamera,
                REQUEST_CAMERA
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionReadVideoFiles = ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_MEDIA_VIDEO
            )

            if (permissionReadVideoFiles != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    activity,
                    permissionReadVideo,
                    REQUEST_READ_VIDEO
                )
            }
        }
    }
}