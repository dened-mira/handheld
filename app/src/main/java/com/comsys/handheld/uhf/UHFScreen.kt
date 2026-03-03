package com.comsys.handheld.uhf

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.comsys.handheld.ui.components.BrutalistLabeledTextBox
import com.comsys.handheld.ui.theme.BrutalistColors
import com.comsys.handheld.ui.theme.BrutalistTypography
import com.comsys.handheld.ui.theme.brutalistBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UHFScreen(
    tags: List<UHFManager.TagInfo>,
    status: String,
    isConnected: Boolean,
    isScanning: Boolean,
    onBackClick: () -> Unit,
    onConnectClick: () -> Unit,
    onScanClick: (isScanning: Boolean) -> Unit,
    onClearClick: () -> Unit,
    onOpenDoorClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf<UHFManager.TagInfo?>(null) }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "RFID SCANNER",
                        style = BrutalistTypography.Header
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BrutalistColors.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = BrutalistColors.Black
                ),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Connection Status Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isConnected) BrutalistColors.BrightGreen
                        else BrutalistColors.BrightRed
                    )
                    .brutalistBorder()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isConnected) "CONECTADO" else "DESCONECTADO",
                            style = BrutalistTypography.ButtonLabel,
                            color = BrutalistColors.Black
                        )
                        Text(
                            text = status.uppercase(),
                            style = BrutalistTypography.NavLabel,
                            color = BrutalistColors.Black
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrutalistColors.Black)
                            .brutalistBorder()
                            .clickable { onConnectClick() }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = if (isConnected) "DESCONECTAR" else "CONECTAR",
                            style = BrutalistTypography.ButtonLabel,
                            color = BrutalistColors.White
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Scan/Stop Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isScanning) BrutalistColors.BrightRed
                            else if (isConnected) BrutalistColors.BrightYellow
                            else BrutalistColors.White
                        )
                        .brutalistBorder()
                        .clickable(enabled = isConnected) {
                            onScanClick(isScanning)
                        }
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isScanning) "DETENER" else "ESCANEAR",
                        style = BrutalistTypography.ButtonLabel,
                        color = BrutalistColors.Black
                    )
                }
            }

            // Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // Clear Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (!isScanning && tags.isNotEmpty())
                                BrutalistColors.White
                            else BrutalistColors.White.copy(alpha = 0.5f)
                        )
                        .brutalistBorder()
                        .clickable(enabled = !isScanning && tags.isNotEmpty()) {
                            onClearClick()
                        }
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LIMPIAR",
                        style = BrutalistTypography.ButtonLabel,
                        color = BrutalistColors.Black
                    )
                }
            }

            // Tags Counter
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrutalistColors.Black)
                    .brutalistBorder()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ETIQUETAS: ${tags.size}",
                        style = BrutalistTypography.Header,
                        color = BrutalistColors.White
                    )
                    if (isScanning) {
                        Text(
                            text = "[ ESCANEANDO... ]",
                            style = BrutalistTypography.NavLabel,
                            color = BrutalistColors.BrightYellow
                        )
                    }
                }
            }

            // Tags List
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(BrutalistColors.White)
                    .brutalistBorder()
            ) {
                if (tags.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "[ NO HAY ETIQUETAS ]",
                            style = BrutalistTypography.Header,
                            color = BrutalistColors.Black.copy(alpha = 0.3f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tags, key = { it.epc }) { tag ->
                            BrutalistTagCard(
                                tag = tag,
                                onClick = {
                                    selectedTag = tag
                                    showDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Tag Details Dialog
    if (showDialog && selectedTag != null) {
        BrutalistTagDetailsDialog(
            tag = selectedTag!!,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun BrutalistTagCard(
    tag: UHFManager.TagInfo,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrutalistColors.White)
            .brutalistBorder()
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "EPC:", style = BrutalistTypography.NavLabel, color = BrutalistColors.Black)
                Text(text = tag.epc, style = BrutalistTypography.NavLabel, color = BrutalistColors.Black)
            }

            if (tag.tid.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "TID:", style = BrutalistTypography.NavLabel, color = BrutalistColors.Black)
                    Text(text = tag.tid, style = BrutalistTypography.NavLabel, color = BrutalistColors.Black)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "RSSI: ${tag.rssi}", style = BrutalistTypography.NavLabel, color = BrutalistColors.Black)
                Text(text = "×${tag.count}", style = BrutalistTypography.NavLabel, color = BrutalistColors.Black)
            }
        }
    }
}

@Composable
fun BrutalistTagDetailsDialog(
    tag: UHFManager.TagInfo,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrutalistColors.White)
                .brutalistBorder()
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrutalistColors.BrightYellow)
                        .brutalistBorder()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "DETALLES DE ETIQUETA",
                        style = BrutalistTypography.Header,
                        color = BrutalistColors.Black
                    )
                }

                BrutalistLabeledTextBox(label = "EPC", text = tag.epc)
                if (tag.tid.isNotEmpty()) {
                    BrutalistLabeledTextBox(label = "TID", text = tag.tid)
                }
                BrutalistLabeledTextBox(label = "RSSI", text = tag.rssi)
                BrutalistLabeledTextBox(label = "LECTURAS", text = tag.count.toString())

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrutalistColors.Black)
                        .brutalistBorder()
                        .clickable(onClick = onDismiss)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "CERRAR", style = BrutalistTypography.ButtonLabel, color = BrutalistColors.White)
                }
            }
        }
    }
}

// --- Previews ---

private val sampleTags = listOf(
    UHFManager.TagInfo(epc = "E2000017211100000000000A", tid = "E2801160600002061234ABCD", rssi = "-65", count = 3),
    UHFManager.TagInfo(epc = "E2000017211100000000000B", tid = "", rssi = "-72", count = 1),
    UHFManager.TagInfo(epc = "E2000017211100000000000C", tid = "E2801160600002061234ABCE", rssi = "-58", count = 7),
)

@Preview(showBackground = true, name = "Screen - Empty / Disconnected")
@Composable
private fun UHFScreenEmptyPreview() {
    UHFScreen(
        tags = emptyList(),
        status = "Not initialized",
        isConnected = false,
        isScanning = false,
        onBackClick = {},
        onConnectClick = {},
        onScanClick = {},
        onClearClick = {}
    )
}

@Preview(showBackground = true, name = "Screen - Connected with tags")
@Composable
private fun UHFScreenWithTagsPreview() {
    UHFScreen(
        tags = sampleTags,
        status = "Ready",
        isConnected = true,
        isScanning = false,
        onBackClick = {},
        onConnectClick = {},
        onScanClick = {},
        onClearClick = {}
    )
}

@Preview(showBackground = true, name = "Screen - Scanning")
@Composable
private fun UHFScreenScanningPreview() {
    UHFScreen(
        tags = sampleTags,
        status = "Scanning...",
        isConnected = true,
        isScanning = true,
        onBackClick = {},
        onConnectClick = {},
        onScanClick = {},
        onClearClick = {}
    )
}

@Preview(showBackground = true, name = "Tag Card")
@Composable
private fun BrutalistTagCardPreview() {
    BrutalistTagCard(tag = sampleTags.first(), onClick = {})
}

@Preview(showBackground = true, name = "Tag Details Dialog")
@Composable
private fun BrutalistTagDetailsDialogPreview() {
    BrutalistTagDetailsDialog(tag = sampleTags.first(), onDismiss = {})
}
