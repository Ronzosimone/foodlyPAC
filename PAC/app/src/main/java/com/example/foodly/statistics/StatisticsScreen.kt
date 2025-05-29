package com.example.foodly.statistics

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
import com.example.foodly.ui.theme.FoodlyTheme // Import your custom theme
import com.patrykandpatryk.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatryk.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatryk.vico.compose.chart.Chart
import com.patrykandpatryk.vico.compose.chart.column.columnChart
import com.patrykandpatryk.vico.compose.component.rememberLineComponent
import com.patrykandpatryk.vico.compose.component.rememberTextComponent
import com.patrykandpatryk.vico.compose.dimensions.dimensionsOf
import com.patrykandpatryk.vico.compose.style.currentChartStyle
import com.patrykandpatryk.vico.core.axis.AxisPosition
import com.patrykandpatryk.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatryk.vico.core.component.shape.Shapes
import com.patrykandpatryk.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatryk.vico.core.entry.entryOf

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = viewModel()
) {
    val weeklyKcalData by viewModel.weeklyKcalData.collectAsState()
    val healthyScore by viewModel.healthyScoreData.collectAsState()

    FoodlyTheme { // Ensure the screen uses the FoodlyTheme
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Le tue Statistiche") }, // "Your Statistics"
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
                KcalConsumptionCard(weeklyKcalData)
                HealthyScoreCard(healthyScore)
            }
        }
    }
}

@Composable
fun KcalConsumptionCard(kcalData: Map<String, Float>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Consumo Kcal Settimanale", // "Weekly Kcal Consumption"
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (kcalData.isEmpty()) {
                Text(
                    "No kcal data available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val chartEntryModelProducer = ChartEntryModelProducer(
                    kcalData.entries.mapIndexed { index, entry -> entryOf(index.toFloat(), entry.value) }
                )
                val days = kcalData.keys.toList()
                val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                    days.getOrNull(value.toInt()) ?: ""
                }

                Chart(
                    chart = columnChart(
                        columns = listOf(
                            rememberLineComponent(
                                color = MaterialTheme.colorScheme.primary,
                                thickness = 16.dp, // Adjust thickness of bars
                                shape = Shapes.roundedCornerShape(topPercent = 20) // Rounded tops for bars
                            )
                        ),
                        spacing = 8.dp // Spacing between bars
                    ),
                    chartModelProducer = chartEntryModelProducer,
                    startAxis = rememberStartAxis(
                        titleComponent = rememberTextComponent(
                            color = MaterialTheme.colorScheme.onBackground,
                            padding = dimensionsOf(end = 8.dp)
                        ),
                        title = "Kcal"
                    ),
                    bottomAxis = rememberBottomAxis(
                        valueFormatter = bottomAxisValueFormatter,
                        titleComponent = rememberTextComponent(
                            color = MaterialTheme.colorScheme.onBackground,
                            padding = dimensionsOf(top = 4.dp)
                        ),
                        title = "Giorno" // "Day"
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Adjust height as needed
                )
            }
        }
    }
}

@Composable
fun HealthyScoreCard(score: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Punteggio Salute", // "Healthy Score"
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { score / 100f },
                    modifier = Modifier.weight(1f).height(12.dp),
                    color = getThemedScoreColor(score), // Use themed color
                    trackColor = MaterialTheme.colorScheme.surfaceTint // A less prominent track color
                )
                Text(
                    text = "${score.toInt()}/100",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // Use themed color
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Il tuo punteggio salute settimanale.", // "Your weekly healthy score."
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun getThemedScoreColor(score: Float): Color {
    return when {
        score < 40f -> MaterialTheme.colorScheme.error // Use theme's error color
        score < 70f -> MaterialTheme.colorScheme.tertiary // Use theme's tertiary/accent
        else -> MaterialTheme.colorScheme.primary // Use theme's primary color
    }
}


@Preview(showBackground = true, name = "Statistics Screen Preview")
@Composable
fun StatisticsScreenPreview() {
    FoodlyTheme { // Ensure preview uses the app's theme
        StatisticsScreen()
    }
}

@Preview(name = "Kcal Card Preview")
@Composable
fun KcalConsumptionCardPreview() {
    FoodlyTheme {
        KcalConsumptionCard(mapOf("Lun" to 2000f, "Mar" to 1800f, "Mer" to 2200f, "Gio" to 1900f, "Ven" to 2300f, "Sab" to 2500f, "Dom" to 2100f))
    }
}

@Preview(name = "Score Card Preview")
@Composable
fun HealthyScoreCardPreview() {
    FoodlyTheme {
        HealthyScoreCard(score = 75.5f)
    }
}
