package com.umain.omnismytho.presentation.ui.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.umain.omnismytho.presentation.ui.organism.EntityGrid
import com.umain.omnismytho.presentation.ui.organism.FilterBar
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface
import com.umain.omnismytho.presentation.ui.preview.SampleData
import com.umain.omnismytho.presentation.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogTemplate(
    title: String,
    onBack: () -> Unit,
    filterBar: @Composable () -> Unit,
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
                        text = title,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
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
            filterBar()
            content()
        }
    }
}

@Preview
@Composable
private fun CatalogTemplatePreview() {
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
