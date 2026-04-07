package com.umain.omnismytho.presentation.ui.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.umain.omnismytho.presentation.ui.molecule.OmSearchBar
import com.umain.omnismytho.presentation.ui.organism.SearchResults
import com.umain.omnismytho.presentation.ui.template.SearchTemplate
import com.umain.omnismytho.presentation.viewmodel.SearchEffect
import com.umain.omnismytho.presentation.viewmodel.SearchEvent
import com.umain.omnismytho.presentation.viewmodel.SearchState
import com.umain.omnismytho.presentation.viewmodel.SearchViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface
import com.umain.omnismytho.presentation.ui.preview.SampleData
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchPage(
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchEffect.NavigateToDetail -> onNavigateToDetail(effect.entityId)
            }
        }
    }

    SearchTemplate(
        onBack = onNavigateBack,
        searchBar = {
            OmSearchBar(
                query = query,
                onQueryChange = { newQuery ->
                    query = newQuery
                    viewModel.emit(SearchEvent.OnQueryChanged(newQuery))
                },
                onClear = {
                    query = ""
                    viewModel.emit(SearchEvent.OnClear)
                },
            )
        },
        content = {
            AnimatedContent(
                targetState = state,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                label = "search-state",
            ) { s ->
                when (s) {
                    is SearchState.Idle -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Search for gods, demons, angels...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    is SearchState.Searching -> {
                        Text("Searching...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    is SearchState.Results -> {
                        SearchResults(
                            entities = s.entities,
                            onEntityClick = { id ->
                                viewModel.emit(SearchEvent.OnEntityClicked(id))
                            },
                        )
                    }

                    is SearchState.Empty -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No results for \"${s.query}\"",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    is SearchState.Error -> {
                        Text(s.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
    )
}

@Preview
@Composable
private fun SearchPageIdlePreview() {
    OmPreviewSurface {
        SearchTemplate(
            onBack = {},
            searchBar = { OmSearchBar(query = "", onQueryChange = {}) },
            content = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Search for gods, demons, angels...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
        )
    }
}

@Preview
@Composable
private fun SearchPageResultsPreview() {
    OmPreviewSurface {
        SearchTemplate(
            onBack = {},
            searchBar = { OmSearchBar(query = "Zeus", onQueryChange = {}) },
            content = {
                SearchResults(entities = SampleData.entities.take(3), onEntityClick = {})
            },
        )
    }
}
