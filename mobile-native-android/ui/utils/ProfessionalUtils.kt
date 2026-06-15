// ============================================
// 🚀 Edham Logistics - Professional Utils
// Premium Dark Theme with Advanced Utilities
// ============================================

package com.edham.logistics.ui.utils

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edham.logistics.ui.theme.*
import kotlinx.coroutines.delay

/**
 * ============================================
 * Professional UI Utils
 * ============================================
 * أدوات مساعدة احترافية للواجهة
 */

// ============================================
// Animation Extensions
// ============================================
fun Modifier.neonGlow(
    glowColor: Color = EdhamOrange,
    glowAlpha: Float = 0.3f
): Modifier = this.shadow(
    elevation = 8.dp,
    spotColor = glowColor.copy(alpha = glowAlpha),
    shape = RoundedCornerShape(16.dp)
)

fun Modifier.pulsingEffect(
    isActive: Boolean = true
): Modifier {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    return this.then(if (isActive) Modifier.scale(scale) else Modifier)
}

fun Modifier.shimmerEffect(
    isLoading: Boolean
): Modifier {
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    return this.then(
        if (isLoading) {
            Modifier.background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        EdhamOrange.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    startX = shimmerOffset - 300f,
                    endX = shimmerOffset + 300f
                )
            )
        } else {
            Modifier
        }
    )
}

// ============================================
// Professional Click Effects
// ============================================
@Composable
fun ProfessionalClickable(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150, easing = EaseInOutCubic)
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.5f,
        animationSpec = tween(150, easing = EaseInOutCubic)
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .alpha(alpha)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        content()
    }
}

// ============================================
// Professional Status Indicators
// ============================================
@Composable
fun ProfessionalStatusIndicator(
    status: String,
    statusType: ProfessionalStatusType,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    val (backgroundColor, textColor, icon) = when (statusType) {
        ProfessionalStatusType.SUCCESS -> 
            SuccessGreen.copy(alpha = 0.2f) to SuccessGreen to Icons.Default.CheckCircle
        ProfessionalStatusType.WARNING -> 
            WarningYellow.copy(alpha = 0.2f) to WarningYellow to Icons.Default.Warning
        ProfessionalStatusType.ERROR -> 
            ErrorRed.copy(alpha = 0.2f) to ErrorRed to Icons.Default.Error
        ProfessionalStatusType.INFO -> 
            IceBlue.copy(alpha = 0.2f) to IceBlue to Icons.Default.Info
        ProfessionalStatusType.LOADING -> 
            EdhamOrange.copy(alpha = 0.2f) to EdhamOrange to Icons.Default.Refresh
    }
    
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcon) {
            Icon(
                imageVector = icon,
                contentDescription = status,
                tint = textColor,
                modifier = Modifier
                    .size(16.dp)
                    .then(if (statusType == ProfessionalStatusType.LOADING) {
                        Modifier.rotation(rotation)
                    } else {
                        Modifier
                    })
            )
        }
        
        Text(
            text = status,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

// ============================================
// Professional Loading States
// ============================================
@Composable
fun ProfessionalLoadingState(
    message: String = "جاري التحميل...",
    showProgress: Boolean = false,
    progress: Float = 0f
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        Color(0xFF0A0A0A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Loading Animation
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                EdhamOrange.copy(alpha = pulseAlpha),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = EdhamOrange,
                    strokeWidth = 4.dp
                )
            }
            
            // Loading Message
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            
            // Progress Bar (if shown)
            if (showProgress) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = EdhamOrange,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .width(200.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = EdhamOrange,
                        trackColor = Color(0xFF2A2A2A)
                    )
                }
            }
        }
    }
}

// ============================================
// Professional Empty States
// ============================================
@Composable
fun ProfessionalEmptyState(
    title: String,
    message: String,
    icon: ImageVector,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Icon with animation
            val infiniteTransition = rememberInfiniteTransition()
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.9f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = EaseInOutCubic),
                    repeatMode = RepeatMode.Reverse
                )
            )
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EdhamOrange.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale)
            )
            
            // Title and Message
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Text(
                text = message,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            // Action Button
            if (actionText != null && onAction != null) {
                ProfessionalButton(
                    onClick = onAction,
                    text = actionText,
                    buttonColor = EdhamOrange,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

// ============================================
// Professional Error States
// ============================================
@Composable
fun ProfessionalErrorState(
    title: String = "حدث خطأ",
    message: String,
    onRetry: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val shakeOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse,
            iterations = 3
        )
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Error Icon
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = ErrorRed,
                modifier = Modifier
                    .size(80.dp)
                    .offset(androidx.compose.ui.unit.dp.dp * shakeOffset)
            )
            
            // Error Message
            Text(
                text = title,
                color = ErrorRed,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Text(
                text = message,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (onRetry != null) {
                    ProfessionalButton(
                        onClick = onRetry,
                        text = "إعادة المحاولة",
                        buttonColor = WarningYellow
                    )
                }
                
                if (onDismiss != null) {
                    ProfessionalButton(
                        onClick = onDismiss,
                        text = "إغلاق",
                        buttonColor = Color(0xFF3A3A3A)
                    )
                }
            }
        }
    }
}

// ============================================
// Professional Success States
// ============================================
@Composable
fun ProfessionalSuccessState(
    title: String,
    message: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Success Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                SuccessGreen.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier
                        .size(48.dp)
                        .rotation(rotation)
                )
            }
            
            // Success Message
            Text(
                text = title,
                color = SuccessGreen,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Text(
                text = message,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            // Action Button
            if (actionText != null && onAction != null) {
                ProfessionalButton(
                    onClick = onAction,
                    text = actionText,
                    buttonColor = SuccessGreen,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

// ============================================
// Professional Button Component
// ============================================
@Composable
fun ProfessionalButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    buttonColor: Color = EdhamOrange,
    textColor: Color = Color.White,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    fullWidth: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150, easing = EaseInOutCubic)
    )
    
    val buttonModifier = if (fullWidth) {
        modifier.fillMaxWidth()
    } else {
        modifier
    }

    Box(
        modifier = buttonModifier
            .scale(scale)
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        buttonColor,
                        buttonColor.copy(alpha = 0.8f)
                    )
                )
                .copy(
                    alpha = if (enabled) 1f else 0.5f
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = textColor,
                strokeWidth = 3.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Text(
                    text = text,
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

// ============================================
// Enums
// ============================================
enum class ProfessionalStatusType {
    SUCCESS, WARNING, ERROR, INFO, LOADING
}
