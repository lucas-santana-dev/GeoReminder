package br.com.plussapps.georeminder.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
/**
 * Composable que exibe e controla um valor de raio (em metros) através de um `Slider`.
 *
 * Responsabilidades:
 * - Renderiza um rótulo textual mostrando o valor inteiro atual do raio.
 * - Propaga alterações contínuas do usuário via `onRadiusChange`.
 *
 * Parâmetros:
 * @param radius Valor atual do raio (fonte externa; este composable é stateless).
 * @param onRadiusChange Callback disparado a cada mudança de posição do slider.
 * @param modifier `Modifier` para customização de layout.
 * @param valueRange Faixa permitida para o raio (padrão 50f..1000f), em metros.
 *
 * Comportamento:
 * - O slider é controlado de fora (state hoisting); não armazena estado interno do raio.
 * - Exibe o valor arredondado para inteiro apenas para leitura humana.
 *
 * Uso:
 * - Encapsular em componente de formulário de configuração de geofence / lembrete baseado em distância.
 * - Ajustar `valueRange` para cenários com limites diferentes.
 */
@Composable
fun RadiusSlider(
    radius: Float,
    onRadiusChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 50f..1000f
) {
    Column(modifier = modifier) {
        Text("Raio de ativação: ${radius.toInt()} metros")
        Slider(
            value = radius,
            onValueChange = onRadiusChange,
            valueRange = valueRange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RadiusSliderPreview() {
    var radius by remember { mutableStateOf(250f) }
    RadiusSlider(
        radius = radius,
        onRadiusChange = { radius = it }
    )
}