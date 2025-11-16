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

/**
 * Um Composable que exibe uma versão "esqueleto" (skeleton) do `AppointmentCard`.
 * É usado para indicar que o conteúdo está sendo carregado (Shimmer effect).
 * Melhora a experiência do usuário ao fornecer um feedback visual imediato.
 */
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
            // Cabeçalho do esqueleto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                ShimmerBox(
                    modifier = Modifier
                        .height(14.dp)
                        .fillMaxWidth(0.2f)
                )
            }

            // Placeholder para o nome do paciente
            ShimmerBox(
                modifier = Modifier
                    .height(18.dp)
                    .fillMaxWidth(0.7f)
                    .padding(top = 12.dp)
            )

            // Placeholder para o procedimento
            ShimmerBox(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.5f)
                    .padding(top = 8.dp)
            )

            // Placeholder para o telefone
            ShimmerBox(
                modifier = Modifier
                    .height(12.dp)
                    .fillMaxWidth(0.4f)
                    .padding(top = 8.dp)
            )
        }
    }
}

/**
 * O bloco de construção básico para o efeito shimmer/skeleton.
 * É apenas uma caixa com um fundo cinza semitransparente e cantos arredondados.
 * @param modifier O modificador para controlar o tamanho e a forma da caixa.
 */
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
        // O conteúdo é vazio, pois o objetivo é apenas exibir o fundo como um placeholder.
    }
}
