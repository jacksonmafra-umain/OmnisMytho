package com.umain.omnismytho.presentation.ui.organism

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umain.omnismytho.domain.model.Entity
import com.umain.omnismytho.presentation.ui.molecule.EntityCard
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface
import com.umain.omnismytho.presentation.ui.preview.SampleData

@Composable
fun EntityGrid(
    entities: List<Entity>,
    onEntityClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(entities, key = { it.id }) { entity ->
            EntityCard(
                entity = entity,
                onClick = { onEntityClick(entity.id) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Preview
@Composable
private fun EntityGridPreview() {
    OmPreviewSurface {
        EntityGrid(entities = SampleData.entities, onEntityClick = {})
    }
}
