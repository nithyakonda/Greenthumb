package com.nkonda.greenthumb.notification

import android.app.NotificationManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nkonda.greenthumb.data.TaskKey
import com.nkonda.greenthumb.data.TaskType
import com.nkonda.greenthumb.data.source.IRepository
import com.nkonda.greenthumb.ui.plantdetails.SchedulingDialogFragment
import com.nkonda.greenthumb.util.getCurrentTimeString
import com.nkonda.greenthumb.util.getNotificationContent
import com.nkonda.greenthumb.util.sendNotification
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class NotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {
    private val repository: IRepository by inject()

    companion object {
        const val ARG_PLANT_ID = "plant_id"
        const val ARG_PLANT_NAME = "plant_name"
        const val ARG_TASK_TYPE = "task_type"
    }

    override suspend fun doWork(): Result {
        val plantId = inputData.getLong(ARG_PLANT_ID, -1)
        val plantName = inputData.getString(ARG_PLANT_NAME)
        val taskType = inputData.getString(ARG_TASK_TYPE)?.let { TaskType.valueOf(it) }
        val taskKey = taskType?.let { TaskKey(plantId, it) }

        if (taskKey?.let { repository.hasTask(it) } == true) {
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (plantName != null) {
                val (title, text) = getNotificationContent(applicationContext, taskType, plantName)
                Timber.d("Sending notification @ ${getCurrentTimeString()}}")
                notificationManager.sendNotification(title, text, applicationContext)
            }
        } else {
            Timber.w("Couldn't send notification for $plantId, $plantName, $taskType")
        }

        return Result.success()
    }
}