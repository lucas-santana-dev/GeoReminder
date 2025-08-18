package br.com.plussapps.georeminder.ui.composables

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.SearchByTextRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "LocationSearchBar"

/**
 * Composable que renderiza uma barra de busca de locais usando a Places API (Search By Text) com debounce.
 *
 * Responsabilidades:
 * - Captura e mantém o texto digitado pelo usuário.
 * - Executa busca remota após 300ms sem novas teclas (debounce manual via coroutine).
 * - Limita resultados a 5 itens e exibe lista clicável.
 * - Dispara `onLocationSelected` ao usuário escolher um lugar com `LatLng` válido.
 *
 * Estados internos:
 * - `query`: texto atual.
 * - `results`: lista de `Place` retornados.
 * - `loading`: indica requisição em andamento.
 * - `searchJob`: `Job` atual para cancelamento ao alterar a query.
 *
 * Efeitos / Concorrência:
 * - Cada alteração de texto (>= 3 caracteres) cancela busca anterior e agenda nova após `delay(300)`.
 * - Uso de listeners (`addOnSuccessListener` / `addOnFailureListener`) para atualizar estado reativo.
 *
 * Parâmetros:
 * @param modifier Ajustes de layout externos.
 * @param onLocationSelected Callback invocado com `LatLng` quando o usuário seleciona um resultado válido.
 *
 * Tratamento de erros:
 * - Falhas limpam resultados e desmarcam `loading`.
 * - Logs detalhados com a constante `TAG`.
 *
 * Limitações:
 * - Sem paginação ou cache local.
 * - Debounce e limite de resultados são fixos (300ms / 5).
 * - Não exibe mensagem de vazio ou erro ao usuário (apenas logs).
 *
 * Possíveis extensões:
 * - Botão para limpar texto.
 * - Expor estados via parâmetros (lifting state up).
 * - Mensagens de feedback (erro, nenhum resultado).
 */

@Composable
fun LocationSearchBar(
    modifier: Modifier = Modifier,
    onLocationSelected: (LatLng) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(listOf<Place>()) }
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }
    val coroutineScope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                results = emptyList()
                searchJob?.cancel()

                if (it.length > 2) {
                    loading = true
                    searchJob = coroutineScope.launch {
                        delay(300) // debounce
                        val placeFields = listOf(
                            Place.Field.ID,
                            Place.Field.DISPLAY_NAME,
                            Place.Field.LAT_LNG
                        )
                        val request = SearchByTextRequest.builder(it, placeFields)
                            .setMaxResultCount(5)
                            .build()
                        placesClient.searchByText(request)
                            .addOnSuccessListener { response ->
                                results = response.places
                                loading = false
                                Log.d(TAG, "Busca OK: ${response.places.size} resultados")
                            }
                            .addOnFailureListener { exception ->
                                results = emptyList()
                                loading = false
                                Log.e(TAG, "Erro Places: ${exception.message}", exception)
                            }
                    }
                } else {
                    loading = false
                }
            },
            label = { Text("Buscar local") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (results.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Column {
                    results.forEach { place ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clickable {
                                    query = place.displayName?.toString() ?: place.name ?: ""
                                    results = emptyList()
                                    val latLng = place.latLng
                                    if (latLng != null) {
                                        onLocationSelected(LatLng(latLng.latitude, latLng.longitude))
                                        Log.d(TAG, "Selecionado: ${place.displayName?.toString()}, lat: ${latLng.latitude}, lng: ${latLng.longitude}")
                                    } else {
                                        Log.e(TAG, "LatLng nulo para o lugar selecionado.")
                                    }
                                },
                            elevation = CardDefaults.elevatedCardElevation(2.dp)
                        ) {
                            Text(
                                text = place.displayName?.toString() ?: place.name ?: "Sem nome",
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}