package com.rodrigodecastro.dentalflow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background

@Composable
fun AppointmentCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Cabeçalho skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Data/horário skeleton
                Column {
                    ShimmerBox(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.4f)
                    )
                    ShimmerBox(
                        modifier = Modifier
                            .height(12.dp)
                            .fillMaxWidth(0.3f)
                            .padding(top = 4.dp)
                    )
                }

                // Status skeleton
                ShimmerBox(
                    modifier = Modifier
                        .height(14.dp)
                        .fillMaxWidth(0.2f)
                )
            }

            // Nome do paciente skeleton
            ShimmerBox(
                modifier = Modifier
                    .height(18.dp)
                    .fillMaxWidth(0.7f)
                    .padding(top = 12.dp)
            )

            // Procedimento skeleton
            ShimmerBox(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.5f)
                    .padding(top = 8.dp)
            )

            // Telefone skeleton
            ShimmerBox(
                modifier = Modifier
                    .height(12.dp)
                    .fillMaxWidth(0.4f)
                    .padding(top = 8.dp)
            )
        }
    }
}

// COMPONENTE SHIMMER
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            )
            .clip(RoundedCornerShape(4.dp))
    ) {
        // Conteúdo vazio - só mostra o background
    }
}