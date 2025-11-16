package com.rodrigodecastro.dentalflow.data.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val userType: String = "" //dentist
)