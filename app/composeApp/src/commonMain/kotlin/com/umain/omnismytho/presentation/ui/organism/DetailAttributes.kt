package com.umain.omnismytho.presentation.ui.organism

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.umain.omnismytho.domain.model.Entity
import com.umain.omnismytho.presentation.ui.atom.OmDivider
import com.umain.omnismytho.presentation.ui.atom.OmSectionHeader
import com.umain.omnismytho.presentation.ui.molecule.OmFilterChip

@Composable
fun DetailAttributes(
    entity: Entity,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Description
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OmSectionHeader(text = "Description")
            Text(
                text = entity.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
            )
        }

        OmDivider()

        // Powers
        if (entity.powers.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OmSectionHeader(text = "Powers")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    entity.powers.forEach { power ->
                        OmFilterChip(
                            label = power,
                            selected = false,
                            onClick = {},
                        )
                    }
                }
            }
        }

        // Symbols
        if (entity.symbols.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OmSectionHeader(text = "Symbols")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    entity.symbols.forEach { symbol ->
                        OmFilterChip(
                            label = symbol,
                            selected = false,
                            onClick = {},
                        )
                    }
                }
            }
        }

        OmDivider()

        // Personality
        if (entity.personality.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OmSectionHeader(text = "Personality")
                Text(
                    text = entity.personality,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
