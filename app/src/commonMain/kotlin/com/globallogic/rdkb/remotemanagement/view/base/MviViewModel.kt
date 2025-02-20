package com.globallogic.rdkb.remotemanagement.view.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class MviViewModel<State: Any>(
    initialState: State,
) : ViewModel() {
    protected val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState
        .shareIn(viewModelScope, SharingStarted.Lazily)
        .onSubscription { onSubscribeState() }
        .onStart { onInitState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialState)

    protected open suspend fun onInitState() = Unit

    protected open suspend fun onSubscribeState() = Unit

    protected fun launchOnViewModelScope(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }

    protected inline fun updateState(update: (State) -> State) =
        _uiState.update { state -> update(state) }

    protected fun launchUpdateState(update: suspend (State) -> State) = launchOnViewModelScope {
        updateState { state -> update(state) }
    }
}
