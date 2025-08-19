package br.com.plussapps.georeminder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.plussapps.georeminder.ui.screens.ReminderFormScreen
import br.com.plussapps.georeminder.ui.theme.GeoReminderTheme

class MainActivity : ComponentActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 123
        const val PERMISSION_BG_REQUEST_CODE = 124
        const val PERMISSION_NOTIFICATION_REQUEST_CODE = 125
        const val PERMISSION_OVERLAY_REQUEST_CODE = 126
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Permissão de localização foreground
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(this, locationPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(locationPermission), PERMISSION_REQUEST_CODE)
        }

        // Permissão de localização em background (Android Q+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val bgPermission = Manifest.permission.ACCESS_BACKGROUND_LOCATION
            if (ContextCompat.checkSelfPermission(this, bgPermission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(bgPermission), PERMISSION_BG_REQUEST_CODE)
            }
        }

        // Permissão de notificação (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, notificationPermission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(notificationPermission), PERMISSION_NOTIFICATION_REQUEST_CODE)
            }
        }

        // Permissão de sobreposição (overlay) - Opcional, só se for usar overlay!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, PERMISSION_OVERLAY_REQUEST_CODE)
        }

        setContent {
            GeoReminderTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .verticalScroll(scrollState)
                    ) {
                        ReminderFormScreen(
                            onSaveSuccess = { finish() }
                        )
                    }
                }
            }
        }
    }

    // Exemplo de tratamento do resultado das permissões
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                PERMISSION_REQUEST_CODE -> {
                    // Permissão de localização negada, avise o usuário!
                }
                PERMISSION_BG_REQUEST_CODE -> {
                    // Permissão de localização em background negada
                }
                PERMISSION_NOTIFICATION_REQUEST_CODE -> {
                    // Permissão de notificação negada
                }
                PERMISSION_OVERLAY_REQUEST_CODE -> {
                    // Permissão de sobreposição negada
                }
            }
        }
        // Se quiser, pode exibir um Snackbar para avisar o usuário
    }
}