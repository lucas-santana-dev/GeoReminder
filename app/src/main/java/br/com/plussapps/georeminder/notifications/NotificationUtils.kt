package br.com.plussapps.georeminder.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import br.com.plussapps.georeminder.R
object NotificationUtils {
    private const val CHANNEL_ID = "georeminder_channel"
    private const val CHANNEL_NAME = "Lembretes de Localização"

    fun showReminderNotification(context: Context, title: String, content: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Canal de notificação
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT // IMPORTANCE_DEFAULT é padrão para notificações normais
            ).apply {
                description = "Notificações ao chegar ao local do lembrete"
            }
            manager.createNotificationChannel(channel)
        }

        // Intent normal para abrir tela de detalhes
        val detailsIntent = Intent(context, ReminderDetailsActivity::class.java).apply {
            putExtra("title", title)
            putExtra("content", content)
        }
        val detailsPendingIntent = PendingIntent.getActivity(
            context,
            0,
            detailsIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(detailsPendingIntent) // Não usa FullScreenIntent!
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}