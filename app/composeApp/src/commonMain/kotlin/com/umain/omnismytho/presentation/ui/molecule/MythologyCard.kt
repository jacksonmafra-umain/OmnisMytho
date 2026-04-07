package com.umain.omnismytho.presentation.ui.molecule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.umain.omnismytho.domain.model.Mythology
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface
import com.umain.omnismytho.presentation.ui.preview.SampleData

@Composable
fun MythologyCard(
    mythology: Mythology,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable(onClick = onClick),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                text =
                    mythology.name
                        .replace(" Mythology", "")
                        .replace(" Demonology & Angelology", ""),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${mythology.entityCount} entities",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ── Previews ────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun MythologyCardPreview() {
    OmPreviewSurface { MythologyCard(mythology = SampleData.mythology, onClick = {}) }
}

@Preview
@Composable
private fun MythologyCardLargeTextPreview() {
    OmPreviewSurface {
        CompositionLocalProvider(
            LocalDensity provides Density(LocalDensity.current.density, fontScale = 1.5f),
        ) {
            MythologyCard(mythology = SampleData.mythology, onClick = {})
        }
    }
}

@Preview
@Composable
private fun MythologyCardLandscapePreview() {
    OmPreviewSurface { MythologyCard(mythology = SampleData.mythology, onClick = {}) }
}
