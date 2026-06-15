// ============================================
// 🚀 Edham Logistics - Professional Navigation
// Premium Dark Theme with Advanced Transitions
// ============================================

package com.edham.logistics.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.edham.logistics.ui.theme.*

/**
 * ============================================
 * Professional Bottom Navigation
 * ============================================
 */
@Composable
fun ProfessionalBottomNavigation(
    navController: NavController,
    items: List<BottomNavItem>,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                spotColor = EdhamOrange.copy(alpha = 0.3f),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ),
        color = Color(0xFF1A1A1A)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                ProfessionalBottomNavItem(
                    item = item,
                    isSelected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfessionalBottomNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
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

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(300, easing = EaseInOutCubic)
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) EdhamOrange else Color.White.copy(alpha = 0.6f),
        animationSpec = tween(300, easing = EaseInOutCubic)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) EdhamOrange.copy(alpha = 0.2f) else Color.Transparent,
        animationSpec = tween(300, easing = EaseInOutCubic)
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .shadow(
                        elevation = if (isSelected) 4.dp else 0.dp,
                        spotColor = EdhamOrange.copy(alpha = glowAlpha),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = item.title,
                color = iconColor,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                ),
                fontSize = 10.sp
            )
        }
    }
}

/**
 * ============================================
 * Professional Navigation Drawer
 * ============================================
 */
@Composable
fun ProfessionalNavigationDrawer(
    navController: NavController,
    items: List<DrawerNavItem>,
    currentRoute: String?,
    onDrawerClose: () -> Unit,
    userRole: String = "client"
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        Color(0xFF0A0A0A),
                        Color(0xFF1A1A1A)
                    )
                )
            )
    ) {
        // Header
        DrawerHeader(
            userRole = userRole,
            onDrawerClose = onDrawerClose
        )

        // Navigation Items
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items) { item ->
                ProfessionalDrawerItem(
                    item = item,
                    isSelected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        onDrawerClose()
                    }
                )
            }
        }
    }
}

@Composable
private fun DrawerHeader(
    userRole: String,
    onDrawerClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        EdhamOrange.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    center = androidx.compose.ui.geometry.Offset(
                        x = Float.POSITIVE_INFINITY,
                        y = 0f
                    ),
                    radius = 400f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Logo placeholder
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
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
                    Text(
                        text = "إ",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                IconButton(
                    onClick = onDrawerClose,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2A2A2A))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "إدهام اللوجستيك",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = when (userRole) {
                        "client" -> "العميل الذكي"
                        "driver" -> "السائق المحترف"
                        "supervisor" -> "المشرف المتميز"
                        "maintenance" -> "فني الصيانة"
                        "accounting" -> "المحاسب الدقيق"
                        else -> "مستخدم"
                    },
                    color = EdhamOrange,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun ProfessionalDrawerItem(
    item: DrawerNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(300, easing = EaseInOutCubic)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) EdhamOrange.copy(alpha = 0.2f) else Color.Transparent,
        animationSpec = tween(300, easing = EaseInOutCubic)
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) EdhamOrange else Color.White.copy(alpha = 0.7f),
        animationSpec = tween(300, easing = EaseInOutCubic)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = iconColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = item.title,
                color = if (isSelected) EdhamOrange else Color.White,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            )

            if (item.badge != null) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ErrorRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.badge,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

/**
 * ============================================
 * Professional Top Bar
 * ============================================
 */
@Composable
fun ProfessionalTopBar(
    title: String,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = Color.Transparent,
    showBackButton: Boolean = false
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(backgroundColor)
            .shadow(
                elevation = 4.dp,
                spotColor = EdhamOrange.copy(alpha = glowAlpha),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Navigation button or logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (showBackButton && onNavigationClick != null) {
                    IconButton(
                        onClick = onNavigationClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF2A2A2A))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = EdhamOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                actions()
            }
        }
    }
}

/**
 * ============================================
 * Professional Floating Action Menu
 * ============================================
 */
@Composable
fun ProfessionalFloatingActionMenu(
    items: List<FloatingActionItem>,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = tween(300, easing = EaseInOutCubic)
    )

    Box(
        modifier = modifier
    ) {
        // Menu items
        items.forEachIndexed { index, item ->
            val scale by animateFloatAsState(
                targetValue = if (expanded) 1f else 0f,
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = index * 50,
                    easing = EaseInOutCubic
                )
            )

            val offsetY by animateDpAsState(
                targetValue = if (expanded) -(80 + (index * 60)).dp else 0.dp,
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = index * 50,
                    easing = EaseInOutCubic
                )
            )

            Box(
                modifier = Modifier
                    .offset(y = offsetY)
                    .scale(scale)
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(item.color)
                    .clickable {
                        item.onClick()
                        onExpandedChange(false)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Main FAB
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            EdhamOrange,
                            EdhamOrange.copy(alpha = 0.8f)
                        )
                    )
                )
                .clickable { onExpandedChange(!expanded) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotation)
            )
        }
    }
}

/**
 * ============================================
 * Data Classes
 * ============================================
 */

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val badge: String? = null
)

data class DrawerNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val badge: String? = null,
    val isHeader: Boolean = false
)

data class FloatingActionItem(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)
