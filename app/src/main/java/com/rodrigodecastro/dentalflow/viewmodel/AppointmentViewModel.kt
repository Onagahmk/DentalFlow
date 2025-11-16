package com.rodrigodecastro.dentalflow.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rodrigodecastro.dentalflow.data.models.Appointment
import com.rodrigodecastro.dentalflow.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsável por toda a lógica de negócios relacionada aos agendamentos.
 * Ele serve como uma ponte entre a UI (telas) e a camada de dados (Repository).
 */
class AppointmentViewModel(
    // Injeção de dependência do repositório para facilitar testes futuros.
    private val appointmentRepository: AppointmentRepository = AppointmentRepository()
) : ViewModel() {

    // --- StateFlows para a UI --- //

    // StateFlow privado para armazenar a lista de agendamentos. Apenas o ViewModel pode modificar.
    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    // StateFlow público e imutável para a UI observar as mudanças na lista de agendamentos.
    val appointments = _appointments.asStateFlow()

    // Armazena o nome do dentista logado.
    private val _dentistName = MutableStateFlow("Dr(a). ...")
    val dentistName = _dentistName.asStateFlow()

    // Gerencia o estado do processo de criação de um novo agendamento (Ocioso, Carregando, Sucesso, Erro).
    private val _createAppointmentState = MutableStateFlow<CreateAppointmentState>(CreateAppointmentState.Idle)
    val createAppointmentState = _createAppointmentState.asStateFlow()

    // Controla o estado de carregamento da lista principal (usado para o pull-to-refresh e loading inicial).
    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    /**
     * Carrega a lista de agendamentos de um dentista específico a partir do repositório.
     * Atualiza o `_loadingState` para true no início e false no final da operação.
     */
    fun loadAppointments(dentistId: String) {
        _loadingState.value = true
        viewModelScope.launch {
            appointmentRepository.getAppointmentsByDentist(dentistId).onSuccess {
                _appointments.value = it
            }
            // Garante que o estado de loading seja desativado mesmo se a busca falhar.
            _loadingState.value = false
        }
    }

    /**
     * Busca e atualiza o nome do dentista com base no ID fornecido.
     */
    fun loadDentistName(dentistId: String) {
        viewModelScope.launch {
            appointmentRepository.getDentistName(dentistId).onSuccess {
                _dentistName.value = it
            }
        }
    }

    /**
     * Orquestra a criação de um novo agendamento.
     * 1. Define o estado para Loading.
     * 2. Chama o repositório para criar o agendamento no Firestore.
     * 3. Se for sucesso, enfileira o e-mail de confirmação e recarrega a lista.
     * 4. Atualiza o estado para Success ou Error, passando a mensagem apropriada para a UI.
     */
    fun createAppointment(appointment: Appointment) {
        _createAppointmentState.value = CreateAppointmentState.Loading
        viewModelScope.launch {
            val result = appointmentRepository.createAppointment(appointment)
            if (result.isSuccess) {
                val newId = result.getOrNull() ?: ""
                val finalAppointment = appointment.copy(id = newId)

                if (finalAppointment.patientEmail.isNotBlank()) {
                    sendConfirmationEmail(finalAppointment)
                }

                // Recarrega a lista para refletir o novo agendamento.
                loadAppointments(finalAppointment.dentistId)

                _createAppointmentState.value = CreateAppointmentState.Success(finalAppointment)

            } else {
                _createAppointmentState.value = CreateAppointmentState.Error(result.exceptionOrNull()?.message ?: "Erro ao criar agendamento")
            }
        }
    }

    /**
     * Enfileira um e-mail para ser enviado por uma extensão do Firebase (ex: Trigger Email).
     * Esta função não envia o e-mail diretamente, mas cria um documento na coleção 'mail'
     * no Firestore, que por sua vez aciona a extensão configurada.
     */
    private fun sendConfirmationEmail(appointment: Appointment) {
        val emailData = mapOf(
            "to" to appointment.patientEmail,
            "message" to mapOf(
                "subject" to "Confirmação de Agendamento - DentalFlow",
                "text" to "Olá, ${appointment.patientName}! Sua consulta para o procedimento '${appointment.procedure}' foi agendada com sucesso para o dia ${appointment.date} às ${appointment.time}. Até lá!"
            )
        )

        Firebase.firestore.collection("mail").add(emailData)
            .addOnSuccessListener { Log.d("EmailTrigger", "Documento de e-mail para ${appointment.patientEmail} enfileirado.") }
            .addOnFailureListener { e -> Log.e("EmailTrigger", "Erro ao enfileirar e-mail.", e) }
    }

    /**
     * Atualiza um agendamento existente e recarrega a lista em caso de sucesso.
     */
    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            val result = appointmentRepository.updateAppointment(appointment)
            if (result.isSuccess) {
                loadAppointments(appointment.dentistId)
            }
        }
    }

    /**
     * Deleta um agendamento e recarrega a lista para refletir a remoção.
     */
    fun deleteAppointment(appointmentId: String, dentistId: String) {
        viewModelScope.launch {
            val result = appointmentRepository.deleteAppointment(appointmentId)
            if (result.isSuccess) {
                loadAppointments(dentistId)
            }
        }
    }

    /**
     * Reseta o estado da tela de criação para 'Idle'.
     * Essencial para limpar a mensagem de sucesso/erro da UI após a navegação.
     */
    fun resetCreateState() {
        _createAppointmentState.value = CreateAppointmentState.Idle
    }
}

/**
 * Representa os diferentes estados possíveis da tela de criação de agendamento.
 * Usar uma sealed class garante que todos os estados sejam tratados de forma segura (type-safe).
 */
sealed class CreateAppointmentState {
    object Idle : CreateAppointmentState()      // Estado inicial ou ocioso.
    object Loading : CreateAppointmentState()   // A criação está em andamento.
    data class Success(val appointment: Appointment) : CreateAppointmentState() // A criação foi bem-sucedida.
    data class Error(val message: String) : CreateAppointmentState()        // Ocorreu um erro.
}
