package com.example.ui.components

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notification.ReminderUrgency
import com.example.notification.SmartReminderAdvice
import com.example.ui.theme.appSurfaceStyle

@Composable
fun SmartReminderCard(
    advice: SmartReminderAdvice,
    onTriggerNotification: () -> Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var notificationSentSuccess by remember { mutableStateOf(false) }

    // Android 13+ runtime notification permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val sent = onTriggerNotification()
            notificationSentSuccess = sent
            if (sent) {
                Toast.makeText(context, "Сповіщення надіслано в системну шторку!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Дозвіл на сповіщення відхилено", Toast.LENGTH_SHORT).show()
        }
    }

    val (badgeColor, icon, badgeLabel) = when (advice.urgency) {
        ReminderUrgency.HIGH -> Triple(
            Color(0xFFF59E0B),
            Icons.Default.NotificationsActive,
            "Час для гирі!"
        )
        ReminderUrgency.GAP_RECOVERY -> Triple(
            Color(0xFFEF4444),
            Icons.Default.Warning,
            "Перерва понад 72 год"
        )
        ReminderUrgency.RECOVERY -> Triple(
            Color(0xFF10B981),
            Icons.Default.SelfImprovement,
            "День відновлення"
        )
        ReminderUrgency.ON_TRACK -> Triple(
            Color(0xFF10B981),
            Icons.Default.CheckCircle,
            "План виконано"
        )
        ReminderUrgency.MEDIUM -> Triple(
            Color(0xFF38BDF8),
            Icons.Default.FitnessCenter,
            "Порада тренера"
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .appSurfaceStyle(cornerRadius = 14.dp)
            .border(1.dp, badgeColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(14.dp)
            .testTag("smart_reminder_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Розумний асистент 40+",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                    Text(
                        text = advice.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badgeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = advice.message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎯 ${advice.suggestedRoutine}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                modifier = Modifier.weight(1f)
            )

            OutlinedButton(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        val sent = onTriggerNotification()
                        notificationSentSuccess = sent
                        if (sent) {
                            Toast.makeText(context, "Сповіщення надіслано в системну шторку!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.testTag("send_smart_reminder_notification_btn")
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (notificationSentSuccess) "Надіслано!" else "В шторку",
                    fontSize = 11.sp,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}
