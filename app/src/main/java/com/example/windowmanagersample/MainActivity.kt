package com.example.windowmanagersample

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import com.example.windowmanagersample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // オーバーレイ表示の権限を要求する
        requestOverlayPermission()

        // トグルボタンの設定
        binding.toggleButton.apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // サービスを開始する
                    OverlayService.start(this@MainActivity)
                } else {
                    // サービスを停止する
                    OverlayService.stop(this@MainActivity)
                }

            }
        }
    }

    // オーバーレイ表示の権限を要求する
    private fun requestOverlayPermission() {
        if (isOverlayGranted()) {
            // オーバーレイがすでに許可されている
            return
        }

        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )

        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (!isOverlayGranted()) {
                finish()
            }
        }

        // オーバーレイ設定画面に遷移する
        getContent.launch(intent)
    }

    private fun isOverlayGranted() =
        Settings.canDrawOverlays(this)

}