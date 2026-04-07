package com.umain.omnismytho.presentation.viewmodel

import com.umain.omnismytho.domain.model.Entity
import com.umain.revolver.RevolverEffect
import com.umain.revolver.RevolverEvent
import com.umain.revolver.RevolverState

sealed interface SavedEvent : RevolverEvent {
    data object LoadSaved : SavedEvent
    data class OnEntityClicked(val entityId: String) : SavedEvent
}

sealed interface SavedState : RevolverState {
    data object Loading : SavedState
    data class Loaded(val entities: List<Entity>) : SavedState
    data class Error(val message: String) : SavedState
}

sealed interface SavedEffect : RevolverEffect {
    data class NavigateToDetail(val entityId: String) : SavedEffect
}
