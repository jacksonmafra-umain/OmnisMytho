package com.umain.omnismytho.presentation.ui.atom

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface

@Composable
fun OmDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}

// ── Previews ────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun OmDividerPreview() {
    OmPreviewSurface { OmDivider() }
}

@Preview
@Composable
private fun OmDividerLargeTextPreview() {
    OmPreviewSurface {
        CompositionLocalProvider(
            LocalDensity provides Density(LocalDensity.current.density, fontScale = 1.5f),
        ) {
            OmDivider()
        }
    }
}

@Preview
@Composable
private fun OmDividerLandscapePreview() {
    OmPreviewSurface { OmDivider() }
}
