package com.ternovyi.musicplayer.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

object PermissionChecker {

    val STORAGE_PERMISSIONS =
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val CAMERA_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val SMS_PERMISSIONS = arrayOf(Manifest.permission.SEND_SMS)

    val CALL_PERMISSIONS = arrayOf(Manifest.permission.CALL_PHONE)

    fun lacksPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (lacksPermission(context, permission)) {
                return true
            }
        }
        return false
    }

    private fun lacksPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED
    }

    fun grantPermissions(grantResults: IntArray): Boolean {
        if (grantResults.isEmpty()) return false
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) return false
        }
        return true
    }
}