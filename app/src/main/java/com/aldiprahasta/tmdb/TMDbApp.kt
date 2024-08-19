package com.aldiprahasta.tmdb

import android.app.Application
import com.aldiprahasta.tmdb.di.localeModule
import com.aldiprahasta.tmdb.di.remoteModule
import com.aldiprahasta.tmdb.di.repositoryModule
import com.aldiprahasta.tmdb.di.useCaseModule
import com.aldiprahasta.tmdb.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class TMDbApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TMDbApp)
            modules(
                    remoteModule,
                    localeModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule,
            )
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}