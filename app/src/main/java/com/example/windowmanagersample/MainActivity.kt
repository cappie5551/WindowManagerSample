package com.example.windowmanagersample

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.example.windowmanagersample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        // ID for the runtime permission dialog
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1
    }

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // トグルボタンの設定
    }

    private fun requestOverlayPermission() {
        if (isOverlayGranted()) {
            // オーバーレイがすでに許可されている
            return
        }

        // オーバーレイ設定画面に遷移する
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )

        val getContent = registerForActivityResult()

        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!isOverlayGranted()) {
                // オーバーレイ設定画面から戻ってきてもオーバーレイが許可されていない場合はアプリを終了する
                finish()
            }
        }
    }

    private fun isOverlayGranted() =
        Settings.canDrawOverlays(this)

}