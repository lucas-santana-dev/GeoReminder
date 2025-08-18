package br.com.plussapps.georeminder.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.Geofence
import br.com.plussapps.georeminder.notifications.NotificationUtils
import br.com.plussapps.georeminder.data.ReminderDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("GeofenceReceiver", "onReceive chamado!")

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null) {
            Log.e("GeofenceReceiver", "GeofencingEvent é nulo!")
            return
        }
        if (geofencingEvent.hasError()) {
            Log.e("GeofenceReceiver", "Erro no GeofencingEvent: ${geofencingEvent.errorCode}")
            return
        }

        val transition = geofencingEvent.geofenceTransition
        Log.d("GeofenceReceiver", "Transition: $transition")

        val triggeringGeofences = geofencingEvent.triggeringGeofences
        Log.d("GeofenceReceiver", "Qtde Geofences disparados: ${triggeringGeofences?.size ?: 0}")

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            triggeringGeofences?.forEach { geofence ->
                val reminderId = geofence.requestId
                Log.d("GeofenceReceiver", "Disparou geofence com reminderId: $reminderId")
                CoroutineScope(Dispatchers.IO).launch {
                    val db = ReminderDatabase.getInstance(context)
                    val reminderEntity = db.reminderDao().getById(reminderId.toLongOrNull() ?: -1)
                    Log.d("GeofenceReceiver", "Resultado busca no banco: $reminderEntity")
                    val title = reminderEntity?.title ?: "Você chegou ao local do lembrete!"
                    val description = reminderEntity?.description ?: "Lembrete ID: $reminderId"
                    Log.d("GeofenceReceiver", "Título: $title | Descrição: $description")
                    NotificationUtils.showReminderNotification(
                        context,
                        title,
                        description
                    )
                }
            }
        }
    }
}