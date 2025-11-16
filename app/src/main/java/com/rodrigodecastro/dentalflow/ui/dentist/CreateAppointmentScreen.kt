package com.rodrigodecastro.dentalflow.ui.dentist

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rodrigodecastro.dentalflow.data.models.Appointment
import com.rodrigodecastro.dentalflow.ui.components.TimePickerDialog
import com.rodrigodecastro.dentalflow.utils.PhoneVisualTransformation
import com.rodrigodecastro.dentalflow.viewmodel.AppointmentViewModel
import com.rodrigodecastro.dentalflow.viewmodel.CreateAppointmentState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAppointmentScreen(
    onSaveAppointment: () -> Unit,
    onCancel: () -> Unit,
    dentistId: String,
    appointmentViewModel: AppointmentViewModel
) {
    var patientName by remember { mutableStateOf("") }
    var patientPhone by remember { mutableStateOf("") }
    var patientEmail by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var procedure by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val createState by appointmentViewModel.createAppointmentState.collectAsState()
    val dentistName by appointmentViewModel.dentistName.collectAsState()

    val isFormValid = patientName.isNotEmpty() &&
            patientPhone.length >= 10 &&
            isEmailValid(patientEmail) &&
            date.isNotEmpty() &&
            time.isNotEmpty() &&
            procedure.isNotEmpty()

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
        topBar = { TopAppBar(title = { Text("Agendar Consulta") }) }
    ) { paddingValues ->
        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)) {
                        Text("Nova Consulta", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
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
                        OutlinedTextField(
                            value = patientEmail,
                            onValueChange = { patientEmail = it },
                            label = { Text("E-mail do Paciente") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            isError = patientEmail.isNotEmpty() && !isEmailValid(patientEmail)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = date,
                            onValueChange = {},
                            label = { Text("Data") },
                            enabled = false,
                            readOnly = true,
                            trailingIcon = { Icon(Icons.Default.DateRange, "Selecionar Data") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { showDatePicker = true },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = time,
                            onValueChange = {},
                            label = { Text("Horário") },
                            enabled = false,
                            readOnly = true,
                            trailingIcon = { Icon(Icons.Default.AccessTime, "Selecionar Horário") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { showTimePicker = true },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(value = procedure, onValueChange = { procedure = it }, label = { Text("Procedimento") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Observações") }, modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp), singleLine = false)

                        if (createState is CreateAppointmentState.Error) {
                            Text(text = (createState as CreateAppointmentState.Error).message, color = MaterialTheme.colorScheme.error)
                        }
                        if (createState is CreateAppointmentState.Success) {
                            Text(text = "✅ Consulta agendada com sucesso!", color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = {
                                    val newAppointment = Appointment(
                                        patientName = patientName,
                                        patientPhone = patientPhone,
                                        patientEmail = patientEmail,
                                        date = date,
                                        time = time,
                                        procedure = procedure,
                                        notes = notes,
                                        dentistId = dentistId,
                                        dentistName = dentistName,
                                        status = "Agendada",
                                        emailStatus = "PENDING"
                                    )
                                    appointmentViewModel.createAppointment(newAppointment)
                                },
                                enabled = isFormValid && createState !is CreateAppointmentState.Loading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                if (createState is CreateAppointmentState.Loading) {
                                    Text("Salvando...")
                                } else {
                                    Text("Agendar Consulta")
                                }
                            }
                            TextButton(onClick = onCancel, enabled = createState !is CreateAppointmentState.Loading) {
                                Text("Cancelar")
                            }
                        }
                    }
                }
            }
        }
    }

    if (createState is CreateAppointmentState.Success) {
        LaunchedEffect(createState) {
            kotlinx.coroutines.delay(1500)
            onSaveAppointment()
            appointmentViewModel.resetCreateState()
        }
    }
}

private fun isEmailValid(email: String): Boolean {
    return email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
