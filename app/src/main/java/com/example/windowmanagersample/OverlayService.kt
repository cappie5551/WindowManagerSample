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

class OverlayService : Service(){
    companion object {
        private const val ACTION_SHOW = "SHOW"
        private const val  ACTION_HIDE = "HIDE"

        private const val ACTION_ZOOM = "ZOOM"
        private const val ACTION_ZOOM_IN = "ZOOM_IN"
        private const val ACTION_ZOOM_OUT = "ZOOM_OUT"

        private const val PROGRESS = "PROGRESS"

        fun start(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_SHOW
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_HIDE
            }
            context.startService(intent)
        }

        fun zoomIn(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_ZOOM_IN
            }
            context.startService(intent)
        }

        fun zoomOut(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_ZOOM_OUT
            }
            context.startService(intent)
        }

        fun zoom(context: Context, progress: Int) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_ZOOM
                putExtra(PROGRESS, progress)
            }
            context.startService(intent)
        }

        // To control toggle button in MainActivity. this is not elegant but works.
        var isActive = false
            private set
    }

    private lateinit var overlayView: OverlayView
    private lateinit var windowManager: WindowManager

    override fun onCreate() {
        // Start as a foreground service
        MyLog.e( "onCreate start")
        val notification = MyNotification.build(this)
        startForeground(1, notification)

        overlayView = OverlayView.create(this)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        MyLog.e( "onCreate end")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_SHOW -> {
                    isActive = true
                    showOverlayView()
                }
                ACTION_HIDE -> {
                    isActive = false
                    hideOverlayView()
                    stopSelf()
                }
                ACTION_ZOOM_IN -> {
                    overlayView.zoomIn()
                }
                ACTION_ZOOM_OUT -> {
                    overlayView.zoomOut()
                }
                ACTION_ZOOM -> {
                    val progress = it.getIntExtra(PROGRESS, 0)
                    overlayView.zoom(progress)
                }
                else -> {
                    MyLog.e("Need action property to start ${OverlayService::class.java.simpleName}")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showOverlayView() {
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.cat)

        // TODO: 画像サイズをshared preferencesで管理する
        val imageLayoutParams = FrameLayout.LayoutParams(300.toDp().toPx(), 300.toDp().toPx())
        imageView.layoutParams = imageLayoutParams

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            0, 80,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.TOP or Gravity.START

        overlayView.setOnLongClickListener { _ ->
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
            windowManager.updateViewLayout(overlayView, layoutParams)

            overlayView.startDragAndDrop(null, View.DragShadowBuilder(imageView), imageView, 0)
        }


        overlayView.setOnDragListener { v, event ->
            when(event. action) {
                DragEvent.ACTION_DRAG_STARTED -> {
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
                    layoutParams.x = event.x.toInt() - 150
                    layoutParams.y = event.y.toInt() - 150
                    layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                    windowManager.updateViewLayout(overlayView, layoutParams)
                    MyLog.e("ACTION_DROP x = ${event.x}, y = ${event.y}")
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    MyLog.e("ACTION_DRAG_ENDED x = ${v.x}, y = ${v.y}")
                    MyLog.e("ACTION_DRAG_ENDED x = ${event.x}, y = ${event.y}")
                    true
                }
                else -> {
                    MyLog.e("ACTION_DRAG_ENTERED")

                    false
                }
            }
        }

        overlayView.addView(imageView)
        windowManager.addView(overlayView, layoutParams)
    }

    private fun hideOverlayView() {
        windowManager.removeView(overlayView)
    }

    // Cleans up views just in case
    override fun onDestroy() {
        hideOverlayView()
    }

    override fun onBind(intent: Intent?): Nothing? = null
}