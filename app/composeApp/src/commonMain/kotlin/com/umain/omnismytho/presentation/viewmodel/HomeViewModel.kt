package com.umain.omnismytho.presentation.viewmodel

import com.umain.omnismytho.domain.repository.MythologyRepository
import com.umain.revolver.RevolverViewModel

class HomeViewModel(
    private val mythologyRepository: MythologyRepository,
) : RevolverViewModel<HomeEvent, HomeState, HomeEffect>(
    initialState = HomeState.Loading
) {
    init {
        addEventHandler<HomeEvent.LoadMythologies> { _, emit ->
            emit.state(HomeState.Loading)
            try {
                val mythologies = mythologyRepository.getMythologies()
                emit.state(HomeState.Loaded(mythologies))
            } catch (e: Exception) {
                emit.state(HomeState.Error(e.message ?: "Failed to load mythologies"))
            }
        }

        addEventHandler<HomeEvent.OnMythologyClicked> { event, emit ->
            emit.effect(HomeEffect.NavigateToCatalog(event.mythologyId))
        }

        addEventHandler<HomeEvent.OnSearchClicked> { _, emit ->
            emit.effect(HomeEffect.NavigateToSearch)
        }
    }
}
