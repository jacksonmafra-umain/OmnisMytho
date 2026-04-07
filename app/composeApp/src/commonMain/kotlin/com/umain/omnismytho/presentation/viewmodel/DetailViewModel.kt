package com.umain.omnismytho.presentation.viewmodel

import com.umain.omnismytho.domain.repository.EntityRepository
import com.umain.revolver.RevolverViewModel

class DetailViewModel(
    private val entityId: String,
    private val entityRepository: EntityRepository,
) : RevolverViewModel<DetailEvent, DetailState, DetailEffect>(
        initialState = DetailState.Loading,
    ) {
    init {
        addEventHandler<DetailEvent.LoadEntity> { _, emit ->
            emit.state(DetailState.Loading)
            try {
                val entity = entityRepository.getEntity(entityId)
                emit.state(DetailState.Loaded(entity))
            } catch (e: Exception) {
                emit.state(DetailState.Error(e.message ?: "Failed to load entity"))
            }
        }
    }
}
