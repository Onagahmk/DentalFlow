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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.runtime.collectAsState
import com.rodrigodecastro.dentalflow.viewmodel.RegisterState

/**
 * Tela de Registro.
 * Permite que um novo usuário (dentista) crie uma conta no sistema.
 * A lógica de validação do formulário e comunicação com o backend é delegada ao `AuthViewModel`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    // Callbacks para navegação.
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()

    // Estados para cada campo do formulário de registro.
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Observa o estado de registro do ViewModel para reagir a mudanças (Loading, Success, Error).
    val registerState by authViewModel.registerState.collectAsState()

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
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
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
                            text = "Criar Conta",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nome Completo") },
                            placeholder = { Text("Dr. Nome Sobrenome") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

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
                            placeholder = { Text("Mínimo 6 caracteres") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirmar Senha") },
                            placeholder = { Text("Digite a senha novamente") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botão de cadastro que é habilitado apenas quando todos os campos
                        // são válidos (campos não vazios, senhas coincidem e têm tamanho mínimo).
                        Button(
                            onClick = {
                                if (password == confirmPassword) {
                                    authViewModel.registerUser(name, email, password)
                                }
                            },
                            enabled = name.isNotEmpty() &&
                                    email.isNotEmpty() &&
                                    password.isNotEmpty() &&
                                    confirmPassword.isNotEmpty() &&
                                    password == confirmPassword &&
                                    password.length >= 6,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (registerState is RegisterState.Loading) {
                                Text("Cadastrando...")
                            } else {
                                Text("Cadastrar")
                            }
                        }

                        // Exibe a mensagem de erro retornada pelo ViewModel.
                        if (registerState is RegisterState.Error) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = (registerState as RegisterState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Validações em tempo real para dar feedback ao usuário.
                        if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "As senhas não coincidem",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        if (password.isNotEmpty() && password.length < 6) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "A senha deve ter pelo menos 6 caracteres",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onBackClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text("Voltar")
                        }
                    }
                }
            }
        }
    }

    // Efeito para navegar para a próxima tela em caso de sucesso no registro.
    if (registerState is RegisterState.Success) {
        LaunchedEffect(Unit) {
            onRegisterSuccess()
            authViewModel.resetRegisterState() // Limpa o estado para evitar re-navegação.
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onRegisterSuccess = { },
        onBackClick = { }
    )
}
