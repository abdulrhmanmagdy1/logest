package com.edham.logistics.user

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * User Management Admin Screen - Simplified version
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementAdminScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // State management
    var selectedTab by remember { mutableStateOf(UserManagementTab.OVERVIEW) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        UserManagementHeader(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            onBack = onBack
        )
        
        // Content
        when (selectedTab) {
            UserManagementTab.OVERVIEW -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "نظرة عامة - قيد التطوير",
                        color = Color.Gray
                    )
                }
            }
            UserManagementTab.USERS -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "المستخدمون - قيد التطوير",
                        color = Color.Gray
                    )
                }
            }
            UserManagementTab.ROLES -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "الأدوار - قيد التطوير",
                        color = Color.Gray
                    )
                }
            }
            UserManagementTab.ACTIVITY -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "سجلات النشاط - قيد التطوير",
                        color = Color.Gray
                    )
                }
            }
            UserManagementTab.SECURITY -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "الأحداث الأمنية - قيد التطوير",
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * User Management Header
 */
@Composable
private fun UserManagementHeader(
    selectedTab: UserManagementTab,
    onTabSelected: (UserManagementTab) -> Unit,
    onBack: () -> Unit
) {
    Column {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF2C3E50)
                    )
                }
                
                Text(
                    text = "إدارة المستخدمين",
                    color = Color(0xFF2C3E50)
                )
            }
        }
        
        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            modifier = Modifier.background(Color.White),
            containerColor = Color.White,
            contentColor = Color(0xFF2C3E50)
        ) {
            UserManagementTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Text(
                            text = when (tab) {
                                UserManagementTab.OVERVIEW -> "نظرة عامة"
                                UserManagementTab.USERS -> "المستخدمون"
                                UserManagementTab.ROLES -> "الأدوار"
                                UserManagementTab.ACTIVITY -> "النشاط"
                                UserManagementTab.SECURITY -> "الأمان"
                            }
                        )
                    }
                )
            }
        }
    }
}

// Enums
enum class UserManagementTab {
    OVERVIEW,
    USERS,
    ROLES,
    ACTIVITY,
    SECURITY
}
