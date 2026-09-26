package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkoutExercise
import com.example.ui.components.CalendarWeekView
import com.example.ui.components.ExerciseCatalogData
import com.example.ui.theme.appSurfaceStyle

@Composable
fun PlanBuilderScreen(
    exercises: List<WorkoutExercise>,
    scheduledDays: Set<Int>,
    onDayToggle: (Int) -> Unit,
    onAddExercise: (String, Int, Int, Int, String) -> Unit,
    onDeleteExercise: (WorkoutExercise) -> Unit,
    onClearAll: () -> Unit,
    onResetDefaults: () -> Unit,
    onLaunchPlanInTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearDialog by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 768.dp

        if (isTablet) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Calendar & Builder Form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    CalendarWeekView(
                        selectedDays = scheduledDays,
                        onDayToggle = onDayToggle
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    BuilderFormCard(
                        onAddExercise = onAddExercise,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Right Column: Current Plan List
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    PlanListCard(
                        exercises = exercises,
                        onDeleteExercise = onDeleteExercise,
                        onClearClick = { showClearDialog = true },
                        onResetClick = onResetDefaults,
                        onLaunchInTimer = onLaunchPlanInTimer,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            // Smartphone Layout (Vertical Scroll)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    CalendarWeekView(
                        selectedDays = scheduledDays,
                        onDayToggle = onDayToggle
                    )
                }
                item {
                    BuilderFormCard(onAddExercise = onAddExercise)
                }
                item {
                    PlanListCard(
                        exercises = exercises,
                        onDeleteExercise = onDeleteExercise,
                        onClearClick = { showClearDialog = true },
                        onResetClick = onResetDefaults,
                        onLaunchInTimer = onLaunchPlanInTimer
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Очистити поточну програму?") },
            text = { Text("Всі поточні вправи будуть видалені з плану. Ви зможете відновити рекомендований план будь-коли.") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAll()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_clear_plan_button")
                ) {
                    Text("Видалити", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Скасувати", maxLines = 1, softWrap = false)
                }
            }
        )
    }
}

@Composable
private fun BuilderFormCard(
    onAddExercise: (String, Int, Int, Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val catalog = ExerciseCatalogData.options
    var selectedOption by remember { mutableStateOf(catalog.first()) }
    var isCustomSelected by remember { mutableStateOf(false) }
    var customName by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var setsText by remember { mutableStateOf("3") }
    var repsText by remember { mutableStateOf("10") }
    var restText by remember { mutableStateOf("90") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .appSurfaceStyle(cornerRadius = 16.dp)
            .padding(14.dp)
    ) {
        Text(
            text = "Конструктор плану",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Оберіть вправу з бази або додайте свою",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Exercise Dropdown Selector
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { dropdownExpanded = true }
                    .padding(12.dp)
                    .testTag("exercise_dropdown_selector"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isCustomSelected) "✏️ Додати власну вправу" else "${selectedOption.category}: ${selectedOption.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            }

            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                catalog.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text("${item.category} • ${item.name}", fontSize = 13.sp)
                        },
                        onClick = {
                            selectedOption = item
                            isCustomSelected = false
                            setsText = item.defaultSets.toString()
                            repsText = item.defaultReps.toString()
                            restText = item.defaultRest.toString()
                            dropdownExpanded = false
                        }
                    )
                }
                DropdownMenuItem(
                    text = {
                        Text("✏️ Додати свою власну вправу...", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    },
                    onClick = {
                        isCustomSelected = true
                        dropdownExpanded = false
                    }
                )
            }
        }

        if (isCustomSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = customName,
                onValueChange = { customName = it },
                label = { Text("Назва вправи") },
                placeholder = { Text("Наприклад: Станова тяга гирі") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_exercise_input"),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = setsText,
                onValueChange = { setsText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Сети", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_sets"),
                singleLine = true
            )
            OutlinedTextField(
                value = repsText,
                onValueChange = { repsText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Повтори", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_reps"),
                singleLine = true
            )
            OutlinedTextField(
                value = restText,
                onValueChange = { restText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Відпочинок (с)", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1.2f)
                    .testTag("input_rest"),
                singleLine = true
            )
        }

        errorMessage?.let { err ->
            Spacer(modifier = Modifier.height(6.dp))
            Text(err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val finalName = if (isCustomSelected) customName.trim() else selectedOption.name
                val category = if (isCustomSelected) "Власна" else selectedOption.category.replace(Regex("[^\\p{L}\\s]"), "").trim()
                val sets = setsText.toIntOrNull() ?: 3
                val reps = repsText.toIntOrNull() ?: 10
                val rest = restText.toIntOrNull() ?: 60

                if (finalName.isBlank()) {
                    errorMessage = "Будь ласка, вкажіть назву вправи"
                    return@Button
                }
                errorMessage = null
                onAddExercise(finalName, sets, reps, rest, category)
                if (isCustomSelected) customName = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("add_exercise_button")
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("+ Додати вправу", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
        }
    }
}

@Composable
private fun PlanListCard(
    exercises: List<WorkoutExercise>,
    onDeleteExercise: (WorkoutExercise) -> Unit,
    onClearClick: () -> Unit,
    onResetClick: () -> Unit,
    onLaunchInTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .appSurfaceStyle(cornerRadius = 16.dp)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Поточна програма",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Вправ: ${exercises.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action Buttons Row (Responsive, no text wrapping by letter!)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (exercises.isNotEmpty()) {
                Button(
                    onClick = onLaunchInTimer,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag("launch_user_plan_timer")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("В таймер", fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                }
            }

            OutlinedButton(
                onClick = onResetClick,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("reset_defaults_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Базовий", fontSize = 12.sp, maxLines = 1, softWrap = false)
            }

            if (exercises.isNotEmpty()) {
                OutlinedButton(
                    onClick = onClearClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .weight(0.9f)
                        .height(44.dp)
                        .testTag("clear_plan_button")
                ) {
                    Text("Очистити", fontSize = 12.sp, maxLines = 1, softWrap = false)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (exercises.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "План порожній. Додайте вправи або відновіть базовий комплекс.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                exercises.forEach { exercise ->
                    ExerciseRowItem(
                        exercise = exercise,
                        onDelete = { onDeleteExercise(exercise) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseRowItem(
    exercise: WorkoutExercise,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("exercise_item_${exercise.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${exercise.sets} підходи × ${exercise.reps} повторів  |  Відпочинок: ${exercise.restSeconds}с",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.testTag("delete_exercise_${exercise.id}")
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Видалити",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
