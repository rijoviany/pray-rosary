package com.trivium.rosarium.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.R
import com.trivium.rosarium.data.model.MysterySet

private val NavyHeader = Color(0xFF0D2036)
private val ParchmentBg = Color(0xFFF7F3EA)
private val CardIvory = Color(0xFFFFFFFF)
private val GoldAccent = Color(0xFFC5A059)
private val GoldBright = Color(0xFFE5BE42)
private val CharcoalText = Color(0xFF1E2836)
private val MutedText = Color(0xFF6B7A8D)

@Composable
fun HomeScreen(
    todayMysterySet: MysterySet,
    onNavigateToPray: () -> Unit,
    onNavigateToPrayers: () -> Unit,
    onNavigateToChaplets: () -> Unit,
    onNavigateToNovenas: () -> Unit,
    onNavigateToScripture: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ParchmentBg)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // 1. Top Bar with Hamburger & Notification Bell
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = NavyHeader,
                    modifier = Modifier.size(26.dp)
                )
            }

            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = NavyHeader,
                        modifier = Modifier.size(24.dp)
                    )
                }
                // Red badge dot
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, end = 10.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935))
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Hero Banner: Mary Portrait + Rosarium Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFCF9F2), Color(0xFFF3ECE0))
                    )
                )
                .border(1.dp, Color(0xFFE5DEC8), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mary Sacred Portrait
                Image(
                    painter = painterResource(id = R.drawable.hero_mary),
                    contentDescription = "Blessed Virgin Mary",
                    modifier = Modifier
                        .size(width = 110.dp, height = 125.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Title & Subtitle with Golden Cross
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Golden Latin Cross Symbol
                    Text(
                        text = "✟",
                        color = GoldAccent,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Rosarium",
                        color = NavyHeader,
                        fontSize = 28.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Pray • Meditate • Grow",
                        color = MutedText,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. 6 Quick Action Grid Cards (2 rows x 3 columns)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Row 1: Prayers, Rosary (Primary Navy Card), Chaplets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Prayers",
                    drawableRes = R.drawable.ic_our_father,
                    isPrimary = false,
                    onClick = onNavigateToPrayers,
                    modifier = Modifier.weight(1f)
                )

                QuickActionCard(
                    title = "Rosary",
                    drawableRes = R.drawable.ic_joyful_mysteries,
                    isPrimary = true,
                    onClick = onNavigateToPray,
                    modifier = Modifier.weight(1f)
                )

                QuickActionCard(
                    title = "Chaplets",
                    drawableRes = R.drawable.ic_sacred_heart_chaplet,
                    isPrimary = false,
                    onClick = onNavigateToChaplets,
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 2: Novenas, Scripture, Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Novenas",
                    drawableRes = R.drawable.ic_novena_saint_jude,
                    isPrimary = false,
                    onClick = onNavigateToNovenas,
                    modifier = Modifier.weight(1f)
                )

                QuickActionVectorCard(
                    title = "Scripture",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    onClick = onNavigateToScripture,
                    modifier = Modifier.weight(1f)
                )

                QuickActionVectorCard(
                    title = "Progress",
                    icon = Icons.Default.BarChart,
                    onClick = onNavigateToProgress,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. "Today's Rosary" Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(CardIvory)
                .border(1.dp, Color(0xFFE2D9CB), RoundedCornerShape(16.dp))
                .clickable(onClick = onNavigateToPray)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF9F5EC))
                    .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Today's Rosary",
                    color = CharcoalText,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = todayMysterySet.name,
                    color = MutedText,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Serif
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Start",
                tint = GoldAccent,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    drawableRes: Int,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(98.dp)
            .shadow(elevation = if (isPrimary) 4.dp else 2.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(if (isPrimary) NavyHeader else CardIvory)
            .border(
                width = 1.dp,
                color = if (isPrimary) GoldAccent else Color(0xFFE2D9CB),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = title,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = if (isPrimary) Color.White else CharcoalText,
                fontSize = 13.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun QuickActionVectorCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(98.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(CardIvory)
            .border(1.dp, Color(0xFFE2D9CB), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = GoldAccent,
                modifier = Modifier.size(34.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = CharcoalText,
                fontSize = 13.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
