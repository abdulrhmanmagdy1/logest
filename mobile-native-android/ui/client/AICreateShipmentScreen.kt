// ============================================
// 🚀 Edham Logistics - AI Create Shipment Screen
// Premium Dark Theme with AI Integration
// ============================================

package com.edham.logistics.ui.client

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edham.logistics.ai.AIPredictionService
import com.edham.logistics.ui.components.*
import com.edham.logistics.ui.theme.*

/**
 * ============================================
 * AI Create Shipment Screen
 * ============================================
 * شاشة إنشاء شحنة ذكية بالذكاء الاصطناعي
 */
@Composable
fun AICreateShipmentScreen(
    onBack: () -> Unit,
    onShipmentCreated: () -> Unit
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

    var senderName by remember { mutableStateOf("") }
    var senderPhone by remember { mutableStateOf("") }
    var receiverName by remember { mutableStateOf("") }
    var receiverPhone by remember { mutableStateOf("") }
    var pickupAddress by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var packageType by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var requiredTemperature by remember { mutableStateOf("") }
    var specialInstructions by remember { mutableStateOf("") }

    var isAIAnalyzing by remember { mutableStateOf(false) }
    var aiSuggestions by remember { mutableStateOf<List<AISuggestion>>(emptyList()) }
    var estimatedCost by remember { mutableStateOf(0.0) }
    var estimatedTime by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

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
                            x = 0f,
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
                AICreateShipmentTopBar(onBack = onBack)
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    AIAnalysisSection(
                        isAnalyzing = isAIAnalyzing,
                        suggestions = aiSuggestions,
                        estimatedCost = estimatedCost,
                        estimatedTime = estimatedTime,
                        onAnalyze = {
                            isAIAnalyzing = true
                            // Simulate AI analysis
                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(2000)
                                isAIAnalyzing = false
                                aiSuggestions = listOf(
                                    AISuggestion(
                                        type = "ROUTE_OPTIMIZATION",
                                        title = "تحسين المسار",
                                        description = "يمكن توفير 15% من الوقت عبر مسار بديل",
                                        savings = "45 دقيقة"
                                    ),
                                    AISuggestion(
                                        type = "COST_REDUCTION",
                                        title = "تخفيض التكلفة",
                                        description = "اختيار خدمة اقتصادية مناسبة",
                                        savings = "25 ريال"
                                    ),
                                    AISuggestion(
                                        type = "TIMING",
                                        title = "التوقيت المثالي",
                                        description = "التسليم في الصباح يقلل من التكلفة",
                                        savings = "10 ريال"
                                    )
                                )
                                estimatedCost = 85.0
                                estimatedTime = "2-3 ساعات"
                            }
                        }
                    )
                }

                item {
                    ShipmentFormSection(
                        senderName = senderName,
                        onSenderNameChange = { senderName = it },
                        senderPhone = senderPhone,
                        onSenderPhoneChange = { senderPhone = it },
                        receiverName = receiverName,
                        onReceiverNameChange = { receiverName = it },
                        receiverPhone = receiverPhone,
                        onReceiverPhoneChange = { receiverPhone = it },
                        pickupAddress = pickupAddress,
                        onPickupAddressChange = { pickupAddress = it },
                        deliveryAddress = deliveryAddress,
                        onDeliveryAddressChange = { deliveryAddress = it },
                        packageType = packageType,
                        onPackageTypeChange = { packageType = it },
                        weight = weight,
                        onWeightChange = { weight = it },
                        requiredTemperature = requiredTemperature,
                        onRequiredTemperatureChange = { requiredTemperature = it },
                        specialInstructions = specialInstructions,
                        onSpecialInstructionsChange = { specialInstructions = it }
                    )
                }

                item {
                    CostEstimationSection(
                        estimatedCost = estimatedCost,
                        estimatedTime = estimatedTime,
                        aiSuggestions = aiSuggestions
                    )
                }

                item {
                    ActionButtonsSection(
                        isLoading = isLoading,
                        onCreateShipment = {
                            isLoading = true
                            // Simulate shipment creation
                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(3000)
                                isLoading = false
                                onShipmentCreated()
                            }
                        },
                        onSaveDraft = {
                            // Save draft logic
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AICreateShipmentTopBar(
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
                IconButton(
                    onClick = onBack,
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
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = EdhamOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "إنشاء شحنة ذكية",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "AI Powered Creation",
                        color = EdhamOrange,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            NeonBadge(
                text = "AI",
                badgeType = NeonBadgeType.INFO
            )
        }
    }
}

@Composable
private fun AIAnalysisSection(
    isAnalyzing: Boolean,
    suggestions: List<AISuggestion>,
    estimatedCost: Double,
    estimatedTime: String,
    onAnalyze: () -> Unit
) {
    NeonCard(
        onClick = {},
        cardType = NeonCardType.GLOW,
        glowColor = EdhamOrange
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تحليل الذكاء الاصطناعي",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                NeonButton(
                    onClick = onAnalyze,
                    text = "تحليل",
                    icon = Icons.Default.SmartToy,
                    buttonColor = EdhamOrange,
                    isLoading = isAnalyzing
                )
            }

            if (isAnalyzing) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = EdhamOrange,
                        strokeWidth = 3.dp
                    )

                    Text(
                        text = "جاري تحليل البيانات...",
                        color = EdhamOrange,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )

                    LinearProgressIndicator(
                        modifier = Modifier
                            .width(200.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = EdhamOrange,
                        trackColor = Color(0xFF2A2A2A)
                    )
                }
            } else if (suggestions.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    suggestions.forEach { suggestion ->
                        AISuggestionCard(suggestion = suggestion)
                    }

                    // Cost and Time Summary
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF0F0F0F)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "التكلفة المقدرة",
                                    color = Color.White.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "${estimatedCost.toInt()} ريال",
                                    color = SuccessGreen,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "الوقت المتوقع",
                                    color = Color.White.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = estimatedTime,
                                    color = IceBlue,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
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
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI",
                            tint = EdhamOrange.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            text = "اضغط على تحليل للحصول على توصيات ذكية",
                            color = Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AISuggestionCard(suggestion: AISuggestion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F0F0F)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = suggestion.title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    text = suggestion.description,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "توفير",
                    color = SuccessGreen,
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = suggestion.savings,
                    color = SuccessGreen,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun ShipmentFormSection(
    senderName: String,
    onSenderNameChange: (String) -> Unit,
    senderPhone: String,
    onSenderPhoneChange: (String) -> Unit,
    receiverName: String,
    onReceiverNameChange: (String) -> Unit,
    receiverPhone: String,
    onReceiverPhoneChange: (String) -> Unit,
    pickupAddress: String,
    onPickupAddressChange: (String) -> Unit,
    deliveryAddress: String,
    onDeliveryAddressChange: (String) -> Unit,
    packageType: String,
    onPackageTypeChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit,
    requiredTemperature: String,
    onRequiredTemperatureChange: (String) -> Unit,
    specialInstructions: String,
    onSpecialInstructionsChange: (String) -> Unit
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
                text = "معلومات الشحنة",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            // Sender Information
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "معلومات المرسل",
                    color = EdhamOrange,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                NeonTextField(
                    value = senderName,
                    onValueChange = onSenderNameChange,
                    placeholder = "اسم المرسل",
                    leadingIcon = Icons.Default.Person,
                    fieldType = NeonFieldType.DEFAULT
                )

                NeonTextField(
                    value = senderPhone,
                    onValueChange = onSenderPhoneChange,
                    placeholder = "رقم الهاتف",
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    fieldType = NeonFieldType.DEFAULT
                )
            }

            // Receiver Information
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "معلومات المستلم",
                    color = EdhamOrange,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                NeonTextField(
                    value = receiverName,
                    onValueChange = onReceiverNameChange,
                    placeholder = "اسم المستلم",
                    leadingIcon = Icons.Default.Person,
                    fieldType = NeonFieldType.DEFAULT
                )

                NeonTextField(
                    value = receiverPhone,
                    onValueChange = onReceiverPhoneChange,
                    placeholder = "رقم الهاتف",
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    fieldType = NeonFieldType.DEFAULT
                )
            }

            // Addresses
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "العناوين",
                    color = EdhamOrange,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                NeonTextField(
                    value = pickupAddress,
                    onValueChange = onPickupAddressChange,
                    placeholder = "عنوان الاستلام",
                    leadingIcon = Icons.Default.LocationOn,
                    fieldType = NeonFieldType.DEFAULT
                )

                NeonTextField(
                    value = deliveryAddress,
                    onValueChange = onDeliveryAddressChange,
                    placeholder = "عنوان التوصيل",
                    leadingIcon = Icons.Default.LocationOn,
                    fieldType = NeonFieldType.DEFAULT
                )
            }

            // Package Information
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "معلومات الطرد",
                    color = EdhamOrange,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                NeonTextField(
                    value = packageType,
                    onValueChange = onPackageTypeChange,
                    placeholder = "نوع الطرد",
                    leadingIcon = Icons.Default.Inventory,
                    fieldType = NeonFieldType.DEFAULT
                )

                NeonTextField(
                    value = weight,
                    onValueChange = onWeightChange,
                    placeholder = "الوزن (كجم)",
                    leadingIcon = Icons.Default.Scale,
                    keyboardType = KeyboardType.Number,
                    fieldType = NeonFieldType.DEFAULT
                )

                NeonTextField(
                    value = requiredTemperature,
                    onValueChange = onRequiredTemperatureChange,
                    placeholder = "درجة الحرارة المطلوبة (°م)",
                    leadingIcon = Icons.Default.Thermostat,
                    keyboardType = KeyboardType.Number,
                    fieldType = NeonFieldType.DEFAULT
                )

                NeonTextField(
                    value = specialInstructions,
                    onValueChange = onSpecialInstructionsChange,
                    placeholder = "تعليمات خاصة",
                    leadingIcon = Icons.Default.Note,
                    fieldType = NeonFieldType.DEFAULT
                )
            }
        }
    }
}

@Composable
private fun CostEstimationSection(
    estimatedCost: Double,
    estimatedTime: String,
    aiSuggestions: List<AISuggestion>
) {
    if (estimatedCost > 0) {
        NeonCard(
            onClick = {},
            cardType = NeonCardType.GLOW,
            glowColor = SuccessGreen
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "تفاصيل التكلفة",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                // Cost Breakdown
                val breakdown = listOf(
                    CostItem("الخدمة الأساسية", estimatedCost * 0.6),
                    CostItem("التغليف", estimatedCost * 0.15),
                    CostItem("التأمين", estimatedCost * 0.1),
                    CostItem("الخدمة الإضافية", estimatedCost * 0.15)
                )

                breakdown.forEach { item ->
                    CostItemRow(item = item)
                }

                // Total with AI Savings
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "الإجمالي:",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            Text(
                                text = "${estimatedCost.toInt()} ريال",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        if (aiSuggestions.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "توفير AI:",
                                    color = SuccessGreen,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Text(
                                    text = "${(estimatedCost * 0.15).toInt()} ريال",
                                    color = SuccessGreen,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "الإجمالي مع التوفير:",
                                    color = SuccessGreen,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                Text(
                                    text = "${(estimatedCost * 0.85).toInt()} ريال",
                                    color = SuccessGreen,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CostItemRow(item: CostItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.name,
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = "${item.cost.toInt()} ريال",
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ActionButtonsSection(
    isLoading: Boolean,
    onCreateShipment: () -> Unit,
    onSaveDraft: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NeonButton(
            onClick = onCreateShipment,
            text = "إنشاء الشحنة",
            icon = Icons.Default.Send,
            buttonColor = EdhamOrange,
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        NeonButton(
            onClick = onSaveDraft,
            text = "حفظ كمسودة",
            icon = Icons.Default.Save,
            buttonColor = WarningYellow,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ============================================
// Data Classes
// ============================================

data class AISuggestion(
    val type: String,
    val title: String,
    val description: String,
    val savings: String
)

data class CostItem(
    val name: String,
    val cost: Double
)
