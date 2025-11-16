package com.rodrigodecastro.dentalflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigodecastro.dentalflow.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun loginUser(email: String, password: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val result = authRepository.loginUser(email, password)

            _loginState.value = if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null) {
                    LoginState.Success(user.uid)
                } else {
                    LoginState.Error("Usuário não encontrado após o login.")
                }
            } else {
                LoginState.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido")
            }
        }
    }

    fun registerUser(name: String, email: String, password: String) { // Modificado
        _registerState.value = RegisterState.Loading

        viewModelScope.launch {
            val result = authRepository.registerUser(name, email, password) // Modificado

            _registerState.value = if (result.isSuccess) {
                RegisterState.Success
            } else {
                RegisterState.Error(result.exceptionOrNull()?.message ?: "Erro no cadastro")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val dentistId: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}
