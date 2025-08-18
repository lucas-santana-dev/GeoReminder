import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import br.com.plussapps.georeminder.domain.model.Location
import br.com.plussapps.georeminder.domain.model.Reminder
import br.com.plussapps.georeminder.domain.usecase.CreateReminderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ReminderFormUiState(
    val title: String = "",
    val description: String = "",
    val location: LatLng = LatLng(-23.5505, -46.6333), // Default: São Paulo
    val radius: Float = 100f
)

class ReminderFormViewModel(
    application: Application,
    private val createReminderUseCase: CreateReminderUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ReminderFormUiState())
    val uiState: StateFlow<ReminderFormUiState> = _uiState

    init {
        fetchCurrentLocation()
    }

    private fun fetchCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication())
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    _uiState.value = _uiState.value.copy(
                        location = LatLng(location.latitude, location.longitude)
                    )
                }
            }
        } catch (e: SecurityException) {
            // Permissão não concedida, mantém o default
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

    fun onSave(onSuccess: () -> Unit) {
        val state = _uiState.value
        val locationDomain = Location(
            latitude = state.location.latitude,
            longitude = state.location.longitude,
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
                onSuccess()
            }
            // Pode adicionar tratamento de erro aqui
        }
    }
}