package com.rodrigodecastro.dentalflow

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.rodrigodecastro.dentalflow.navigation.AppNavigation

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DentalFlowApp() {
    AppNavigation()
}