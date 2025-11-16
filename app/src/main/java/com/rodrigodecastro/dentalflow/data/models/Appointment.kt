package com.rodrigodecastro.dentalflow.data.models

/**
 * Representa a estrutura de dados de um agendamento no sistema.
 * Esta data class é usada para mapear documentos do Firestore.
 *
 * @property id O ID único do agendamento, gerado pelo Firestore.
 * @property patientName O nome completo do paciente.
 * @property patientPhone O número de telefone do paciente.
 * @property patientEmail O endereço de e-mail do paciente, para envio de confirmações.
 * @property emailStatus O status de entrega do e-mail de confirmação (ex: PENDING, SENT, ERROR).
 * @property dentistId O ID único do dentista responsável pelo agendamento.
 * @property dentistName O nome do dentista, para exibição na UI.
 * @property date A data do agendamento, formatada como "dd/MM/yyyy".
 * @property time O horário do agendamento, formatado como "HH:mm".
 * @property procedure A descrição do procedimento a ser realizado.
 * @property status O status atual do agendamento (ex: "Agendada", "Concluída", "Cancelada").
 * @property notes Observações adicionais sobre o agendamento ou paciente.
 * @property createdBy O ID do usuário que criou o agendamento (pode ser o próprio dentista ou um assistente).
 */
data class Appointment(
    var id: String = "",
    var patientName: String = "",
    var patientPhone: String = "",
    var patientEmail: String = "",
    var emailStatus: String = "UNKNOWN",
    var dentistId: String = "",
    var dentistName: String = "",
    var date: String = "",
    var time: String = "",
    var procedure: String = "",
    var status: String = "Agendada",
    var notes: String = "",
    var createdBy: String = "" 
)
