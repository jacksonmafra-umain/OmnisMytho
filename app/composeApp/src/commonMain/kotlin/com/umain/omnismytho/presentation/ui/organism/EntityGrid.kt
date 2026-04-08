package com.umain.omnismytho.presentation.ui.organism

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
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
        items(entities.size, key = { entities[it].id }) { index ->
            val entity = entities[index]
            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(index * 60L)
                visible = true
            }

            val alpha by animateFloatAsState(
                targetValue = if (visible) 1f else 0f,
                animationSpec = tween(400, easing = FastOutSlowInEasing),
                label = "alpha",
            )
            val translationY by animateFloatAsState(
                targetValue = if (visible) 0f else 40f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
                label = "translationY",
            )

            EntityCard(
                entity = entity,
                onClick = { onEntityClick(entity.id) },
                modifier = Modifier
                    .animateItem()
                    .graphicsLayer {
                        this.alpha = alpha
                        this.translationY = translationY
                    },
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
