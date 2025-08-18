package br.com.plussapps.georeminder.service

import android.app.Service
import android.content.Intent
import android.os.IBinder


class LocationForegroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    // adicionar lógica para iniciar o serviço em foreground depois (com notificação)
}