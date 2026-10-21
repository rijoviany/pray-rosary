package com.trivium.rosarium.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.ui.theme.MedievalGold
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.theme.MedievalGoldDark
import com.trivium.rosarium.ui.theme.ParchmentBase
import com.trivium.rosarium.ui.theme.ParchmentLight

/**
 * Renders the illuminated Gothic arch border and ornate corner flourishes
 * matching the authentic medieval manuscript aesthetic.
 */
@Composable
fun MedievalFrameOverlay(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val margin = 14.dp.toPx()
        val cornerSize = 40.dp.toPx()

        // Outer delicate gold perimeter border
        val goldBorderBrush = Brush.linearGradient(
            colors = listOf(MedievalGoldDark, MedievalGoldBright, MedievalGold, MedievalGoldDark),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        )

        drawRect(
            brush = goldBorderBrush,
            topLeft = Offset(margin, margin),
            size = Size(w - 2 * margin, h - 2 * margin),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Inner micro-line
        drawRect(
            brush = goldBorderBrush,
            topLeft = Offset(margin + 5.dp.toPx(), margin + 5.dp.toPx()),
            size = Size(w - 2 * (margin + 5.dp.toPx()), h - 2 * (margin + 5.dp.toPx())),
            style = Stroke(width = 0.75.dp.toPx())
        )

        // Ornate Corner Flourishes (Gothic trefoils / corner accents)
        fun drawCornerFlourish(ox: Float, oy: Float, flipX: Float, flipY: Float) {
            val path = Path().apply {
                moveTo(ox, oy + (flipY * cornerSize))
                lineTo(ox, oy)
                lineTo(ox + (flipX * cornerSize), oy)
                // Diagonal leaf curve
                quadraticBezierTo(
                    ox + (flipX * cornerSize * 0.4f),
                    oy + (flipY * cornerSize * 0.4f),
                    ox + (flipX * 12.dp.toPx()),
                    oy + (flipY * 12.dp.toPx())
                )
            }
            drawPath(
                path = path,
                brush = goldBorderBrush,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // 4 corners
        drawCornerFlourish(margin, margin, 1f, 1f) // Top-Left
        drawCornerFlourish(w - margin, margin, -1f, 1f) // Top-Right
        drawCornerFlourish(margin, h - margin, 1f, -1f) // Bottom-Left
        drawCornerFlourish(w - margin, h - margin, -1f, -1f) // Bottom-Right
    }
}

/**
 * Top bar displaying current mystery title and selector dropdown,
 * scripture reference toggle, and ornamental heraldic crest.
 */
@Composable
fun TopMedievalBar(
    mysterySetName: String,
    currentMysteryTitle: String,
    onTitleClick: () -> Unit,
    onScriptureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Heraldic Crest / Fleur-de-lis
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .border(1.dp, MedievalGold.copy(alpha = 0.5f), CircleShape)
                .background(Color(0xFF16120E).copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(20.dp)) {
                // Heraldic fleur-de-lis symbol
                val path = Path().apply {
                    moveTo(size.width * 0.5f, 2f)
                    quadraticBezierTo(size.width * 0.75f, size.height * 0.4f, size.width * 0.5f, size.height * 0.85f)
                    quadraticBezierTo(size.width * 0.25f, size.height * 0.4f, size.width * 0.5f, 2f)
                }
                drawPath(path, MedievalGoldBright)
                drawCircle(MedievalGoldBright, radius = 2.5.dp.toPx(), center = Offset(size.width * 0.25f, size.height * 0.55f))
                drawCircle(MedievalGoldBright, radius = 2.5.dp.toPx(), center = Offset(size.width * 0.75f, size.height * 0.55f))
            }
        }

        // Center Mystery Titles (clickable dropdown selector)
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(role = Role.Button, onClick = onTitleClick)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = mysterySetName,
                    color = ParchmentLight,
                    fontSize = 17.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "▾",
                    color = MedievalGoldBright,
                    fontSize = 14.sp
                )
            }
            Text(
                text = currentMysteryTitle,
                color = ParchmentBase.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center
            )
        }

        // Right Scripture Book Icon Button
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .border(1.dp, MedievalGold.copy(alpha = 0.5f), CircleShape)
                .background(Color(0xFF16120E).copy(alpha = 0.7f))
                .clickable(role = Role.Button, onClick = onScriptureClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoStories,
                contentDescription = "Scripture and Meditation",
                tint = MedievalGoldBright,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Bottom navigation controls: Menu and Progress circular medieval buttons.
 */
@Composable
fun BottomMedievalControls(
    onMenuClick: () -> Unit,
    onProgressClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Menu Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = onMenuClick)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .border(1.dp, MedievalGold.copy(alpha = 0.7f), CircleShape)
                    .background(Color(0xFF1E1711).copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MedievalGoldBright,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Menu",
                color = ParchmentLight.copy(alpha = 0.9f),
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Progress Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = onProgressClick)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .border(1.dp, MedievalGold.copy(alpha = 0.7f), CircleShape)
                    .background(Color(0xFF1E1711).copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Progress",
                    tint = MedievalGoldBright,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Progress",
                color = ParchmentLight.copy(alpha = 0.9f),
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif,
                letterSpacing = 0.5.sp
            )
        }
    }
}
