package com.example.foodly.statistics

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodly.ui.theme.FoodlyTheme
import com.example.foodly.ui.theme.pie1
import com.example.foodly.ui.theme.pie2
import com.example.foodly.ui.theme.pie3
import com.example.foodly.ui.theme.pie4
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = viewModel()
) {
    val context = LocalContext.current
    val nutritionalData by viewModel.nutritionalData.collectAsState()
    val healthyScore by viewModel.healthyScoreData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Carica i dati quando il composable viene creato
    LaunchedEffect(Unit) {
        viewModel.loadStatistics(context)
    }

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
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mostra loading, error o contenuto
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null -> {
                    ErrorCard(
                        errorMessage = errorMessage.toString(),
                        onRetry = { viewModel.loadStatistics(context) },
                        onDismiss = { viewModel.clearError() }
                    )
                }

                nutritionalData != null -> {
                    NutritionalDataCard(nutritionalData!!)
                    HealthyScoreCard(healthyScore)
                    CaloriesScoreCard(nutritionalData!!.calories)
                }
            }
        }
    }
}

@Composable
fun NutritionalDataCard(nutritionalData: NutritionalData) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Composizione Nutrizionale Media",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Grafico a torta personalizzato
            PieChart(
                nutritionalData = nutritionalData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Legenda
            NutritionalLegend(nutritionalData)
        }
    }
}

@Composable
fun PieChart(
    nutritionalData: NutritionalData,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        //MaterialTheme.colorScheme.primary,
        pie1,
        pie2,
        pie3,
        pie4
    )

    val values = listOf(
        //nutritionalData.calories,
        nutritionalData.carbohydrates,
        nutritionalData.fat,
        nutritionalData.fiber,
        nutritionalData.protein

    )

    val total = values.sum()
    val proportions = values.map { it / total }
    val sweepAngles = proportions.map { it * 360f }

    Canvas(modifier = modifier) {
        val radius = size.minDimension / 3
        val center = Offset(size.width / 2, size.height / 2)

        var currentAngle = -90f // Inizia dall'alto

        sweepAngles.forEachIndexed { index, sweepAngle ->
            drawArc(
                color = colors[index],
                startAngle = currentAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = Size(radius * 2, radius * 2)
            )
            currentAngle += sweepAngle
        }

        // Disegna un cerchio interno per creare l'effetto "donut"
        drawCircle(
            color = androidx.compose.ui.graphics.Color.White,
            radius = radius * 0.5f,
            center = center
        )
    }
}

@Composable
fun NutritionalLegend(nutritionalData: NutritionalData) {
    val legendItems = listOf(
        //"Calorie" to "${nutritionalData.calories.toInt()} kcal" to MaterialTheme.colorScheme.primary,

        "Carboidrati" to "${nutritionalData.carbohydrates.toInt()}g" to pie1,
        "Grassi" to "${nutritionalData.fat.toInt()}g" to pie2,
        "Fibre" to "${nutritionalData.fiber.toInt()}g" to pie3,
        "Proteine" to "${nutritionalData.protein.toInt()}g" to pie4,

        )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        legendItems.forEach { (nameValue, color) ->
            val (name, value) = nameValue
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ErrorCard(
    errorMessage: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Errore",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Chiudi")
                }
                Button(onClick = onRetry) {
                    Text("Riprova")
                }
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
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Muted helper text
            )
        }
    }
}

@Composable
fun CaloriesScoreCard(score: Float, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .animateContentSize(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp) // area verticale minima in cui centrare
                .padding(16.dp)
        ) {

            Text(
                text = "${score.toInt()} kCal",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Center)
                    .padding(bottom = 16.dp)
            )

            Text(
                "di kCal medie consumate nell'ultima settimana",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(top = 8.dp)
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
