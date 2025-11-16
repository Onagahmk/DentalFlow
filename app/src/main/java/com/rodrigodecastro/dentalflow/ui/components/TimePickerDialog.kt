package com.rodrigodecastro.dentalflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Um Composable que encapsula o `TimePicker` do Material 3 dentro de um `Dialog` customizado.
 * Isso permite reutilizar a lógica de exibição do seletor de tempo em diferentes telas.
 *
 * @param onDismissRequest Callback acionado quando o usuário tenta fechar o diálogo (ex: clicando fora).
 * @param onConfirm Callback acionado quando o usuário clica no botão "OK".
 * @param state O estado do `TimePicker`, que armazena a hora e o minuto selecionados. 
 *              Este estado é geralmente criado fora do Composable usando `rememberTimePickerState()`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    state: TimePickerState
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.large) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // O componente principal do Material 3 para seleção de tempo.
                TimePicker(state = state)
                Spacer(modifier = Modifier.height(16.dp))
                // Botões de ação para confirmar ou cancelar a seleção.
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onConfirm) {
                        Text("OK")
                    }
                }
            }
        }
    }
}