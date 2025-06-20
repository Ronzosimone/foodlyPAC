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
import com.example.foodly.ui.theme.FoodlyTheme

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

    // Use solid background color for a cleaner M3 look
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Solid background
            .padding(24.dp), // Keep padding
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo in a Card
        Card(
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Consistent elevation
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Use surface for card
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape) // clip is redundant if Card shape is CircleShape, but harmless
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Foodly",
                modifier = Modifier.fillMaxSize() // Image fills the card
            )
        }

        Spacer(Modifier.height(32.dp)) // Increased spacing
        Text(
            text = "Benvenuto in Foodly!",
            style = MaterialTheme.typography.headlineSmall, // Adjusted style for prominence
            color = MaterialTheme.colorScheme.primary // Keep primary color for title
        )
        Spacer(Modifier.height(16.dp)) // Increased spacing

        // Email Field - Rely on M3 defaults for OutlinedTextField colors
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
            // Removed explicit colors to use M3 defaults
            // focusedBorderColor will be primary, cursorColor primary, label onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        // Password Field - Rely on M3 defaults
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
            // Removed explicit colors
        )

        Spacer(Modifier.height(32.dp)) // Increased spacing

        // Login Button
        Button(
            onClick = { viewModel.login(email, password) },
            enabled = loginUiState != LoginUiState.Loading,
            modifier = Modifier.fillMaxWidth(),
            // M3 buttons have standard shapes, explicit shape might not be needed unless specific design
            // shape = RoundedCornerShape(12.dp) // Can be removed for M3 default
        ) {
            if (loginUiState == LoginUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary // Correct for Button text color
                )
            } else {
                Text("Accedi") // Text color will be onPrimary
            }
        }

        Spacer(Modifier.height(16.dp)) // Increased spacing

        if (loginUiState is LoginUiState.Error) {
            Text(
                text = (loginUiState as LoginUiState.Error).message,
                color = MaterialTheme.colorScheme.error, // Correct for error messages
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp) // Add some bottom padding
            )
            // Spacer(Modifier.height(12.dp)) // Adjusted by padding
        }

        // TextButtons - Colors are fine
        TextButton(onClick = { /* TODO: Forgot password */ }) {
            Text("Password dimenticata?", color = MaterialTheme.colorScheme.secondary)
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onNavigateToRegister) {
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
    FoodlyTheme {
        LoginScreen(onLoginSuccess = { }, onNavigateToRegister = { })
    }
}