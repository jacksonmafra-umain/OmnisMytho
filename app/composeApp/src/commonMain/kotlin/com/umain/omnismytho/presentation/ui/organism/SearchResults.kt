package com.umain.omnismytho.presentation.ui.organism

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.umain.omnismytho.domain.model.Entity
import com.umain.omnismytho.presentation.ui.atom.OmDivider
import com.umain.omnismytho.presentation.ui.molecule.EntityListItem
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface
import com.umain.omnismytho.presentation.ui.preview.SampleData

@Composable
fun SearchResults(
    entities: List<Entity>,
    onEntityClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(entities, key = { it.id }) { entity ->
            EntityListItem(
                entity = entity,
                onClick = { onEntityClick(entity.id) },
                modifier = Modifier.animateItem(),
            )
            OmDivider()
        }
    }
}

@Preview
@Composable
private fun SearchResultsPreview() {
    OmPreviewSurface {
        SearchResults(entities = SampleData.entities.take(3), onEntityClick = {})
    }
}
