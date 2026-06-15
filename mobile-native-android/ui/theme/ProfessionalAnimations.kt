// ============================================
// 🚀 Edham Logistics - Professional Animations
// Premium Dark Theme with Advanced Transitions
// ============================================

package com.edham.logistics.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp

/**
 * ============================================
 * Professional Animation Definitions
 * ============================================
 * تأثيرات حركية احترافية للتطبيق
 */

// ============================================
// Animation Durations
// ============================================
object AnimationDurations {
    const val EXTRA_FAST = 150
    const val FAST = 250
    const val MEDIUM = 400
    const val SLOW = 600
    const val EXTRA_SLOW = 800
}

// ============================================
// Animation Specs
// ============================================
object AnimationSpecs {
    val FAST_EASE = tween<Int>(AnimationDurations.FAST, easing = EaseInOutCubic)
    val MEDIUM_EASE = tween<Int>(AnimationDurations.MEDIUM, easing = EaseInOutCubic)
    val SLOW_EASE = tween<Int>(AnimationDurations.SLOW, easing = EaseInOutCubic)
    
    val FAST_FLOAT = tween<Float>(AnimationDurations.FAST, easing = EaseInOutCubic)
    val MEDIUM_FLOAT = tween<Float>(AnimationDurations.MEDIUM, easing = EaseInOutCubic)
    val SLOW_FLOAT = tween<Float>(AnimationDurations.SLOW, easing = EaseInOutCubic)
    
    val SPRING = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy)
    val BOUNCY = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

// ============================================
// Slide Animations
// ============================================
object SlideAnimations {
    fun slideInFromRight(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = AnimationSpecs.MEDIUM_EASE
        ) + fadeIn(animationSpec = AnimationSpecs.MEDIUM_EASE)
    }
    
    fun slideInFromLeft(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = AnimationSpecs.MEDIUM_EASE
        ) + fadeIn(animationSpec = AnimationSpecs.MEDIUM_EASE)
    }
    
    fun slideInFromTop(): EnterTransition {
        return slideInVertically(
            initialOffsetY = { -it },
            animationSpec = AnimationSpecs.MEDIUM_EASE
        ) + fadeIn(animationSpec = AnimationSpecs.MEDIUM_EASE)
    }
    
    fun slideInFromBottom(): EnterTransition {
        return slideInVertically(
            initialOffsetY = { it },
            animationSpec = AnimationSpecs.MEDIUM_EASE
        ) + fadeIn(animationSpec = AnimationSpecs.MEDIUM_EASE)
    }
    
    fun slideOutToRight(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = AnimationSpecs.MEDIUM_EASE
        ) + fadeOut(animationSpec = AnimationSpecs.MEDIUM_EASE)
    }
    
    fun slideOutToLeft(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = AnimationSpecs.MEDIUM_EASE
        ) + fadeOut(animationSpec = AnimationSpecs.MEDIUM_EASE)
    }
    
    fun slideOutToTop(): ExitTransition {
        return slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = AnimationSpecs.MEDIUM_EASE
        ) + fadeOut(animationSpec = AnimationSpecs.MEDIUM_EASE)
    }
    
    fun slideOutToBottom(): ExitTransition {
        return slideOutVertically(
            targetOffsetY = { it },
            animationSpec = AnimationSpecs.MEDIUM_EASE
        ) + fadeOut(animationSpec = AnimationSpecs.MEDIUM_EASE)
    }
}

// ============================================
// Scale Animations
// ============================================
object ScaleAnimations {
    fun scaleIn(): EnterTransition {
        return scaleIn(
            initialScale = 0.8f,
            animationSpec = AnimationSpecs.BOUNCY
        ) + fadeIn(animationSpec = AnimationSpecs.MEDIUM_EASE)
    }
    
    fun scaleOut(): ExitTransition {
        return scaleOut(
            targetScale = 0.8f,
            animationSpec = AnimationSpecs.MEDIUM_EASE
        ) + fadeOut(animationSpec = AnimationSpecs.MEDIUM_EASE)
    }
    
    fun elasticScaleIn(): EnterTransition {
        return scaleIn(
            initialScale = 0.5f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(animationSpec = AnimationSpecs.SLOW_EASE)
    }
    
    fun elasticScaleOut(): ExitTransition {
        return scaleOut(
            targetScale = 1.2f,
            animationSpec = AnimationSpecs.MEDIUM_EASE
        ) + fadeOut(animationSpec = AnimationSpecs.FAST_EASE)
    }
}

// ============================================
// Fade Animations
// ============================================
object FadeAnimations {
    fun fadeIn(): EnterTransition {
        return fadeIn(
            animationSpec = tween(
                AnimationDurations.MEDIUM,
                easing = LinearEasing
            )
        )
    }
    
    fun fadeOut(): ExitTransition {
        return fadeOut(
            animationSpec = tween(
                AnimationDurations.MEDIUM,
                easing = LinearEasing
            )
        )
    }
    
    fun slowFadeIn(): EnterTransition {
        return fadeIn(
            animationSpec = tween(
                AnimationDurations.SLOW,
                easing = EaseInOutCubic
            )
        )
    }
    
    fun slowFadeOut(): ExitTransition {
        return fadeOut(
            animationSpec = tween(
                AnimationDurations.SLOW,
                easing = EaseInOutCubic
            )
        )
    }
}

// ============================================
// Special Effects
// ============================================
object SpecialEffects {
    
    @Composable
    fun NeonPulseEffect(
        content: @Composable () -> Unit
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            )
        )
        
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.7f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            )
        )
        
        androidx.compose.animation.AnimatedVisibility(
            visible = true,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier
                    .scale(scale)
                    .alpha(alpha)
            ) {
                content()
            }
        }
    }
    
    @Composable
    fun ShimmerLoadingEffect(
        isLoading: Boolean,
        content: @Composable () -> Unit
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
        
        androidx.compose.animation.AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            androidx.compose.foundation.layout.Box {
                content()
                androidx.compose.foundation.layout.Box(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(
                                    androidx.compose.ui.graphics.Color.Transparent,
                                    EdhamOrange.copy(alpha = 0.3f),
                                    androidx.compose.ui.graphics.Color.Transparent
                                ),
                                startX = shimmerOffset - 300f,
                                endX = shimmerOffset + 300f
                            )
                        )
                )
            }
        }
    }
    
    @Composable
    fun LoadingDotsEffect() {
        val infiniteTransition = rememberInfiniteTransition()
        
        val dot1Scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            )
        )
        
        val dot2Scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse,
                initialOffsetMillis = 200
            )
        )
        
        val dot3Scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse,
                initialOffsetMillis = 400
            )
        )
        
        Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            listOf(dot1Scale, dot2Scale, dot3Scale).forEach { scale ->
                androidx.compose.foundation.layout.Box(
                    modifier = androidx.compose.ui.Modifier
                        .size(8.dp)
                        .scale(scale)
                        .background(
                            color = EdhamOrange,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            }
        }
    }
    
    @Composable
    fun SuccessCheckmarkAnimation(
        isVisible: Boolean
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        
        androidx.compose.animation.AnimatedVisibility(
            visible = isVisible,
            enter = ScaleAnimations.elasticScaleIn(),
            exit = ScaleAnimations.elasticScaleOut()
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = SuccessGreen,
                modifier = androidx.compose.ui.Modifier
                    .size(64.dp)
                    .rotation(rotation)
            )
        }
    }
    
    @Composable
    fun ErrorShakeAnimation(
        hasError: Boolean,
        content: @Composable () -> Unit
    ) {
        val shakeOffset by animateFloatAsState(
            targetValue = if (hasError) 10f else 0f,
            animationSpec = repeatable(
                iterations = 3,
                animation = tween(100, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            )
        )
        
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .offset(androidx.compose.ui.unit.dp.dp * shakeOffset)
        ) {
            content()
        }
    }
}

// ============================================
// Page Transitions
// ============================================
object PageTransitions {
    fun slideTransition(): AnimatedContentTransitionScope<*>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = AnimationSpecs.MEDIUM_EASE
        ) + fadeIn(animationSpec = AnimationSpecs.MEDIUM_EASE)
    }
    
    fun slideTransitionOut(): AnimatedContentTransitionScope<*>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = AnimationSpecs.MEDIUM_EASE
        ) + fadeOut(animationSpec = AnimationSpecs.MEDIUM_EASE)
    }
    
    fun scaleTransition(): AnimatedContentTransitionScope<*>.() -> EnterTransition = {
        scaleIn(
            initialScale = 0.8f,
            animationSpec = AnimationSpecs.BOUNCY
        ) + fadeIn(animationSpec = AnimationSpecs.MEDIUM_EASE)
    }
    
    fun scaleTransitionOut(): AnimatedContentTransitionScope<*>.() -> ExitTransition = {
        scaleOut(
            targetScale = 0.8f,
            animationSpec = AnimationSpecs.MEDIUM_EASE
        ) + fadeOut(animationSpec = AnimationSpecs.MEDIUM_EASE)
    }
}

// ============================================
// List Item Animations
// ============================================
object ListAnimations {
    @Composable
    fun AnimatedListItem(
        isVisible: Boolean,
        delay: Int = 0,
        content: @Composable () -> Unit
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(
                    AnimationDurations.MEDIUM,
                    delayMillis = delay,
                    easing = EaseInOutCubic
                )
            ) + fadeIn(
                animationSpec = tween(
                    AnimationDurations.MEDIUM,
                    delayMillis = delay,
                    easing = EaseInOutCubic
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = AnimationSpecs.MEDIUM_EASE
            ) + fadeOut(animationSpec = AnimationSpecs.MEDIUM_EASE)
        ) {
            content()
        }
    }
    
    @Composable
    fun StaggeredListAnimation(
        items: List<Any>,
        itemContent: @Composable (item: Any, index: Int) -> Unit
    ) {
        Column(
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            items.forEachIndexed { index, item ->
                AnimatedListItem(
                    isVisible = true,
                    delay = index * 100
                ) {
                    itemContent(item, index)
                }
            }
        }
    }
}

// ============================================
// Button Animations
// ============================================
object ButtonAnimations {
    @Composable
    fun AnimatedButton(
        onClick: () -> Unit,
        modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
        enabled: Boolean = true,
        content: @Composable () -> Unit
    ) {
        val scale by animateFloatAsState(
            targetValue = if (enabled) 1f else 0.8f,
            animationSpec = AnimationSpecs.FAST_FLOAT
        )
        
        val interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource()
        val isPressed by interactionSource.collectIsPressedAsState()
        
        val buttonScale by animateFloatAsState(
            targetValue = if (isPressed) 0.95f else scale,
            animationSpec = AnimationSpecs.FAST_FLOAT
        )
        
        androidx.compose.foundation.layout.Box(
            modifier = modifier
                .scale(buttonScale)
                .clickable(
                    interactionSource = interactionSource,
                    indication = androidx.compose.foundation.Indication.rememberRipple(bounded = true),
                    enabled = enabled,
                    onClick = onClick
                )
        ) {
            content()
        }
    }
    
    @Composable
    fun PulsingButton(
        onClick: () -> Unit,
        isPulsing: Boolean = true,
        modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
        content: @Composable () -> Unit
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            )
        )
        
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            )
        )
        
        val finalScale = if (isPulsing) scale else 1f
        val finalAlpha = if (isPulsing) alpha else 1f
        
        androidx.compose.foundation.layout.Box(
            modifier = modifier
                .scale(finalScale)
                .alpha(finalAlpha)
                .clickable(onClick = onClick)
        ) {
            content()
        }
    }
}
