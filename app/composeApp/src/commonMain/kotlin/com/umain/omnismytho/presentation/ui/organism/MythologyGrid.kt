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
        items(mythologies.size, key = { mythologies[it].id }) { index ->
            val mythology = mythologies[index]
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

            MythologyCard(
                mythology = mythology,
                onClick = { onMythologyClick(mythology.id) },
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
private fun MythologyGridPreview() {
    OmPreviewSurface {
        MythologyGrid(mythologies = SampleData.mythologies, onMythologyClick = {})
    }
}
