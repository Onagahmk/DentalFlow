package com.rodrigodecastro.dentalflow.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.rodrigodecastro.dentalflow.R
import com.rodrigodecastro.dentalflow.data.models.Appointment

object NotificationHelper {

    private const val CHANNEL_ID = "new_appointment_channel"

    fun showNewAppointmentNotification(context: Context, appointment: Appointment) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Cria o canal de notificação para Android 8.0 (Oreo) e superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Novas Consultas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificações para novas consultas agendadas"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Constrói a notificação
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ícone do seu app
            .setContentTitle("Nova Consulta Agendada!")
            .setContentText("Paciente: ${appointment.patientName} em ${appointment.date}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // A notificação some ao ser tocada
            .build()

        // Exibe a notificação usando um ID único baseado no appointment
        notificationManager.notify(appointment.id.hashCode(), notification)
    }
}
