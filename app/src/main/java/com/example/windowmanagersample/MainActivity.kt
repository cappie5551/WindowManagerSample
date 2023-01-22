package com.example.windowmanagersample

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
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

        requestOverlayPermission()

        // トグルボタンの設定
        binding.toggleButton.apply {
            isChecked = OverlayService.isActive
            setOnCheckedChangeListener { _, isChecked ->
                MyLog.e(isChecked.toString())
                if (isChecked) {
                    OverlayService.start(this@MainActivity)
                } else {
                    OverlayService.stop(this@MainActivity)
                }

            }
        }
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

        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (!isOverlayGranted()) {
                finish()
            }
        }

        getContent.launch(intent)

        // 古い方法
//        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
//            if (!isOverlayGranted()) {
//                // オーバーレイ設定画面から戻ってきてもオーバーレイが許可されていない場合はアプリを終了する
//                finish()
//            }
//        }
//    }

    private fun isOverlayGranted() =
        Settings.canDrawOverlays(this)

}