package com.umain.omnismytho.presentation.ui.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.umain.omnismytho.presentation.ui.organism.DetailAttributes
import com.umain.omnismytho.presentation.ui.organism.DetailHeader
import com.umain.omnismytho.presentation.ui.template.DetailTemplate
import com.umain.omnismytho.presentation.viewmodel.DetailEffect
import com.umain.omnismytho.presentation.viewmodel.DetailEvent
import com.umain.omnismytho.presentation.viewmodel.DetailState
import com.umain.omnismytho.presentation.viewmodel.DetailViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface
import com.umain.omnismytho.presentation.ui.preview.SampleData
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DetailPage(
    onNavigateBack: () -> Unit,
    entityId: String = "",
    viewModel: DetailViewModel = koinViewModel { parametersOf(entityId) },
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.emit(DetailEvent.LoadEntity)
        viewModel.effect.collect { effect ->
            when (effect) {
                DetailEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    AnimatedContent(
        targetState = state,
        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
        label = "detail-state",
    ) { s ->
        when (s) {
            is DetailState.Loading -> {
                DetailTemplate(
                    onBack = onNavigateBack,
                    header = {
                        Text(
                            "Loading...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    attributes = {},
                )
            }

            is DetailState.Loaded -> {
                DetailTemplate(
                    onBack = onNavigateBack,
                    onBookmark = { scope.launch { snackbarHostState.showSnackbar("Bookmarked!") } },
                    onShare = { scope.launch { snackbarHostState.showSnackbar("Share coming soon") } },
                    snackbarHostState = snackbarHostState,
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
}

@Preview
@Composable
private fun DetailPagePreview() {
    OmPreviewSurface {
        DetailTemplate(
            onBack = {},
            header = { DetailHeader(entity = SampleData.entity) },
            attributes = { DetailAttributes(entity = SampleData.entity) },
        )
    }
}
