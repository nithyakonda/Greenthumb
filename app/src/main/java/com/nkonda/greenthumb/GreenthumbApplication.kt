package com.nkonda.greenthumb

import android.app.Application
import androidx.work.*
import com.nkonda.greenthumb.data.source.IRepository
import com.nkonda.greenthumb.data.source.Repository
import com.nkonda.greenthumb.data.source.local.GreenthumbDatabase
import com.nkonda.greenthumb.data.source.local.ILocalDataSource
import com.nkonda.greenthumb.data.source.local.LocalDataSource
import com.nkonda.greenthumb.data.source.remote.IRemoteDataSource
import com.nkonda.greenthumb.data.source.remote.RemoteDataSource
import com.nkonda.greenthumb.notification.TaskNotificationScheduler
import com.nkonda.greenthumb.ui.home.HomeViewModel
import com.nkonda.greenthumb.ui.myplants.MyPlantsViewModel
import com.nkonda.greenthumb.ui.plantdetails.PlantDetailsViewModel
import com.nkonda.greenthumb.ui.search.SearchViewModel
import com.nkonda.greenthumb.util.getDelayUntilTaskSchedulerStartTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber
import java.util.concurrent.TimeUnit

class GreenthumbApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)
    override fun onCreate() {
        super.onCreate()

        val myModule = module {
            viewModel {
                SearchViewModel(get())
            }
            viewModel {
                PlantDetailsViewModel(get())
            }
            viewModel {
                MyPlantsViewModel(get())
            }
            viewModel {
                HomeViewModel(get())
            }
            single<IRepository> { Repository(get(), get()) }
            single<IRemoteDataSource> { RemoteDataSource() }
            single<ILocalDataSource> { LocalDataSource(get(), get()) }
            single { GreenthumbDatabase.createPlantsDao(this@GreenthumbApplication) }
            single { GreenthumbDatabase.createTasksDao(this@GreenthumbApplication) }
        }

        startKoin {
            androidContext(this@GreenthumbApplication)
            modules(listOf(myModule))
        }
        Timber.plant(Timber.DebugTree())
        delayedInit()
    }

    private fun delayedInit() = applicationScope.launch {
        setupTaskScheduler()
    }

    private fun setupTaskScheduler() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(true)
            .build()

        val repeatingRequest =
            PeriodicWorkRequestBuilder<TaskNotificationScheduler>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(getDelayUntilTaskSchedulerStartTime(), TimeUnit.MILLISECONDS)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    TaskNotificationScheduler.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
        // .setInitialDelay(0, TimeUnit.MILLISECONDS) - for testing
        Timber.i("Setting up Task Scheduler")
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            TaskNotificationScheduler.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            repeatingRequest
        )
    }
}