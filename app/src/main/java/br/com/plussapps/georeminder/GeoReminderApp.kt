package br.com.plussapps.georeminder

import android.app.Application
import br.com.plussapps.georeminder.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class GeoReminderApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Em produção, use Level.NONE; durante dev, ERROR/INFO ajuda
            androidLogger(Level.ERROR)
            androidContext(this@GeoReminderApp)
            modules(appModule)
        }
    }
}