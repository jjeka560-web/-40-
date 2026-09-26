package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.KnowledgeBaseScreen
import com.example.ui.screens.PlanBuilderScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TimerTerminalScreen
import com.example.ui.screens.WorkoutsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val exercises by viewModel.exercises.collectAsStateWithLifecycle()
    val scheduledDays by viewModel.scheduledDays.collectAsStateWithLifecycle()
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()
    val timerConfig by viewModel.timerConfig.collectAsStateWithLifecycle()
    val workoutSessions by viewModel.workoutSessions.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val reminderHour by viewModel.reminderHour.collectAsStateWithLifecycle()

    // BackHandler: if on secondary tab, back button returns to WORKOUTS
    if (currentTab != AppNavTab.WORKOUTS) {
        BackHandler {
            viewModel.selectTab(AppNavTab.WORKOUTS)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 768.dp

        if (isTablet) {
            // Tablet: Side NavigationRail + Content Pane
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    header = {
                        Spacer(modifier = Modifier.height(16.dp))
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Coach 40+",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "©СЄРОУХПРОДАКШН",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                ) {
                    AppNavTab.entries.forEach { tab ->
                        val icon = getTabIcon(tab)
                        NavigationRailItem(
                            selected = currentTab == tab,
                            onClick = { viewModel.selectTab(tab) },
                            icon = { Icon(icon, contentDescription = tab.title) },
                            label = { Text(tab.title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                indicatorColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("nav_rail_tab_${tab.name}")
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    TabContent(
                        currentTab = currentTab,
                        viewModel = viewModel,
                        exercises = exercises,
                        scheduledDays = scheduledDays,
                        currentTheme = currentTheme,
                        soundEnabled = soundEnabled,
                        vibrationEnabled = vibrationEnabled,
                        timerConfig = timerConfig,
                        workoutSessions = workoutSessions,
                        notificationsEnabled = notificationsEnabled,
                        reminderHour = reminderHour
                    )
                }
            }
        } else {
            // Smartphone: TopAppBar + Content + Bottom NavigationBar
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.padding(start = 8.dp))
                                Column {
                                    Text(
                                        text = "Kettlebell Coach 40+",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        lineHeight = 20.sp
                                    )
                                    Text(
                                        text = "©СЄРОУХПРОДАКШН",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        letterSpacing = 1.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        AppNavTab.entries.forEach { tab ->
                            val icon = getTabIcon(tab)
                            NavigationBarItem(
                                selected = currentTab == tab,
                                onClick = { viewModel.selectTab(tab) },
                                icon = { Icon(icon, contentDescription = tab.title) },
                                label = { Text(tab.title, fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    indicatorColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("nav_bottom_tab_${tab.name}")
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    TabContent(
                        currentTab = currentTab,
                        viewModel = viewModel,
                        exercises = exercises,
                        scheduledDays = scheduledDays,
                        currentTheme = currentTheme,
                        soundEnabled = soundEnabled,
                        vibrationEnabled = vibrationEnabled,
                        timerConfig = timerConfig,
                        workoutSessions = workoutSessions,
                        notificationsEnabled = notificationsEnabled,
                        reminderHour = reminderHour
                    )
                }
            }
        }
    }
}

@Composable
private fun TabContent(
    currentTab: AppNavTab,
    viewModel: MainViewModel,
    exercises: List<com.example.data.WorkoutExercise>,
    scheduledDays: Set<Int>,
    currentTheme: com.example.ui.theme.AppThemeMode,
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    timerConfig: TimerLaunchConfig,
    workoutSessions: List<com.example.data.WorkoutSessionRecord>,
    notificationsEnabled: Boolean,
    reminderHour: Int
) {
    Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
        when (tab) {
            AppNavTab.WORKOUTS -> {
                WorkoutsScreen(
                    onLaunchInTimer = { routine ->
                        viewModel.launchRoutineInTimer(routine)
                    },
                    workoutSessions = workoutSessions,
                    onLogSession = { title, type, durationSec, rounds, notes, date ->
                        viewModel.logSession(title, type, durationSec, rounds, notes, date)
                    },
                    onDeleteSession = { session ->
                        viewModel.deleteSession(session)
                    },
                    reminderAdvice = viewModel.getSmartReminderAdvice(),
                    onTriggerNotification = { viewModel.triggerSmartNotification() }
                )
            }
            AppNavTab.PLAN_BUILDER -> {
                PlanBuilderScreen(
                    exercises = exercises,
                    scheduledDays = scheduledDays,
                    onDayToggle = { viewModel.toggleScheduledDay(it) },
                    onAddExercise = { name, sets, reps, rest, category ->
                        viewModel.addExercise(name, sets, reps, rest, category)
                    },
                    onDeleteExercise = { viewModel.deleteExercise(it) },
                    onClearAll = { viewModel.clearAllExercises() },
                    onResetDefaults = { viewModel.resetToDefaults() },
                    onLaunchPlanInTimer = { viewModel.launchUserPlanInTimer() }
                )
            }
            AppNavTab.TIMER -> {
                TimerTerminalScreen(
                    soundManager = viewModel.soundManager,
                    soundEnabled = soundEnabled,
                    vibrationEnabled = vibrationEnabled,
                    onToggleSound = { viewModel.setSoundEnabled(!soundEnabled) },
                    onToggleVibration = { viewModel.setVibrationEnabled(!vibrationEnabled) },
                    initialWorkSec = timerConfig.workSec,
                    initialRestSec = timerConfig.restSec,
                    initialRounds = timerConfig.rounds,
                    routineTitle = timerConfig.title,
                    exercisesList = timerConfig.exercises,
                    onSessionCompleted = { title, type, durationSec, rounds ->
                        viewModel.logSession(title, type, durationSec, rounds)
                    }
                )
            }
            AppNavTab.KNOWLEDGE -> {
                KnowledgeBaseScreen(
                    onAddExerciseToPlan = { name, sets, reps, rest, cat ->
                        viewModel.addExercise(name, sets, reps, rest, cat)
                    },
                    onLaunchExerciseInTimer = { ex ->
                        viewModel.launchAtlasExerciseInTimer(ex)
                    }
                )
            }
            AppNavTab.SETTINGS -> {
                SettingsScreen(
                    currentTheme = currentTheme,
                    onSelectTheme = { viewModel.setTheme(it) },
                    soundEnabled = soundEnabled,
                    onToggleSound = { viewModel.setSoundEnabled(it) },
                    vibrationEnabled = vibrationEnabled,
                    onToggleVibration = { viewModel.setVibrationEnabled(it) },
                    notificationsEnabled = notificationsEnabled,
                    onToggleNotifications = { viewModel.setNotificationsEnabled(it) },
                    reminderHour = reminderHour,
                    onSelectReminderHour = { viewModel.setReminderTime(it, 0) },
                    onSendTestNotification = { viewModel.triggerSmartNotification() },
                    onResetAllData = { viewModel.resetToDefaults() }
                )
            }
        }
    }
}

private fun getTabIcon(tab: AppNavTab): ImageVector {
    return when (tab) {
        AppNavTab.WORKOUTS -> Icons.Default.FitnessCenter
        AppNavTab.PLAN_BUILDER -> Icons.Default.CalendarMonth
        AppNavTab.TIMER -> Icons.Default.Timer
        AppNavTab.KNOWLEDGE -> Icons.Default.MenuBook
        AppNavTab.SETTINGS -> Icons.Default.Palette
    }
}
