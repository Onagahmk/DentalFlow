package com.rodrigodecastro.dentalflow.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rodrigodecastro.dentalflow.data.models.Appointment
import kotlinx.coroutines.tasks.await

/**
 * Repositório para gerenciar todas as operações relacionadas a agendamentos no Firestore.
 * Encapsula a lógica de acesso a dados, permitindo que o resto do app (principalmente o ViewModel)
 * interaja com o banco de dados de forma mais abstrata e segura.
 */
class AppointmentRepository {
    private val db = FirebaseFirestore.getInstance()
    private val appointmentsCollection = db.collection("appointments")
    private val dentistsCollection = db.collection("dentists")

    /**
     * Cria um novo agendamento no Firestore.
     * 1. Gera um ID de documento único e o atribui ao objeto `appointment`.
     * 2. Salva o objeto completo no banco de dados.
     * @return Um `Result` com o ID do novo agendamento em caso de sucesso, ou a exceção em caso de falha.
     */
    suspend fun createAppointment(appointment: Appointment): Result<String> {
        return try {
            val newDocument = appointmentsCollection.document()
            val appointmentWithId = appointment.copy(id = newDocument.id)
            newDocument.set(appointmentWithId).await()
            Result.success(appointmentWithId.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Busca todos os agendamentos associados a um ID de dentista específico.
     * @param dentistId O ID único do dentista.
     * @return Um `Result` com a lista de `Appointment` em caso de sucesso, ou a exceção em caso de falha.
     */
    suspend fun getAppointmentsByDentist(dentistId: String): Result<List<Appointment>> {
        return try {
            val result = appointmentsCollection
                .whereEqualTo("dentistId", dentistId)
                .get()
                .await()
            // Converte a resposta do Firestore diretamente em uma lista de objetos `Appointment`.
            val appointments = result.toObjects(Appointment::class.java)
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Busca o nome de um dentista específico no Firestore usando seu ID.
     * @param dentistId O UID do dentista.
     * @return Um `Result` com o nome do dentista ou uma mensagem de erro padrão.
     */
    suspend fun getDentistName(dentistId: String): Result<String> {
        return try {
            val result = dentistsCollection.document(dentistId).get().await()
            val name = result.getString("name") ?: "Nome não encontrado"
            Result.success(name)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Atualiza um agendamento existente no Firestore.
     * A operação `set` com um documento que já existe substitui seu conteúdo.
     * @param appointment O objeto `Appointment` com os dados atualizados (deve conter o ID correto).
     * @return Um `Result` indicando sucesso (`Unit`) ou falha.
     */
    suspend fun updateAppointment(appointment: Appointment): Result<Unit> {
        return try {
            appointmentsCollection.document(appointment.id).set(appointment).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deleta um agendamento do Firestore usando seu ID.
     * @param appointmentId O ID do agendamento a ser deletado.
     * @return Um `Result` indicando sucesso (`Unit`) ou falha.
     */
    suspend fun deleteAppointment(appointmentId: String): Result<Unit> {
        return try {
            appointmentsCollection.document(appointmentId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}