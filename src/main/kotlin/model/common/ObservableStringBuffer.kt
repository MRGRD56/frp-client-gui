package model.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ObservableStringBuffer {
    private val buffer = StringBuffer()
    private val _state = MutableStateFlow(buffer.toString())
    val state = _state.asStateFlow()

    fun append(value: String) {
        buffer.append(value)
        _state.value = buffer.toString()
    }

    fun clear() {
        buffer.setLength(0)
        _state.value = buffer.toString()
    }

    fun get() = buffer.toString()
}