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

    private lateinit var windowManager: WindowManager

    private lateinit var overlayView: FrameLayout
    private lateinit var overlayViewLayoutParams: WindowManager.LayoutParams


    private lateinit var imageView: ImageView

    override fun onCreate() {
        MyLog.e( "onCreate start")
        val notification = MyNotification.build(this)
        startForeground(1, notification)

        overlayView = View.inflate(this, R.layout.overlay_view, null) as FrameLayout
        imageView = overlayView.findViewById(R.id.cat)

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
                    stopSelf()
                }
                ACTION_ZOOM_IN -> {
//                    overlayView.zoomIn()
                }
                ACTION_ZOOM_OUT -> {
//                    overlayView.zoomOut()
                }
                ACTION_ZOOM -> {
                    val progress = it.getIntExtra(PROGRESS, 0)
//                    overlayView.zoom(progress)
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

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.TOP or Gravity.START

        overlayView.setOnLongClickListener { _ ->

            imageView.visibility = View.INVISIBLE

            overlayView.startDragAndDrop(null, View.DragShadowBuilder(imageView), imageView, 0)
        }

        overlayView.setOnDragListener { v, event ->
            when(event. action) {
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

        windowManager.addView(overlayView, layoutParams)
    }

    // Cleans up views just in case
    override fun onDestroy() {
        windowManager.removeView(overlayView)

    }

    override fun onBind(intent: Intent?): Nothing? = null

    //    fun zoomIn() {
//        imageLayoutParams.width += 100.toDp().toPx()
//        imageLayoutParams.height += 100.toDp().toPx()
//        imageView.layoutParams = imageLayoutParams
//    }
//
//    fun zoomOut() {
//        imageLayoutParams.width -= 100.toDp().toPx()
//        imageLayoutParams.height -= 100.toDp().toPx()
//        imageView.layoutParams = imageLayoutParams
//    }
//
//    fun zoom(progress: Int) {
//        imageLayoutParams.width = progress
//        imageLayoutParams.height = progress
//        imageView.layoutParams = imageLayoutParams
//    }
}
