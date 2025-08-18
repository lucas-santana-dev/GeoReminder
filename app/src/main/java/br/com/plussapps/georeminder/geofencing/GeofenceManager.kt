package br.com.plussapps.georeminder.geofencing

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class GeofenceManager(private val context: Context) {
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun registerGeofence(reminderId: String, latLng: LatLng, radius: Float) {
        val geofence = Geofence.Builder()
            .setRequestId(reminderId)
            .setCircularRegion(latLng.latitude, latLng.longitude, radius)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(
            geofencingRequest,
            getGeofencePendingIntent()
        ).addOnSuccessListener {
            // Geofence registrado com sucesso
        }.addOnFailureListener {
            // Falha ao registrar geofence
        }
    }

    fun removeGeofence(reminderId: String) {
        geofencingClient.removeGeofences(listOf(reminderId)).addOnSuccessListener {
            // Geofence removido com sucesso
        }.addOnFailureListener {
            // Falha ao remover geofence
        }
    }

    private fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
}