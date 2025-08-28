package com.example.foodly.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodly.utils.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(),
    onLogout: () -> Unit // Callback for logout
) {
    val userName by viewModel.userName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val userPhoneNumber by viewModel.userPhoneNumber.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val profileState by viewModel.profileUiState.collectAsState()
    val context = LocalContext.current

    // Load profile when screen is first displayed
    LaunchedEffect(Unit) {
        // Get real userId from SharedPreferences
        val userId = UserPreferences.getInstance(context).getUserId()
        if (userId != null) {
            viewModel.loadProfile(userId, context)
        } else {
            // User not logged in, handle this case (maybe navigate to login)
            // For now just log it
            android.util.Log.w("SettingsScreen", "User ID not found in preferences")
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Impostazioni Profilo") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background // Set screen background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Explicitly set background for Column too
        ) {
            // User Information Section
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Informazioni Utente",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Show loading or error state
                    when (profileState) {
                        is ProfileUiState.Loading -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Caricamento profilo...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        is ProfileUiState.Error -> {
                            Text(
                                "Errore nel caricamento: ${(profileState as ProfileUiState.Error).message}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else -> {
                            // Show profile data (Success or Idle with default values)
                            InfoRow(icon = Icons.Filled.Person, label = "Nome", value = userName)
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            InfoRow(icon = Icons.Filled.Email, label = "Email", value = userEmail)
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            InfoRow(icon = Icons.Filled.Phone, label = "Telefono", value = userPhoneNumber)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Other Settings Options
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    SettingItemRow(
                        icon = Icons.Filled.Notifications,
                        title = "Notifiche",
                        onClick = { /* Toggle notifications - viewModel call */ },
                        trailingContent = {
                            Switch( // Switch colors will be themed by M3
                                checked = notificationsEnabled,
                                onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                            )
                        }
                    )
                    SettingItemRow(
                        icon = Icons.Filled.Edit,
                        title = "Cambia Password",
                        onClick = { Toast.makeText(context, "Change Password Clicked", Toast.LENGTH_SHORT).show() }
                    )
                    SettingItemRow(
                        icon = Icons.Filled.Settings, // Consider a different icon for "Terms" e.g. Article
                        title = "Termini di Servizio",
                        onClick = { Toast.makeText(context, "Terms of Service Clicked", Toast.LENGTH_SHORT).show() }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Push logout to bottom

            ElevatedButton(
                onClick = {
                    // Pulisci le SharedPreferences quando si fa logout
                    UserPreferences.getInstance(context).logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp), // Consistent padding
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) // Correct for logout
            ) {
                Icon(Icons.Filled.ExitToApp, contentDescription = "Logout Icon", modifier = Modifier.padding(end = 8.dp))
                Text("Logout") // Text color will be onError by default
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Icon(
            icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.secondary // Changed tint to secondary for less emphasis
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Good
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface // Ensure text color
            )
        }
    }
}

@Composable
fun SettingItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.secondary // Changed tint to secondary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface // Ensure text color
        )
        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                Icons.Filled.KeyboardArrowRight,
                contentDescription = "Go to $title",
                tint = MaterialTheme.colorScheme.onSurfaceVariant // Theme tint for less emphasis
            )
        }
    }
    // Optional: Keep divider if it visually separates items well, or remove if Card separation is enough
    Divider(modifier = Modifier.padding(start = 56.dp)) // Indent divider, color will be outline
}


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen(onLogout = {})
    }
}
