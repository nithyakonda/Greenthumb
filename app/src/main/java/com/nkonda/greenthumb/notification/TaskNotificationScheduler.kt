package com.nkonda.greenthumb.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.work.*
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.TaskType
import com.nkonda.greenthumb.data.TaskWithPlant
import com.nkonda.greenthumb.data.source.Repository
import com.nkonda.greenthumb.data.succeeded
import com.nkonda.greenthumb.util.getTimeInMillis
import com.nkonda.greenthumb.util.getToday
import com.nkonda.greenthumb.util.getYesterday
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import timber.log.Timber
import java.util.concurrent.TimeUnit

class TaskNotificationScheduler(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {
    private lateinit var repository: Repository

    companion object {
        const val WORK_NAME = "TaskNotificationScheduler"
    }

    override suspend fun doWork(): Result {
        return try {
            repository = get()
            val result = repository.getTasks()
            if (result.succeeded) {
                result as com.nkonda.greenthumb.data.Result.Success
                val oldTasks = result.data.filter {
                    val schedule = it.task.schedule
                    val (day, month) = getYesterday()
                    schedule.days.contains(day) ||
                            schedule.months.contains(month)
                }
                val newTasks = result.data.filter {
                    val schedule = it.task.schedule
                    val (day, month) = getToday()
                    schedule.days.contains(day) ||
                            schedule.months.contains(month)
                }
                resetOldTasks(oldTasks)
                if (ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    scheduleNotificationsForCurrentTasks(newTasks)
                }
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: java.lang.Exception) {
            Result.retry()
        }
    }

    private fun scheduleNotificationsForCurrentTasks(newTasks: List<TaskWithPlant>) {
        Timber.d("Scheduling notifications for ${newTasks.size} task(s)")
        newTasks.forEach {
            if (!it.task.completed) {
                var inputData =
                    when (it.task.key.taskType) {
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
                                        it.plantName
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
                                        it.plantName
                                    )
                                )
                                .build()
                        }
                        TaskType.CUSTOM -> TODO()
                    }

                val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInputData(inputData)
                    .setInitialDelay(
                        getTimeInMillis(
                            it.task.schedule.hourOfDay,
                            it.task.schedule.minute
                        ), TimeUnit.MILLISECONDS
                    )
                    .build()

                WorkManager.getInstance(applicationContext).enqueue(workRequest)
            }
        }
    }

    private suspend fun resetOldTasks(oldTasks: List<TaskWithPlant>) {
        oldTasks.forEach {
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