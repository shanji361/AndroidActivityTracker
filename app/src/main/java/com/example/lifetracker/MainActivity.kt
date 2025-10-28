package com.example.lifetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.lifetracker.ui.theme.LifeTrackerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // lifecycle observer
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                viewModel.logLifecycleEvent("onCreate", EventType.CREATE)
            }

            override fun onStart(owner: LifecycleOwner) {
                viewModel.logLifecycleEvent("onStart", EventType.START)
            }

            override fun onResume(owner: LifecycleOwner) {
                viewModel.logLifecycleEvent("onResume", EventType.RESUME)
            }

            override fun onPause(owner: LifecycleOwner) {
                viewModel.logLifecycleEvent("onPause", EventType.PAUSE)
            }

            override fun onStop(owner: LifecycleOwner) {
                viewModel.logLifecycleEvent("onStop", EventType.STOP)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                viewModel.logLifecycleEvent("onDestroy", EventType.DESTROY)
            }
        })

        enableEdgeToEdge()
        setContent {
            LifeTrackerTheme {
                LifeTrackerScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeTrackerScreen(viewModel: MainViewModel) {
    val lifecycleEvents by viewModel.lifecycleEvents.collectAsState()
    val currentState by viewModel.currentState.collectAsState()
    val showSnackbar by viewModel.showSnackbarOnTransition.collectAsState()
    val latestEvent by viewModel.latestEventForSnackbar.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // show snackbar when theres new event
    LaunchedEffect(latestEvent) {
        latestEvent?.let { event ->
            if (showSnackbar) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Lifecycle: ${event.eventName}",
                        duration = SnackbarDuration.Short
                    )
                    viewModel.clearLatestEvent()
                }
            } else {
                viewModel.clearLatestEvent()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("LifeTracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // current state indicator here
            CurrentStateCard(currentState = currentState)

            Spacer(modifier = Modifier.height(16.dp))

            // show snackbar and clear button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = showSnackbar,
                        onCheckedChange = { viewModel.toggleSnackbarSetting() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Show Snackbar", fontSize = 14.sp)
                }

                Button(
                    onClick = { viewModel.clearLogs() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("Clear Logs")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // log header
            Text(
                text = "Lifecycle Events Log (${lifecycleEvents.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // event log list
            if (lifecycleEvents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No events logged yet...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                }
            } else {
                val listState = rememberLazyListState()

                // scroll to bottom when theres new item
                LaunchedEffect(lifecycleEvents.size) {
                    if (lifecycleEvents.isNotEmpty()) {
                        listState.animateScrollToItem(lifecycleEvents.size - 1)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lifecycleEvents) { event ->
                        EventLogItem(event = event)
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentStateCard(currentState: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Current State",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = currentState,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun EventLogItem(event: LifecycleEvent) {
    val backgroundColor = when (event.eventType) {
        EventType.CREATE -> Color(0xFF4CAF50)   // Green
        EventType.START -> Color(0xFF2196F3)    // Blue
        EventType.RESUME -> Color(0xFF8BC34A)   // Light Green
        EventType.PAUSE -> Color(0xFFFFC107)    // Amber
        EventType.STOP -> Color(0xFFFF9800)     // Orange
        EventType.DESTROY -> Color(0xFFF44336)  // Red
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(backgroundColor, RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // event time and name
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.eventName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = event.timestamp,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // status
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
            )
        }
    }
}