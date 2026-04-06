package com.umain.omnismytho.presentation.viewmodel

import com.umain.omnismytho.domain.model.Entity
import com.umain.revolver.RevolverEffect
import com.umain.revolver.RevolverEvent
import com.umain.revolver.RevolverState

sealed interface SearchEvent : RevolverEvent {
    data class OnQueryChanged(val query: String) : SearchEvent
    data class OnEntityClicked(val entityId: String) : SearchEvent
    data object OnClear : SearchEvent
}

sealed interface SearchState : RevolverState {
    data object Idle : SearchState
    data object Searching : SearchState
    data class Results(val entities: List<Entity>, val query: String) : SearchState
    data class Empty(val query: String) : SearchState
    data class Error(val message: String) : SearchState
}

sealed interface SearchEffect : RevolverEffect {
    data class NavigateToDetail(val entityId: String) : SearchEffect
}
