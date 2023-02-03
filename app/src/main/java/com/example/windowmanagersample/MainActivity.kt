package com.example.windowmanagersample

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.DragEvent
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat.startDragAndDrop
import com.example.windowmanagersample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
                    binding.sb.visibility = View.VISIBLE
                } else {
                    OverlayService.stop(this@MainActivity)
                    binding.sb.visibility = View.INVISIBLE
                }

            }
        }

        // 拡大ボタンの設定
        binding.ivZoomInButton.setOnClickListener {
            OverlayService.zoomIn(this)
        }

        // 縮小ボタンの設定
        binding.ivZoomOutButton.setOnClickListener {
            OverlayService.zoomOut(this)
        }

        // シークバーの設定
        binding.sb.apply {
            min = 100
            max = 1000
            progress = 500
            visibility = View.INVISIBLE
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    OverlayService.zoom(this@MainActivity, progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
            )
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
    }

    private fun isOverlayGranted() =
        Settings.canDrawOverlays(this)

}