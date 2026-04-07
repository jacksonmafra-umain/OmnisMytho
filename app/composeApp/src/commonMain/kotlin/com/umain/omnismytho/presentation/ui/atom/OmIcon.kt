package com.umain.omnismytho.presentation.ui.atom

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface

@Composable
fun OmIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

// ── Previews ────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun OmIconPreview() {
    OmPreviewSurface {
        OmIcon(imageVector = Icons.Default.Search)
    }
}

@Preview
@Composable
private fun OmIconLargeTextPreview() {
    OmPreviewSurface {
        CompositionLocalProvider(
            LocalDensity provides Density(LocalDensity.current.density, fontScale = 1.5f),
        ) {
            OmIcon(imageVector = Icons.Default.Search)
        }
    }
}

@Preview
@Composable
private fun OmIconLandscapePreview() {
    OmPreviewSurface {
        OmIcon(imageVector = Icons.Default.Search)
    }
}
