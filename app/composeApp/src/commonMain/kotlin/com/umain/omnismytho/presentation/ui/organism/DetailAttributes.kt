package com.umain.omnismytho.presentation.ui.organism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umain.omnismytho.domain.model.Entity
import com.umain.omnismytho.presentation.ui.atom.OmDivider
import com.umain.omnismytho.presentation.ui.atom.OmSectionHeader
import com.umain.omnismytho.presentation.ui.molecule.OmFilterChip
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface
import com.umain.omnismytho.presentation.ui.preview.SampleData
import com.umain.omnismytho.presentation.ui.theme.DarkColorGold

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

        // Attributes — 3 cards in a row (from symbols)
        if (entity.symbols.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OmSectionHeader(text = "Attributes")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    val labels = listOf("Power", "Domain", "Symbol")
                    val icons = listOf(Icons.Default.AutoAwesome, Icons.Default.Star, Icons.Default.Shield)
                    entity.symbols.take(3).forEachIndexed { index, symbol ->
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    imageVector = icons.getOrElse(index) { Icons.Default.Star },
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = DarkColorGold,
                                )
                                Text(
                                    text = symbol,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                )
                                Text(
                                    text = labels.getOrElse(index) { "Attribute" },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
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
                        OmFilterChip(label = power, selected = false, onClick = {})
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

@Preview
@Composable
private fun DetailAttributesPreview() {
    OmPreviewSurface {
        DetailAttributes(entity = SampleData.entity)
    }
}
