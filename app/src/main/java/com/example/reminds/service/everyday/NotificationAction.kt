package com.example.reminds.service.everyday

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.IdRes
import androidx.annotation.Keep
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.reminds.R
import com.example.reminds.service.ScheduledWorker
import com.example.reminds.ui.activity.MainActivity
import kotlin.random.Random

@Keep
class NotificationAction(private val context: Context) {
    companion object {
        const val ACTION_NOTIFICATION_BUTTON_CLICK = "ACTION_NOTIFICATION_BUTTON_CLICK"
        const val EXTRA_BUTTON_CLICKED = "EXTRA_BUTTON_CLICKED"
    }

    private fun onButtonNotificationClick(@IdRes id: Int): PendingIntent {
        val intent = Intent(ACTION_NOTIFICATION_BUTTON_CLICK)
        intent.putExtra(EXTRA_BUTTON_CLICKED, id)
        return PendingIntent.getBroadcast(context, id, intent, 0)
    }


    fun showNotification(title: String, message: String, idTopic: Long) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(ScheduledWorker.TOPIC_ID_OPEN, idTopic)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_daily_task)
        notificationLayout.setOnClickPendingIntent(R.id.tv_cancel, onButtonNotificationClick(R.id.tv_cancel))
        notificationLayout.setOnClickPendingIntent(R.id.tv_apply, onButtonNotificationClick(R.id.tv_apply))

        val channelId = context.getString(R.string.notify_channel)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setColor(ContextCompat.getColor(context, R.color.pink_500))
            .setSmallIcon(R.drawable.playstore)
//            .setContentTitle(title)
//            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setCustomContentView(notificationLayout)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }
}
