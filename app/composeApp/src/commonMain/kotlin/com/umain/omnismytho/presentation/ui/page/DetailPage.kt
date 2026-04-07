package com.umain.omnismytho.presentation.ui.page

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.umain.omnismytho.presentation.ui.organism.DetailAttributes
import com.umain.omnismytho.presentation.ui.organism.DetailHeader
import com.umain.omnismytho.presentation.ui.template.DetailTemplate
import com.umain.omnismytho.presentation.viewmodel.*
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DetailPage(
    onNavigateBack: () -> Unit,
    entityId: String = "",
    viewModel: DetailViewModel = koinViewModel { parametersOf(entityId) },
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.emit(DetailEvent.LoadEntity)
        viewModel.effect.collect { effect ->
            when (effect) {
                DetailEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    when (val s = state) {
        is DetailState.Loading -> {
            DetailTemplate(
                onBack = onNavigateBack,
                header = { Text("Loading...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                attributes = {},
            )
        }
        is DetailState.Loaded -> {
            DetailTemplate(
                onBack = onNavigateBack,
                header = { DetailHeader(entity = s.entity) },
                attributes = { DetailAttributes(entity = s.entity) },
            )
        }
        is DetailState.Error -> {
            DetailTemplate(
                onBack = onNavigateBack,
                header = { Text(s.message, color = MaterialTheme.colorScheme.error) },
                attributes = {},
            )
        }
    }
}
