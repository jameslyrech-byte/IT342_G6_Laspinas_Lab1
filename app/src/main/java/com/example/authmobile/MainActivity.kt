package com.example.authmobile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.authmobile.ui.network.ApiService
import com.example.authmobile.ui.network.LoginRequest
import com.example.authmobile.ui.theme.AuthMobileTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AuthMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    LoginScreen { username, password ->

                        // Ensure fields are not empty
                        if (username.isEmpty() || password.isEmpty()) {
                            Toast.makeText(context, "Enter username & password", Toast.LENGTH_SHORT).show()
                            return@LoginScreen
                        }

                        // Use Coroutine to call Retrofit
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = ApiService.authApi.login(LoginRequest(username, password))

                                if (response.isSuccessful) {
                                    val body = response.body()
                                    val token = body?.token ?: ""
                                    val role = body?.role ?: ""
                                    val user = body?.username ?: ""

                                    // Switch to Main thread to show Toast
                                    runOnUiThread {
                                        Toast.makeText(
                                            context,
                                            "Logged in as $user, Role: $role",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    runOnUiThread {
                                        Toast.makeText(
                                            context,
                                            "Login failed: ${response.code()}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            } catch (e: Exception) {
                                runOnUiThread {
                                    Toast.makeText(
                                        context,
                                        "Error: ${e.localizedMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
