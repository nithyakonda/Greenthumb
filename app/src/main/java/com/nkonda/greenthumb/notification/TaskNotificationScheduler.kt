package com.nkonda.greenthumb.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.nkonda.greenthumb.data.TaskType
import com.nkonda.greenthumb.data.TaskWithPlant
import com.nkonda.greenthumb.data.source.IRepository
import com.nkonda.greenthumb.data.succeeded
import com.nkonda.greenthumb.util.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class TaskNotificationScheduler(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {
    val repository: IRepository by inject()

    companion object {
        const val WORK_NAME = "TaskNotificationScheduler"
        const val MIN_BACKOFF_MILLIS = 300000L
    }

    override suspend fun doWork(): Result {
        return try {
            val result = repository.getTasks()
            if (result.succeeded) {
                result as com.nkonda.greenthumb.data.Result.Success

                resetOldTasks(result.data)
                if (ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    scheduleNotificationsForCurrentTasks(result.data)
                } else {
                    Timber.w("Post Notifications permission not granted. Couldn't schedule notifications")
                }
                Result.success()
            } else {
                Timber.e("getTasks failed. Couldn't reset tasks or schedule notifications. Attempting retry")
                Result.retry()
            }
        } catch (e: java.lang.Exception) {
            Timber.e("Exception occurred. Couldn't reset tasks or schedule notifications. Attempting retry")
            Timber.e(e.stackTraceToString())
            Result.retry()
        }
    }

    private fun scheduleNotificationsForCurrentTasks(tasks: List<TaskWithPlant>) {
        val newTasks = tasks.filter {
            val schedule = it.task.schedule
            val (day, month) = getToday()
            schedule.days.contains(day) ||
                    schedule.months.contains(month)
        }
        Timber.i("Scheduling notifications for ${newTasks.size} task(s)")
        newTasks.forEach { taskWithPlant ->
            if (!taskWithPlant.task.completed) {
                val hour = taskWithPlant.task.schedule.hourOfDay
                val min = taskWithPlant.task.schedule.minute
                Timber.d("${taskWithPlant.plantName}: ${taskWithPlant.task.key.taskType} @ ${getFormattedTimeString(hour, min)}")
                var inputData =
                    getNotificationContent(applicationContext, taskWithPlant)

                val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInputData(inputData)
                    .setInitialDelay(
                        getDelayUntil(hour, min), TimeUnit.MILLISECONDS
                    )
                    .build()

                WorkManager.getInstance(applicationContext).enqueue(workRequest)
            }
        }
    }

    private suspend fun resetOldTasks(tasks: List<TaskWithPlant>) {
        val oldTasks = tasks.filter {
            val schedule = it.task.schedule
            val (day, month) = getYesterday()
            schedule.days.contains(day) ||
                    schedule.months.contains(month)
        }
        Timber.i("Resetting ${oldTasks.size} old tasks")
        oldTasks.forEach {
            Timber.d("${it.plantName}: ${it.task.key.taskType}")
            when (it.task.key.taskType) {
                TaskType.PRUNE -> {
                    // if yesterday is a different month
                    if (getYesterday().second != getToday().second && it.task.completed) {
                        // mark task as incomplete
                        repository.completeTask(it.task.key, false)
                    }
                }
                TaskType.WATER -> {
                    if (it.task.completed) {
                        repository.completeTask(it.task.key, false)
                    }
                }
                TaskType.CUSTOM -> TODO()
            }
        }
    }
}