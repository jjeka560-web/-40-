package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkoutSessionRecord
import com.example.model.PredefinedWorkouts
import com.example.model.RoutineExercise
import com.example.model.WorkoutRoutine
import com.example.model.WorkoutType
import com.example.notification.SmartReminderAdvice
import com.example.ui.components.SmartReminderCard
import com.example.ui.components.WorkoutHistoryCalendar
import com.example.ui.theme.appSurfaceStyle
import java.time.LocalDate

@Composable
fun WorkoutsScreen(
    onLaunchInTimer: (WorkoutRoutine) -> Unit,
    workoutSessions: List<WorkoutSessionRecord> = emptyList(),
    onLogSession: (String, String, Int, Int, String, LocalDate) -> Unit = { _, _, _, _, _, _ -> },
    onDeleteSession: (WorkoutSessionRecord) -> Unit = {},
    reminderAdvice: SmartReminderAdvice? = null,
    onTriggerNotification: () -> Boolean = { false },
    modifier: Modifier = Modifier
) {
    val routines = PredefinedWorkouts.allRoutines
    var selectedRoutine by remember { mutableStateOf<WorkoutRoutine?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Комплекси, 1 = Календар та Історія

    Column(modifier = modifier.fillMaxSize()) {
        // Top Workout Section Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "🏋️ Комплекси (6)",
                        fontSize = 13.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        softWrap = false
                    )
                },
                modifier = Modifier.testTag("tab_workout_routines")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = "📅 Календар та Історія",
                        fontSize = 13.sp,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        softWrap = false
                    )
                },
                modifier = Modifier.testTag("tab_workout_calendar")
            )
        }

        if (selectedTab == 1) {
            // Calendar & Training History Section
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp)
            ) {
                WorkoutHistoryCalendar(
                    sessions = workoutSessions,
                    onLogSession = onLogSession,
                    onDeleteSession = onDeleteSession,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            // Workout Routines Section
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isTablet = maxWidth >= 768.dp

                if (isTablet) {
                    // Tablet: Dual-Pane List-Detail layout
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left Pane: Routine Cards + Consistency bar
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                QuickConsistencyHeader(
                                    sessions = workoutSessions,
                                    onOpenCalendar = { selectedTab = 1 }
                                )
                            }

                            if (reminderAdvice != null) {
                                item {
                                    SmartReminderCard(
                                        advice = reminderAdvice,
                                        onTriggerNotification = onTriggerNotification
                                    )
                                }
                            }

                            items(routines) { routine ->
                                WorkoutRoutineCard(
                                    routine = routine,
                                    isSelected = routine == (selectedRoutine ?: routines.first()),
                                    onClick = { selectedRoutine = routine }
                                )
                            }
                        }

                        // Right Pane: Detail View of Selected Routine
                        Column(
                            modifier = Modifier
                                .weight(1.3f)
                                .fillMaxSize()
                        ) {
                            val activeRoutine = selectedRoutine ?: routines.first()
                            WorkoutRoutineDetailView(
                                routine = activeRoutine,
                                onBack = null,
                                onLaunchInTimer = { onLaunchInTimer(activeRoutine) },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    // Smartphone: List or Detail Screen with BackHandler
                    if (selectedRoutine != null) {
                        BackHandler { selectedRoutine = null }
                        WorkoutRoutineDetailView(
                            routine = selectedRoutine!!,
                            onBack = { selectedRoutine = null },
                            onLaunchInTimer = { onLaunchInTimer(selectedRoutine!!) },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                QuickConsistencyHeader(
                                    sessions = workoutSessions,
                                    onOpenCalendar = { selectedTab = 1 }
                                )
                            }

                            if (reminderAdvice != null) {
                                item {
                                    SmartReminderCard(
                                        advice = reminderAdvice,
                                        onTriggerNotification = onTriggerNotification
                                    )
                                }
                            }

                            item {
                                Column {
                                    Text(
                                        text = "Програми та Комплекси",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Науково обґрунтовані комплекси для чоловіків 40+: від васкуляризації до статики Засса",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            items(routines) { routine ->
                                WorkoutRoutineCard(
                                    routine = routine,
                                    isSelected = false,
                                    onClick = { selectedRoutine = routine }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickConsistencyHeader(
    sessions: List<WorkoutSessionRecord>,
    onOpenCalendar: () -> Unit
) {
    val totalCount = sessions.size
    val recentDateText = if (sessions.isNotEmpty()) {
        val last = sessions.first()
        val date = LocalDate.ofEpochDay(last.dateEpochDay)
        if (date == LocalDate.now()) "Сьогодні"
        else if (date == LocalDate.now().minusDays(1)) "Вчора"
        else "${date.dayOfMonth}.${date.monthValue}"
    } else "Немає"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable { onOpenCalendar() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Історія: $totalCount тренувань (останнє: $recentDateText)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Натисніть для перегляду календаря та дат",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 10.sp
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun WorkoutRoutineCard(
    routine: WorkoutRoutine,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon = when (routine.type) {
        WorkoutType.CARDIO -> Icons.Default.DirectionsRun
        WorkoutType.BODY_FLUSH -> Icons.Default.Favorite
        WorkoutType.WARMUP -> Icons.Default.SelfImprovement
        WorkoutType.MORNING -> Icons.Default.WbSunny
        WorkoutType.STRENGTH -> Icons.Default.FitnessCenter
        WorkoutType.ISOMETRIC_STATIC -> Icons.Default.Shield
    }

    val badgeColor = when (routine.type) {
        WorkoutType.CARDIO -> Color(0xFFEF4444)
        WorkoutType.BODY_FLUSH -> Color(0xFF10B981)
        WorkoutType.WARMUP -> Color(0xFF38BDF8)
        WorkoutType.MORNING -> Color(0xFFF59E0B)
        WorkoutType.STRENGTH -> Color(0xFF8B5CF6)
        WorkoutType.ISOMETRIC_STATIC -> Color(0xFFEC4899)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .appSurfaceStyle(cornerRadius = 14.dp)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("workout_card_${routine.type.name}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(badgeColor.copy(alpha = 0.15f))
                    .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = routine.type.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = routine.type.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "⏱ ~${routine.durationMinutes} хв",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• ${routine.intensity}",
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WorkoutRoutineDetailView(
    routine: WorkoutRoutine,
    onBack: (() -> Unit)?,
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("workout_detail_back")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = routine.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = routine.type.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Target Goal Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(10.dp)
        ) {
            Text(
                text = "🎯 Мета: ${routine.targetGoal}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action: Launch in Timer Button
        Button(
            onClick = onLaunchInTimer,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("launch_routine_timer_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Запустити в таймері",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                softWrap = false
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Вправи комплексу (${routine.exercises.size}):",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(routine.exercises) { exercise ->
                RoutineExerciseItem(exercise = exercise)
            }
        }
    }
}

@Composable
private fun RoutineExerciseItem(exercise: RoutineExercise) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        // Exercise Name gets full width so words never get squeezed into narrow line breaks
        Text(
            text = exercise.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Clean Badges Row below the title for Sets, Reps/Time, Rest
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = exercise.sets,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    maxLines = 1,
                    softWrap = false
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = exercise.repsOrTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    maxLines = 1,
                    softWrap = false
                )
            }

            if (exercise.restSec > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Відпочинок: ${exercise.restSec}с",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "🎯 М'язи: ${exercise.targetMuscle}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "💡 Техніка: ${exercise.executionTip}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "🔬 Біомеханіка: ${exercise.biomechanicsNote}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.sp
        )
    }
}
