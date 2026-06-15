// ============================================
// 🚀 Edham Logistics - Blockchain Shipment Screen
// Premium Dark Theme with Blockchain Integration
// ============================================

package com.edham.logistics.ui.client

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edham.logistics.ai.BlockchainService
import com.edham.logistics.ui.components.*
import com.edham.logistics.ui.theme.*
import kotlinx.coroutines.launch

/**
 * ============================================
 * Blockchain Shipment Screen
 * ============================================
 * شاشة البلوك تشين للشحنات الموثقة
 */
@Composable
fun BlockchainShipmentScreen(
    onBack: () -> Unit,
    onShipmentMinted: (String) -> Unit
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

    var selectedShipment by remember { mutableStateOf<String?>(null) }
    var isMintingNFT by remember { mutableStateOf(false) }
    var blockchainStats by remember { mutableStateOf<BlockchainStats?>(null) }
    var ownedNFTs by remember { mutableStateOf<List<ShipmentNFT>>(emptyList()) }
    var transactionHistory by remember { mutableStateOf<List<BlockchainTransaction>>(emptyList()) }

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
                            WarningYellow.copy(alpha = glowAlpha * 0.1f),
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
                BlockchainTopBar(onBack = onBack)
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
                    BlockchainOverviewSection(
                        stats = blockchainStats,
                        onLoadStats = {
                            // Simulate loading blockchain stats
                            blockchainStats = BlockchainStats(
                                totalBlocks = 1247,
                                totalTransactions = 8923,
                                pendingTransactions = 3,
                                lastBlockTime = System.currentTimeMillis() - 12000,
                                difficulty = 4,
                                hashRate = 1547.8
                            )
                        }
                    )
                }

                item {
                    NFTMintingSection(
                        selectedShipment = selectedShipment,
                        isMinting = isMintingNFT,
                        onShipmentSelected = { selectedShipment = it },
                        onMintNFT = { shipmentId ->
                            isMintingNFT = true
                            // Simulate NFT minting
                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(3000)
                                isMintingNFT = false
                                val nftId = "NFT-${System.currentTimeMillis()}"
                                onShipmentMinted(nftId)
                                
                                // Add to owned NFTs
                                val newNFT = ShipmentNFT(
                                    id = nftId,
                                    shipmentId = shipmentId,
                                    trackingNumber = "EDH-2024-${(100..999).random()}",
                                    mintedAt = System.currentTimeMillis(),
                                    owner = "العميل الذكي",
                                    value = (100..1000).random().toDouble(),
                                    metadata = ShipmentMetadata(
                                        trackingNumber = "EDH-2024-${(100..999).random()}",
                                        origin = "الرياض",
                                        destination = "جدة",
                                        temperature = 4,
                                        weight = 2.5,
                                        specialInstructions = "معاملة خاصة"
                                    )
                                )
                                ownedNFTs = ownedNFTs + newNFT
                            }
                        }
                    )
                }

                item {
                    OwnedNFTsSection(
                        nfts = ownedNFTs,
                        onNFTClick = { /* Handle NFT click */ }
                    )
                }

                item {
                    TransactionHistorySection(
                        transactions = transactionHistory,
                        onLoadHistory = {
                            // Simulate loading transaction history
                            transactionHistory = listOf(
                                BlockchainTransaction(
                                    id = "TX-${System.currentTimeMillis()}",
                                    from = "SYSTEM",
                                    to = "العميل الذكي",
                                    data = mapOf(
                                        "shipmentId" to "EDH-2024-001",
                                        "action" to "MINT_NFT",
                                        "value" to "250.0"
                                    ),
                                    type = "DOCUMENT_VERIFICATION",
                                    timestamp = System.currentTimeMillis() - 3600000,
                                    status = "CONFIRMED",
                                    blockNumber = 1245
                                ),
                                BlockchainTransaction(
                                    id = "TX-${System.currentTimeMillis() - 1000}",
                                    from = "العميل الذكي",
                                    to = "SYSTEM",
                                    data = mapOf(
                                        "shipmentId" to "EDH-2024-002",
                                        "action" to "TRANSFER_NFT",
                                        "value" to "180.0"
                                    ),
                                    type = "DOCUMENT_VERIFICATION",
                                    timestamp = System.currentTimeMillis() - 7200000,
                                    status = "CONFIRMED",
                                    blockNumber = 1243
                                )
                            )
                        }
                    )
                }

                item {
                    BlockchainSecuritySection()
                }
            }
        }
    }
}

@Composable
private fun BlockchainTopBar(
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
                spotColor = WarningYellow.copy(alpha = glowAlpha),
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
                            color = WarningYellow.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = WarningYellow,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "بلوك تشين الشحنات",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Blockchain Verification",
                        color = WarningYellow,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            NeonBadge(
                text = "BLOCKCHAIN",
                badgeType = NeonBadgeType.WARNING
            )
        }
    }
}

@Composable
private fun BlockchainOverviewSection(
    stats: BlockchainStats?,
    onLoadStats: () -> Unit
) {
    NeonCard(
        onClick = onLoadStats,
        cardType = NeonCardType.GLOW,
        glowColor = WarningYellow
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "نظرة عامة على البلوك تشين",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            if (stats != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BlockchainMetricItem(
                        label = "إجمالي الكتل",
                        value = stats.totalBlocks.toString(),
                        icon = Icons.Default.Cube,
                        color = WarningYellow
                    )

                    BlockchainMetricItem(
                        label = "المعاملات",
                        value = stats.totalTransactions.toString(),
                        icon = Icons.Default.SwapHoriz,
                        color = EdhamOrange
                    )

                    BlockchainMetricItem(
                        label = "معدل الهاش",
                        value = "${stats.hashRate.toInt()} H/s",
                        icon = Icons.Default.Speed,
                        color = SuccessGreen
                    )
                }

                // Network Status
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "حالة الشبكة",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            NeonBadge(
                                text = "نشط",
                                badgeType = NeonBadgeType.SUCCESS
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "آخر كتلة:",
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                text = formatTime(stats.lastBlockTime),
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الصعوبة:",
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                text = stats.difficulty.toString(),
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall
                            )
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
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Blockchain",
                            tint = WarningYellow.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            text = "اضغط لتحميل إحصائيات البلوك تشين",
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
private fun BlockchainMetricItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            color,
                            color.copy(alpha = 0.7f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

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
private fun NFTMintingSection(
    selectedShipment: String?,
    isMinting: Boolean,
    onShipmentSelected: (String) -> Unit,
    onMintNFT: (String) -> Unit
) {
    NeonCard(
        onClick = {},
        cardType = NeonCardType.GLOW,
        glowColor = EdhamOrange
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "صك NFT للشحنة",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            // Shipment Selection
            if (selectedShipment == null) {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "اختر الشحنة لصك NFT",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        // Mock shipment list
                        listOf(
                            "EDH-2024-001",
                            "EDH-2024-002",
                            "EDH-2024-003"
                        ).forEach { shipmentId ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onShipmentSelected(shipmentId) },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF1A1A1A)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = shipmentId,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Select",
                                        tint = EdhamOrange,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Selected Shipment Info
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الشحنة المختارة:",
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodySmall
                            )

                            IconButton(
                                onClick = { onShipmentSelected("") },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = ErrorRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Text(
                            text = selectedShipment,
                            color = EdhamOrange,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        // Mint Button
                        NeonButton(
                            onClick = { onMintNFT(selectedShipment) },
                            text = "صك NFT",
                            icon = Icons.Default.Cube,
                            buttonColor = EdhamOrange,
                            isLoading = isMinting,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (isMinting) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = EdhamOrange,
                                    strokeWidth = 3.dp
                                )

                                Text(
                                    text = "جاري صك NFT...",
                                    color = EdhamOrange,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .width(200.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(1.5.dp)),
                                    color = EdhamOrange,
                                    trackColor = Color(0xFF2A2A2A)
                                )
                            }
                        }
                    }
                }
            }

            // NFT Benefits
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
                    Text(
                        text = "مزايا NFT:",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    listOf(
                        "• توثيق غير قابل للتلاعب",
                        "• ملكية رقمية فريدة",
                        "• إمكانية التداول والبيع",
                        "• تتبع شفاف للسلسلة",
                        "• قيمة استثمارية محتملة"
                    ).forEach { benefit ->
                        Text(
                            text = benefit,
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
private fun OwnedNFTsSection(
    nfts: List<ShipmentNFT>,
    onNFTClick: (ShipmentNFT) -> Unit
) {
    NeonCard(
        onClick = {},
        cardType = NeonCardType.DEFAULT,
        glowColor = SuccessGreen
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
                    text = "NFTs المملوكة",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                NeonBadge(
                    text = "${nfts.size}",
                    badgeType = NeonBadgeType.SUCCESS
                )
            }

            if (nfts.isEmpty()) {
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
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cube,
                            contentDescription = "No NFTs",
                            tint = SuccessGreen.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            text = "لا توجد NFTs مملوكة بعد",
                            color = Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                nfts.forEach { nft ->
                    NFTCard(nft = nft, onClick = { onNFTClick(nft) })
                }
            }
        }
    }
}

@Composable
private fun NFTCard(
    nft: ShipmentNFT,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = nft.id,
                        color = SuccessGreen,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Text(
                        text = nft.trackingNumber,
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${nft.value.toInt()} ريال",
                        color = EdhamOrange,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = formatTime(nft.mintedAt),
                        color = Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${nft.metadata.origin} → ${nft.metadata.destination}",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = nft.owner,
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun TransactionHistorySection(
    transactions: List<BlockchainTransaction>,
    onLoadHistory: () -> Unit
) {
    NeonCard(
        onClick = onLoadHistory,
        cardType = NeonCardType.DEFAULT,
        glowColor = IceBlue
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "سجل المعاملات",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            if (transactions.isEmpty()) {
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
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "No Transactions",
                            tint = IceBlue.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            text = "اضغط لتحميل سجل المعاملات",
                            color = Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                transactions.forEach { transaction ->
                    TransactionCard(transaction = transaction)
                }
            }
        }
    }
}

@Composable
private fun TransactionCard(
    transaction: BlockchainTransaction
) {
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.id,
                    color = IceBlue,
                    style = MaterialTheme.typography.bodySmall
                )

                NeonBadge(
                    text = transaction.status,
                    badgeType = when (transaction.status) {
                        "CONFIRMED" -> NeonBadgeType.SUCCESS
                        "PENDING" -> NeonBadgeType.WARNING
                        else -> NeonBadgeType.DEFAULT
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${transaction.from} → ${transaction.to}",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "كتلة #${transaction.blockNumber}",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Text(
                text = getTransactionTypeDescription(transaction.type),
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = formatTime(transaction.timestamp),
                color = Color.White.copy(alpha = 0.5f),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun BlockchainSecuritySection() {
    NeonCard(
        onClick = {},
        cardType = NeonCardType.MINIMAL,
        glowColor = ErrorRed
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
                    text = "الأمان والخصوصية",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                NeonBadge(
                    text = "SECURE",
                    badgeType = NeonBadgeType.SUCCESS
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(
                    SecurityFeature(
                        icon = Icons.Default.Security,
                        title = "تشفير متقدم",
                        description = "تشفير AES-256 لجميع المعاملات"
                    ),
                    SecurityFeature(
                        icon = Icons.Default.Verified,
                        title = "تحقق من الهوية",
                        description = "تواقيع رقمية فريدة لكل معاملة"
                    ),
                    SecurityFeature(
                        icon = Icons.Default.GppGood,
                        title = "مقاومة التلاعب",
                        description = "بنية لا مركزية غير قابلة للتغيير"
                    ),
                    SecurityFeature(
                        icon = Icons.Default.PrivacyTip,
                        title = "خصوصية البيانات",
                        description = "بيانات مشفرة ومجهولة المصدر"
                    )
                ).forEach { feature ->
                    SecurityFeatureCard(feature = feature)
                }
            }
        }
    }
}

@Composable
private fun SecurityFeatureCard(feature: SecurityFeature) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
                            SuccessGreen,
                            SuccessGreen.copy(alpha = 0.7f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.title,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = feature.title,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Text(
                text = feature.description,
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// ============================================
// Helper Functions
// ============================================

private fun formatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "الآن"
        diff < 3600_000 -> "${diff / 60_000} دقيقة"
        diff < 86_400_000 -> "${diff / 3_600_000} ساعة"
        diff < 604_800_000 -> "${diff / 86_400_000} يوم"
        else -> "${diff / 604_800_000} أسبوع"
    }
}

private fun getTransactionTypeDescription(type: String): String {
    return when (type) {
        "DOCUMENT_VERIFICATION" -> "توثيق مستند"
        "SHIPMENT_PAYMENT" -> "دفع شحنة"
        "INVOICE_SETTLEMENT" -> "تسوية فاتورة"
        else -> "معاملة بلوك تشين"
    }
}

// ============================================
// Data Classes
// ============================================

data class BlockchainStats(
    val totalBlocks: Int,
    val totalTransactions: Int,
    val pendingTransactions: Int,
    val lastBlockTime: Long,
    val difficulty: Int,
    val hashRate: Double
)

data class ShipmentNFT(
    val id: String,
    val shipmentId: String,
    val trackingNumber: String,
    val mintedAt: Long,
    val owner: String,
    val value: Double,
    val metadata: ShipmentMetadata
)

data class BlockchainTransaction(
    val id: String,
    val from: String,
    val to: String,
    val data: Map<String, Any>,
    val type: String,
    val timestamp: Long,
    val status: String,
    val blockNumber: Int
)

data class SecurityFeature(
    val icon: ImageVector,
    val title: String,
    val description: String
)

data class ShipmentMetadata(
    val trackingNumber: String,
    val origin: String,
    val destination: String,
    val temperature: Int,
    val weight: Double,
    val specialInstructions: String
)
