package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.model.AtlasDiagramType

@Composable
fun AtlasDiagramIllustration(
    diagramType: AtlasDiagramType,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val accentColor = Color(0xFF10B981)   // Movement / Direction Green
    val neutralColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
    val guideColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val bellColor = Color(0xFFE11D48)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Background subtle grid lines
            drawLine(guideColor, Offset(0f, h * 0.85f), Offset(w, h * 0.85f), strokeWidth = 2f)

            when (diagramType) {
                AtlasDiagramType.SWING -> drawSwingDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
                AtlasDiagramType.CLEAN -> drawCleanDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
                AtlasDiagramType.PRESS -> drawPressDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
                AtlasDiagramType.SNATCH -> drawSnatchDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
                AtlasDiagramType.SQUAT -> drawSquatDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
                AtlasDiagramType.LUNGE -> drawLungeDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
                AtlasDiagramType.DEADLIFT -> drawDeadliftDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
                AtlasDiagramType.GRIP_WRIST -> drawGripDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
                AtlasDiagramType.CORE_ABS -> drawCoreDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
                AtlasDiagramType.NECK -> drawNeckDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
                AtlasDiagramType.ROTATION_HALO -> drawHaloDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
                AtlasDiagramType.THROW -> drawThrowDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
                AtlasDiagramType.ISOMETRIC_HOLD -> drawIsometricDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
                AtlasDiagramType.TURKISH_GETUP -> drawGetUpDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
                AtlasDiagramType.THRUSTER -> drawThrusterDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
                AtlasDiagramType.STRETCHING -> drawStretchDiagram(w, h, primaryColor, accentColor, bellColor, neutralColor)
            }
        }
    }
}

// 1. SWING DIAGRAM
private fun DrawScope.drawSwingDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    val floorY = h * 0.85f

    // Athlete in hip hinge (left side)
    drawCircle(athlete, radius = 10f, center = Offset(w * 0.32f, h * 0.38f)) // head
    drawLine(athlete, Offset(w * 0.32f, h * 0.44f), Offset(w * 0.36f, h * 0.60f), strokeWidth = 7f, cap = StrokeCap.Round) // spine
    drawLine(athlete, Offset(w * 0.36f, h * 0.60f), Offset(w * 0.38f, floorY), strokeWidth = 7f, cap = StrokeCap.Round) // legs
    drawLine(athlete, Offset(w * 0.32f, h * 0.46f), Offset(w * 0.22f, h * 0.72f), strokeWidth = 6f, cap = StrokeCap.Round) // arm back
    drawCircle(bell, radius = 12f, center = Offset(w * 0.22f, h * 0.72f)) // kettlebell bottom

    // Athlete in standing lockout (right side)
    drawCircle(athlete, radius = 10f, center = Offset(w * 0.65f, h * 0.25f)) // head
    drawLine(athlete, Offset(w * 0.65f, h * 0.30f), Offset(w * 0.65f, h * 0.58f), strokeWidth = 7f, cap = StrokeCap.Round) // spine
    drawLine(athlete, Offset(w * 0.65f, h * 0.58f), Offset(w * 0.65f, floorY), strokeWidth = 7f, cap = StrokeCap.Round) // legs
    drawLine(athlete, Offset(w * 0.65f, h * 0.34f), Offset(w * 0.85f, h * 0.38f), strokeWidth = 6f, cap = StrokeCap.Round) // arm forward
    drawCircle(bell, radius = 14f, center = Offset(w * 0.85f, h * 0.38f)) // kettlebell at chest

    // Arc path with arrow
    val path = Path().apply {
        moveTo(w * 0.24f, h * 0.70f)
        quadraticTo(w * 0.50f, h * 0.80f, w * 0.82f, h * 0.40f)
    }
    drawPath(path, accent, style = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))))
}

// 2. CLEAN DIAGRAM
private fun DrawScope.drawCleanDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    val floorY = h * 0.85f
    // Standing rack position
    drawCircle(athlete, radius = 10f, center = Offset(w * 0.5f, h * 0.22f))
    drawLine(athlete, Offset(w * 0.5f, h * 0.28f), Offset(w * 0.5f, h * 0.56f), strokeWidth = 7f, cap = StrokeCap.Round)
    drawLine(athlete, Offset(w * 0.5f, h * 0.56f), Offset(w * 0.46f, floorY), strokeWidth = 6f, cap = StrokeCap.Round)
    drawLine(athlete, Offset(w * 0.5f, h * 0.56f), Offset(w * 0.54f, floorY), strokeWidth = 6f, cap = StrokeCap.Round)
    // Arm bent at chest
    drawLine(athlete, Offset(w * 0.5f, h * 0.32f), Offset(w * 0.56f, h * 0.48f), strokeWidth = 6f, cap = StrokeCap.Round)
    drawLine(athlete, Offset(w * 0.56f, h * 0.48f), Offset(w * 0.52f, h * 0.38f), strokeWidth = 6f, cap = StrokeCap.Round)
    drawCircle(bell, radius = 14f, center = Offset(w * 0.58f, h * 0.40f)) // bell on rack

    // Vertical clean lift vector
    val liftPath = Path().apply {
        moveTo(w * 0.32f, floorY)
        cubicTo(w * 0.32f, h * 0.65f, w * 0.42f, h * 0.50f, w * 0.56f, h * 0.42f)
    }
    drawPath(liftPath, accent, style = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))))
}

// 3. PRESS / OVERHEAD DIAGRAM
private fun DrawScope.drawPressDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    val floorY = h * 0.85f
    drawCircle(athlete, radius = 10f, center = Offset(w * 0.5f, h * 0.32f))
    drawLine(athlete, Offset(w * 0.5f, h * 0.38f), Offset(w * 0.5f, h * 0.62f), strokeWidth = 7f, cap = StrokeCap.Round)
    drawLine(athlete, Offset(w * 0.5f, h * 0.62f), Offset(w * 0.46f, floorY), strokeWidth = 6f, cap = StrokeCap.Round)
    drawLine(athlete, Offset(w * 0.5f, h * 0.62f), Offset(w * 0.54f, floorY), strokeWidth = 6f, cap = StrokeCap.Round)

    // Arm locked overhead
    drawLine(athlete, Offset(w * 0.5f, h * 0.40f), Offset(w * 0.54f, h * 0.16f), strokeWidth = 6f, cap = StrokeCap.Round)
    drawCircle(bell, radius = 14f, center = Offset(w * 0.54f, h * 0.14f))

    // Vertical thrust line
    drawLine(accent, Offset(w * 0.64f, h * 0.44f), Offset(w * 0.64f, h * 0.16f), strokeWidth = 4f, cap = StrokeCap.Round)
    // Arrow head
    drawLine(accent, Offset(w * 0.64f, h * 0.16f), Offset(w * 0.61f, h * 0.22f), strokeWidth = 4f)
    drawLine(accent, Offset(w * 0.64f, h * 0.16f), Offset(w * 0.67f, h * 0.22f), strokeWidth = 4f)
}

// 4. SNATCH DIAGRAM
private fun DrawScope.drawSnatchDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    drawPressDiagram(w, h, primary, accent, bell, athlete)
    // Continuous arc from floor to sky
    val path = Path().apply {
        moveTo(w * 0.25f, h * 0.85f)
        cubicTo(w * 0.28f, h * 0.50f, w * 0.42f, h * 0.30f, w * 0.52f, h * 0.14f)
    }
    drawPath(path, Color(0xFFF59E0B), style = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))))
}

// 5. SQUAT DIAGRAM
private fun DrawScope.drawSquatDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    val floorY = h * 0.85f
    // Deep squat posture
    drawCircle(athlete, radius = 10f, center = Offset(w * 0.5f, h * 0.42f))
    drawLine(athlete, Offset(w * 0.5f, h * 0.48f), Offset(w * 0.48f, h * 0.66f), strokeWidth = 7f, cap = StrokeCap.Round)
    // Thighs parallel to floor (horizontal)
    drawLine(athlete, Offset(w * 0.48f, h * 0.66f), Offset(w * 0.58f, h * 0.66f), strokeWidth = 7f, cap = StrokeCap.Round)
    drawLine(athlete, Offset(w * 0.58f, h * 0.66f), Offset(w * 0.54f, floorY), strokeWidth = 7f, cap = StrokeCap.Round)
    // Arms holding goblet kettlebell at chest
    drawLine(athlete, Offset(w * 0.5f, h * 0.50f), Offset(w * 0.58f, h * 0.52f), strokeWidth = 6f, cap = StrokeCap.Round)
    drawCircle(bell, radius = 13f, center = Offset(w * 0.58f, h * 0.52f))

    // 90 degree angle indicator
    drawLine(accent, Offset(w * 0.36f, h * 0.66f), Offset(w * 0.36f, floorY), strokeWidth = 3f, cap = StrokeCap.Round)
}

// 6. LUNGE DIAGRAM
private fun DrawScope.drawLungeDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    val floorY = h * 0.85f
    drawCircle(athlete, radius = 10f, center = Offset(w * 0.45f, h * 0.32f))
    drawLine(athlete, Offset(w * 0.45f, h * 0.38f), Offset(w * 0.45f, h * 0.60f), strokeWidth = 7f, cap = StrokeCap.Round)
    // Front bent leg (90 deg)
    drawLine(athlete, Offset(w * 0.45f, h * 0.60f), Offset(w * 0.60f, h * 0.62f), strokeWidth = 6f, cap = StrokeCap.Round)
    drawLine(athlete, Offset(w * 0.60f, h * 0.62f), Offset(w * 0.60f, floorY), strokeWidth = 6f, cap = StrokeCap.Round)
    // Rear knee down
    drawLine(athlete, Offset(w * 0.45f, h * 0.60f), Offset(w * 0.32f, floorY * 0.95f), strokeWidth = 6f, cap = StrokeCap.Round)
    // Hands holding bells by sides
    drawCircle(bell, radius = 11f, center = Offset(w * 0.42f, h * 0.66f))
    drawCircle(bell, radius = 11f, center = Offset(w * 0.52f, h * 0.66f))
}

// 7. DEADLIFT DIAGRAM
private fun DrawScope.drawDeadliftDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    val floorY = h * 0.85f
    drawCircle(athlete, radius = 10f, center = Offset(w * 0.38f, h * 0.45f))
    // Straight back angled
    drawLine(athlete, Offset(w * 0.38f, h * 0.50f), Offset(w * 0.48f, h * 0.62f), strokeWidth = 7f, cap = StrokeCap.Round)
    drawLine(athlete, Offset(w * 0.48f, h * 0.62f), Offset(w * 0.52f, floorY), strokeWidth = 7f, cap = StrokeCap.Round)
    // Arms extending down to bells
    drawLine(athlete, Offset(w * 0.40f, h * 0.52f), Offset(w * 0.46f, floorY - 14f), strokeWidth = 5f, cap = StrokeCap.Round)
    drawCircle(bell, radius = 13f, center = Offset(w * 0.46f, floorY - 14f))

    // Vertical arrow of extension
    drawLine(accent, Offset(w * 0.65f, floorY - 10f), Offset(w * 0.65f, h * 0.42f), strokeWidth = 4f, cap = StrokeCap.Round)
    drawLine(accent, Offset(w * 0.65f, h * 0.42f), Offset(w * 0.62f, h * 0.48f), strokeWidth = 4f)
    drawLine(accent, Offset(w * 0.65f, h * 0.42f), Offset(w * 0.68f, h * 0.48f), strokeWidth = 4f)
}

// 8. GRIP / WRIST DIAGRAM
private fun DrawScope.drawGripDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    val floorY = h * 0.85f
    // Forearm flat on floor
    drawLine(athlete, Offset(w * 0.25f, floorY - 10f), Offset(w * 0.55f, floorY - 10f), strokeWidth = 10f, cap = StrokeCap.Round)
    // Wrist and kettlebell levering from horizontal to vertical
    drawCircle(bell, radius = 18f, center = Offset(w * 0.70f, floorY - 26f))
    // Arc showing wrist rotation (pronation / flexion)
    drawArc(accent, startAngle = 180f, sweepAngle = 90f, useCenter = false, topLeft = Offset(w * 0.55f, floorY - 50f), size = Size(50f, 50f), style = Stroke(width = 4f))
}

// 9. CORE / ABS DIAGRAM
private fun DrawScope.drawCoreDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    val floorY = h * 0.85f
    // Athlete seated V-hold
    drawCircle(athlete, radius = 10f, center = Offset(w * 0.35f, h * 0.48f))
    drawLine(athlete, Offset(w * 0.35f, h * 0.53f), Offset(w * 0.48f, floorY - 10f), strokeWidth = 7f, cap = StrokeCap.Round) // torso
    drawLine(athlete, Offset(w * 0.48f, floorY - 10f), Offset(w * 0.70f, h * 0.55f), strokeWidth = 7f, cap = StrokeCap.Round) // legs elevated
    // Kettlebell held at chest
    drawCircle(bell, radius = 13f, center = Offset(w * 0.42f, h * 0.58f))

    // Rotation trajectory curve
    drawArc(accent, startAngle = 45f, sweepAngle = 150f, useCenter = false, topLeft = Offset(w * 0.30f, h * 0.52f), size = Size(90f, 50f), style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))))
}

// 10. NECK DIAGRAM
private fun DrawScope.drawNeckDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    val floorY = h * 0.85f
    // Torso forward bent with hands on knees
    drawCircle(athlete, radius = 12f, center = Offset(w * 0.40f, h * 0.45f))
    drawLine(athlete, Offset(w * 0.40f, h * 0.52f), Offset(w * 0.60f, h * 0.62f), strokeWidth = 8f, cap = StrokeCap.Round)
    drawLine(athlete, Offset(w * 0.60f, h * 0.62f), Offset(w * 0.62f, floorY), strokeWidth = 8f, cap = StrokeCap.Round)
    // Strap hanging down from head
    drawLine(Color(0xFF38BDF8), Offset(w * 0.40f, h * 0.45f), Offset(w * 0.40f, floorY - 15f), strokeWidth = 3f)
    drawCircle(bell, radius = 13f, center = Offset(w * 0.40f, floorY - 15f))
}

// 11. HALO ROTATION DIAGRAM
private fun DrawScope.drawHaloDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    val floorY = h * 0.85f
    drawCircle(athlete, radius = 14f, center = Offset(w * 0.50f, h * 0.40f)) // head
    drawLine(athlete, Offset(w * 0.50f, h * 0.48f), Offset(w * 0.50f, floorY), strokeWidth = 9f, cap = StrokeCap.Round)

    // Halo ellipse orbit around head
    drawOval(
        color = accent,
        topLeft = Offset(w * 0.32f, h * 0.28f),
        size = Size(w * 0.36f, h * 0.22f),
        style = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f)))
    )
    drawCircle(bell, radius = 13f, center = Offset(w * 0.68f, h * 0.35f))
}

// 12. THROW DIAGRAM
private fun DrawScope.drawThrowDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    val floorY = h * 0.85f
    drawCircle(athlete, radius = 10f, center = Offset(w * 0.25f, h * 0.40f))
    drawLine(athlete, Offset(w * 0.25f, h * 0.45f), Offset(w * 0.25f, floorY), strokeWidth = 7f, cap = StrokeCap.Round)
    drawLine(athlete, Offset(w * 0.25f, h * 0.48f), Offset(w * 0.38f, h * 0.34f), strokeWidth = 6f, cap = StrokeCap.Round)

    // Parabolic arc into the distance
    val path = Path().apply {
        moveTo(w * 0.38f, h * 0.34f)
        cubicTo(w * 0.55f, h * 0.12f, w * 0.75f, h * 0.25f, w * 0.90f, floorY)
    }
    drawPath(path, accent, style = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f))))
    drawCircle(bell, radius = 12f, center = Offset(w * 0.60f, h * 0.16f))
}

// 13. ISOMETRIC DIAGRAM
private fun DrawScope.drawIsometricDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    val floorY = h * 0.85f
    // Static Wall Sit or Iron Samson
    drawLine(athlete, Offset(w * 0.30f, h * 0.20f), Offset(w * 0.30f, floorY), strokeWidth = 8f) // wall
    drawCircle(athlete, radius = 10f, center = Offset(w * 0.38f, h * 0.46f))
    drawLine(athlete, Offset(w * 0.38f, h * 0.52f), Offset(w * 0.38f, h * 0.68f), strokeWidth = 7f, cap = StrokeCap.Round)
    drawLine(athlete, Offset(w * 0.38f, h * 0.68f), Offset(w * 0.54f, h * 0.68f), strokeWidth = 7f, cap = StrokeCap.Round)
    drawLine(athlete, Offset(w * 0.54f, h * 0.68f), Offset(w * 0.54f, floorY), strokeWidth = 7f, cap = StrokeCap.Round)
    drawCircle(bell, radius = 14f, center = Offset(w * 0.46f, h * 0.58f))

    // Radiating isometric tension waves
    drawCircle(color = accent.copy(alpha = 0.4f), radius = 24f, center = Offset(w * 0.46f, h * 0.58f), style = Stroke(width = 3f))
    drawCircle(color = accent.copy(alpha = 0.2f), radius = 34f, center = Offset(w * 0.46f, h * 0.58f), style = Stroke(width = 2f))
}

// 14. TURKISH GET-UP DIAGRAM
private fun DrawScope.drawGetUpDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    val floorY = h * 0.85f
    // Show 3 silhouettes: lying -> lunge -> standing with vertical eye-line
    // 1. Lying
    drawLine(athlete, Offset(w * 0.10f, floorY - 5f), Offset(w * 0.28f, floorY - 5f), strokeWidth = 6f, cap = StrokeCap.Round)
    drawLine(athlete, Offset(w * 0.15f, floorY - 5f), Offset(w * 0.15f, h * 0.52f), strokeWidth = 4f)
    drawCircle(bell, radius = 8f, center = Offset(w * 0.15f, h * 0.50f))

    // 2. High lunge bridge
    drawLine(athlete, Offset(w * 0.42f, h * 0.55f), Offset(w * 0.52f, floorY), strokeWidth = 5f)
    drawLine(athlete, Offset(w * 0.42f, h * 0.55f), Offset(w * 0.42f, h * 0.32f), strokeWidth = 4f)
    drawCircle(bell, radius = 9f, center = Offset(w * 0.42f, h * 0.30f))

    // 3. Standing lockout
    drawCircle(athlete, radius = 8f, center = Offset(w * 0.80f, h * 0.35f))
    drawLine(athlete, Offset(w * 0.80f, h * 0.40f), Offset(w * 0.80f, floorY), strokeWidth = 6f, cap = StrokeCap.Round)
    drawLine(athlete, Offset(w * 0.80f, h * 0.42f), Offset(w * 0.80f, h * 0.16f), strokeWidth = 5f, cap = StrokeCap.Round)
    drawCircle(bell, radius = 10f, center = Offset(w * 0.80f, h * 0.14f))

    // Eye line
    drawLine(accent, Offset(w * 0.80f, h * 0.33f), Offset(w * 0.80f, h * 0.22f), strokeWidth = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f)))
}

// 15. THRUSTER DIAGRAM
private fun DrawScope.drawThrusterDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    drawSquatDiagram(w, h, primary, accent, bell, athlete)
    // Continuous explosive drive line upward
    drawLine(accent, Offset(w * 0.72f, h * 0.75f), Offset(w * 0.72f, h * 0.15f), strokeWidth = 5f, cap = StrokeCap.Round)
    drawLine(accent, Offset(w * 0.72f, h * 0.15f), Offset(w * 0.68f, h * 0.22f), strokeWidth = 5f)
    drawLine(accent, Offset(w * 0.72f, h * 0.15f), Offset(w * 0.76f, h * 0.22f), strokeWidth = 5f)
    drawCircle(bell, radius = 12f, center = Offset(w * 0.72f, h * 0.14f))
}

// 16. STRETCHING DIAGRAM
private fun DrawScope.drawStretchDiagram(w: Float, h: Float, primary: Color, accent: Color, bell: Color, athlete: Color) {
    val floorY = h * 0.85f
    // Seated hamstring stretch
    drawCircle(athlete, radius = 10f, center = Offset(w * 0.45f, h * 0.52f))
    drawLine(athlete, Offset(w * 0.35f, floorY - 5f), Offset(w * 0.75f, floorY - 5f), strokeWidth = 6f, cap = StrokeCap.Round) // legs
    drawLine(athlete, Offset(w * 0.35f, floorY - 5f), Offset(w * 0.58f, h * 0.62f), strokeWidth = 6f, cap = StrokeCap.Round) // reaching forward
    // Gentle recovery curve
    drawArc(accent, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(w * 0.35f, h * 0.35f), size = Size(w * 0.35f, h * 0.35f), style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))))
}
