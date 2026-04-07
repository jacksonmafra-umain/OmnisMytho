package com.umain.omnismytho.presentation.ui.molecule

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface

@Composable
fun OmSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search entities, gods, demons...",
    onClear: (() -> Unit)? = null,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(12.dp)
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier =
            modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape),
        readOnly = readOnly,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon =
            if (query.isNotEmpty() && onClear != null) {
                {
                    IconButton(onClick = onClear) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            } else {
                null
            },
        singleLine = true,
        shape = shape,
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
            ),
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}

// ── Previews ────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun OmSearchBarEmptyPreview() {
    OmPreviewSurface { OmSearchBar(query = "", onQueryChange = {}) }
}

@Preview
@Composable
private fun OmSearchBarFilledPreview() {
    OmPreviewSurface { OmSearchBar(query = "Zeus", onQueryChange = {}, onClear = {}) }
}

@Preview
@Composable
private fun OmSearchBarLargeTextPreview() {
    OmPreviewSurface {
        CompositionLocalProvider(
            LocalDensity provides Density(LocalDensity.current.density, fontScale = 1.5f),
        ) {
            OmSearchBar(query = "Zeus", onQueryChange = {}, onClear = {})
        }
    }
}
