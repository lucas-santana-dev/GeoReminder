package br.com.plussapps.georeminder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.plussapps.georeminder.ui.screens.ReminderFormScreen
import br.com.plussapps.georeminder.ui.theme.GeoReminderTheme

class MainActivity : ComponentActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Solicita permissão de localização
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(this, locationPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(locationPermission), PERMISSION_REQUEST_CODE)
        }

        // Pode mostrar sua tela normalmente, o ViewModel deve lidar com a ausência de permissão!
        setContent {
            GeoReminderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ReminderFormScreen(
                        onSaveSuccess = {
                            finish()
                        }
                    )
                }
            }
        }
    }

    // Opcional: lidando com o resultado da permissão
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permissão concedida!
//                // Aqui você pode acionar lógica extra se precisar
//            } else {
//                // Permissão negada, avise o usuário ou ajuste o app
//            }
//        }
//    }
}