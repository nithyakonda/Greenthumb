package com.nkonda.greenthumb.notification

import android.app.NotificationManager
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nkonda.greenthumb.util.sendNotification

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
        title?.let {_title ->
            text?.let { _text ->
                notificationManager.sendNotification(_title,
                _text, applicationContext)
            }
        }
        return Result.success()
    }
}