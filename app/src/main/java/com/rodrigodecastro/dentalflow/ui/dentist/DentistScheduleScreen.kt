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

/**
 * A tela principal do dentista, exibindo a lista de agendamentos.
 * Esta tela é reativa e observa as mudanças do `AppointmentViewModel`.
 */
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
    // Observa os StateFlows do ViewModel. A UI será recomposta automaticamente quando esses valores mudarem.
    val appointments by appointmentViewModel.appointments.collectAsState()
    val loading by appointmentViewModel.loadingState.collectAsState()
    val dentistName by appointmentViewModel.dentistName.collectAsState()

    // Estados locais da tela para controlar a busca e o diálogo de exclusão.
    var searchQuery by remember { mutableStateOf("") }
    var appointmentToDelete by remember { mutableStateOf<Appointment?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Estado para o componente "puxar para atualizar" (pull-to-refresh).
    val pullToRefreshState = rememberPullToRefreshState()

    // --- EFEITOS (LaunchedEffect) --- //

    // Carrega os dados iniciais (agendamentos e nome do dentista) assim que a tela
    // recebe um dentistId válido. O `key1 = dentistId` garante que isso só execute
    // uma vez ou se o ID do dentista mudar.
    LaunchedEffect(dentistId) {
        if (dentistId.isNotEmpty()) {
            appointmentViewModel.loadAppointments(dentistId)
            appointmentViewModel.loadDentistName(dentistId)
        }
    }

    // Gerencia a lógica do "puxar para atualizar".
    // Se o estado indicar que o usuário puxou a tela, ele aciona o recarregamento dos dados.
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            appointmentViewModel.loadAppointments(dentistId)
        }
    }

    // Observa o estado de carregamento do ViewModel para parar a animação do pull-to-refresh.
    // Isso desacopla a animação da UI da lógica de carregamento de dados.
    LaunchedEffect(loading) {
        if (!loading) {
            pullToRefreshState.endRefresh()
        }
    }

    // --- LÓGICA DE FILTRO E ORDENAÇÃO --- //

    // A lista de agendamentos exibida é um valor computado.
    // Primeiro, filtra a lista original com base na `searchQuery`.
    // Depois, ordena a lista filtrada pela data. O `try-catch` lida com datas mal formatadas.
    val filteredAndSortedAppointments = appointments.filter { appointment ->
        appointment.patientName.contains(searchQuery, ignoreCase = true)
    }.sortedBy { appointment ->
        try {
            LocalDate.parse(appointment.date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (_: Exception) {
            LocalDate.MIN // Joga datas inválidas para o início da lista.
        }
    }

    // --- UI (COMPOSABLES) --- //

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
        // O Box serve como container para o `PullToRefresh` e o conteúdo principal.
        // O `nestedScroll` conecta o gesto de scroll da lista com o estado do `pullToRefreshState`.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Seção do cabeçalho com saudação e campo de busca.
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

                // Lista principal de agendamentos.
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Exibe o esqueleto (shimmer) APENAS no primeiro carregamento.
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
                        // Mensagem para o caso de lista vazia (após o loading).
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

                // Seção de botões de ação no rodapé.
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

            // O container da animação do "puxar para atualizar".
            // Fica "por cima" do conteúdo, alinhado ao topo.
            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    // Diálogo de confirmação para exclusão de agendamento.
    // Só é exibido quando `showDeleteConfirmation` é true.
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
