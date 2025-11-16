package com.rodrigodecastro.dentalflow.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rodrigodecastro.dentalflow.viewmodel.AuthViewModel
import com.rodrigodecastro.dentalflow.viewmodel.LoginState

/**
 * Tela de Login.
 * Permite que um usuário existente acesse o sistema.
 * Esta tela é "stateless" (sem estado próprio complexo), pois a lógica de negócio
 * é delegada ao `AuthViewModel`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    // Funções de callback para comunicar eventos de navegação para o `AppNavigation`.
    onLoginSuccess: (dentistId: String) -> Unit,
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    // Obtém a instância do ViewModel.
    val authViewModel: AuthViewModel = viewModel()

    // Estados locais para os campos do formulário.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observa o estado de login do ViewModel. A UI será recomposta quando ele mudar.
    val loginState by authViewModel.loginState.collectAsState()

    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Acessar Sistema",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("E-mail") },
                            placeholder = { Text("seu@email.com") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Senha") },
                            placeholder = { Text("Sua senha") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // O botão de entrar dispara a função de login no ViewModel.
                        // Ele é desabilitado se os campos estiverem vazios.
                        Button(
                            onClick = {
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    authViewModel.loginUser(email, password)
                                }
                            },
                            enabled = email.isNotEmpty() && password.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            // Exibe um texto diferente dependendo do estado de carregamento.
                            if (loginState is LoginState.Loading) {
                                Text("Entrando...")
                            } else {
                                Text("Entrar")
                            }
                        }

                        // Exibe a mensagem de erro se o estado for `Error`.
                        if (loginState is LoginState.Error) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = (loginState as LoginState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = onRegisterClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Criar Nova Conta")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = onBackClick,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Voltar")
                        }
                    }
                }
            }
        }
    }


    // Efeito colateral que é executado quando o estado do login muda para `Success`.
    // Dispara a navegação para a próxima tela e depois reseta o estado no ViewModel
    // para evitar que a navegação seja disparada novamente em uma recomposição.
    val currentState = loginState
    if (currentState is LoginState.Success) {
        LaunchedEffect(currentState) {
            onLoginSuccess(currentState.dentistId)
            authViewModel.resetLoginState()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onLoginSuccess = { },
        onBackClick = { },
        onRegisterClick = {}
    )
}
