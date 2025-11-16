package com.rodrigodecastro.dentalflow.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rodrigodecastro.dentalflow.R

/**
 * Um serviço que estende `FirebaseMessagingService` para lidar com o recebimento de mensagens
 * do Firebase Cloud Messaging (FCM) quando o aplicativo está em primeiro ou segundo plano.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Chamado quando uma nova mensagem FCM é recebida.
     * Este método é o ponto de entrada para notificações push.
     *
     * @param remoteMessage O objeto que contém os dados da mensagem recebida do Firebase.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // O Firebase trata as notificações automaticamente quando o app está em segundo plano.
        // Este código é executado principalmente quando o app está em primeiro plano.
        // Ele extrai o título e o corpo da notificação e chama a função para exibi-la.
        remoteMessage.notification?.let {
            sendNotification(it.title, it.body)
        }
    }

    /**
     * Cria e exibe uma notificação simples no sistema Android.
     *
     * @param title O título da notificação.
     * @param messageBody O corpo da mensagem da notificação.
     */
    private fun sendNotification(title: String?, messageBody: String?) {
        val channelId = "default_channel_id" // ID único para o canal de notificação.

        // Constrói a notificação usando o NotificationCompat para garantir compatibilidade.
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ícone que aparece na barra de status.
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true) // A notificação é removida quando o usuário clica nela.

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // A partir do Android 8.0 (Oreo, API 26), todos as notificações precisam pertencer a um canal.
        // Este bloco de código cria o canal se ele ainda não existir.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificações Gerais", // Nome do canal visível para o usuário nas configurações do app.
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Exibe a notificação. O ID `0` pode ser usado para atualizar esta notificação no futuro se necessário.
        notificationManager.notify(0, notificationBuilder.build())
    }

    /**
     * Chamado quando um novo token FCM é gerado para a instância do aplicativo.
     * Este token é o endereço único do dispositivo para o qual o Firebase pode enviar mensagens.
     * Aqui você poderia, por exemplo, enviar o token para o seu servidor para salvar e
     * direcionar notificações para este usuário específico no futuro.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Ex: sendTokenToServer(token)
    }
}
