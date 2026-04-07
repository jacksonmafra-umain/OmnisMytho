package com.umain.omnismytho.presentation.ui.atom

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface

@Composable
fun OmDisplayText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
    style: TextStyle = MaterialTheme.typography.displayMedium,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = style,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        textAlign = textAlign,
    )
}

@Composable
fun OmBodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = style,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        textAlign = textAlign,
    )
}

@Composable
fun OmLabelText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    maxLines: Int = 1,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = style,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun OmSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelSmall,
        letterSpacing = MaterialTheme.typography.labelSmall.letterSpacing * 4,
    )
}

// ── Previews ────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun OmDisplayTextPreview() {
    OmPreviewSurface { OmDisplayText("Omnis Mytho") }
}

@Preview
@Composable
private fun OmDisplayTextLargeTextPreview() {
    OmPreviewSurface {
        CompositionLocalProvider(
            LocalDensity provides Density(LocalDensity.current.density, fontScale = 1.5f),
        ) {
            OmDisplayText("Omnis Mytho")
        }
    }
}

@Preview
@Composable
private fun OmBodyTextPreview() {
    OmPreviewSurface { OmBodyText("Sovereign of the sky and lord of thunder.") }
}

@Preview
@Composable
private fun OmBodyTextLargeTextPreview() {
    OmPreviewSurface {
        CompositionLocalProvider(
            LocalDensity provides Density(LocalDensity.current.density, fontScale = 1.5f),
        ) {
            OmBodyText("Sovereign of the sky and lord of thunder.")
        }
    }
}

@Preview
@Composable
private fun OmLabelTextPreview() {
    OmPreviewSurface { OmLabelText("MYTHOLOGIES") }
}

@Preview
@Composable
private fun OmLabelTextLargeTextPreview() {
    OmPreviewSurface {
        CompositionLocalProvider(
            LocalDensity provides Density(LocalDensity.current.density, fontScale = 1.5f),
        ) {
            OmLabelText("MYTHOLOGIES")
        }
    }
}

@Preview
@Composable
private fun OmSectionHeaderPreview() {
    OmPreviewSurface { OmSectionHeader("Description") }
}

@Preview
@Composable
private fun OmSectionHeaderLargeTextPreview() {
    OmPreviewSurface {
        CompositionLocalProvider(
            LocalDensity provides Density(LocalDensity.current.density, fontScale = 1.5f),
        ) {
            OmSectionHeader("Description")
        }
    }
}

@Preview
@Composable
private fun OmTextLandscapePreview() {
    OmPreviewSurface {
        OmDisplayText("Omnis Mytho")
    }
}
