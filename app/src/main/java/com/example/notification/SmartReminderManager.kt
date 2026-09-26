package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.data.WorkoutSessionRecord
import java.time.DayOfWeek
import java.time.LocalDate

enum class ReminderUrgency {
    HIGH,          // Scheduled today & ready after 48h recovery
    MEDIUM,        // Action recommended
    GAP_RECOVERY,  // 3+ days gap without workout
    RECOVERY,      // Yesterday was workout -> active recovery recommended
    ON_TRACK       // Already completed today
}

data class SmartReminderAdvice(
    val title: String,
    val message: String,
    val urgency: ReminderUrgency,
    val daysSinceLastWorkout: Long,
    val isScheduledDayToday: Boolean,
    val suggestedRoutine: String
)

class SmartReminderManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "kettlebell_coach_reminders"
        const val CHANNEL_NAME = "Розумні нагадування про тренування"
        const val NOTIFICATION_ID = 40401
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Науково обґрунтовані нагадування для підтримки регулярності тренувань 40+"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Evaluates user's training status based on scheduled days and workout history gap.
     */
    fun evaluateReminder(
        scheduledDays: Set<Int>,
        sessions: List<WorkoutSessionRecord>
    ): SmartReminderAdvice {
        val today = LocalDate.now()
        // Day of week in java.time: Monday=1 ... Sunday=7
        val todayDow = today.dayOfWeek.value
        val isScheduledToday = scheduledDays.contains(todayDow)

        // Find last completed session
        val latestSession = sessions.maxByOrNull { it.timestampMillis }
        val daysSinceLastWorkout: Long = if (latestSession != null) {
            val lastDate = LocalDate.ofEpochDay(latestSession.dateEpochDay)
            today.toEpochDay() - lastDate.toEpochDay()
        } else {
            -1L // No workouts yet
        }

        return when {
            // Case 1: Workout was completed today!
            daysSinceLastWorkout == 0L -> {
                SmartReminderAdvice(
                    title = "Сьогоднішній план виконано! 🌟",
                    message = "Чудова робота! Запущено синтез колагену та ангіогенез. Організму необхідний якісний сон та гідратація.",
                    urgency = ReminderUrgency.ON_TRACK,
                    daysSinceLastWorkout = 0,
                    isScheduledDayToday = isScheduledToday,
                    suggestedRoutine = "Відновлення та відпочинок"
                )
            }

            // Case 2: Workout was yesterday (24h gap)
            daysSinceLastWorkout == 1L -> {
                if (isScheduledToday) {
                    SmartReminderAdvice(
                        title = "Сьогодні за графіком активність 🔔",
                        message = "Вчора було тренування. Щоб уникнути перевантаження сухожиль, оберіть комплекс меншої інтенсивності.",
                        urgency = ReminderUrgency.MEDIUM,
                        daysSinceLastWorkout = 1,
                        isScheduledDayToday = true,
                        suggestedRoutine = "Васкуляризація (Промивка)"
                    )
                } else {
                    SmartReminderAdvice(
                        title = "День суперкомпенсації та відпочинку 💤",
                        message = "Для віку 40+ колаген I типу в зв'язках відновлюється 48 годин. Сьогодні ідеально підійде прогулянка або легка розтяжка.",
                        urgency = ReminderUrgency.RECOVERY,
                        daysSinceLastWorkout = 1,
                        isScheduledDayToday = false,
                        suggestedRoutine = "Ранкова зарядка або стретчинг"
                    )
                }
            }

            // Case 3: 2 days gap (~48-72 hours) - The golden window!
            daysSinceLastWorkout == 2L -> {
                if (isScheduledToday) {
                    SmartReminderAdvice(
                        title = "Час для гирі: пік суперкомпенсації! ⚡",
                        message = "Минуло 48 годин після попереднього заняття. Зв'язки та ЦНС повністю відновилися й готові до навантаження.",
                        urgency = ReminderUrgency.HIGH,
                        daysSinceLastWorkout = 2,
                        isScheduledDayToday = true,
                        suggestedRoutine = "Фундаментальна Сила або Статика Засса"
                    )
                } else {
                    SmartReminderAdvice(
                        title = "Організм повністю відновлено 🛡️",
                        message = "Минуло 48 годин. Хоча за календарем сьогодні відпочинок, коротка зарядка додасть бадьорості.",
                        urgency = ReminderUrgency.MEDIUM,
                        daysSinceLastWorkout = 2,
                        isScheduledDayToday = false,
                        suggestedRoutine = "Ранкова зарядка"
                    )
                }
            }

            // Case 4: 3 or more days gap (72h+ - adaptation starts declining)
            daysSinceLastWorkout >= 3L -> {
                SmartReminderAdvice(
                    title = "Увага: перерва $daysSinceLastWorkout дн. ⏳",
                    message = "Після 72 годин без стимулу капілярна мережа та щільність зв'язок втрачають адаптацію. Зробіть короткий 15-хвилинний комплекс!",
                    urgency = ReminderUrgency.GAP_RECOVERY,
                    daysSinceLastWorkout = daysSinceLastWorkout,
                    isScheduledDayToday = isScheduledToday,
                    suggestedRoutine = "Васкуляризація та Ангіогенез (20 хв)"
                )
            }

            // Case 5: No workouts in history
            else -> {
                SmartReminderAdvice(
                    title = "Почніть шлях богатирського здоров'я 🏋️",
                    message = "Оберіть свій перший комплекс для плавного старту та зміцнення зв'язкового апарату.",
                    urgency = ReminderUrgency.HIGH,
                    daysSinceLastWorkout = 0,
                    isScheduledDayToday = isScheduledToday,
                    suggestedRoutine = "Розминка та мобільність"
                )
            }
        }
    }

    /**
     * Sends a system notification with smart advice if permission is granted.
     */
    fun sendSmartNotification(advice: SmartReminderAdvice): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionGranted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!permissionGranted) {
                return false
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(advice.title)
            .setContentText(advice.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText("${advice.message}\n\n🎯 Рекомендовано: ${advice.suggestedRoutine}"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        return true
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
