package com.umain.omnismytho.presentation.viewmodel

import com.umain.omnismytho.domain.model.Mythology
import com.umain.revolver.RevolverEffect
import com.umain.revolver.RevolverEvent
import com.umain.revolver.RevolverState

sealed interface HomeEvent : RevolverEvent {
    data object LoadMythologies : HomeEvent

    data class OnMythologyClicked(
        val mythologyId: String,
    ) : HomeEvent

    data object OnSearchClicked : HomeEvent
}

sealed interface HomeState : RevolverState {
    data object Loading : HomeState

    data class Loaded(
        val mythologies: List<Mythology>,
    ) : HomeState

    data class Error(
        val message: String,
    ) : HomeState
}

sealed interface HomeEffect : RevolverEffect {
    data class NavigateToCatalog(
        val mythologyId: String,
    ) : HomeEffect

    data object NavigateToSearch : HomeEffect
}
