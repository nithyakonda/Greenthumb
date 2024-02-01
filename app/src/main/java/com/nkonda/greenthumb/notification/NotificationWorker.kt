package com.nkonda.greenthumb.notification

import android.app.NotificationManager
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nkonda.greenthumb.util.sendNotification
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class NotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    companion object {
        const val ARG_NOTIFICATION_TITLE = "notification_title"
        const val ARG_NOTIFICATION_TEXT = "notification_text"
    }

    override fun doWork(): Result {
        val title = inputData.getString(ARG_NOTIFICATION_TITLE)
        val text = inputData.getString(ARG_NOTIFICATION_TEXT)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (title != null && text != null) {
            val currentTime = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val formattedTime = dateFormat.format(currentTime)
            Timber.i("Sending notification @ $formattedTime}")
            notificationManager.sendNotification(title, text, applicationContext)
        } else {
            Timber.w("Null input data. Couldn't send notification")
        }

        return Result.success()
    }
}