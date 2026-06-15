// ============================================
// 🚀 Edham Logistics - Advanced Client Dashboard
// Premium Dark Theme with AI & AR Integration
// ============================================

package com.edham.logistics.ui.client

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edham.logistics.ai.AIPredictionService
import com.edham.logistics.ar.ARCoreManager
import com.edham.logistics.ai.BlockchainService
import com.edham.logistics.ui.components.*
import com.edham.logistics.ui.theme.*
import kotlinx.coroutines.launch

/**
 * ============================================
 * Advanced Client Dashboard Screen
 * ============================================
 * واجهة لوحة تحكم العميل المتقدمة مع AI و AR
 */
@Composable
fun AdvancedClientDashboardScreen(
    onNavigateToCreateShipment: () -> Unit,
    onNavigateToTrackShipment: () -> Unit,
    onNavigateToARMeasurement: () -> Unit,
    onNavigateToBlockchain: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
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
                            EdhamOrange.copy(alpha = glowAlpha * 0.1f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(
                            x = Float.POSITIVE_INFINITY,
                            y = 0f
                        ),
                        radius = 800f
                    )
                )
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                AdvancedTopBar(
                    onProfileClick = onNavigateToProfile,
                    onLogoutClick = onLogout
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    WelcomeSection()
                }

                item {
                    QuickActionsSection(
                        onCreateShipment = onNavigateToCreateShipment,
                        onTrackShipment = onNavigateToTrackShipment,
                        onARMeasurement = onNavigateToARMeasurement,
                        onBlockchain = onNavigateToBlockchain
                    )
                }

                item {
                    AIInsightsSection()
                }

                item {
                    ARFeaturesSection()
                }

                item {
                    BlockchainSection()
                }

                item {
                    PerformanceMetricsSection()
                }

                item {
                    RecentShipmentsSection()
                }
            }
        }
    }
}

@Composable
private fun AdvancedTopBar(
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
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
                spotColor = EdhamOrange.copy(alpha = glowAlpha),
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
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    EdhamOrange,
                                    EdhamOrange.copy(alpha = 0.8f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = "مرحباً بك",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "العميل الذكي",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2A2A2A))
                        .border(
                            width = 1.dp,
                            color = EdhamOrange.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = EdhamOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2A2A2A))
                        .border(
                            width = 1.dp,
                            color = ErrorRed.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = ErrorRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "لوحة التحكم الذكية",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        
        Text(
            text = "إدهام اللوجستيك - الجيل الرابع من الخدمات اللوجستية",
            color = EdhamOrange,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun QuickActionsSection(
    onCreateShipment: () -> Unit,
    onTrackShipment: () -> Unit,
    onARMeasurement: () -> Unit,
    onBlockchain: () -> Unit
) {
    val actions = listOf(
        QuickAction(
            icon = Icons.Default.Add,
            title = "إنشاء شحنة",
            subtitle = "ذكاء اصطناعي",
            color = EdhamOrange,
            onClick = onCreateShipment
        ),
        QuickAction(
            icon = Icons.Default.Search,
            title = "تتبع شحنة",
            subtitle = "مباشر",
            color = SuccessGreen,
            onClick = onTrackShipment
        ),
        QuickAction(
            icon = Icons.Default.Camera,
            title = "قياس AR",
            subtitle = "واقع معزز",
            color = IceBlue,
            onClick = onARMeasurement
        ),
        QuickAction(
            icon = Icons.Default.Link,
            title = "بلوك تشين",
            subtitle = "آمن",
            color = WarningYellow,
            onClick = onBlockchain
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "إجراءات سريعة",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(actions) { action ->
                QuickActionCard(action = action)
            }
        }
    }
}

@Composable
private fun QuickActionCard(action: QuickAction) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .width(140.dp)
            .height(120.dp)
            .clickable { action.onClick() }
            .shadow(
                elevation = 6.dp,
                spotColor = action.color.copy(alpha = pulseAlpha * 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                action.color,
                                action.color.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = action.title,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1
            )

            Text(
                text = action.subtitle,
                color = action.color,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AIInsightsSection() {
    val insights = listOf(
        AIInsight(
            title = "توقيت التوصيل",
            description = "شحنة #123456 ستصل خلال 45 دقيقة",
            confidence = 0.92,
            type = "DELIVERY"
        ),
        AIInsight(
            title = "تحسين التكلفة",
            description = "يمكن توفير 15% على الشحنة القادمة",
            confidence = 0.87,
            type = "COST"
        ),
        AIInsight(
            title = "تنبؤ الطلب",
            description = "زيادة الطلب المتوقع الأسبوع القادم",
            confidence = 0.78,
            type = "DEMAND"
        )
    )

    NeonCard(
        onClick = {},
        cardType = NeonCardType.GLOW,
        glowColor = EdhamOrange
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "رؤى الذكاء الاصطناعي",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                NeonBadge(
                    text = "AI",
                    badgeType = NeonBadgeType.INFO
                )
            }

            insights.forEach { insight ->
                InsightCard(insight = insight)
            }
        }
    }
}

@Composable
private fun InsightCard(insight: AIInsight) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F0F0F)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = insight.title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    text = "${(insight.confidence * 100).toInt()}% ثقة",
                    color = when {
                        insight.confidence >= 0.9 -> SuccessGreen
                        insight.confidence >= 0.7 -> WarningYellow
                        else -> ErrorRed
                    },
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Text(
                text = insight.description,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ARFeaturesSection() {
    NeonCard(
        onClick = {},
        cardType = NeonCardType.GLOW,
        glowColor = IceBlue
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ميزات الواقع المعزز",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                NeonBadge(
                    text = "AR",
                    badgeType = NeonBadgeType.INFO
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ARFeatureCard(
                    icon = Icons.Default.Straighten,
                    title = "قياس الأبعاد",
                    description = "قياس دقيق للطرد",
                    color = IceBlue
                )

                ARFeatureCard(
                    icon = Icons.Default.Navigation,
                    title = "ملاحة ذكية",
                    description = "توجيه AR متقدم",
                    color = EdhamOrange
                )
            }
        }
    }
}

@Composable
private fun ARFeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F0F0F)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1
            )

            Text(
                text = description,
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun BlockchainSection() {
    NeonCard(
        onClick = {},
        cardType = NeonCardType.GLOW,
        glowColor = WarningYellow
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "بلوك تشين آمن",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                NeonBadge(
                    text = "BLOCKCHAIN",
                    badgeType = NeonBadgeType.WARNING
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "المعاملات الموثقة",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "1,247 معاملة آمنة",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "NFTs",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "23 شحنة موثقة",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun PerformanceMetricsSection() {
    NeonCard(
        onClick = {},
        cardType = NeonCardType.DEFAULT,
        glowColor = SuccessGreen
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "مؤشرات الأداء",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    label = "سرعة التطبيق",
                    value = "94%",
                    color = SuccessGreen
                )

                MetricItem(
                    label = "استهلاك الذاكرة",
                    value = "42%",
                    color = EdhamOrange
                )

                MetricItem(
                    label = "دقة التنبؤ",
                    value = "89%",
                    color = IceBlue
                )
            }
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = color,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun RecentShipmentsSection() {
    val shipments = listOf(
        RecentShipment(
            trackingNumber = "EDH-2024-001",
            status = "في الطريق",
            progress = 0.75f,
            estimatedTime = "45 دقيقة"
        ),
        RecentShipment(
            trackingNumber = "EDH-2024-002",
            status = "تم التسليم",
            progress = 1.0f,
            estimatedTime = "تم"
        ),
        RecentShipment(
            trackingNumber = "EDH-2024-003",
            status = "قيد المعالجة",
            progress = 0.25f,
            estimatedTime = "2 ساعة"
        )
    )

    NeonCard(
        onClick = {},
        cardType = NeonCardType.DEFAULT,
        glowColor = EdhamOrange
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الشحنات الأخيرة",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    text = "عرض الكل",
                    color = EdhamOrange,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            shipments.forEach { shipment ->
                ShipmentProgressCard(shipment = shipment)
            }
        }
    }
}

@Composable
private fun ShipmentProgressCard(shipment: RecentShipment) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = shipment.trackingNumber,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    text = shipment.estimatedTime,
                    color = EdhamOrange,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Text(
                text = shipment.status,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )

            NeonProgressBar(
                progress = shipment.progress,
                progressColor = when {
                    shipment.progress >= 0.75f -> SuccessGreen
                    shipment.progress >= 0.5f -> WarningYellow
                    else -> EdhamOrange
                },
                showPercentage = false
            )
        }
    }
}

// ============================================
// Data Classes
// ============================================

data class QuickAction(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val color: Color,
    val onClick: () -> Unit
)

data class AIInsight(
    val title: String,
    val description: String,
    val confidence: Double,
    val type: String
)

data class RecentShipment(
    val trackingNumber: String,
    val status: String,
    val progress: Float,
    val estimatedTime: String
)
