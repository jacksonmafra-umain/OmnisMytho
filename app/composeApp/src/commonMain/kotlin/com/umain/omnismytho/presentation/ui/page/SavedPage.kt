package com.umain.omnismytho.presentation.ui.page

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umain.omnismytho.presentation.ui.organism.EntityGrid
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface
import com.umain.omnismytho.presentation.viewmodel.SavedEffect
import com.umain.omnismytho.presentation.viewmodel.SavedEvent
import com.umain.omnismytho.presentation.viewmodel.SavedState
import com.umain.omnismytho.presentation.viewmodel.SavedViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SavedPage(
    onNavigateToDetail: (String) -> Unit = {},
    viewModel: SavedViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.emit(SavedEvent.LoadSaved)
        viewModel.effect.collect { effect ->
            when (effect) {
                is SavedEffect.NavigateToDetail -> onNavigateToDetail(effect.entityId)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Text(
            text = "Saved",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(Modifier.height(24.dp))

        when (val s = state) {
            is SavedState.Loading -> {
                Text("Loading...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            is SavedState.Loaded -> {
                if (s.entities.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "No saved entities yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "Bookmark entities to find them here",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline,
                            )
                        }
                    }
                } else {
                    EntityGrid(
                        entities = s.entities,
                        onEntityClick = { id ->
                            viewModel.emit(SavedEvent.OnEntityClicked(id))
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            is SavedState.Error -> {
                Text(s.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Preview
@Composable
private fun SavedPagePreview() {
    OmPreviewSurface { SavedPage() }
}
