package com.nkonda.greenthumb.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import com.nkonda.greenthumb.MainActivity
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.Schedule
import com.nkonda.greenthumb.data.TaskKey
import com.nkonda.greenthumb.data.TaskType
import com.nkonda.greenthumb.notification.NotificationWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

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


fun getNotificationWorkRequest(taskKey: TaskKey, schedule: Schedule, plantName: String): OneTimeWorkRequest {
    val hour = schedule.hourOfDay
    val min = schedule.minute
    Timber.d(
        "Scheduling notification for ${plantName}: ${taskKey.taskType} @ ${
            getFormattedTimeString(
                hour,
                min
            )
        }"
    )
    var inputData = Data.Builder()
        .putLong(NotificationWorker.ARG_PLANT_ID, taskKey.plantId)
        .putString(NotificationWorker.ARG_TASK_TYPE, taskKey.taskType.name)
        .putString(NotificationWorker.ARG_PLANT_NAME, plantName)
        .build()

    return OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInputData(inputData)
        .setInitialDelay(
            getDelayUntil(hour, min), TimeUnit.MILLISECONDS
        )
        .build()
}

fun getNotificationContent(applicationContext: Context, taskType: TaskType, plantName: String): Pair<String, String> {
    var title: String
    var message: String

    when(taskType) {
        TaskType.Prune -> {
            title = applicationContext.getString(R.string.notification_title_prune)
            message = String.format(
                applicationContext.getString(R.string.notification_description_prune),
                plantName
            )
        }
        TaskType.Water -> {
            title = applicationContext.getString(R.string.notification_title_water)
            message = String.format(
                applicationContext.getString(R.string.notification_description_water),
                plantName
            )
        }
        TaskType.Custom -> TODO()
    }
    return Pair(title, message)
}