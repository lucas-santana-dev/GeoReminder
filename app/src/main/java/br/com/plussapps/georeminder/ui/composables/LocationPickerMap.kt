package br.com.plussapps.georeminder.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


/**
 * Composable que exibe um mapa (Google Maps) para seleção de localização com `Marker` arrastável
 * e um `Circle` visualizando um raio ao redor do ponto escolhido.
 *
 * Responsabilidades:
 * - Centraliza e anima a câmera conforme mudanças externas de `location`.
 * - Mantém um `MarkerState` interno sincronizado com a localização fornecida.
 * - Notifica o chamador quando o usuário arrasta o marker via `onLocationChange`.
 * - Desenha um círculo representando o alcance definido por `radius` (em metros).
 *
 * Parâmetros:
 * @param location Localização atual selecionada (fonte da verdade externa).
 * @param radius Raio em metros usado para desenhar o círculo ao redor do marker.
 * @param onLocationChange Callback disparado quando o usuário altera a posição arrastando o marker.
 * @param modifier Modifier para customização externa do layout.
 *
 * Efeitos colaterais:
 * - `LaunchedEffect(location)`: atualiza a posição do marker e anima a câmera quando a localização externa muda.
 * - `LaunchedEffect(markerState.position)`: propaga alterações iniciadas pelo usuário (evita loop checando diferença).
 *
 * Detalhes de implementação:
 * - O zoom inicial é fixo em 15f.
 * - Para controle mais avançado da câmera (zoom, tilt, bearing), considere expor `CameraPositionState`.
 * - `radius` é convertido para `Double` para atender a API do `Circle`.
 */
@Composable
fun LocationPickerMap(
    location: LatLng,
    radius: Float,
    onLocationChange: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    val markerState = remember { MarkerState(position = location) }

    // Camera que segue a posição
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 15f)
    }

    LaunchedEffect(location) {
        if (markerState.position != location) {
            markerState.position = location
            cameraPositionState.animate(CameraUpdateFactory.newLatLng(location))
        }
    }

    LaunchedEffect(markerState.position) {
        if (markerState.position != location) {
            onLocationChange(markerState.position)
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = markerState,
            draggable = true,
        )
        Circle(
            center = markerState.position,
            radius = radius.toDouble(),
            fillColor = Color(0x2200FF00),
            strokeColor = Color(0xFF00FF00),
            strokeWidth = 2f
        )
    }
}
