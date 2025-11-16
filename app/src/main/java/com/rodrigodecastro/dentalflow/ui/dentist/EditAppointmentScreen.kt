package com.rodrigodecastro.dentalflow.ui.dentist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rodrigodecastro.dentalflow.data.models.Appointment
import com.rodrigodecastro.dentalflow.ui.components.TimePickerDialog
import com.rodrigodecastro.dentalflow.utils.PhoneVisualTransformation
import com.rodrigodecastro.dentalflow.viewmodel.AppointmentViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAppointmentScreen(
    appointmentId: String,
    appointmentViewModel: AppointmentViewModel,
    onAppointmentUpdated: () -> Unit,
    onCancel: () -> Unit
) {
    val appointments by appointmentViewModel.appointments.collectAsState()
    val appointment = remember(appointments, appointmentId) {
        appointments.find { it.id == appointmentId }
    }

    var patientName by remember(appointment) { mutableStateOf(appointment?.patientName ?: "") }
    var patientPhone by remember(appointment) { mutableStateOf(appointment?.patientPhone ?: "") }
    var patientEmail by remember(appointment) { mutableStateOf(appointment?.patientEmail ?: "") }
    var date by remember(appointment) { mutableStateOf(appointment?.date ?: "") }
    var time by remember(appointment) { mutableStateOf(appointment?.time ?: "") }
    var procedure by remember(appointment) { mutableStateOf(appointment?.procedure ?: "") }
    var notes by remember(appointment) { mutableStateOf(appointment?.notes ?: "") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                            date = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onConfirm = {
                time = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                showTimePicker = false
            },
            state = timePickerState
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Editar Agendamento") }) }
    ) { paddingValues ->
        Surface(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (appointment == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                            Text("Editar Agendamento", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(32.dp))

                            OutlinedTextField(value = patientName, onValueChange = { patientName = it }, label = { Text("Nome do Paciente") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = patientPhone,
                                onValueChange = { patientPhone = it.filter { char -> char.isDigit() }.take(11) },
                                label = { Text("Telefone") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                visualTransformation = PhoneVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(value = patientEmail, onValueChange = { patientEmail = it }, label = { Text("E-mail do Paciente") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = date,
                                onValueChange = {},
                                label = { Text("Data") },
                                enabled = false,
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Default.DateRange, "Selecionar Data") },
                                modifier = Modifier.fillMaxWidth().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { showDatePicker = true },
                                colors = OutlinedTextFieldDefaults.colors(disabledBorderColor = MaterialTheme.colorScheme.outline, disabledTextColor = MaterialTheme.colorScheme.onSurface)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = time,
                                onValueChange = {},
                                label = { Text("Horário") },
                                enabled = false,
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Default.AccessTime, "Selecionar Horário") },
                                modifier = Modifier.fillMaxWidth().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { showTimePicker = true },
                                colors = OutlinedTextFieldDefaults.colors(disabledBorderColor = MaterialTheme.colorScheme.outline, disabledTextColor = MaterialTheme.colorScheme.onSurface)
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(value = procedure, onValueChange = { procedure = it }, label = { Text("Procedimento") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Observações") }, modifier = Modifier.fillMaxWidth().height(100.dp), singleLine = false)

                            Spacer(modifier = Modifier.height(32.dp))
                            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = {
                                        val updatedAppointment = appointment.copy(
                                            patientName = patientName,
                                            patientPhone = patientPhone,
                                            patientEmail = patientEmail,
                                            date = date,
                                            time = time,
                                            procedure = procedure,
                                            notes = notes
                                        )
                                        appointmentViewModel.updateAppointment(updatedAppointment)
                                        onAppointmentUpdated()
                                    },
                                    enabled = true, // Adicionar validação depois
                                    modifier = Modifier.fillMaxWidth().height(50.dp)
                                ) {
                                    Text("Salvar Alterações")
                                }
                                TextButton(onClick = onCancel) {
                                    Text("Cancelar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
