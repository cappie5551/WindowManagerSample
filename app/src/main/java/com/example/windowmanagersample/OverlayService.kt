package com.example.windowmanagersample

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder

class OverlayService : Service() {
    companion object {
        private const val ACTION_SHOW = "SHOW"
        private const val  ACTION_HIDE = "HIDE"

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

        // To control toggle button in MainActivity. this is not elegant but works.
        var isActive = false
            private set
    }

    private lateinit var overlayView: OverlayView

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

}