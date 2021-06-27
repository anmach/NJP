package com.example.expiration

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions(1,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun checkPermissions(callbackId: Int, vararg permissionsId: String) {
        var permissions = true
        for (p in permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED
        }
        if (!permissions) {
            ActivityCompat.requestPermissions(this, permissionsId, callbackId)
        }
    }

    override fun onRequestPermissionsResult(callbackId: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(callbackId, permissions, grantResults)
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                finish()
            }
        }
    }
}