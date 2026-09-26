package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundFeedbackManager
import com.example.data.AppDatabase
import com.example.data.ExerciseRepository
import com.example.data.PreferencesManager
import com.example.data.WorkoutExercise
import com.example.model.AtlasExercise
import com.example.model.WorkoutRoutine
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String) {
    WORKOUTS("Комплекси"),
    PLAN_BUILDER("Конструктор"),
    TIMER("Термінал"),
    KNOWLEDGE("База знань"),
    SETTINGS("Налаштування")
}

data class TimerExerciseStep(
    val name: String,
    val targetMuscle: String = "",
    val tip: String = ""
)

data class TimerLaunchConfig(
    val workSec: Int = 20,
    val restSec: Int = 10,
    val rounds: Int = 8,
    val title: String? = null,
    val exercises: List<TimerExerciseStep> = emptyList()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = ExerciseRepository(db.exerciseDao())
    private val sessionRepository = com.example.data.WorkoutSessionRepository(db.workoutSessionDao())
    val preferences = PreferencesManager(application)
    val soundManager = SoundFeedbackManager(application)

    val exercises: StateFlow<List<WorkoutExercise>> = repository.allExercises
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val workoutSessions: StateFlow<List<com.example.data.WorkoutSessionRecord>> = sessionRepository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentTheme: StateFlow<AppThemeMode> = preferences.currentTheme
    val scheduledDays: StateFlow<Set<Int>> = preferences.scheduledDays
    val soundEnabled: StateFlow<Boolean> = preferences.soundEnabled
    val vibrationEnabled: StateFlow<Boolean> = preferences.vibrationEnabled
    val notificationsEnabled: StateFlow<Boolean> = preferences.notificationsEnabled
    val reminderHour: StateFlow<Int> = preferences.reminderHour
    val reminderMinute: StateFlow<Int> = preferences.reminderMinute

    val reminderManager = com.example.notification.SmartReminderManager(application)

    private val _currentTab = MutableStateFlow(AppNavTab.WORKOUTS)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    private val _timerConfig = MutableStateFlow(TimerLaunchConfig())
    val timerConfig: StateFlow<TimerLaunchConfig> = _timerConfig.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedDefaultsIfEmpty()
            sessionRepository.seedDemoSessionsIfEmpty()
        }
    }

    fun logSession(
        title: String,
        type: String,
        durationSeconds: Int,
        totalRounds: Int,
        notes: String = "",
        date: java.time.LocalDate = java.time.LocalDate.now()
    ) {
        viewModelScope.launch {
            sessionRepository.logSession(title, type, durationSeconds, totalRounds, notes, date)
        }
    }

    fun deleteSession(session: com.example.data.WorkoutSessionRecord) {
        viewModelScope.launch {
            sessionRepository.deleteSession(session)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        preferences.setNotificationsEnabled(enabled)
    }

    fun setReminderTime(hour: Int, minute: Int) {
        preferences.setReminderTime(hour, minute)
    }

    fun getSmartReminderAdvice(): com.example.notification.SmartReminderAdvice {
        return reminderManager.evaluateReminder(scheduledDays.value, workoutSessions.value)
    }

    fun triggerSmartNotification(): Boolean {
        val advice = getSmartReminderAdvice()
        return reminderManager.sendSmartNotification(advice)
    }

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun launchRoutineInTimer(routine: WorkoutRoutine) {
        _timerConfig.value = TimerLaunchConfig(
            workSec = routine.type.recommendedWorkSec,
            restSec = routine.type.recommendedRestSec,
            rounds = routine.exercises.size.coerceAtLeast(routine.type.recommendedRounds),
            title = routine.title,
            exercises = routine.exercises.map {
                TimerExerciseStep(
                    name = it.name,
                    targetMuscle = it.targetMuscle,
                    tip = it.executionTip
                )
            }
        )
        _currentTab.value = AppNavTab.TIMER
    }

    fun launchAtlasExerciseInTimer(exercise: AtlasExercise, workSec: Int = 30, restSec: Int = 15, rounds: Int = 5) {
        _timerConfig.value = TimerLaunchConfig(
            workSec = workSec,
            restSec = restSec,
            rounds = rounds,
            title = "Вправа: ${exercise.name}",
            exercises = List(rounds) {
                TimerExerciseStep(
                    name = exercise.name,
                    targetMuscle = exercise.targetMuscles,
                    tip = exercise.breathingTip
                )
            }
        )
        _currentTab.value = AppNavTab.TIMER
    }

    fun launchUserPlanInTimer() {
        val currentPlan = exercises.value
        if (currentPlan.isEmpty()) return
        _timerConfig.value = TimerLaunchConfig(
            workSec = 40,
            restSec = 60,
            rounds = currentPlan.size,
            title = "Мій персональний план",
            exercises = currentPlan.map {
                TimerExerciseStep(
                    name = it.name,
                    targetMuscle = it.category,
                    tip = "${it.sets} підходи по ${it.reps} повторів (відпочинок ${it.restSeconds}с)"
                )
            }
        )
        _currentTab.value = AppNavTab.TIMER
    }

    fun addExercise(name: String, sets: Int, reps: Int, restSeconds: Int, category: String) {
        viewModelScope.launch {
            val count = exercises.value.size
            repository.insert(
                WorkoutExercise(
                    name = name,
                    sets = sets,
                    reps = reps,
                    restSeconds = restSeconds,
                    category = category,
                    orderIndex = count + 1
                )
            )
        }
    }

    fun deleteExercise(exercise: WorkoutExercise) {
        viewModelScope.launch {
            repository.delete(exercise)
        }
    }

    fun clearAllExercises() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            repository.resetToDefaultPlan()
        }
    }

    fun toggleScheduledDay(dayNumber: Int) {
        preferences.toggleScheduledDay(dayNumber)
    }

    fun setTheme(theme: AppThemeMode) {
        preferences.setTheme(theme)
    }

    fun setSoundEnabled(enabled: Boolean) {
        preferences.setSoundEnabled(enabled)
    }

    fun setVibrationEnabled(enabled: Boolean) {
        preferences.setVibrationEnabled(enabled)
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
