package com.example.windowmanagersample

import android.app.Service
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView

class OverlayService : Service(), View.OnLongClickListener {
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
    private lateinit var ivCat: ImageView

    override fun onCreate() {
        // Start as a foreground service
        MyLog.e( "onCreate start")
        val notification = MyNotification.build(this)
        startForeground(1, notification)

        overlayView = OverlayView.create(this)
        MyLog.e( "onCreate end")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_SHOW -> {
                    isActive = true
                    overlayView.show()
                }
                ACTION_HIDE -> {
                    isActive = false
                    overlayView.hide()
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

    // Cleans up views just in case
    override fun onDestroy() {
        overlayView.hide()
    }

    override fun onBind(intent: Intent?): Nothing? = null

    override fun onLongClick(v: View?): Boolean {
        TODO("Not yet implemented")
    }

}