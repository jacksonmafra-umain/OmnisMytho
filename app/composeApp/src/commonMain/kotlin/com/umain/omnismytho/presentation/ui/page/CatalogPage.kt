package com.umain.omnismytho.presentation.ui.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.umain.omnismytho.presentation.ui.organism.EntityGrid
import com.umain.omnismytho.presentation.ui.organism.FilterBar
import com.umain.omnismytho.presentation.ui.template.CatalogTemplate
import com.umain.omnismytho.presentation.viewmodel.*
import androidx.compose.ui.tooling.preview.Preview
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface
import com.umain.omnismytho.presentation.ui.preview.SampleData
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CatalogPage(
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    mythologyId: String = "",
    viewModel: CatalogViewModel = koinViewModel { parametersOf(mythologyId) },
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.emit(CatalogEvent.LoadEntities)
        viewModel.effect.collect { effect ->
            when (effect) {
                is CatalogEffect.NavigateToDetail -> onNavigateToDetail(effect.entityId)
            }
        }
    }

    val loadedState = state as? CatalogState.Loaded

    CatalogTemplate(
        title = mythologyId.replaceFirstChar { it.uppercase() } + " Mythology",
        subtitle = if (loadedState != null) "${loadedState.entities.size} entities found" else "",
        sortLabel = if (loadedState?.sortAscending != false) "A-Z" else "Z-A",
        onBack = onNavigateBack,
        onSort = { viewModel.emit(CatalogEvent.OnToggleSort) },
        filterBar = {
            FilterBar(
                selectedType = (state as? CatalogState.Loaded)?.currentFilter,
                onFilterChanged = { type ->
                    viewModel.emit(CatalogEvent.OnFilterChanged(type))
                },
            )
        },
        content = {
            when (val s = state) {
                is CatalogState.Loading -> {
                    Text("Loading...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                is CatalogState.Loaded -> {
                    EntityGrid(
                        entities = s.entities,
                        onEntityClick = { id ->
                            viewModel.emit(CatalogEvent.OnEntityClicked(id))
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                is CatalogState.Error -> {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                }
            }
        },
    )
}

@Preview
@Composable
private fun CatalogPagePreview() {
    OmPreviewSurface {
        CatalogTemplate(
            title = "Greek Mythology",
            onBack = {},
            filterBar = { FilterBar(selectedType = null, onFilterChanged = {}) },
            content = {
                EntityGrid(entities = SampleData.entities, onEntityClick = {})
            },
        )
    }
}
