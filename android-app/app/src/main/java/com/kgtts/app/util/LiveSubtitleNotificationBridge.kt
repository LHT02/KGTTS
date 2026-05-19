package com.lhtstudio.kigtts.app.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.lhtstudio.kigtts.app.R
import com.lhtstudio.kigtts.app.overlay.OverlayBridge
import com.lhtstudio.kigtts.app.service.RealtimeHostService

object LiveSubtitleNotificationBridge {
    const val ACTION_PLAY_TEXT = "com.lhtstudio.kigtts.app.action.LIVE_SUBTITLE_PLAY_TEXT"
    const val ACTION_DISABLE = "com.lhtstudio.kigtts.app.action.LIVE_SUBTITLE_DISABLE"
    const val EXTRA_TEXT = "live_subtitle_text"

    private const val CHANNEL_ID = "kgtts_live_subtitle"
    private const val CHANNEL_NAME = "实时通知"
    private const val NOTIFICATION_ID = 1031
    private const val EXTRA_REQUEST_PROMOTED_ONGOING = "android.requestPromotedOngoing"

    fun update(
        context: Context,
        enabled: Boolean,
        text: String,
        status: String
    ) {
        if (!enabled) {
            cancel(context)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        ensureChannel(context)
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        runCatching {
            manager.notify(NOTIFICATION_ID, buildNotification(context, text, status))
        }
    }

    fun cancel(context: Context) {
        context.getSystemService(NotificationManager::class.java)?.cancel(NOTIFICATION_ID)
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "显示当前上屏的大字幕，并提供播放、打开便捷字幕和关闭实时通知操作"
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(
        context: Context,
        rawText: String,
        rawStatus: String
    ): Notification {
        val text = rawText.trim().ifBlank { "暂无字幕" }
        val status = rawStatus.trim().ifBlank { "待命" }
        val openQuickSubtitleIntent = OverlayBridge.buildOpenPageIntent(
            context,
            OverlayBridge.TARGET_OPEN
        )
        val openQuickSubtitlePendingIntent = PendingIntent.getActivity(
            context,
            3101,
            openQuickSubtitleIntent,
            pendingIntentFlags()
        )
        val playPendingIntent = PendingIntent.getService(
            context,
            3102,
            Intent(context, RealtimeHostService::class.java).apply {
                action = ACTION_PLAY_TEXT
                putExtra(EXTRA_TEXT, text)
            },
            pendingIntentFlags()
        )
        val disablePendingIntent = PendingIntent.getService(
            context,
            3103,
            Intent(context, RealtimeHostService::class.java).apply {
                action = ACTION_DISABLE
            },
            pendingIntentFlags()
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_live_subtitle_notification_logo)
            .setLargeIcon(loadLogoBitmap(context))
            .setColor(0xFF038387.toInt())
            .setContentTitle("KIGTTS")
            .setContentText(status)
            .setTicker(text)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(text)
                    .bigText(status)
            )
            .setContentIntent(openQuickSubtitlePendingIntent)
            .setDeleteIntent(disablePendingIntent)
            .addAction(R.drawable.ic_live_subtitle_notification_logo, "播放文本", playPendingIntent)
            .addAction(R.drawable.ic_live_subtitle_notification_logo, "打开便捷字幕", openQuickSubtitlePendingIntent)
            .addAction(R.drawable.ic_live_subtitle_notification_logo, "关闭实时通知", disablePendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setShowWhen(false)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addExtras(Bundle().apply {
                putBoolean(EXTRA_REQUEST_PROMOTED_ONGOING, true)
                putString(EXTRA_SHORT_CRITICAL_TEXT, text)
            })
            .build()
    }

    private fun loadLogoBitmap(context: Context): Bitmap? {
        val drawable = ContextCompat.getDrawable(
            context,
            R.drawable.ic_live_subtitle_notification_logo
        ) ?: return null
        val size = 96
        return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).also { bitmap ->
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
    }

    private fun pendingIntentFlags(): Int {
        return PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
    }

    private const val EXTRA_SHORT_CRITICAL_TEXT = "android.shortCriticalText"
}
