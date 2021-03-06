package com.george.fs_korinthias.ui

import android.app.Application
import com.george.fs_korinthias.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FSKorinthiasApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            //androidContext(applicationContext)
            androidContext(this@FSKorinthiasApplication)
            modules(mainViewModelModule,
                detailsActivityViewModelModule,
                allNewsViewModelModule,
                efimeriesViewModelModule,
                efimeriesDetailsViewModelModule,
                importantViewModelModule)
        }
    }
}