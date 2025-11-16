package com.rodrigodecastro.dentalflow.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A tela inicial (splash screen) do aplicativo.
 * Seu único propósito é apresentar a marca e fornecer um ponto de entrada
 * para o fluxo de login.
 */
@Composable
fun HomeScreen(
    // Callback para notificar o `AppNavigation` que o botão de login foi clicado.
    onLoginClick: () -> Unit
) {
    Scaffold {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Título principal do aplicativo.
                Text(
                    text = "🦷 DentalFlow",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Sistema de Agendamento",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(64.dp))

                // O único ponto de ação da tela, que inicia o fluxo de login.
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .height(56.dp)
                ) {
                    Text(
                        text = "Acessar Sistema",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onLoginClick = { }
    )
}