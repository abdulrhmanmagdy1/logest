// ============================================
// 🚀 Edham Logistics - AR Measurement Screen
// Premium Dark Theme with AR Integration
// ============================================

package com.edham.logistics.ui.client

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edham.logistics.ar.ARCoreManager
import com.edham.logistics.ui.components.*
import com.edham.logistics.ui.theme.*

/**
 * ============================================
 * AR Measurement Screen
 * ============================================
 * شاشة القياس بالواقع المعزز للعملاء
 */
@Composable
fun ARMeasurementScreen(
    onBack: () -> Unit,
    onMeasurementComplete: (Float, String) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color.Black,
            Color(0xFF0A0A0A),
            Color(0xFF0F0F0F)
        )
    )

    var isScanning by remember { mutableStateOf(false) }
    var currentMeasurement by remember { mutableStateOf(0f) }
    var measurementHistory by remember { mutableStateOf<List<MeasurementRecord>>(emptyList()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Ambient Glow Effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            IceBlue.copy(alpha = glowAlpha * 0.1f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(
                            x = Float.POSITIVE_INFINITY,
                            y = Float.POSITIVE_INFINITY
                        ),
                        radius = 800f
                    )
                )
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                ARMeasurementTopBar(onBack = onBack)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ARCameraPreview(
                    isScanning = isScanning,
                    currentMeasurement = currentMeasurement
                )

                MeasurementControls(
                    isScanning = isScanning,
                    onStartScan = { isScanning = true },
                    onStopScan = { 
                        isScanning = false
                        val newMeasurement = (0.5f..5.0f).random()
                        currentMeasurement = newMeasurement
                        val record = MeasurementRecord(
                            value = newMeasurement,
                            unit = "متر",
                            timestamp = System.currentTimeMillis(),
                            type = "الطول"
                        )
                        measurementHistory = measurementHistory + record
                        onMeasurementComplete(newMeasurement, "القياس اكتمل")
                    },
                    onClearHistory = { 
                        measurementHistory = emptyList()
                        currentMeasurement = 0f
                    }
                )

                MeasurementInfo(
                    currentMeasurement = currentMeasurement,
                    measurementHistory = measurementHistory
                )
            }
        }
    }
}

@Composable
private fun ARMeasurementTopBar(
    onBack: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                spotColor = IceBlue.copy(alpha = glowAlpha),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2A2A2A))
                        .border(
                            width = 1.dp,
                            color = IceBlue.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = IceBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "قياس الواقع المعزز",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "AR Measurement",
                        color = IceBlue,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            NeonBadge(
                text = "AR",
                badgeType = NeonBadgeType.INFO
            )
        }
    }
}

@Composable
private fun ARCameraPreview(
    isScanning: Boolean,
    currentMeasurement: Float
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    NeonCard(
        onClick = {},
        cardType = NeonCardType.GLOW,
        glowColor = if (isScanning) IceBlue else EdhamOrange
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            // AR Camera Preview Simulation
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0A0A0A),
                                Color(0xFF1A1A1A),
                                Color(0xFF0A0A0A)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isScanning) {
                    // Scanning Animation
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(64.dp),
                            color = IceBlue,
                            strokeWidth = 4.dp
                        )

                        Text(
                            text = "جاري المسح...",
                            color = IceBlue,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        LinearProgressIndicator(
                            progress = scanProgress,
                            modifier = Modifier
                                .width(200.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = IceBlue,
                            trackColor = Color(0xFF2A2A2A)
                        )
                    }
                } else {
                    // AR View Placeholder
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = "AR Camera",
                            tint = EdhamOrange.copy(alpha = 0.6f),
                            modifier = Modifier.size(64.dp)
                        )

                        Text(
                            text = "كاميرا الواقع المعزز",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (currentMeasurement > 0) {
                            Text(
                                text = "${currentMeasurement.toInt()} سم",
                                color = EdhamOrange,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                // AR Overlay Lines
                if (isScanning) {
                    AROverlayLines()
                }
            }
        }
    }
}

@Composable
private fun AROverlayLines() {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Corner Markers
        listOf(
            androidx.compose.ui.geometry.Offset(20f, 20f),
            androidx.compose.ui.geometry.Offset(-20f, 20f),
            androidx.compose.ui.geometry.Offset(20f, -20f),
            androidx.compose.ui.geometry.Offset(-20f, -20f)
        ).forEach { offset ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .offset(offset.x.dp, offset.y.dp)
                    .border(
                        width = 2.dp,
                        color = IceBlue.copy(alpha = pulseAlpha),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }

        // Center Crosshair
        Box(
            modifier = Modifier
                .size(60.dp)
                .border(
                    width = 1.dp,
                    color = IceBlue.copy(alpha = pulseAlpha * 0.5f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        color = IceBlue.copy(alpha = pulseAlpha),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
private fun MeasurementControls(
    isScanning: Boolean,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onClearHistory: () -> Unit
) {
    NeonCard(
        onClick = {},
        cardType = NeonCardType.DEFAULT,
        glowColor = EdhamOrange
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "عناصر التحكم",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NeonButton(
                    onClick = if (isScanning) onStopScan else onStartScan,
                    text = if (isScanning) "إيقاف المسح" else "بدء المسح",
                    icon = if (isScanning) Icons.Default.Stop else Icons.Default.PlayArrow,
                    buttonColor = if (isScanning) ErrorRed else SuccessGreen,
                    modifier = Modifier.weight(1f)
                )

                NeonButton(
                    onClick = onClearHistory,
                    text = "مسح",
                    icon = Icons.Default.Clear,
                    buttonColor = WarningYellow,
                    modifier = Modifier.weight(1f)
                )
            }

            // Measurement Tips
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0F0F0F)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "نصائح للقياس الدقيق:",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    listOf(
                        "• تأكد من إضاءة جيدة",
                        "• حافظ على استقرار الكاميرا",
                        "• قم بتحديد نقاط البداية والنهاية بوضوح",
                        "• انتظر حتى يستقر القياس"
                    ).forEach { tip ->
                        Text(
                            text = tip,
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MeasurementInfo(
    currentMeasurement: Float,
    measurementHistory: List<MeasurementRecord>
) {
    NeonCard(
        onClick = {},
        cardType = NeonCardType.DEFAULT,
        glowColor = SuccessGreen
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "معلومات القياس",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            // Current Measurement
            if (currentMeasurement > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0F0F0F)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "القياس الحالي",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "${currentMeasurement.toInt()} سم",
                            color = SuccessGreen,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            text = "${(currentMeasurement / 100).toInt()} متر",
                            color = Color.White.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Measurement History
            if (measurementHistory.isNotEmpty()) {
                Text(
                    text = "سجل القياسات",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                measurementHistory.takeLast(3).forEach { record ->
                    MeasurementHistoryItem(record = record)
                }
            }
        }
    }
}

@Composable
private fun MeasurementHistoryItem(record: MeasurementRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A0A0A)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${record.value.toInt()} ${record.unit}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    text = record.type,
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = formatTimestamp(record.timestamp),
                color = Color.White.copy(alpha = 0.5f),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "الآن"
        diff < 3600_000 -> "${diff / 60_000} دقيقة"
        diff < 86_400_000 -> "${diff / 3_600_000} ساعة"
        else -> "${diff / 86_400_000} يوم"
    }
}

// ============================================
// Data Classes
// ============================================

data class MeasurementRecord(
    val value: Float,
    val unit: String,
    val timestamp: Long,
    val type: String
)
