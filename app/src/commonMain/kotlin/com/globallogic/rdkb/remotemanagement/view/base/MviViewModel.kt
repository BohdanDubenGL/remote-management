package com.globallogic.rdkb.remotemanagement.view.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

abstract class MviViewModel<State: Any>(
    initialState: State,
) : ViewModel() {
    protected val mutableUiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    private val immutableUiState: StateFlow<State> = mutableUiState
        .onStart { onInitState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds), initialState)

    val uiState: StateFlow<State> get() {
        launchOnViewModelScope { onCollectState() }
        return immutableUiState
    }

    protected open suspend fun onInitState() = Unit

    protected open suspend fun onCollectState() = Unit

    protected fun launchOnViewModelScope(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }

    protected inline fun updateState(update: (State) -> State) =
        mutableUiState.update { state -> update(state) }

    protected fun launchUpdateState(update: suspend (State) -> State) = launchOnViewModelScope {
        updateState { state -> update(state) }
    }
}
