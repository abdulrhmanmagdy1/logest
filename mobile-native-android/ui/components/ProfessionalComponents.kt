// ============================================
// 🚀 Edham Logistics - Professional Components
// Premium Dark Theme with Advanced UI Elements
// ============================================

package com.edham.logistics.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edham.logistics.ui.theme.*

/**
 * ============================================
 * Professional Card Components
 * ============================================
 */

@Composable
fun ProfessionalCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: Int = 4,
    glowColor: Color = EdhamOrange,
    content: @Composable ColumnScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    val cardModifier = if (onClick != null) {
        modifier
            .clickable { onClick() }
            .shadow(
                elevation = elevation.dp,
                spotColor = glowColor.copy(alpha = glowAlpha),
                shape = RoundedCornerShape(16.dp)
            )
    } else {
        modifier.shadow(
            elevation = elevation.dp,
            spotColor = glowColor.copy(alpha = glowAlpha),
            shape = RoundedCornerShape(16.dp)
        )
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Box(
        modifier = cardModifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = EdhamOrange.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

/**
 * ============================================
 * Professional Button Components
 * ============================================
 */

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

@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    backgroundColor: Color = EdhamOrange,
    contentColor: Color = Color.White
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = modifier
            .size(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.8f)
                    )
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * ============================================
 * Professional Input Components
 * ============================================
 */

@Composable
fun ProfessionalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> ErrorRed
            isFocused -> EdhamOrange
            else -> Color(0xFF3A3A3A)
        },
        animationSpec = tween(300, easing = EaseInOutCubic)
    )
    
    val containerColor by animateColorAsState(
        targetValue = when {
            isFocused -> Color(0xFF252525)
            else -> Color(0xFF1A1A1A)
        },
        animationSpec = tween(300, easing = EaseInOutCubic)
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = containerColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                leadingIcon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isError) ErrorRed else EdhamOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = imeAction
                    ),
                    enabled = enabled,
                    singleLine = singleLine,
                    cursorBrush = SolidColor(EdhamOrange)
                )
                
                trailingIcon?.let { icon ->
                    IconButton(
                        onClick = { onTrailingIconClick?.invoke() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isError) ErrorRed else EdhamOrange.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                color = Color.White.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * ============================================
 * Professional Status Components
 * ============================================
 */

@Composable
fun StatusBadge(
    status: String,
    statusType: StatusType,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (statusType) {
        StatusType.SUCCESS -> SuccessGreen to Color.White
        StatusType.WARNING -> WarningYellow to Color.Black
        StatusType.ERROR -> ErrorRed to Color.White
        StatusType.INFO -> IceBlue to Color.White
        StatusType.NEUTRAL -> Color(0xFF3A3A3A) to Color.White
    }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    progressColor: Color = EdhamOrange,
    backgroundColor: Color = Color(0xFF2A2A2A),
    showPercentage: Boolean = true,
    animated: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = if (animated) {
            tween(1000, easing = EaseInOutCubic)
        } else {
            tween(0)
        },
        label = "progress"
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (showPercentage) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                color = progressColor,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.align(Alignment.End)
            )
        }
        
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(backgroundColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                progressColor,
                                progressColor.copy(alpha = 0.8f)
                            )
                        )
                    )
            )
        }
    }
}

/**
 * ============================================
 * Professional List Components
 * ============================================
 */

@Composable
fun ProfessionalListItem(
    title: String,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    iconColor: Color = EdhamOrange
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFF2A2A2A) else Color.Transparent,
        animationSpec = tween(150, easing = EaseInOutCubic)
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onClick?.invoke() }
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            leadingIcon?.let { icon ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(iconColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                
                subtitle?.let {
                    Text(
                        text = it,
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            trailingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * ============================================
 * Professional Loading Components
 * ============================================
 */

@Composable
fun ProfessionalLoadingScreen(
    message: String = "جاري التحميل..."
) {
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
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = EdhamOrange,
                strokeWidth = 4.dp
            )
            
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            
            ProfessionalProgressBar(
                progress = 0.7f,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

@Composable
fun ShimmerListItem(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A1A))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
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
        )
    }
}

/**
 * ============================================
 * Professional Error Components
 * ============================================
 */

@Composable
fun ProfessionalErrorDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Text(
                text = message,
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            ProfessionalButton(
                onClick = onDismiss,
                text = "موافق",
                buttonColor = EdhamOrange
            )
        },
        dismissButton = onRetry?.let { retry ->
            {
                ProfessionalButton(
                    onClick = retry,
                    text = "إعادة المحاولة",
                    buttonColor = WarningYellow
                )
            }
        },
        containerColor = Color(0xFF1A1A1A),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

@Composable
fun ProfessionalEmptyState(
    title: String,
    message: String,
    icon: ImageVector,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EdhamOrange.copy(alpha = 0.6f),
                modifier = Modifier.size(64.dp)
            )
            
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = message,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            
            if (actionText != null && onAction != null) {
                ProfessionalButton(
                    onClick = onAction,
                    text = actionText,
                    buttonColor = EdhamOrange
                )
            }
        }
    }
}

/**
 * ============================================
 * Professional Stats Components
 * ============================================
 */

@Composable
fun StatsCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color = EdhamOrange,
    change: String? = null,
    changeType: ChangeType? = null,
    modifier: Modifier = Modifier
) {
    ProfessionalCard(
        modifier = modifier,
        glowColor = color
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
                
                Text(
                    text = value,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                if (change != null && changeType != null) {
                    val changeColor = when (changeType) {
                        ChangeType.INCREASE -> SuccessGreen
                        ChangeType.DECREASE -> ErrorRed
                        ChangeType.NEUTRAL -> Color.White.copy(alpha = 0.7f)
                    }
                    
                    Text(
                        text = change,
                        color = changeColor,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                color,
                                color.copy(alpha = 0.8f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * ============================================
 * Enums and Data Classes
 * ============================================
 */

enum class StatusType {
    SUCCESS, WARNING, ERROR, INFO, NEUTRAL
}

enum class ChangeType {
    INCREASE, DECREASE, NEUTRAL
}
