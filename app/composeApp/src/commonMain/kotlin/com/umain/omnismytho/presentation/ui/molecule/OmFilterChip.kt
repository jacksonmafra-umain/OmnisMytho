package com.umain.omnismytho.presentation.ui.molecule

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface

@Composable
fun OmFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
            )
        },
        modifier = modifier,
        colors =
            FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.surface,
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        shape = MaterialTheme.shapes.extraLarge,
        border =
            FilterChipDefaults.filterChipBorder(
                borderColor = MaterialTheme.colorScheme.outline,
                selectedBorderColor = MaterialTheme.colorScheme.primary,
                enabled = true,
                selected = selected,
            ),
    )
}

// ── Previews ────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun OmFilterChipSelectedPreview() {
    OmPreviewSurface { OmFilterChip(label = "Gods", selected = true, onClick = {}) }
}

@Preview
@Composable
private fun OmFilterChipUnselectedPreview() {
    OmPreviewSurface { OmFilterChip(label = "Demons", selected = false, onClick = {}) }
}

@Preview
@Composable
private fun OmFilterChipLargeTextPreview() {
    OmPreviewSurface {
        CompositionLocalProvider(
            LocalDensity provides Density(LocalDensity.current.density, fontScale = 1.5f),
        ) {
            OmFilterChip(label = "Gods", selected = true, onClick = {})
        }
    }
}
