package com.umain.omnismytho.presentation.viewmodel

import com.umain.omnismytho.domain.model.Entity
import com.umain.revolver.RevolverEffect
import com.umain.revolver.RevolverEvent
import com.umain.revolver.RevolverState

sealed interface DetailEvent : RevolverEvent {
    data object LoadEntity : DetailEvent
    data object ToggleBookmark : DetailEvent
}

sealed interface DetailState : RevolverState {
    data object Loading : DetailState

    data class Loaded(
        val entity: Entity,
        val isBookmarked: Boolean = false,
    ) : DetailState

    data class Error(
        val message: String,
    ) : DetailState
}

sealed interface DetailEffect : RevolverEffect {
    data object NavigateBack : DetailEffect
    data class ShowSnackbar(val message: String) : DetailEffect
}
