package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("kettlebell_prefs", Context.MODE_PRIVATE)

    private val _currentTheme = MutableStateFlow(loadTheme())
    val currentTheme: StateFlow<AppThemeMode> = _currentTheme.asStateFlow()

    private val _scheduledDays = MutableStateFlow(loadScheduledDays())
    val scheduledDays: StateFlow<Set<Int>> = _scheduledDays.asStateFlow()

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean("sound_enabled", true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(prefs.getBoolean("vibration_enabled", true))
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(prefs.getBoolean("notifications_enabled", true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _reminderHour = MutableStateFlow(prefs.getInt("reminder_hour", 18))
    val reminderHour: StateFlow<Int> = _reminderHour.asStateFlow()

    private val _reminderMinute = MutableStateFlow(prefs.getInt("reminder_minute", 0))
    val reminderMinute: StateFlow<Int> = _reminderMinute.asStateFlow()

    private fun loadTheme(): AppThemeMode {
        val name = prefs.getString("app_theme", AppThemeMode.DARK_ATHLETIC.name) ?: AppThemeMode.DARK_ATHLETIC.name
        return try {
            AppThemeMode.valueOf(name)
        } catch (e: Exception) {
            AppThemeMode.DARK_ATHLETIC
        }
    }

    fun setTheme(theme: AppThemeMode) {
        prefs.edit().putString("app_theme", theme.name).apply()
        _currentTheme.value = theme
    }

    private fun loadScheduledDays(): Set<Int> {
        val raw = prefs.getString("scheduled_days", "1,3,5") ?: "1,3,5"
        return raw.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }

    fun toggleScheduledDay(dayNumber: Int) {
        val current = _scheduledDays.value.toMutableSet()
        if (current.contains(dayNumber)) {
            current.remove(dayNumber)
        } else {
            current.add(dayNumber)
        }
        prefs.edit().putString("scheduled_days", current.joinToString(",")).apply()
        _scheduledDays.value = current
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
        _soundEnabled.value = enabled
    }

    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("vibration_enabled", enabled).apply()
        _vibrationEnabled.value = enabled
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
        _notificationsEnabled.value = enabled
    }

    fun setReminderTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt("reminder_hour", hour)
            .putInt("reminder_minute", minute)
            .apply()
        _reminderHour.value = hour
        _reminderMinute.value = minute
    }
}
