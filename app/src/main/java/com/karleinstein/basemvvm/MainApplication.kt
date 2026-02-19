package com.karleinstein.basemvvm

import android.app.Application
import com.karleinstein.basemvvm.di.appModule
import com.karleinstein.basemvvm.di.viewModelModule
import com.karleinstein.basemvvm.notification.TodoScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule, viewModelModule)
        }
        TodoScheduler.scheduleOverdueCheck(this)
    }
}
