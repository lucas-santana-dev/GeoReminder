package br.com.plussapps.georeminder.ui.viewmodels

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import br.com.plussapps.georeminder.domain.model.Location
import br.com.plussapps.georeminder.domain.model.Reminder
import br.com.plussapps.georeminder.domain.usecase.CreateReminderUseCase
import br.com.plussapps.georeminder.geofencing.GeofenceManager // ADICIONADO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

data class ReminderFormUiState(
    val title: String = "",
    val description: String = "",
    val location: LatLng? = null,
    val radius: Float = 100f,
    val locationPermissionDenied: Boolean = false
)

// Adiciona o GeofenceManager ao construtor (injete via Koin)
class ReminderFormViewModel(
    application: Application,
    private val createReminderUseCase: CreateReminderUseCase,
    private val geofenceManager: GeofenceManager // NOVO
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ReminderFormUiState())
    val uiState: StateFlow<ReminderFormUiState> = _uiState

    init {
        fetchCurrentLocation()
    }

    private fun fetchCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication())
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        Timber.d("Localização obtida: ${location.latitude}, ${location.longitude}")
                        _uiState.value = _uiState.value.copy(
                            location = LatLng(location.latitude, location.longitude),
                            locationPermissionDenied = false
                        )
                    } else {
                        Timber.w("Localização retornou null")
                    }
                }
                .addOnFailureListener { error ->
                    Timber.e(error, "Erro ao buscar localização")
                    _uiState.value = _uiState.value.copy(
                        locationPermissionDenied = true
                    )
                }
        } catch (e: SecurityException) {
            Timber.e(e, "Permissão de localização negada")
            _uiState.value = _uiState.value.copy(
                locationPermissionDenied = true
            )
        }
    }

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(title = value)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun onLocationChange(value: LatLng) {
        _uiState.value = _uiState.value.copy(location = value)
    }

    fun onRadiusChange(value: Float) {
        _uiState.value = _uiState.value.copy(radius = value)
    }

    fun onSave(onSuccess: () -> Unit, onMissingPermission: () -> Unit) {
        val state = _uiState.value
        val locationDomain = Location(
            latitude = state.location?.latitude ?: 0.0,
            longitude = state.location?.longitude ?: 0.0,
            radius = state.radius
        )
        val reminder = Reminder(
            title = state.title,
            description = state.description,
            location = locationDomain
        )
        viewModelScope.launch {
            val result = createReminderUseCase(reminder)
            if (result.isSuccess) {
                val reminderId = result.getOrNull()
                if (reminderId != null && state.location != null) {
                    val context = getApplication<Application>()
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        onMissingPermission()
                        return@launch
                    }
                    geofenceManager.registerGeofence(
                        reminderId = reminderId.toString(),
                        latLng = state.location,
                        radius = state.radius
                    )
                    Timber.d("Geofence registrado para lembrete $reminderId")
                }
                onSuccess()
            }
            // Pode adicionar tratamento de erro aqui
        }
    }
}