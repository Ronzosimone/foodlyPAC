package com.example.foodly.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = viewModel()
) {
    val weeklyKcalData by viewModel.weeklyKcalData.collectAsState()
    val healthyScore by viewModel.healthyScoreData.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Le tue Statistiche") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Weekly Kcal Consumption Section
            KcalConsumptionCard(weeklyKcalData)

            // Healthy Score Section
            HealthyScoreCard(healthyScore)
        }
    }
}

@Composable
fun KcalConsumptionCard(kcalData: Map<String, Float>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Consumo Kcal Settimanale",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (kcalData.isEmpty()) {
                Text("No kcal data available.")
            } else {
                // Simple textual representation for now
                // For a basic bar chart simulation:
                val maxKcal = kcalData.values.maxOrNull() ?: 1f
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp), // Fixed height for the chart area
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    kcalData.forEach { (day, kcal) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(((kcal / maxKcal) * 130).dp) // Scale height
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(day, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Textual details as fallback or additional info
                kcalData.forEach { (day, kcal) ->
                    Text("$day: ${kcal.toInt()} kcal", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun HealthyScoreCard(score: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Punteggio Salute",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { score / 100f }, // Corrected lambda usage
                    modifier = Modifier.weight(1f).height(12.dp),
                    color = getScoreColor(score)
                )
                Text(
                    text = "${score.toInt()}/100",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
             Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Il tuo punteggio salute settimanale.", // Additional context
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

fun getScoreColor(score: Float): Color {
    return when {
        score < 40f -> Color.Red
        score < 70f -> Color(0xFFFFA500) // Orange
        else -> Color.Green
    }
}


@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    MaterialTheme {
        StatisticsScreen()
    }
}

@Preview
@Composable
fun KcalConsumptionCardPreview() {
    MaterialTheme{
        KcalConsumptionCard(mapOf("Lun" to 2000f, "Mar" to 1800f, "Mer" to 2200f))
    }
}

@Preview
@Composable
fun HealthyScoreCardPreview() {
    MaterialTheme{
        HealthyScoreCard(score = 75.5f)
    }
}
