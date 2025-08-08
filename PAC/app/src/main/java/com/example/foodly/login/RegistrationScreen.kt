package com.example.foodly.login // Placing in .login package for now

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodly.R // Assuming R.drawable.logo exists
import com.example.foodly.ui.theme.FoodlyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onRegistrationSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = viewModel()
) {
    val registrationUiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    // Use solid background color for a cleaner M3 look
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Solid background
            .padding(24.dp), // Keep padding
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ElevatedCard(
            shape = CircleShape,
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .size(120.dp) // Smaller logo for registration
                .clip(CircleShape) // clip is redundant
        ) {
            Image(
                painter = painterResource(R.drawable.logo), // Ensure this drawable exists
                contentDescription = "Logo Foodly",
                modifier = Modifier.fillMaxSize() // Image fills the card
            )
        }

        Spacer(Modifier.height(24.dp)) // Adjusted spacing
        Text(
            text = "Create your Foodly Account",
            style = MaterialTheme.typography.headlineSmall, // Adjusted style
            color = MaterialTheme.colorScheme.primary // Keep primary color for title
        )
        Spacer(Modifier.height(16.dp))

        // Name Field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // Surname Field
        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Surname") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // Email Field - Rely on M3 defaults
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
            // Removed explicit colors
        )
        Spacer(Modifier.height(16.dp)) // Consistent spacing

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
        Spacer(Modifier.height(16.dp)) // Consistent spacing

        // Confirm Password Field - Rely on M3 defaults
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            // Removed explicit colors
            isError = registrationUiState is RegistrationUiState.Error && (registrationUiState as RegistrationUiState.Error).message.contains("Passwords do not match")
        )
        Spacer(Modifier.height(24.dp)) // Adjusted spacing

        // Elevated register button
        ElevatedButton(
            onClick = { viewModel.register(name, surname, email, password, confirmPassword, context) },
            enabled = registrationUiState != RegistrationUiState.Loading,
            modifier = Modifier.fillMaxWidth()
            // shape = RoundedCornerShape(12.dp) // Removed for M3 default
        ) {
            if (registrationUiState == RegistrationUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary // Correct
                )
            } else {
                Text("Register") // Correct
            }
        }

        Spacer(Modifier.height(16.dp)) // Consistent spacing

        if (registrationUiState is RegistrationUiState.Error) {
            Text(
                text = (registrationUiState as RegistrationUiState.Error).message,
                color = MaterialTheme.colorScheme.error, // Correct
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    LaunchedEffect(registrationUiState) {
        if (registrationUiState == RegistrationUiState.Success) {
            onRegistrationSuccess()
            viewModel.resetState() // Reset state after navigation
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    FoodlyTheme {
        RegistrationScreen(onRegistrationSuccess = {})
    }
}
