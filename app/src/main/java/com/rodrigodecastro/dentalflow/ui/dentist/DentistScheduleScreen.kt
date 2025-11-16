package com.rodrigodecastro.dentalflow.ui.dentist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rodrigodecastro.dentalflow.data.models.Appointment
import com.rodrigodecastro.dentalflow.ui.components.AppointmentCard
import com.rodrigodecastro.dentalflow.ui.components.AppointmentCardSkeleton
import com.rodrigodecastro.dentalflow.viewmodel.AppointmentViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DentistScheduleScreen(
    onLogout: () -> Unit,
    onCreateAppointment: () -> Unit,
    onEditAppointment: (String) -> Unit,
    appointmentViewModel: AppointmentViewModel,
    dentistId: String
) {
    val appointments by appointmentViewModel.appointments.collectAsState()
    val loading by appointmentViewModel.loadingState.collectAsState()
    val dentistName by appointmentViewModel.dentistName.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var appointmentToDelete by remember { mutableStateOf<Appointment?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val pullToRefreshState = rememberPullToRefreshState()

    // Efeito para carregar os dados iniciais
    LaunchedEffect(dentistId) {
        if (dentistId.isNotEmpty()) {
            appointmentViewModel.loadAppointments(dentistId)
            appointmentViewModel.loadDentistName(dentistId)
        }
    }

    // Efeito para lidar com o refresh
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            appointmentViewModel.loadAppointments(dentistId)
        }
    }

    // Efeito para parar a animação do refresh quando o 'loading' terminar
    LaunchedEffect(loading) {
        if (!loading) {
            pullToRefreshState.endRefresh()
        }
    }

    val filteredAndSortedAppointments = appointments.filter { appointment ->
        appointment.patientName.contains(searchQuery, ignoreCase = true)
    }.sortedBy { appointment ->
        try {
            LocalDate.parse(appointment.date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (_: Exception) {
            LocalDate.MIN
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Agenda de $dentistName") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(pullToRefreshState.nestedScrollConnection) // Conexão do scroll
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "👋 Olá, $dentistName",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (appointments.isNotEmpty()) "${filteredAndSortedAppointments.size} consultas agendadas" else "Nenhuma consulta encontrada",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("🔍 Buscar por nome...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (loading && appointments.isEmpty()) {
                        items(5) { _ -> AppointmentCardSkeleton() }
                    } else if (filteredAndSortedAppointments.isNotEmpty()) {
                        items(filteredAndSortedAppointments) { appointment ->
                            AppointmentCard(
                                appointment = appointment,
                                onEditClick = { onEditAppointment(appointment.id) },
                                onDeleteClick = {
                                    appointmentToDelete = appointment
                                    showDeleteConfirmation = true
                                }
                            )
                        }
                    } else {
                        item {
                            Box(modifier = Modifier.fillParentMaxSize()) {
                                Column(
                                    modifier = Modifier.align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("📅", style = MaterialTheme.typography.displayLarge)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Nenhuma consulta agendada",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onCreateAppointment,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("➕ Agendar Nova Consulta")
                    }
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("🚪 Sair do Sistema")
                    }
                }
            }

            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    if (showDeleteConfirmation && appointmentToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
                appointmentToDelete = null
            },
            title = { Text("Excluir Consulta") },
            text = { Text("Tem certeza que deseja excluir esta consulta?") },
            confirmButton = {
                TextButton(onClick = {
                    appointmentViewModel.deleteAppointment(appointmentToDelete!!.id, dentistId)
                    showDeleteConfirmation = false
                    appointmentToDelete = null
                }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteConfirmation = false
                    appointmentToDelete = null
                }) { Text("Cancelar") }
            }
        )
    }
}