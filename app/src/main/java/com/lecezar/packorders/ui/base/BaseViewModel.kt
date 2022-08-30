package com.lecezar.packorders.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel<Event> : ViewModel(), CoroutineScope {
    override val coroutineContext = SupervisorJob() + Dispatchers.Main

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val errorQueue = MutableSharedFlow<Throwable>()

    private var onError: ((error: Throwable) -> Unit)? = null

    init {
        launch {
            errorQueue.collect {
                onError?.invoke(it)
            }
        }
    }

    fun load(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ) {
        launch(context) {
            try {
                _loading.emit(true)
                block()
            } catch (e: Throwable) {
                emitError(e)
            } finally {
                _loading.emit(false)
            }
        }
    }

    fun execute(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ) {
        launch(context) {
            try {
                block()
            } catch (e: Throwable) {
                emitError(e)
            } finally {
            }
        }
    }

    fun errorHandler(block: (event: Throwable) -> Unit) {
        onError = block
    }

    protected fun emitError(error: Throwable) {
        launch {
            errorQueue.emit(error)
        }
    }

    @CallSuper
    override fun onCleared() {
        coroutineContext.cancel()
        super.onCleared()
    }

    suspend fun <T> Flow<T>.collectSafely(
        block: suspend (value: T) -> Unit = {}
    ) {
        try {
            this.collect {
                block(it)
            }
        } catch (e: Exception) {
            emitError(e)
        }
    }

    suspend fun <T> Flow<T>.collectLoading(
        loader: MutableStateFlow<Boolean>? = null,
        block: suspend (value: T) -> Unit = {}
    ) {
        try {
            this.onStart {
                loader?.update { true } ?: _loading.update { true }
            }.collect {
                block(it)
                if (_loading.value) {
                    loader?.update { false } ?: _loading.update { false }
                }

            }
        } catch (e: Exception) {
            emitError(e)
            if (_loading.value) {
                loader?.update { false } ?: _loading.update { false }
            }
        }
    }

}