package br.com.plussapps.georeminder

import android.app.Application
import br.com.plussapps.georeminder.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GeoReminderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GeoReminderApp)
            modules(appModule)
        }
    }
}