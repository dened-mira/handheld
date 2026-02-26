package com.comsys.handheld

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.comsys.acces_hardware.isapi.AccessController
import com.comsys.handheld.ui.components.BrutalistBottomNavigation
import com.comsys.handheld.ui.components.BrutalistNavItem
import com.comsys.handheld.ui.theme.HandheldTheme
import com.comsys.handheld.uhf.AccessControlScreen
import com.comsys.handheld.uhf.UHFManager
import com.comsys.handheld.uhf.UHFMenuScreen
import com.comsys.handheld.uhf.UHFScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HandheldTheme {
                HandheldApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun HandheldApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.UHF_READER) }
    var uhfScreen by rememberSaveable { mutableStateOf(UHFScreens.MENU) }

    val navItems = AppDestinations.entries.map { destination ->
        BrutalistNavItem(
            icon = destination.icon,
            label = destination.label,
            route = destination.name
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BrutalistBottomNavigation(
                items = navItems,
                selectedRoute = currentDestination.name,
                onItemClick = { route ->
                    currentDestination = AppDestinations.valueOf(route)
                    if (currentDestination == AppDestinations.UHF_READER) {
                        uhfScreen = UHFScreens.MENU
                    }
                },
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        when (currentDestination) {
            AppDestinations.UHF_READER -> {
                when (uhfScreen) {
                    UHFScreens.MENU -> UHFMenuScreen(
                        onAccessControlClick = { uhfScreen = UHFScreens.ACCESS_CONTROL },
                        onRFIDScanClick = { uhfScreen = UHFScreens.SCAN },
                        modifier = Modifier.padding(innerPadding)
                    )
                    UHFScreens.ACCESS_CONTROL -> AccessControlScreen(
                        onBackClick = { uhfScreen = UHFScreens.MENU },
                        modifier = Modifier.padding(innerPadding)
                    )
                    UHFScreens.SCAN -> {
                        val context = LocalContext.current
                        val uhfManager = remember { UHFManager.getInstance() }
                        val accessController = remember { AccessController() }
                        val scope = rememberCoroutineScope()
                        val snackbarHostState = remember { SnackbarHostState() }

                        val tags by uhfManager.tags.collectAsState()
                        val status by uhfManager.status.collectAsState()
                        val isConnected by uhfManager.isConnected.collectAsState()
                        var isScanning by remember { mutableStateOf(false) }

                        LaunchedEffect(tags.size) {
                            if (isScanning && tags.size == 1) {
                                isScanning = false
                                uhfManager.stopInventory()

                                val tag = tags.first()
                                Log.d("AccessController", "Tag read: ${tag.epc} — triggering openDoor()")
                                val result = accessController.openDoor()
                                Log.d("AccessController", "openDoor result: $result")
                                result.fold(
                                    onSuccess = { snackbarHostState.showSnackbar("✓ PUERTA ABIERTA [${tag.epc}]", actionLabel = "OK", duration = SnackbarDuration.Long) },
                                    onFailure = {
                                        Log.e("AccessController", "openDoor failed after tag read", it)
                                        snackbarHostState.showSnackbar("✗ $it", actionLabel = "OK", duration = SnackbarDuration.Indefinite)
                                    }
                                )
                            }
                        }

                        UHFScreen(
                            tags = tags,
                            status = status,
                            isConnected = isConnected,
                            isScanning = isScanning,
                            snackbarHostState = snackbarHostState,
                            onBackClick = { uhfScreen = UHFScreens.MENU },
                            onConnectClick = {
                                scope.launch {
                                    val result = if (isConnected) uhfManager.disconnect()
                                                 else uhfManager.initialize(context)
                                    result.fold(
                                        onSuccess = { snackbarHostState.showSnackbar("✓ $it", actionLabel = "OK", duration = SnackbarDuration.Short) },
                                        onFailure = { snackbarHostState.showSnackbar("✗ ${it.message}", actionLabel = "OK", duration = SnackbarDuration.Indefinite) }
                                    )
                                }
                            },
                            onScanClick = { currentlyScanning ->
                                scope.launch {
                                    if (currentlyScanning) {
                                        isScanning = false
                                        uhfManager.stopInventory()
                                    } else {
                                        uhfManager.clearTags()
                                        isScanning = true
                                        val result = uhfManager.startInventory()
                                        result.onFailure {
                                            isScanning = false
                                            snackbarHostState.showSnackbar("✗ ${it.message}", actionLabel = "OK", duration = SnackbarDuration.Indefinite)
                                        }
                                    }
                                }
                            },
                            onClearClick = { uhfManager.clearTags() },
                            onOpenDoorClick = {
                                Log.d("AccessController", "Button tapped — launching openDoor()")
                                scope.launch {
                                    Log.d("AccessController", "Calling openDoor() on door 1")
                                    val result = accessController.openDoor()
                                    Log.d("AccessController", "Result: $result")
                                    result.fold(
                                        onSuccess = { snackbarHostState.showSnackbar("✓ PUERTA ABIERTA\n$it", actionLabel = "OK", duration = SnackbarDuration.Long) },
                                        onFailure = {
                                            Log.e("AccessController", "openDoor failed", it)
                                            snackbarHostState.showSnackbar("✗ ${it}", actionLabel = "OK", duration = SnackbarDuration.Indefinite)
                                        }
                                    )
                                }
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
            AppDestinations.FAVORITES -> Greeting(
                name = "Ajustes",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

enum class UHFScreens { MENU, ACCESS_CONTROL, SCAN }

enum class AppDestinations(val label: String, val icon: ImageVector) {
    UHF_READER("UHF Reader", Icons.Filled.Star),
    FAVORITES("Ajustes", Icons.Filled.Settings),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HandheldTheme { Greeting("Android") }
}
