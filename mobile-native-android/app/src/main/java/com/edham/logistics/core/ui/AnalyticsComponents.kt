package com.edham.logistics.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

/**
 * Shared analytics UI components
 * These composables are used across multiple analytics fragments
 */

@Composable
fun OverviewMetric(
    title: String,
    value: String,
    subtitle: String,
    icon: Painter,
    color: Color,
    change: Float = 0f
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.size(48.dp),
            colors = CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = color,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 2.dp)
            )
            
            if (change != 0f) {
                val changeText = if (change > 0) "+${String.format("%.1f", change)}%" else "${String.format("%.1f", change)}%"
                val changeColor = if (change > 0) Color(0xFF4CAF50) else if (change < 0) Color(0xFFF44336) else Color(0xFF9E9E9E)
                
                Text(
                    text = changeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = changeColor,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
