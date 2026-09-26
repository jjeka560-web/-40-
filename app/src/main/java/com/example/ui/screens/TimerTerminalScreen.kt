package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundFeedbackManager
import com.example.ui.TimerExerciseStep
import com.example.ui.theme.appSurfaceStyle
import kotlinx.coroutines.delay

enum class TimerPhase {
    IDLE, PREPARE, WORK, REST, FINISHED
}

@Composable
fun TimerTerminalScreen(
    soundManager: SoundFeedbackManager,
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    onToggleSound: () -> Unit,
    onToggleVibration: () -> Unit,
    initialWorkSec: Int = 20,
    initialRestSec: Int = 10,
    initialRounds: Int = 8,
    routineTitle: String? = null,
    exercisesList: List<TimerExerciseStep> = emptyList(),
    onSessionCompleted: (String, String, Int, Int) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var workSecInput by remember { mutableIntStateOf(initialWorkSec) }
    var restSecInput by remember { mutableIntStateOf(initialRestSec) }
    var roundsInput by remember { mutableIntStateOf(initialRounds) }

    var phase by remember { mutableStateOf(TimerPhase.IDLE) }
    var currentRound by remember { mutableIntStateOf(1) }
    var timeRemaining by remember { mutableIntStateOf(initialWorkSec) }
    var isRunning by remember { mutableStateOf(false) }

    // Helper formatting
    fun formatTime(totalSeconds: Int): String {
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        return "%02d:%02d".format(m, s)
    }

    // Keep initial values in sync if user navigates with a workout routine
    LaunchedEffect(initialWorkSec, initialRestSec, initialRounds, routineTitle, exercisesList) {
        if (!isRunning && phase == TimerPhase.IDLE) {
            workSecInput = initialWorkSec
            restSecInput = initialRestSec
            roundsInput = initialRounds
            timeRemaining = initialWorkSec
        }
    }

    // Determine currently active exercise and up-next exercise
    val currentExercise: TimerExerciseStep? = remember(currentRound, exercisesList) {
        if (exercisesList.isNotEmpty()) {
            val index = (currentRound - 1) % exercisesList.size
            exercisesList.getOrNull(index)
        } else null
    }

    val nextExercise: TimerExerciseStep? = remember(currentRound, exercisesList) {
        if (exercisesList.isNotEmpty() && currentRound < roundsInput) {
            val nextIndex = currentRound % exercisesList.size
            exercisesList.getOrNull(nextIndex)
        } else null
    }

    // Timer Tick Loop
    LaunchedEffect(isRunning, phase) {
        if (!isRunning) return@LaunchedEffect

        while (isRunning) {
            delay(1000L)
            if (!isRunning) break

            // Countdown sound on last 3 seconds
            if (timeRemaining in 2..4) {
                soundManager.playCountdownBeep(soundEnabled, vibrationEnabled)
            }

            if (timeRemaining > 1) {
                timeRemaining--
            } else {
                // Phase Transition
                when (phase) {
                    TimerPhase.PREPARE -> {
                        phase = TimerPhase.WORK
                        timeRemaining = workSecInput
                        soundManager.playWorkStartBeep(soundEnabled, vibrationEnabled)
                    }
                    TimerPhase.WORK -> {
                        if (currentRound >= roundsInput) {
                            phase = TimerPhase.FINISHED
                            isRunning = false
                            timeRemaining = 0
                            soundManager.playFinishBeep(soundEnabled, vibrationEnabled)
                            val totalDuration = (workSecInput + restSecInput) * roundsInput
                            onSessionCompleted(
                                routineTitle ?: "Інтервальне тренування",
                                "Таймер",
                                totalDuration,
                                roundsInput
                            )
                        } else {
                            if (restSecInput > 0) {
                                phase = TimerPhase.REST
                                timeRemaining = restSecInput
                                soundManager.playRestStartBeep(soundEnabled, vibrationEnabled)
                            } else {
                                currentRound++
                                timeRemaining = workSecInput
                                soundManager.playWorkStartBeep(soundEnabled, vibrationEnabled)
                            }
                        }
                    }
                    TimerPhase.REST -> {
                        currentRound++
                        phase = TimerPhase.WORK
                        timeRemaining = workSecInput
                        soundManager.playWorkStartBeep(soundEnabled, vibrationEnabled)
                    }
                    TimerPhase.IDLE, TimerPhase.FINISHED -> {
                        isRunning = false
                    }
                }
            }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 768.dp

        if (isTablet) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Timer Terminal Display
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxSize()
                ) {
                    TimerDisplayCard(
                        phase = phase,
                        timeFormatted = formatTime(timeRemaining),
                        currentRound = currentRound,
                        totalRounds = roundsInput,
                        progress = calculateProgress(phase, timeRemaining, workSecInput, restSecInput),
                        routineTitle = routineTitle,
                        currentExercise = currentExercise,
                        nextExercise = nextExercise,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TimerControlsRow(
                        isRunning = isRunning,
                        phase = phase,
                        onStart = {
                            if (phase == TimerPhase.IDLE || phase == TimerPhase.FINISHED) {
                                currentRound = 1
                                phase = TimerPhase.PREPARE
                                timeRemaining = 3
                                soundManager.playCountdownBeep(soundEnabled, vibrationEnabled)
                            }
                            isRunning = true
                        },
                        onPause = { isRunning = false },
                        onReset = {
                            isRunning = false
                            phase = TimerPhase.IDLE
                            currentRound = 1
                            timeRemaining = workSecInput
                        },
                        onSkipRound = {
                            if (currentRound < roundsInput) {
                                currentRound++
                                phase = TimerPhase.WORK
                                timeRemaining = workSecInput
                            }
                        }
                    )
                }

                // Right Column: Configuration & Presets
                Column(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    TimerSettingsCard(
                        workSec = workSecInput,
                        restSec = restSecInput,
                        rounds = roundsInput,
                        onWorkChange = { workSecInput = it; if (phase == TimerPhase.IDLE) timeRemaining = it },
                        onRestChange = { restSecInput = it },
                        onRoundsChange = { roundsInput = it },
                        onPresetSelect = { w, r, rnd ->
                            workSecInput = w
                            restSecInput = r
                            roundsInput = rnd
                            if (phase == TimerPhase.IDLE) timeRemaining = w
                        },
                        soundEnabled = soundEnabled,
                        vibrationEnabled = vibrationEnabled,
                        onToggleSound = onToggleSound,
                        onToggleVibration = onToggleVibration,
                        isRunning = isRunning
                    )
                }
            }
        } else {
            // Smartphone Single-Column Scrollable
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TimerDisplayCard(
                    phase = phase,
                    timeFormatted = formatTime(timeRemaining),
                    currentRound = currentRound,
                    totalRounds = roundsInput,
                    progress = calculateProgress(phase, timeRemaining, workSecInput, restSecInput),
                    routineTitle = routineTitle,
                    currentExercise = currentExercise,
                    nextExercise = nextExercise,
                    modifier = Modifier.fillMaxWidth()
                )

                TimerControlsRow(
                    isRunning = isRunning,
                    phase = phase,
                    onStart = {
                        if (phase == TimerPhase.IDLE || phase == TimerPhase.FINISHED) {
                            currentRound = 1
                            phase = TimerPhase.PREPARE
                            timeRemaining = 3
                            soundManager.playCountdownBeep(soundEnabled, vibrationEnabled)
                        }
                        isRunning = true
                    },
                    onPause = { isRunning = false },
                    onReset = {
                        isRunning = false
                        phase = TimerPhase.IDLE
                        currentRound = 1
                        timeRemaining = workSecInput
                    },
                    onSkipRound = {
                        if (currentRound < roundsInput) {
                            currentRound++
                            phase = TimerPhase.WORK
                            timeRemaining = workSecInput
                        }
                    }
                )

                TimerSettingsCard(
                    workSec = workSecInput,
                    restSec = restSecInput,
                    rounds = roundsInput,
                    onWorkChange = { workSecInput = it; if (phase == TimerPhase.IDLE) timeRemaining = it },
                    onRestChange = { restSecInput = it },
                    onRoundsChange = { roundsInput = it },
                    onPresetSelect = { w, r, rnd ->
                        workSecInput = w
                        restSecInput = r
                        roundsInput = rnd
                        if (phase == TimerPhase.IDLE) timeRemaining = w
                    },
                    soundEnabled = soundEnabled,
                    vibrationEnabled = vibrationEnabled,
                    onToggleSound = onToggleSound,
                    onToggleVibration = onToggleVibration,
                    isRunning = isRunning
                )
            }
        }
    }
}

private fun calculateProgress(phase: TimerPhase, remaining: Int, workSec: Int, restSec: Int): Float {
    return when (phase) {
        TimerPhase.PREPARE -> remaining / 3f
        TimerPhase.WORK -> if (workSec > 0) remaining.toFloat() / workSec.toFloat() else 1f
        TimerPhase.REST -> if (restSec > 0) remaining.toFloat() / restSec.toFloat() else 1f
        TimerPhase.IDLE -> 1f
        TimerPhase.FINISHED -> 0f
    }
}

@Composable
private fun TimerDisplayCard(
    phase: TimerPhase,
    timeFormatted: String,
    currentRound: Int,
    totalRounds: Int,
    progress: Float,
    routineTitle: String?,
    currentExercise: TimerExerciseStep?,
    nextExercise: TimerExerciseStep?,
    modifier: Modifier = Modifier
) {
    val statusColor by animateColorAsState(
        targetValue = when (phase) {
            TimerPhase.IDLE -> MaterialTheme.colorScheme.primary
            TimerPhase.PREPARE -> Color(0xFF38BDF8)
            TimerPhase.WORK -> Color(0xFF10B981)     // Green
            TimerPhase.REST -> Color(0xFFF59E0B)     // Amber
            TimerPhase.FINISHED -> Color(0xFF3B82F6) // Blue
        },
        label = "status_color"
    )

    val statusText = when (phase) {
        TimerPhase.IDLE -> "ОЧІКУВАННЯ"
        TimerPhase.PREPARE -> "ПІДГОТУВАТИСЯ!"
        TimerPhase.WORK -> "РОБОТА!"
        TimerPhase.REST -> "ВІДПОЧИНОК"
        TimerPhase.FINISHED -> "ТРЕНУВАННЯ ЗАВЕРШЕНО!"
    }

    Column(
        modifier = modifier
            .appSurfaceStyle(cornerRadius = 20.dp)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Active Routine Title Header
        if (!routineTitle.isNullOrBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = routineTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Active Exercise Prominent Banner (Solves Issue #1!)
        if (currentExercise != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (phase == TimerPhase.REST) "ЗАРАЗ ВІДПОЧИНОК" else "ПОТОЧНА ВПРАВА:",
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = currentExercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        lineHeight = 22.sp
                    )
                    if (currentExercise.targetMuscle.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "🎯 ${currentExercise.targetMuscle}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                    if (currentExercise.tip.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "💡 ${currentExercise.tip}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        Text(
            text = statusText,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = statusColor,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("timer_status_text")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Circular timer indicator with digital read-out inside
        Box(
            modifier = Modifier.size(210.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 12.dp,
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeFormatted,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("timer_digital_display")
                )

                Text(
                    text = if (phase == TimerPhase.FINISHED) "Фініш!" else "Раунд: $currentRound / $totalRounds",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("timer_round_display")
                )
            }
        }

        if (phase == TimerPhase.FINISHED) {
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF10B981).copy(alpha = 0.15f))
                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "✅ Зафіксовано в календарі тренувань!",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Up next teaser during rest or work
        if (nextExercise != null && phase != TimerPhase.FINISHED) {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "🔜 Далі: ${nextExercise.name}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TimerControlsRow(
    isRunning: Boolean,
    phase: TimerPhase,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onSkipRound: () -> Unit
) {
    // Solves Issue #2: Clear, short, responsive button labels without letter hyphens
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isRunning) {
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                modifier = Modifier
                    .weight(1.3f)
                    .height(50.dp)
                    .testTag("timer_start_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (phase == TimerPhase.IDLE || phase == TimerPhase.FINISHED) "Старт" else "Продовжити",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    softWrap = false
                )
            }
        } else {
            Button(
                onClick = onPause,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                modifier = Modifier
                    .weight(1.3f)
                    .height(50.dp)
                    .testTag("timer_pause_button")
            ) {
                Icon(Icons.Default.Pause, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Пауза",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        OutlinedButton(
            onClick = onSkipRound,
            modifier = Modifier
                .height(50.dp)
                .testTag("timer_skip_button")
        ) {
            Icon(Icons.Default.SkipNext, contentDescription = "Наступний раунд")
        }

        OutlinedButton(
            onClick = onReset,
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .testTag("timer_reset_button")
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Скинути",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
private fun TimerSettingsCard(
    workSec: Int,
    restSec: Int,
    rounds: Int,
    onWorkChange: (Int) -> Unit,
    onRestChange: (Int) -> Unit,
    onRoundsChange: (Int) -> Unit,
    onPresetSelect: (Int, Int, Int) -> Unit,
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    onToggleSound: () -> Unit,
    onToggleVibration: () -> Unit,
    isRunning: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appSurfaceStyle(cornerRadius = 16.dp)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Налаштування протоколу",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row {
                IconButton(onClick = onToggleSound, modifier = Modifier.testTag("toggle_sound_button")) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                        contentDescription = "Звук",
                        tint = if (soundEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onToggleVibration, modifier = Modifier.testTag("toggle_vibration_button")) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = "Вібрація",
                        tint = if (vibrationEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Швидкі пресети:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = (workSec == 20 && restSec == 10 && rounds == 8),
                onClick = { if (!isRunning) onPresetSelect(20, 10, 8) },
                label = { Text("Табата (20/10)", fontSize = 11.sp, maxLines = 1, softWrap = false) },
                modifier = Modifier.testTag("preset_tabata")
            )
            FilterChip(
                selected = (workSec == 12 && restSec == 45 && rounds == 6),
                onClick = { if (!isRunning) onPresetSelect(12, 45, 6) },
                label = { Text("Засс (12/45)", fontSize = 11.sp, maxLines = 1, softWrap = false) },
                modifier = Modifier.testTag("preset_zass")
            )
            FilterChip(
                selected = (workSec == 30 && restSec == 15 && rounds == 10),
                onClick = { if (!isRunning) onPresetSelect(30, 15, 10) },
                label = { Text("Промивка", fontSize = 11.sp, maxLines = 1, softWrap = false) },
                modifier = Modifier.testTag("preset_flush")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = workSec.toString(),
                onValueChange = { onWorkChange(it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0) },
                label = { Text("Робота (с)", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .testTag("timer_work_input"),
                enabled = !isRunning,
                singleLine = true
            )
            OutlinedTextField(
                value = restSec.toString(),
                onValueChange = { onRestChange(it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0) },
                label = { Text("Відпочинок (с)", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1.1f)
                    .testTag("timer_rest_input"),
                enabled = !isRunning,
                singleLine = true
            )
            OutlinedTextField(
                value = rounds.toString(),
                onValueChange = { onRoundsChange(it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 1) },
                label = { Text("Раунди", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(0.9f)
                    .testTag("timer_rounds_input"),
                enabled = !isRunning,
                singleLine = true
            )
        }
    }
}
