package com.nkonda.greenthumb

import android.app.Application
import com.nkonda.greenthumb.data.source.IRepository
import com.nkonda.greenthumb.data.source.Repository
import com.nkonda.greenthumb.data.source.local.GreenthumbDatabase
import com.nkonda.greenthumb.data.source.local.LocalDataSource
import com.nkonda.greenthumb.data.source.remote.IRemoteDataSource
import com.nkonda.greenthumb.data.source.remote.RemoteDataSource
import com.nkonda.greenthumb.ui.myplants.MyPlantsViewModel
import com.nkonda.greenthumb.ui.plantdetails.PlantDetailsViewModel
import com.nkonda.greenthumb.ui.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class GreenthumbApplication : Application() {
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
            single<IRepository> { Repository(get(), get()) }
            single<IRemoteDataSource> { RemoteDataSource() }
            single { LocalDataSource(get(), get()) }
            single { GreenthumbDatabase.createPlantsDao(this@GreenthumbApplication) }
            single { GreenthumbDatabase.createTasksDao(this@GreenthumbApplication) }
        }

        startKoin {
            androidContext(this@GreenthumbApplication)
            modules(listOf(myModule))
        }
        Timber.plant(Timber.DebugTree())
    }
}