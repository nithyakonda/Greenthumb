package com.nkonda.greenthumb.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Data
import com.nkonda.greenthumb.MainActivity
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.TaskType
import com.nkonda.greenthumb.data.TaskWithPlant
import com.nkonda.greenthumb.notification.NotificationWorker

private const val NOTIFICATION_ID = 10

fun NotificationManager.sendNotification(title: String, message: String, context: Context) {
    val contentIntent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder =
        NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

fun getNotificationContent(applicationContext: Context, taskWithPlant: TaskWithPlant) =
    when (taskWithPlant.task.key.taskType) {
        TaskType.PRUNE -> {
            Data.Builder()
                .putString(
                    NotificationWorker.ARG_NOTIFICATION_TITLE,
                    applicationContext.getString(
                        R.string.notification_title_prune
                    )
                )
                .putString(
                    NotificationWorker.ARG_NOTIFICATION_TEXT,
                    String.format(
                        applicationContext.getString(R.string.notification_description_prune),
                        taskWithPlant.plantName
                    )
                )
                .build()
        }
        TaskType.WATER -> {
            Data.Builder()
                .putString(
                    NotificationWorker.ARG_NOTIFICATION_TITLE,
                    applicationContext.getString(
                        R.string.notification_title_water
                    )
                )
                .putString(
                    NotificationWorker.ARG_NOTIFICATION_TEXT,
                    String.format(
                        applicationContext.getString(R.string.notification_description_water),
                        taskWithPlant.plantName
                    )
                )
                .build()
        }
        TaskType.CUSTOM -> TODO()
    }