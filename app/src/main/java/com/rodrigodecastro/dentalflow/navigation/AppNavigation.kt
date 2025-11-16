package com.rodrigodecastro.dentalflow.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rodrigodecastro.dentalflow.ui.auth.LoginScreen
import com.rodrigodecastro.dentalflow.ui.auth.RegisterScreen
import com.rodrigodecastro.dentalflow.ui.dentist.CreateAppointmentScreen
import com.rodrigodecastro.dentalflow.ui.dentist.DentistScheduleScreen
import com.rodrigodecastro.dentalflow.ui.dentist.EditAppointmentScreen
import com.rodrigodecastro.dentalflow.ui.home.HomeScreen
import com.rodrigodecastro.dentalflow.viewmodel.AppointmentViewModel

/**
 * Gerencia toda a navegação do aplicativo.
 * Define o `NavHost` e todas as rotas (telas) possíveis, bem como as transições entre elas.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    // O NavController é o cérebro da navegação no Compose. Ele gerencia a pilha de telas (back stack).
    val navController = rememberNavController()
    // Compartilha uma única instância do ViewModel entre as telas que precisam dele.
    // Isso permite que a `DentistScheduleScreen`, `Create` e `Edit` acessem os mesmos dados.
    val appointmentViewModel: AppointmentViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home" // A primeira tela que o usuário vê.
    ) {
        // Rota para a tela de Login.
        composable("login") {
            LoginScreen(
                onLoginSuccess = { dentistId ->
                    // Após o login bem-sucedido, navega para a agenda do dentista.
                    // `popUpTo("home")` remove as telas de "home" e "login" da pilha,
                    // impedindo que o usuário volte para elas apertando o botão de voltar.
                    navController.navigate("schedule/$dentistId") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        // Rota para a tela de Registro.
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    // Após o registro, leva o usuário para a tela de login para que ele possa entrar.
                    // Também limpa a pilha de navegação até a tela home.
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() } // Volta para a tela anterior (Login).
            )
        }

        // Rota para a tela inicial (Home).
        composable("home") {
            HomeScreen(
                onLoginClick = { navController.navigate("login") }
            )
        }

        // Rota para a Agenda do Dentista. Espera um `dentistId` como argumento na URL.
        composable(
            route = "schedule/{dentistId}",
            arguments = listOf(navArgument("dentistId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Extrai o `dentistId` da rota para passá-lo para a tela.
            val dentistId = backStackEntry.arguments?.getString("dentistId") ?: ""
            DentistScheduleScreen(
                onLogout = {
                    // Ao fazer logout, volta para a tela "home" e limpa toda a pilha de navegação.
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onCreateAppointment = {
                    navController.navigate("create_appointment/$dentistId")
                },
                onEditAppointment = { appointmentId ->
                    navController.navigate("edit_appointment/$appointmentId")
                },
                appointmentViewModel = appointmentViewModel,
                dentistId = dentistId
            )
        }

        // Rota para a tela de Criação de Agendamento. Também precisa do ID do dentista.
        composable(
            route = "create_appointment/{dentistId}",
            arguments = listOf(navArgument("dentistId") { type = NavType.StringType })
        ) { backStackEntry ->
            val dentistId = backStackEntry.arguments?.getString("dentistId") ?: ""
            CreateAppointmentScreen(
                onSaveAppointment = { navController.popBackStack() }, // Volta para a tela anterior (Agenda).
                onCancel = { navController.popBackStack() },
                dentistId = dentistId,
                appointmentViewModel = appointmentViewModel
            )
        }

        // Rota para a tela de Edição. Precisa do ID do agendamento específico.
        composable(
            route = "edit_appointment/{appointmentId}",
            arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
            EditAppointmentScreen(
                appointmentId = appointmentId,
                appointmentViewModel = appointmentViewModel,
                onAppointmentUpdated = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}
