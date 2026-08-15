package com.example.oraide.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oraide.ui.components.AppIcon
import com.example.oraide.ui.components.AppIconView

@Composable
fun SearchPanel(
    query: String,
    onQueryChange: (String) -> Unit,
    matchCount: Int,
    currentMatchIndex: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            modifier = Modifier
                .width(150.dp)
                .padding(8.dp),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text("Find", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = if (matchCount > 0) "${currentMatchIndex + 1} of $matchCount" else "No results",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onPrevious, modifier = Modifier.size(24.dp)) {
            AppIconView(icon = AppIcon.FOLDER_OPEN, contentDescription = "Previous", tint = MaterialTheme.colorScheme.onSurface) // We don't have up arrow, so let's reuse
        }

        Spacer(modifier = Modifier.width(4.dp))

        IconButton(onClick = onNext, modifier = Modifier.size(24.dp)) {
            AppIconView(icon = AppIcon.FOLDER_OPEN, contentDescription = "Next", tint = MaterialTheme.colorScheme.onSurface)
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
            AppIconView(icon = AppIcon.CLOSE, contentDescription = "Close Search", tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}
