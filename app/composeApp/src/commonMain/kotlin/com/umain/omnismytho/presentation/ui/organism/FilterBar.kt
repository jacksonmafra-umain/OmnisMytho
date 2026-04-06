package com.umain.omnismytho.presentation.ui.organism

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.umain.omnismytho.domain.model.EntityType
import com.umain.omnismytho.presentation.ui.molecule.OmFilterChip

@Composable
fun FilterBar(
    selectedType: EntityType?,
    onFilterChanged: (EntityType?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filters = listOf<Pair<String, EntityType?>>(
        "All" to null,
        "Gods" to EntityType.GOD,
        "Demons" to EntityType.DEMON,
        "Angels" to EntityType.ANGEL,
        "Spirits" to EntityType.SPIRIT,
        "Creatures" to EntityType.CREATURE,
    )

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(filters) { (label, type) ->
            OmFilterChip(
                label = label,
                selected = selectedType == type,
                onClick = { onFilterChanged(type) },
            )
        }
    }
}
