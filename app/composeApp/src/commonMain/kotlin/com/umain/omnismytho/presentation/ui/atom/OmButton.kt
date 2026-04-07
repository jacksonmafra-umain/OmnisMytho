package com.umain.omnismytho.presentation.ui.atom

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface

@Composable
fun OmPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun OmOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors =
            ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

// ── Previews ────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun OmPrimaryButtonPreview() {
    OmPreviewSurface { OmPrimaryButton(text = "Explore", onClick = {}) }
}

@Preview
@Composable
private fun OmPrimaryButtonLargeTextPreview() {
    OmPreviewSurface {
        CompositionLocalProvider(
            LocalDensity provides Density(LocalDensity.current.density, fontScale = 1.5f),
        ) {
            OmPrimaryButton(text = "Explore", onClick = {})
        }
    }
}

@Preview
@Composable
private fun OmOutlinedButtonPreview() {
    OmPreviewSurface { OmOutlinedButton(text = "See All", onClick = {}) }
}

@Preview
@Composable
private fun OmOutlinedButtonLargeTextPreview() {
    OmPreviewSurface {
        CompositionLocalProvider(
            LocalDensity provides Density(LocalDensity.current.density, fontScale = 1.5f),
        ) {
            OmOutlinedButton(text = "See All", onClick = {})
        }
    }
}

@Preview
@Composable
private fun OmButtonLandscapePreview() {
    OmPreviewSurface {
        OmPrimaryButton(text = "Explore", onClick = {})
    }
}
