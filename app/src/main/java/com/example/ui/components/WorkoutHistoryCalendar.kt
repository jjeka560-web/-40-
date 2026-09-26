package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkoutSessionRecord
import com.example.ui.theme.appSurfaceStyle
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WorkoutHistoryCalendar(
    sessions: List<WorkoutSessionRecord>,
    onLogSession: (String, String, Int, Int, String, LocalDate) -> Unit,
    onDeleteSession: (WorkoutSessionRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    val ukrLocale = Locale("uk", "UA")
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showManualLogDialog by remember { mutableStateOf(false) }

    // Map sessions by epochDay for quick lookup
    val sessionsByDay = remember(sessions) {
        sessions.groupBy { it.dateEpochDay }
    }

    // Calculate Consistency Stats
    val totalThisMonth = remember(sessions, currentYearMonth) {
        sessions.count {
            val date = LocalDate.ofEpochDay(it.dateEpochDay)
            date.year == currentYearMonth.year && date.month == currentYearMonth.month
        }
    }

    // Calculate current streak
    val streakDays = remember(sessions) {
        var streak = 0
        var checkDate = LocalDate.now()
        // If no workout today, check from yesterday
        if (!sessionsByDay.containsKey(checkDate.toEpochDay())) {
            checkDate = checkDate.minusDays(1)
        }
        while (sessionsByDay.containsKey(checkDate.toEpochDay())) {
            streak++
            checkDate = checkDate.minusDays(1)
        }
        streak
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .appSurfaceStyle(cornerRadius = 16.dp)
            .padding(14.dp)
    ) {
        // Consistency & Streak Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Календар та Історія Тренувань",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Контроль регулярності та відновлення 40+",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            OutlinedButton(
                onClick = { showManualLogDialog = true },
                modifier = Modifier.testTag("manual_log_session_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Записати", fontSize = 12.sp, maxLines = 1, softWrap = false)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Consistency Metrics Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF10B981).copy(alpha = 0.12f))
                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp, horizontal = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Серія (Streak)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF10B981),
                            fontSize = 10.sp
                        )
                        Text(
                            text = "$streakDays дн. поспіль",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp, horizontal = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "У цьому місяці",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "$totalThisMonth тренувань",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Month Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { currentYearMonth = currentYearMonth.minusMonths(1) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Попередній місяць", modifier = Modifier.size(18.dp))
            }

            Text(
                text = "${currentYearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, ukrLocale).replaceFirstChar { it.uppercase() }} ${currentYearMonth.year}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (currentYearMonth != YearMonth.now()) {
                    TextButton(
                        onClick = {
                            currentYearMonth = YearMonth.now()
                            selectedDate = LocalDate.now()
                        },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
                    ) {
                        Text("Сьогодні", fontSize = 11.sp)
                    }
                }

                IconButton(
                    onClick = { currentYearMonth = currentYearMonth.plusMonths(1) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Наступний місяць", modifier = Modifier.size(18.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days of week header (Пн..Нд)
        val daysOfWeek = listOf(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            daysOfWeek.forEach { dow ->
                Text(
                    text = dow.getDisplayName(TextStyle.SHORT, ukrLocale).take(2).replaceFirstChar { it.uppercase() },
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Days Grid
        val firstDayOfMonth = currentYearMonth.atDay(1)
        val daysInMonth = currentYearMonth.lengthOfMonth()
        // DayOfWeek values: Monday=1 ... Sunday=7 -> offset is dayOfWeek.value - 1
        val startOffset = firstDayOfMonth.dayOfWeek.value - 1
        val totalCells = ((startOffset + daysInMonth + 6) / 7) * 7

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            for (row in 0 until (totalCells / 7)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - startOffset + 1

                        if (dayNumber in 1..daysInMonth) {
                            val date = currentYearMonth.atDay(dayNumber)
                            val epochDay = date.toEpochDay()
                            val daySessions = sessionsByDay[epochDay].orEmpty()
                            val hasCompleted = daySessions.isNotEmpty()
                            val isSelected = date == selectedDate
                            val isToday = date == LocalDate.now()

                            val dayBgColor by animateColorAsState(
                                targetValue = when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    hasCompleted -> Color(0xFF10B981).copy(alpha = 0.18f)
                                    isToday -> MaterialTheme.colorScheme.surfaceVariant
                                    else -> Color.Transparent
                                },
                                label = "cal_day_bg"
                            )

                            val dayTextColor by animateColorAsState(
                                targetValue = when {
                                    isSelected -> MaterialTheme.colorScheme.onPrimary
                                    hasCompleted -> Color(0xFF10B981)
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                label = "cal_day_text"
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(dayBgColor)
                                    .border(
                                        width = when {
                                            isSelected -> 2.dp
                                            isToday -> 1.5.dp
                                            hasCompleted -> 1.dp
                                            else -> 0.5.dp
                                        },
                                        color = when {
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            isToday -> MaterialTheme.colorScheme.primary
                                            hasCompleted -> Color(0xFF10B981).copy(alpha = 0.5f)
                                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedDate = date }
                                    .testTag("cal_cell_${date.year}_${date.monthValue}_$dayNumber"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = dayNumber.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isSelected || isToday || hasCompleted) FontWeight.Bold else FontWeight.Normal,
                                        color = dayTextColor,
                                        fontSize = 12.sp
                                    )

                                    if (hasCompleted) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else Color(0xFF10B981))
                                        )
                                    }
                                }
                            }
                        } else {
                            // Empty spacer cell
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Selected Day Details Section
        val selectedDaySessions = sessionsByDay[selectedDate.toEpochDay()].orEmpty()
        val formattedSelectedDate = selectedDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", ukrLocale))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Тренування за $formattedSelectedDate",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (selectedDaySessions.isNotEmpty()) {
                Text(
                    text = "${selectedDaySessions.size} сесія(-ї)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedDaySessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "У цей день тренувань не зафіксовано.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(
                        onClick = { showManualLogDialog = true },
                        modifier = Modifier.testTag("log_today_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Записати виконане тренування", fontSize = 12.sp)
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                selectedDaySessions.forEach { session ->
                    CompletedSessionCard(
                        session = session,
                        onDelete = { onDeleteSession(session) }
                    )
                }
            }
        }
    }

    // Manual Workout Logging Dialog
    if (showManualLogDialog) {
        ManualLogSessionDialog(
            defaultDate = selectedDate,
            onDismiss = { showManualLogDialog = false },
            onConfirm = { title, type, durationSec, rounds, notes, date ->
                onLogSession(title, type, durationSec, rounds, notes, date)
                showManualLogDialog = false
            }
        )
    }
}

@Composable
private fun CompletedSessionCard(
    session: WorkoutSessionRecord,
    onDelete: () -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val timeStr = Instant.ofEpochMilli(session.timestampMillis)
        .atZone(ZoneId.systemDefault())
        .format(timeFormatter)

    val durationMin = session.durationSeconds / 60

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
            .testTag("completed_session_${session.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = session.workoutTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Видалити запис", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🏷 ${session.workoutType}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "⏱ $durationMin хв",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "🔄 ${session.totalRounds} р.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "🕒 $timeStr",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (session.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📝 ${session.notes}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ManualLogSessionDialog(
    defaultDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, Int, String, LocalDate) -> Unit
) {
    var title by remember { mutableStateOf("Комплекс з гирями") }
    var type by remember { mutableStateOf("Розвиток сили") }
    var minutesText by remember { mutableStateOf("25") }
    var roundsText by remember { mutableStateOf("5") }
    var notes by remember { mutableStateOf("") }

    val presetTypes = listOf("Розвиток сили", "Ізометричні вправи", "Кардіо", "Промивка організму", "Розминка", "Ранкова зарядка")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Зафіксувати тренування", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Назва тренування") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Тип навантаження:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    presetTypes.take(3).forEach { pType ->
                        OutlinedButton(
                            onClick = { type = pType },
                            colors = if (type == pType) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)) else ButtonDefaults.outlinedButtonColors(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(pType.take(8), fontSize = 10.sp, maxLines = 1, softWrap = false)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = minutesText,
                        onValueChange = { minutesText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Тривалість (хв)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = roundsText,
                        onValueChange = { roundsText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Раундів") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Нотатки (вага гирі, пульс, самопочуття)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val minutes = minutesText.toIntOrNull() ?: 20
                    val rounds = roundsText.toIntOrNull() ?: 1
                    onConfirm(title.trim(), type, minutes * 60, rounds, notes.trim(), defaultDate)
                }
            ) {
                Text("Зберегти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}
