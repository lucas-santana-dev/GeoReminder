package br.com.plussapps.georeminder.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.Geofence
import br.com.plussapps.georeminder.notifications.NotificationUtils

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return
        if (geofencingEvent.hasError()) return

        val transition = geofencingEvent.geofenceTransition
        val triggeringGeofences = geofencingEvent.triggeringGeofences

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            triggeringGeofences?.forEach { geofence ->
                val reminderId = geofence.requestId
                NotificationUtils.showReminderNotification(
                    context,
                    "Você chegou ao local do lembrete!",
                    "Lembrete ID: $reminderId"
                )
            }
        }
    }
}