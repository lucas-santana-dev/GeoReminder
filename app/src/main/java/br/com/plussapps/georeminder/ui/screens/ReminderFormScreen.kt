package br.com.plussapps.georeminder.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.plussapps.georeminder.ui.composables.LocationPickerMap
import br.com.plussapps.georeminder.ui.composables.LocationSearchBar
import br.com.plussapps.georeminder.ui.composables.RadiusSlider
import br.com.plussapps.georeminder.ui.viewmodels.ReminderFormViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("MissingPermission")
@Composable
fun ReminderFormScreen(
    viewModel: ReminderFormViewModel = koinViewModel(),
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoadingLocation = uiState.location == null
    val locationPermissionDenied = uiState.locationPermissionDenied == true

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = uiState.title,
            onValueChange = { viewModel.onTitleChange(it) },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.description,
            onValueChange = { viewModel.onDescriptionChange(it) },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Text("Selecione o local do lembrete:")
        Spacer(Modifier.height(16.dp))
        LocationSearchBar(
            modifier = Modifier.fillMaxWidth(),
            onLocationSelected = { latLng ->
                viewModel.onLocationChange(latLng) // Atualiza no ViewModel
            }
        )
        Spacer(Modifier.height(16.dp))
        when {
            locationPermissionDenied -> {
                Text("Permissão de localização negada. Conceda a permissão nas configurações do app para escolher o local.")
            }
            isLoadingLocation -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.width(8.dp))
                    Text("Obtendo localização atual...")
                }
            }
            else -> {
                LocationPickerMap(
                    location = uiState.location!!,
                    radius = uiState.radius,
                    onLocationChange = { viewModel.onLocationChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        RadiusSlider(
            radius = uiState.radius,
            onRadiusChange = { viewModel.onRadiusChange(it) }
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.onSave { onSaveSuccess() }
            },
            enabled = uiState.title.isNotBlank() && uiState.location != null
        ) {
            Text("Salvar lembrete")
        }
    }
}