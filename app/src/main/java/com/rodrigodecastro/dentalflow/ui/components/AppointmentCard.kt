package com.rodrigodecastro.dentalflow.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rodrigodecastro.dentalflow.data.models.Appointment

/**
 * Um Composable que exibe as informações de um único agendamento em um card.
 * Este é um componente reutilizável e de visualização pura (stateless).
 *
 * @param appointment O objeto `Appointment` contendo os dados a serem exibidos.
 * @param onEditClick Callback acionado quando o usuário clica em "Editar".
 * @param onDeleteClick Callback acionado quando o usuário clica em "Excluir".
 * @param modifier Modificador para customização externa.
 */
@Composable
fun AppointmentCard(
    appointment: Appointment,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // A cor da borda do card muda para indicar um estado de erro no envio do e-mail.
    // Isso fornece um feedback visual importante diretamente na lista.
    val cardBorderColor = if (appointment.emailStatus == "ERROR") {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, cardBorderColor), // Aplica a borda condicional.
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // --- Seção de Cabeçalho: Data, Hora e Status ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${appointment.date} • ${appointment.time}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                // A cor do status também é condicional para destaque.
                Text(
                    text = appointment.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = when (appointment.status) {
                        "Confirmada" -> MaterialTheme.colorScheme.primary
                        "Cancelada" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            // --- Detalhes do Paciente e Procedimento ---
            Text(
                text = appointment.patientName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = appointment.procedure,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (appointment.patientPhone.isNotEmpty()) {
                Text(
                    text = "📞 ${appointment.patientPhone}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (appointment.notes.isNotEmpty()) {
                Text(
                    text = "📝 ${appointment.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // --- Indicador de Erro de E-mail ---
            // Esta seção só é exibida se houver um erro no envio do e-mail.
            if (appointment.emailStatus == "ERROR") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Erro de E-mail",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Falha no envio do e-mail",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // --- Botões de Ação ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEditClick) {
                    Text("Editar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            }
        }
    }
}