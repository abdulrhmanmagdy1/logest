// ============================================
// 🚀 Edham Logistics - Neon Design System
// Premium Dark Theme with Edham Orange Identity
// ============================================

package com.edham.logistics.ui.design

import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edham.logistics.ui.theme.*

/**
 * ============================================
 * Neon Design System - نظام التصميم النيوني الموحد
 * ============================================
 * جميع المكونات النيونية في مكان واحد لضمان الاتساق
 */

// ============================================
// Neon Button - الزر النيوني الموحد
// ============================================
@Composable
fun NeonButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    buttonColor: Color = EdhamOrange,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    buttonType: NeonButtonType = NeonButtonType.PRIMARY
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

    val buttonModifier = when (buttonType) {
        NeonButtonType.PRIMARY -> modifier
            .shadow(
                elevation = 8.dp,
                spotColor = buttonColor.copy(alpha = glowAlpha),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        buttonColor,
                        buttonColor.copy(alpha = 0.8f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = buttonColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
        NeonButtonType.SECONDARY -> modifier
            .background(
                color = Color(0xFF0F0F0F),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = buttonColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
        NeonButtonType.OUTLINE -> modifier
            .border(
                width = 2.dp,
                color = buttonColor,
                shape = RoundedCornerShape(12.dp)
            )
    }

    Button(
        onClick = onClick,
        modifier = buttonModifier
            .height(56.dp)
            .fillMaxWidth(),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = if (buttonType == NeonButtonType.OUTLINE) buttonColor else Color.White,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}

// ============================================
// Neon TextField - حقل الإدخال النيوني الموحد
// ============================================
@Composable
fun NeonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    fieldType: NeonFieldType = NeonFieldType.DEFAULT
) {
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    val fieldModifier = when (fieldType) {
        NeonFieldType.DEFAULT -> modifier
            .shadow(
                elevation = 4.dp,
                spotColor = if (isError) ErrorRed.copy(alpha = glowAlpha) else EdhamOrange.copy(alpha = glowAlpha),
                shape = RoundedCornerShape(12.dp)
            )
        NeonFieldType.GLOW -> modifier
            .shadow(
                elevation = 8.dp,
                spotColor = EdhamOrange.copy(alpha = glowAlpha * 2),
                shape = RoundedCornerShape(12.dp)
            )
    }

    Column(modifier = fieldModifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            leadingIcon = leadingIcon?.let { 
                { Icon(it, contentDescription = null, tint = EdhamOrange) }
            },
            trailingIcon = trailingIcon?.let { 
                {
                    IconButton(onClick = { onTrailingIconClick?.invoke() }) {
                        Icon(
                            it, 
                            contentDescription = null, 
                            tint = if (isError) ErrorRed else Color.Gray
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) ErrorRed else EdhamOrange,
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                containerColor = Color(0xFF0F0F0F),
                cursorColor = EdhamOrange
            )
        )
        
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

// ============================================
// Neon Card - البطاقة النيونية الموحدة
// ============================================
@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    cardType: NeonCardType = NeonCardType.DEFAULT,
    glowColor: Color = EdhamOrange,
    content: @Composable ColumnScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    val cardModifier = when (cardType) {
        NeonCardType.DEFAULT -> modifier
            .shadow(
                elevation = 6.dp,
                spotColor = glowColor.copy(alpha = glowAlpha),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = Color(0xFF1A1A1A),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = glowColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
        NeonCardType.GLOW -> modifier
            .shadow(
                elevation = 12.dp,
                spotColor = glowColor.copy(alpha = glowAlpha * 2),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A),
                        Color(0xFF0F0F0F)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = glowColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
        NeonCardType.MINIMAL -> modifier
            .background(
                color = Color(0xFF1A1A1A),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
    }

    Card(
        modifier = if (onClick != null) {
            cardModifier
                .fillMaxWidth()
                .clickable { onClick() }
        } else {
            cardModifier.fillMaxWidth()
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
    }
}

// ============================================
// Neon Badge - الشارة النيونية الموحدة
// ============================================
@Composable
fun NeonBadge(
    text: String,
    badgeType: NeonBadgeType = NeonBadgeType.DEFAULT,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, borderColor) = when (badgeType) {
        NeonBadgeType.DEFAULT -> Triple(
            EdhamOrange.copy(alpha = 0.2f),
            EdhamOrange,
            EdhamOrange.copy(alpha = 0.5f)
        )
        NeonBadgeType.SUCCESS -> Triple(
            SuccessGreen.copy(alpha = 0.2f),
            SuccessGreen,
            SuccessGreen.copy(alpha = 0.5f)
        )
        NeonBadgeType.WARNING -> Triple(
            WarningYellow.copy(alpha = 0.2f),
            WarningYellow,
            WarningYellow.copy(alpha = 0.5f)
        )
        NeonBadgeType.ERROR -> Triple(
            ErrorRed.copy(alpha = 0.2f),
            ErrorRed,
            ErrorRed.copy(alpha = 0.5f)
        )
        NeonBadgeType.INFO -> Triple(
            IceBlue.copy(alpha = 0.2f),
            IceBlue,
            IceBlue.copy(alpha = 0.5f)
        )
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

// ============================================
// Neon Progress Bar - شريط التقدم النيوني
// ============================================
@Composable
fun NeonProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    progressColor: Color = EdhamOrange,
    backgroundColor: Color = Color(0xFF0F0F0F),
    showPercentage: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (showPercentage) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = progressColor,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                progressColor,
                                progressColor.copy(alpha = 0.7f)
                            )
                        ),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

// ============================================
// Enums for Design System
// ============================================
enum class NeonButtonType {
    PRIMARY, SECONDARY, OUTLINE
}

enum class NeonFieldType {
    DEFAULT, GLOW
}

enum class NeonCardType {
    DEFAULT, GLOW, MINIMAL
}

enum class NeonBadgeType {
    DEFAULT, SUCCESS, WARNING, ERROR, INFO
}
