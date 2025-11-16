package com.rodrigodecastro.dentalflow

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.rodrigodecastro.dentalflow.ui.theme.DentalFlowTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("Permission", "Notification permission granted.")
        } else {
            Log.d("Permission", "Notification permission denied.")
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun subscribeToTopic(){
        Firebase.messaging.subscribeToTopic("all")
            .addOnCompleteListener {
                var msg = "Subscribed to topic 'all'"
                if (!it.isSuccessful) {
                    msg = "Subscription to topic 'all' failed"
                }
                Log.d("FCM", msg)
            }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        askNotificationPermission()
        subscribeToTopic()

        setContent {
            DentalFlowTheme {
                DentalFlowApp()
            }
        }
    }
}