package com.rodrigodecastro.dentalflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigodecastro.dentalflow.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para a lógica de autenticação (Login e Registro).
 * Gerencia o estado da UI e se comunica com o AuthRepository para interagir com o Firebase.
 */
class AuthViewModel(
    // O repositório é injetado para desacoplar o ViewModel do Firebase e facilitar testes.
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    // --- Estados da UI (StateFlows) --- //

    // StateFlow privado para o estado do login. Apenas o ViewModel pode alterá-lo.
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    // StateFlow público e imutável para a UI observar o estado do login.
    val loginState: StateFlow<LoginState> = _loginState

    // StateFlow privado para o estado do registro.
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    // StateFlow público para a UI observar o estado do registro.
    val registerState: StateFlow<RegisterState> = _registerState


    /**
     * Tenta realizar o login de um usuário com e-mail and senha.
     * 1. Emite o estado `Loading` para a UI.
     * 2. Chama o método do repositório de forma assíncrona dentro de `viewModelScope`.
     * 3. Com base no `Result` do repositório, emite o estado `Success` com o ID do usuário
     *    ou `Error` com a mensagem de erro apropriada.
     */
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

    /**
     * Tenta registrar um novo usuário com nome, e-mail e senha.
     * A lógica é similar à do login: emite os estados Loading, Success ou Error.
     */
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

    /**
     * Reseta o estado do login para `Idle`. Chamado pela UI para limpar mensagens
     * de sucesso ou erro, geralmente após a navegação.
     */
    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    /**
     * Reseta o estado do registro para `Idle`.
     */
    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }
}

// --- Sealed Classes para os Estados da UI --- //

/**
 * Define os estados possíveis para o fluxo de login.
 * Usar uma sealed class garante que a UI trate todos os casos de forma segura.
 */
sealed class LoginState {
    object Idle : LoginState()      // Estado inicial.
    object Loading : LoginState()   // Login em andamento.
    data class Success(val dentistId: String) : LoginState() // Login bem-sucedido.
    data class Error(val message: String) : LoginState()    // Erro durante o login.
}

/**
 * Define os estados possíveis para o fluxo de registro.
 */
sealed class RegisterState {
    object Idle : RegisterState()      // Estado inicial.
    object Loading : RegisterState()   // Registro em andamento.
    object Success : RegisterState()   // Registro bem-sucedido.
    data class Error(val message: String) : RegisterState()    // Erro durante o registro.
}
