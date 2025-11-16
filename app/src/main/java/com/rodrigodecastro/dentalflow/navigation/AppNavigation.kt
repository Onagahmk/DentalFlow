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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val appointmentViewModel: AppointmentViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { dentistId ->
                    navController.navigate("schedule/$dentistId") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() },
                onRegisterClick = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("home") {
            HomeScreen(
                onLoginClick = { navController.navigate("login") }
            )
        }
        composable(
            route = "schedule/{dentistId}",
            arguments = listOf(navArgument("dentistId") { type = NavType.StringType })
        ) { backStackEntry ->
            val dentistId = backStackEntry.arguments?.getString("dentistId") ?: ""
            DentistScheduleScreen(
                onLogout = {
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
        composable(
            route = "create_appointment/{dentistId}",
            arguments = listOf(navArgument("dentistId") { type = NavType.StringType })
        ) { backStackEntry ->
            val dentistId = backStackEntry.arguments?.getString("dentistId") ?: ""
            CreateAppointmentScreen(
                onSaveAppointment = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
                dentistId = dentistId,
                appointmentViewModel = appointmentViewModel
            )
        }
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
