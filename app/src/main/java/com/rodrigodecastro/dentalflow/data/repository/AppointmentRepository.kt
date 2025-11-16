package com.rodrigodecastro.dentalflow.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rodrigodecastro.dentalflow.data.models.Appointment
import kotlinx.coroutines.tasks.await

class AppointmentRepository {
    private val db = FirebaseFirestore.getInstance()
    private val appointmentsCollection = db.collection("appointments")
    private val dentistsCollection = db.collection("dentists") // Nova coleção

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

    suspend fun getAppointmentsByDentist(dentistId: String): Result<List<Appointment>> {
        return try {
            val result = appointmentsCollection
                .whereEqualTo("dentistId", dentistId)
                .get()
                .await()
            val appointments = result.toObjects(Appointment::class.java)
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Função para buscar o nome do dentista
    suspend fun getDentistName(dentistId: String): Result<String> {
        return try {
            val result = dentistsCollection.document(dentistId).get().await()
            val name = result.getString("name") ?: "Nome não encontrado"
            Result.success(name)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAppointment(appointment: Appointment): Result<Unit> {
        return try {
            appointmentsCollection.document(appointment.id).set(appointment).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAppointment(appointmentId: String): Result<Unit> {
        return try {
            appointmentsCollection.document(appointmentId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}