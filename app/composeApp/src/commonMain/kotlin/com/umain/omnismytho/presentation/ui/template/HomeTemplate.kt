package com.umain.omnismytho.presentation.ui.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.umain.omnismytho.presentation.ui.molecule.OmSearchBar
import com.umain.omnismytho.presentation.ui.organism.MythologyGrid
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface
import com.umain.omnismytho.presentation.ui.preview.SampleData
import com.umain.omnismytho.presentation.ui.theme.LocalSpacing

@Composable
fun HomeTemplate(
    searchBar: @Composable () -> Unit,
    mythologyGrid: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = spacing.md)
                .padding(top = spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        // Header
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Text(
                text = "Explore the Unknown",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 3.sp,
            )
            Text(
                text = "Omnis Mytho",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "A compendium of gods, demons, angels & entities across all mythologies",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        searchBar()

        // Section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "MYTHOLOGIES",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 3.sp,
            )
            Text(
                text = "See All",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        mythologyGrid()
    }
}

@Preview
@Composable
private fun HomeTemplatePreview() {
    OmPreviewSurface {
        HomeTemplate(
            searchBar = { OmSearchBar(query = "", onQueryChange = {}) },
            mythologyGrid = {
                MythologyGrid(mythologies = SampleData.mythologies, onMythologyClick = {})
            },
        )
    }
}
