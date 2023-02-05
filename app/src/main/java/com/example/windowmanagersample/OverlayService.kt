package com.example.windowmanagersample

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.view.DragEvent
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView

class OverlayService : Service() {
    companion object {
        private const val ACTION_SHOW = "SHOW"
        private const val ACTION_HIDE = "HIDE"

        // Service を開始する
        fun start(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_SHOW
            }
            context.startService(intent)
        }

        // Service を停止する
        fun stop(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_HIDE
            }
            context.startService(intent)
        }
    }

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: FrameLayout

    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.START
    }

    private lateinit var imageView: ImageView

    override fun onCreate() {
        val notification = MyNotification.build(this)
        startForeground(1, notification)

        overlayView = View.inflate(this, R.layout.overlay_view, null) as FrameLayout
        imageView = overlayView.findViewById(R.id.cat)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_SHOW -> {
                    showOverlayView()
                }
                ACTION_HIDE -> {
                    stopSelf()
                }
                else -> {
                    MyLog.e("Need action property to start ${OverlayService::class.java.simpleName}")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showOverlayView() {

        // TODO: 画像サイズをshared preferencesで管理する
        overlayView.setOnLongClickListener { _ ->

            imageView.visibility = View.INVISIBLE

            overlayView.startDragAndDrop(null, View.DragShadowBuilder(imageView), imageView, 0)
        }

        overlayView.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // setOnLongClickListener で行うとimageVIewのINVISIBLEが間に合わずちらついてしまう
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                    layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                    windowManager.updateViewLayout(overlayView, layoutParams)
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    MyLog.e("ACTION_DRAG_LOCATION x = ${event.x}, y = ${event.y}")
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    true
                }
                DragEvent.ACTION_DROP -> {
                    // eventのいちは画像の真ん中
                    layoutParams.x = event.x.toInt() - 75.toPx()
                    layoutParams.y = event.y.toInt() - 75.toPx()
                    layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                    windowManager.updateViewLayout(overlayView, layoutParams)
                    imageView.visibility = View.VISIBLE
                    MyLog.e("ACTION_DROP x = ${event.x}, y = ${event.y}")
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    true
                }
                else -> {
                    false
                }
            }
        }

        windowManager.addView(overlayView, layoutParams)
    }

    // Cleans up views just in case
    override fun onDestroy() {
        windowManager.removeView(overlayView)

    }

    override fun onBind(intent: Intent?): Nothing? = null
}