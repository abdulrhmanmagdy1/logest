// ============================================
// 🚀 Edham Logistics - Shimmer Loading Component
// Premium Dark Theme with Edham Orange Identity
// ============================================

package com.edham.logistics.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.edham.logistics.ui.theme.*

/**
 * ============================================
 * Shimmer Loading Component - مكون التحميل المتوهج
 * ============================================
 * تأثير Shimmer احترافي بدلاً من الـ ProgressBar العادي
 */

@Composable
fun ShimmerLoadingCard(
    modifier: Modifier = Modifier,
    cardHeight: Int = 120,
    showAvatar: Boolean = true,
    showTitle: Boolean = true,
    showSubtitle: Boolean = true,
    showFooter: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1A1A1A),
            Color(0xFF2A2A2A),
            Color(0xFF1A1A1A)
        ),
        start = Offset(shimmerOffset, shimmerOffset),
        end = Offset(shimmerOffset + 1000f, shimmerOffset + 1000f)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight.dp)
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Avatar section
            if (showAvatar) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(shimmerBrush)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(shimmerBrush)
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(shimmerBrush)
                    )
                }
            }

            // Title section
            if (showTitle) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(shimmerBrush)
                )
            }

            // Subtitle section
            if (showSubtitle) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(shimmerBrush)
                )
            }

            // Footer section
            if (showFooter) {
                Spacer(modifier = Modifier.weight(1f))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(shimmerBrush)
                    )
                    
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(shimmerBrush)
                    )
                }
            }
        }
    }
}

@Composable
fun ShimmerListItem(
    modifier: Modifier = Modifier,
    itemHeight: Int = 80,
    showLeadingIcon: Boolean = true,
    showTrailingIcon: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0F0F0F),
            Color(0xFF1F1F1F),
            Color(0xFF0F0F0F)
        ),
        start = Offset(shimmerOffset, shimmerOffset),
        end = Offset(shimmerOffset + 1000f, shimmerOffset + 1000f)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showLeadingIcon) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(shimmerBrush)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(shimmerBrush)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(shimmerBrush)
            )
        }

        if (showTrailingIcon) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(shimmerBrush)
            )
        }
    }
}

@Composable
fun ShimmerGridItem(
    modifier: Modifier = Modifier,
    itemWidth: Int = 160,
    itemHeight: Int = 120
) {
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -500f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1A1A1A),
            Color(0xFF2A2A2A),
            Color(0xFF1A1A1A)
        ),
        start = Offset(shimmerOffset, shimmerOffset),
        end = Offset(shimmerOffset + 500f, shimmerOffset + 500f)
    )

    Card(
        modifier = modifier
            .width(itemWidth.dp)
            .height(itemHeight.dp)
            .clip(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(shimmerBrush)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(shimmerBrush)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(shimmerBrush)
            )
        }
    }
}

@Composable
fun ShimmerLoadingList(
    modifier: Modifier = Modifier,
    itemCount: Int = 5,
    itemHeight: Int = 80
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(itemCount) { index ->
            ShimmerListItem(
                itemHeight = itemHeight,
                showLeadingIcon = index % 2 == 0,
                showTrailingIcon = true
            )
        }
    }
}

@Composable
fun ShimmerLoadingGrid(
    modifier: Modifier = Modifier,
    itemCount: Int = 6,
    itemWidth: Int = 160,
    itemHeight: Int = 120
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemCount) { index ->
            ShimmerGridItem(
                itemWidth = itemWidth,
                itemHeight = itemHeight
            )
        }
    }
}

@Composable
fun ShimmerLoadingScreen(
    modifier: Modifier = Modifier,
    showHeader: Boolean = true,
    showCards: Boolean = true,
    showList: Boolean = true,
    cardCount: Int = 3,
    listCount: Int = 5
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header shimmer
        if (showHeader) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0F0F0F),
                                    Color(0xFF1F1F1F),
                                    Color(0xFF0F0F0F)
                                )
                            )
                        )
                )
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0F0F0F),
                                    Color(0xFF1F1F1F),
                                    Color(0xFF0F0F0F)
                                )
                            )
                        )
                )
            }
        }

        // Cards shimmer
        if (showCards) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(cardCount) { index ->
                    ShimmerLoadingCard(
                        cardHeight = if (index == 0) 100 else 120,
                        showAvatar = index == 0,
                        showFooter = index > 0
                    )
                }
            }
        }

        // List shimmer
        if (showList) {
            ShimmerLoadingList(itemCount = listCount)
        }
    }
}
