package com.example.windowmanagersample

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object MyNotification {
    private const val CHANNEL_ID = "channel_id_overlay_sample"
    private const val CHANNEL_NAME = "オーバーレイ表示の切り替え"
    private const val CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT
    private const val FIRST_LINE = "オーバーレイ表示中"
    private const val SECOND_LINE = "ここから表示・非表示を切り替えられます。"
    private val ACTIVITY = MainActivity::class.java

    // set the info for the views that show in the notification panel.
    fun build(context: Context): Notification {
        // Create a notification channel
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_IMPORTANCE)
        )

        val pendingIntent = PendingIntent.getActivity(
            context, 0, Intent(context, ACTIVITY), 0
        )

        return Notification.Builder(context, CHANNEL_ID)
            .setAutoCancel(false)  // タッチされた時に消えない
            .setContentIntent(pendingIntent)  // クリックされた時activityにintentを送る？
            .setContentTitle(FIRST_LINE) // タイトル
            .setContentText(SECOND_LINE) // 内容
            .setSmallIcon(R.drawable.cat) // アイコン
            .setTicker(context.getText(R.string.app_name)) // ステータステキスト
            .setWhen(System.currentTimeMillis())  // タイムスタンプ
            .build()
    }
}