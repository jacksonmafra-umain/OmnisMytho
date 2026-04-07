package com.umain.omnismytho.presentation.ui.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.umain.omnismytho.presentation.ui.molecule.OmSearchBar
import com.umain.omnismytho.presentation.ui.organism.MythologyGrid
import com.umain.omnismytho.presentation.ui.template.HomeTemplate
import com.umain.omnismytho.presentation.viewmodel.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomePage(
    onNavigateToCatalog: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.emit(HomeEvent.LoadMythologies)
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToCatalog -> onNavigateToCatalog(effect.mythologyId)
                HomeEffect.NavigateToSearch -> onNavigateToSearch()
            }
        }
    }

    HomeTemplate(
        searchBar = {
            OmSearchBar(
                query = "",
                onQueryChange = {},
                readOnly = true,
                onClick = { viewModel.emit(HomeEvent.OnSearchClicked) },
            )
        },
        mythologyGrid = {
            when (val s = state) {
                is HomeState.Loading -> {
                    Text(
                        text = "Loading...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                is HomeState.Loaded -> {
                    MythologyGrid(
                        mythologies = s.mythologies,
                        onMythologyClick = { id ->
                            viewModel.emit(HomeEvent.OnMythologyClicked(id))
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                is HomeState.Error -> {
                    Text(
                        text = s.message,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
    )
}
