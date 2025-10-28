package com.example.lifetracker

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// data class = a single lifecycle event log entry

data class LifecycleEvent(
    val eventName: String,
    val timestamp: String,
    val eventType: EventType
)


 // categorize lifecycle events for colors

enum class EventType {
    CREATE,
    START,
    RESUME,
    PAUSE,
    STOP,
    DESTROY
}


class MainViewModel : ViewModel() {

    // list of lifecycle events
    private val _lifecycleEvents = MutableStateFlow<List<LifecycleEvent>>(emptyList())


    val lifecycleEvents: StateFlow<List<LifecycleEvent>> = _lifecycleEvents.asStateFlow()

    // current lifecycle state
    private val _currentState = MutableStateFlow("Unknown")


    val currentState: StateFlow<String> = _currentState.asStateFlow()

    // whether to show snackbar on transitions
    private val _showSnackbarOnTransition = MutableStateFlow(true)


    val showSnackbarOnTransition: StateFlow<Boolean> = _showSnackbarOnTransition.asStateFlow()

    // latest event for snackbar trigger
    private val _latestEventForSnackbar = MutableStateFlow<LifecycleEvent?>(null)


    // stateFlow for the latest event = to trigger snackbar

    val latestEventForSnackbar: StateFlow<LifecycleEvent?> = _latestEventForSnackbar.asStateFlow()


     // logs lifecycle event with timestamp

    fun logLifecycleEvent(eventName: String, eventType: EventType) {
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        val event = LifecycleEvent(eventName, timestamp, eventType)

        // Add event to list
        _lifecycleEvents.value = _lifecycleEvents.value + event

        // Update current state
        _currentState.value = eventName

        // Set latest event for snackbar
        _latestEventForSnackbar.value = event

        println("ViewModel Log: Lifecycle event logged - $eventName at $timestamp")
    }


     // clears latest event after snackbar shown

    fun clearLatestEvent() {
        _latestEventForSnackbar.value = null
    }


     // toggles snackbar notification setting

    fun toggleSnackbarSetting() {
        _showSnackbarOnTransition.value = !_showSnackbarOnTransition.value
    }


    // clears logged events

    fun clearLogs() {
        _lifecycleEvents.value = emptyList()
        println("ViewModel Log: All logs cleared")
    }

    init {
        println("ViewModel Log: MainViewModel initialized")
    }

    override fun onCleared() {
        super.onCleared()
        println("ViewModel Log: MainViewModel cleared")
    }
}