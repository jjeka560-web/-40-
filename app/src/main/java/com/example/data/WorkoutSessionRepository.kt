package com.example.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class WorkoutSessionRepository(private val sessionDao: WorkoutSessionDao) {

    val allSessions: Flow<List<WorkoutSessionRecord>> = sessionDao.getAllSessions()

    fun getSessionsForDay(epochDay: Long): Flow<List<WorkoutSessionRecord>> {
        return sessionDao.getSessionsForDay(epochDay)
    }

    suspend fun logSession(
        title: String,
        type: String,
        durationSeconds: Int,
        totalRounds: Int,
        notes: String = "",
        date: LocalDate = LocalDate.now()
    ): Long {
        return sessionDao.insertSession(
            WorkoutSessionRecord(
                workoutTitle = title,
                workoutType = type,
                dateEpochDay = date.toEpochDay(),
                timestampMillis = System.currentTimeMillis(),
                durationSeconds = durationSeconds,
                totalRounds = totalRounds,
                notes = notes
            )
        )
    }

    suspend fun deleteSession(session: WorkoutSessionRecord) {
        sessionDao.deleteSession(session)
    }

    suspend fun deleteById(id: Long) {
        sessionDao.deleteById(id)
    }

    suspend fun seedDemoSessionsIfEmpty() {
        if (sessionDao.getCount() == 0) {
            val today = LocalDate.now()
            val demoList = listOf(
                WorkoutSessionRecord(
                    workoutTitle = "Фундаментальна Сила та М'язовий Корсет",
                    workoutType = "Розвиток сили",
                    dateEpochDay = today.toEpochDay(),
                    timestampMillis = System.currentTimeMillis() - 3600000L * 2,
                    durationSeconds = 1680,
                    totalRounds = 5,
                    notes = "Відмінне самопочуття, комфортні ваги 24 кг."
                ),
                WorkoutSessionRecord(
                    workoutTitle = "Комплекс Статичної Сили Олександра Засса",
                    workoutType = "Ізометричні вправи",
                    dateEpochDay = today.minusDays(2).toEpochDay(),
                    timestampMillis = System.currentTimeMillis() - 86400000L * 2 - 3600000L,
                    durationSeconds = 1200,
                    totalRounds = 6,
                    notes = "Подолання ізометрії 90% МІС, зв'язки відчуваються монолітними."
                ),
                WorkoutSessionRecord(
                    workoutTitle = "Васкуляризація та Ангіогенез Усіх М'язів",
                    workoutType = "Промивка організму",
                    dateEpochDay = today.minusDays(4).toEpochDay(),
                    timestampMillis = System.currentTimeMillis() - 86400000L * 4,
                    durationSeconds = 1440,
                    totalRounds = 10,
                    notes = "Глибоке омивання м'язів свіжою кров'ю без закислення."
                )
            )
            demoList.forEach { sessionDao.insertSession(it) }
        }
    }
}
