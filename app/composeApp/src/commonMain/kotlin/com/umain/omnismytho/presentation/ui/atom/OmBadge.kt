package com.umain.omnismytho.presentation.ui.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.umain.omnismytho.domain.model.Alignment
import com.umain.omnismytho.domain.model.EntityType

@Composable
fun OmTypeBadge(
    type: EntityType,
    modifier: Modifier = Modifier,
) {
    val color =
        when (type) {
            EntityType.GOD -> MaterialTheme.colorScheme.primary
            EntityType.DEMON -> Color(0xFF8B3A2A)
            EntityType.ANGEL -> Color(0xFFD4A43C)
            EntityType.SPIRIT -> MaterialTheme.colorScheme.tertiary
            EntityType.CREATURE -> MaterialTheme.colorScheme.secondary
        }
    Text(
        text = type.displayName,
        modifier = modifier,
        color = color,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
fun OmAlignmentBadge(
    alignment: Alignment,
    modifier: Modifier = Modifier,
) {
    val (bgColor, textColor) =
        when (alignment) {
            Alignment.GOOD -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) to MaterialTheme.colorScheme.primary
            Alignment.NEUTRAL -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) to MaterialTheme.colorScheme.onSurfaceVariant
            Alignment.EVIL -> Color(0xFF8B3A2A).copy(alpha = 0.15f) to Color(0xFF8B3A2A)
            Alignment.CHAOTIC -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f) to MaterialTheme.colorScheme.tertiary
        }
    Text(
        text = alignment.displayName,
        modifier =
            modifier
                .clip(MaterialTheme.shapes.extraSmall)
                .background(bgColor)
                .padding(horizontal = 8.dp, vertical = 2.dp),
        color = textColor,
        style = MaterialTheme.typography.labelSmall,
    )
}
