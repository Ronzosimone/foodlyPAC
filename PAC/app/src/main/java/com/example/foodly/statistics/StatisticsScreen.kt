package com.example.foodly.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodly.ui.theme.FoodlyTheme
import com.patrykandpatryk.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatryk.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatryk.vico.compose.chart.Chart
import com.patrykandpatryk.vico.compose.chart.column.columnChart
import com.patrykandpatryk.vico.compose.component.shapeComponent
import com.patrykandpatryk.vico.compose.component.textComponent
import com.patrykandpatryk.vico.compose.dimensions.dimensionsOf
import com.patrykandpatryk.vico.core.axis.AxisPosition
import com.patrykandpatryk.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatryk.vico.core.component.shape.Shapes
import com.patrykandpatryk.vico.core.component.text.TextComponent
import com.patrykandpatryk.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatryk.vico.core.entry.entryOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = viewModel()
) {
    val weeklyKcalData by viewModel.weeklyKcalData.collectAsState()
    val healthyScore by viewModel.healthyScoreData.collectAsState()

    // FoodlyTheme is already applied at a higher level, typically in MainActivity or App composable.
    // If this screen is used independently, FoodlyTheme {} wrapper is fine. Assuming it's part of themed app.
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Le tue Statistiche") },
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
                .padding(16.dp) // Outer padding for the content
                .fillMaxSize() // Fill size for the column
                .background(MaterialTheme.colorScheme.background), // Explicit background
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Adjusted spacing
        ) {
            KcalConsumptionCard(weeklyKcalData)
            HealthyScoreCard(healthyScore)
        }
    }
}

@Composable
fun KcalConsumptionCard(kcalData: Map<String, Float>) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Consumo Kcal Settimanale",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface, // Text on surface
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (kcalData.isEmpty()) {
                Text(
                    "No kcal data available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Muted text for empty state
                )
            } else {
                val modelProducer = ChartEntryModelProducer(
                    kcalData.entries.mapIndexed { index, entry ->
                        entryOf(index.toFloat(), entry.value)
                    }
                )
                val days = kcalData.keys.toList()
                val axisLabelColor = MaterialTheme.colorScheme.onSurfaceVariant // Color for axis labels/titles

                Chart(
                    chart = columnChart(
                        columns = listOf(
                            // Define column colors using theme
                            com.patrykandpatryk.vico.core.component.shape.LineComponent(
                                color = MaterialTheme.colorScheme.primary.hashCode(), // Vico uses Int color
                                thicknessDp = 8f, // Example thickness
                                shape = Shapes.roundedCornerShape(allPercent = 40)
                            )
                        ),
                        spacing = 8.dp
                    ),
                    chartModelProducer = modelProducer,
                    startAxis = rememberStartAxis(
                        titleComponent = textComponent(
                            color = axisLabelColor,
                            padding = dimensionsOf(end = 8.dp) // Spacing for title
                        ),
                        label = textComponent(color = axisLabelColor),
                        title = "Kcal"
                    ),
                    bottomAxis = rememberBottomAxis(
                        valueFormatter = { value, _ -> days.getOrNull(value.toInt()) ?: "" },
                        titleComponent = textComponent(
                            color = axisLabelColor,
                            padding = dimensionsOf(top = 4.dp)
                        ),
                        label = textComponent(color = axisLabelColor),
                        guideline = null, // Optionally remove guideline if too busy
                        title = "Giorno"
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}

@Composable
fun HealthyScoreCard(score: Float) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Punteggio Salute",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface, // Text on surface
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = score / 100f,
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp),
                    color = getThemedScoreColor(score), // Uses theme colors, good
                    trackColor = MaterialTheme.colorScheme.surfaceVariant // More appropriate track color
                )
                Text(
                    text = "${score.toInt()}/100",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface, // Text on surface
                    modifier = Modifier.padding(start = 16.dp) // Increased padding
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Il tuo punteggio salute settimanale.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Muted helper text
            )
        }
    }
}

@Composable
fun getThemedScoreColor(score: Float): Color {
    return when {
        score < 40f -> MaterialTheme.colorScheme.error
        score < 70f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    FoodlyTheme {
        StatisticsScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun KcalConsumptionCardPreview() {
    FoodlyTheme {
        KcalConsumptionCard(
            mapOf(
                "Lun" to 2000f,
                "Mar" to 1800f,
                "Mer" to 2200f,
                "Gio" to 1900f,
                "Ven" to 2300f,
                "Sab" to 2500f,
                "Dom" to 2100f
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HealthyScoreCardPreview() {
    FoodlyTheme {
        HealthyScoreCard(score = 75.5f)
    }
}