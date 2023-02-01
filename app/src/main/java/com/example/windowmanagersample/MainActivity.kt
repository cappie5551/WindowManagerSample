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
    companion object {
        // ID for the runtime permission dialog
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var binding: ActivityMainBinding

    private var currentX = 0f
    private var currentY = 0f

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

        val imageView = ImageView(this)
        // 画像を指定
        imageView.setImageResource(R.drawable.cat)


        val imageLayoutParams = FrameLayout.LayoutParams(300.toDp().toPx(), 300.toDp().toPx())
        imageLayoutParams.gravity = Gravity.CENTER_HORIZONTAL
        imageLayoutParams.setMargins(
            100.toDp().toPx(),
            200.toDp().toPx(),
            100.toDp().toPx(),
            100.toDp().toPx())

        imageView.layoutParams = imageLayoutParams

        imageView.setOnLongClickListener { view ->
            binding.root.startDragAndDrop(null, View.DragShadowBuilder(view), view, 0)
        }

        binding.root.setOnDragListener { v, event ->
            when(event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    MyLog.e("ACTION_DRAG_STARTED")

                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    MyLog.e("ACTION_DRAG_ENTERED")

                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    currentX = event.x
                    currentY = event.y
                    MyLog.e("ACTION_DRAG_LOCATION, x = $currentX, y = $currentY")

                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    MyLog.e("ACTION_DRAG_EXITED")

                    true
                }
                DragEvent.ACTION_DROP -> {
//                    layoutParams.x = currentX.toInt()
//                    layoutParams.y = currentY.toInt()
//                    windowManager.updateViewLayout(this, layoutParams)
                    MyLog.e("ACTION_DROP x = ${event.x}, y = ${event.y}")
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    MyLog.e("ACTION_DRAG_ENDED")
//                    layoutParams.x = currentX.toInt()
//                    layoutParams.y = currentY.toInt()
//                    layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
//                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
//                    windowManager.updateViewLayout(this, layoutParams)
                    true
                }
                else -> {
                    MyLog.e("ACTION_DRAG_ENTERED")

                    false
                }
            }
        }

        binding.root.addView(imageView)

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