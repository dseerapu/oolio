package com.oolio.pos.eventbus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class EventBus( private val scope : CoroutineScope) {
    private val _events = MutableSharedFlow<Any>(extraBufferCapacity = 64)
    val events: SharedFlow<Any> = _events.asSharedFlow()

    fun post(event:Any){
        scope.launch {
            _events.emit(event)
        }
    }

    inline fun <reified  T> subscribe(
        scope: CoroutineScope,
        noinline block: suspend (T) -> Unit
    ){
        events.filterIsInstance<T>().onEach { block(it) }.launchIn(scope)
    }
}