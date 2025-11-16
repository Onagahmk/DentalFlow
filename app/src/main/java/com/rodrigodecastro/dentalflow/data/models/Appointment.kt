package com.rodrigodecastro.dentalflow.data.models

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
