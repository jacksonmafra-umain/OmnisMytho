package com.umain.omnismytho.presentation.ui.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.umain.omnismytho.presentation.ui.molecule.OmSearchBar
import com.umain.omnismytho.presentation.ui.organism.SearchResults
import com.umain.omnismytho.presentation.ui.template.SearchTemplate
import com.umain.omnismytho.presentation.viewmodel.*
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
            when (val s = state) {
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
        },
    )
}
