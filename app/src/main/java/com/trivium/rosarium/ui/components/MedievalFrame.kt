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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
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
 * Medieval Frame Overlay:
 * Cleaned up to never overlap or cover the status bar icons or system navigation.
 */
@Composable
fun MedievalFrameOverlay(
    modifier: Modifier = Modifier
) {
    // Deliberately no full-screen canvas borders that cover status bar icons
}

/**
 * Top bar displaying current mystery title and selector dropdown,
 * Menu button on the left, and Read / Scripture button on the right.
 */
@Composable
fun TopMedievalBar(
    mysterySetName: String,
    currentMysteryTitle: String,
    onMenuClick: () -> Unit,
    onTitleClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    onScriptureClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0D2036))
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Back Arrow (or Menu)
        IconButton(onClick = {
            if (onBackClick != null) onBackClick() else onMenuClick()
        }) {
            Icon(
                imageVector = if (onBackClick != null) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Menu,
                contentDescription = if (onBackClick != null) "Back" else "Menu",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Center: Mystery Dropdown Selector
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
                    color = Color.White,
                    fontSize = 17.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Select Mystery",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = currentMysteryTitle,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.5.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center
            )
        }

        // Right: Scripture / Book Icon
        if (onScriptureClick != null) {
            IconButton(onClick = onScriptureClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = "Scripture",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

/**
 * Bottom navigation controls: Intentions, Novenas, and Progress circular medieval buttons.
 */
@Composable
fun BottomMedievalControls(
    onIntentionsClick: () -> Unit,
    onNovenasClick: () -> Unit,
    onProgressClick: () -> Unit,
    currentBeadCount: Int = 4,
    totalBeadCount: Int = 53,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Intentions Button with Candle icon
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = onIntentionsClick)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .border(1.2.dp, MedievalGold.copy(alpha = 0.85f), CircleShape)
                    .background(Color(0xFF140F0A).copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(20.dp)) {
                    val w = size.width
                    val h = size.height
                    // Candle flame
                    val flamePath = Path().apply {
                        moveTo(w * 0.5f, h * 0.12f)
                        quadraticTo(w * 0.72f, h * 0.38f, w * 0.5f, h * 0.48f)
                        quadraticTo(w * 0.28f, h * 0.38f, w * 0.5f, h * 0.12f)
                    }
                    drawPath(flamePath, MedievalGoldBright)
                    // Candle wax body
                    drawRoundRect(
                        color = MedievalGold.copy(alpha = 0.9f),
                        topLeft = Offset(w * 0.36f, h * 0.48f),
                        size = Size(w * 0.28f, h * 0.42f),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Intentions",
                color = ParchmentLight.copy(alpha = 0.9f),
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                letterSpacing = 0.3.sp
            )
            Text(
                text = "Prayer",
                color = MedievalGoldBright.copy(alpha = 0.75f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Serif
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 2. 9-Day Catholic Novenas Button with Cross
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = onNovenasClick)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .border(1.2.dp, MedievalGold.copy(alpha = 0.85f), CircleShape)
                    .background(Color(0xFF140F0A).copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(20.dp)) {
                    val w = size.width
                    val h = size.height
                    // Radiance behind cross
                    drawCircle(
                        color = MedievalGold.copy(alpha = 0.3f),
                        radius = 4.dp.toPx(),
                        center = Offset(w * 0.5f, h * 0.38f)
                    )
                    // Latin cross
                    drawLine(
                        color = MedievalGoldBright,
                        start = Offset(w * 0.5f, h * 0.12f),
                        end = Offset(w * 0.5f, h * 0.88f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = MedievalGoldBright,
                        start = Offset(w * 0.22f, h * 0.38f),
                        end = Offset(w * 0.78f, h * 0.38f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Novenas",
                color = ParchmentLight.copy(alpha = 0.9f),
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                letterSpacing = 0.3.sp
            )
            Text(
                text = "9 Days",
                color = MedievalGoldBright.copy(alpha = 0.9f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 3. Rosary Progress Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = onProgressClick)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .border(1.2.dp, MedievalGold.copy(alpha = 0.85f), CircleShape)
                    .background(Color(0xFF140F0A).copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Progress",
                    tint = MedievalGoldBright,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Progress",
                color = ParchmentLight.copy(alpha = 0.9f),
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                letterSpacing = 0.3.sp
            )
            Text(
                text = "$currentBeadCount / $totalBeadCount",
                color = MedievalGoldBright.copy(alpha = 0.9f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
