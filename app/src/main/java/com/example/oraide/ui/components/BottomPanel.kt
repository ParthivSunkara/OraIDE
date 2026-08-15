package com.example.oraide.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomPanel(
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("OUTPUT", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp))
            Text("TERMINAL", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp))
            Text("PROBLEMS", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp))
            
            Spacer(modifier = Modifier.weight(1f))
            
            AppIconView(
                icon = AppIcon.CLOSE,
                contentDescription = "Close Panel",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onClose() }
            )
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Panel Content (Placeholder)", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
        }
    }
}
