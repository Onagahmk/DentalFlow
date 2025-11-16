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

class AppointmentViewModel(
    private val appointmentRepository: AppointmentRepository = AppointmentRepository()
) : ViewModel() {

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments = _appointments.asStateFlow()

    private val _dentistName = MutableStateFlow("Dr(a). ...")
    val dentistName = _dentistName.asStateFlow()

    private val _createAppointmentState = MutableStateFlow<CreateAppointmentState>(CreateAppointmentState.Idle)
    val createAppointmentState = _createAppointmentState.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    fun loadAppointments(dentistId: String) {
        _loadingState.value = true
        viewModelScope.launch {
            appointmentRepository.getAppointmentsByDentist(dentistId).onSuccess {
                _appointments.value = it
            }
            _loadingState.value = false
        }
    }

    fun loadDentistName(dentistId: String) {
        viewModelScope.launch {
            appointmentRepository.getDentistName(dentistId).onSuccess {
                _dentistName.value = it
            }
        }
    }

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

                loadAppointments(finalAppointment.dentistId)

                _createAppointmentState.value = CreateAppointmentState.Success(finalAppointment)

            } else {
                _createAppointmentState.value = CreateAppointmentState.Error(result.exceptionOrNull()?.message ?: "Erro ao criar agendamento")
            }
        }
    }

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

    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            val result = appointmentRepository.updateAppointment(appointment)
            if (result.isSuccess) {
                loadAppointments(appointment.dentistId)
            }
        }
    }

    fun deleteAppointment(appointmentId: String, dentistId: String) {
        viewModelScope.launch {
            val result = appointmentRepository.deleteAppointment(appointmentId)
            if (result.isSuccess) {
                loadAppointments(dentistId)
            }
        }
    }

    fun resetCreateState() {
        _createAppointmentState.value = CreateAppointmentState.Idle
    }
}

sealed class CreateAppointmentState {
    object Idle : CreateAppointmentState()
    object Loading : CreateAppointmentState()
    data class Success(val appointment: Appointment) : CreateAppointmentState()
    data class Error(val message: String) : CreateAppointmentState()
}
