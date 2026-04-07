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
import com.umain.omnismytho.domain.model.Mythology
import com.umain.omnismytho.presentation.ui.molecule.MythologyCard
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface
import com.umain.omnismytho.presentation.ui.preview.SampleData

@Composable
fun MythologyGrid(
    mythologies: List<Mythology>,
    onMythologyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(mythologies, key = { it.id }) { mythology ->
            MythologyCard(
                mythology = mythology,
                onClick = { onMythologyClick(mythology.id) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Preview
@Composable
private fun MythologyGridPreview() {
    OmPreviewSurface {
        MythologyGrid(mythologies = SampleData.mythologies, onMythologyClick = {})
    }
}
