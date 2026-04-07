package com.umain.omnismytho.presentation.ui.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.umain.omnismytho.presentation.ui.molecule.OmSearchBar
import com.umain.omnismytho.presentation.ui.organism.SearchResults
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface
import com.umain.omnismytho.presentation.ui.preview.SampleData
import com.umain.omnismytho.presentation.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTemplate(
    onBack: () -> Unit,
    searchBar: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.displayMedium,
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            searchBar()
            content()
        }
    }
}

@Preview
@Composable
private fun SearchTemplatePreview() {
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
