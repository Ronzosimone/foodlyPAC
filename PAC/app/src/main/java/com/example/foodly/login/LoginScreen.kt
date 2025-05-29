package com.example.foodly.login


import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.foodly.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit, // New navigation callback
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel()
) {
    val loginUiState by viewModel.uiState.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    // Gradient di sfondo (dal verde chiaro al bianco)
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.background
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo circolare
        Card(
            shape = CircleShape,
            elevation = elevatedCardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Foodly",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(24.dp))
        Text(
            text = "Benvenuto in Foodly!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))

        // Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(Modifier.height(16.dp))

        // Campo Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(Modifier.height(24.dp))

        // Pulsante Accedi
        Button(
            onClick = { viewModel.login(email, password) },
            enabled = loginUiState != LoginUiState.Loading,
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (loginUiState == LoginUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Accedi")
            }
        }

        Spacer(Modifier.height(12.dp))

        if (loginUiState is LoginUiState.Error) {
            Text(
                text = (loginUiState as LoginUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))
        }

        TextButton(onClick = { /* TODO: Forgot password */ }) {
            Text("Password dimenticata?", color = MaterialTheme.colorScheme.secondary)
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onNavigateToRegister) { // Navigate to Registration
            Text("Non hai un account? Registrati", color = MaterialTheme.colorScheme.primary)
        }
    }

    LaunchedEffect(loginUiState) {
        if (loginUiState == LoginUiState.Success) {
            onLoginSuccess()
            viewModel.resetState() // Reset state after navigation
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(onLoginSuccess = { }, onNavigateToRegister = { })
    }
}