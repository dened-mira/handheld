package com.comsys.handheld.uhf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.outlined.DoorSliding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.comsys.handheld.ui.components.BrutalistMenuButton
import com.comsys.handheld.ui.theme.BrutalistColors
import com.comsys.handheld.ui.theme.BrutalistTypography

/**
 * UHF Menu Screen with options for Access Control and RFID Scanning
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UHFMenuScreen(
    onAccessControlClick: () -> Unit,
    onRFIDScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "UFH READER",
                        style = BrutalistTypography.Header,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = BrutalistColors.Black
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Access Control Button
            BrutalistMenuButton(
                icon = Icons.Outlined.DoorSliding,
                title = "ACCESOS",
                subtitle = "REGISTROS DE ENTRADA Y SALIDA",
                onClick = onAccessControlClick
            )

            // RFID Scanning Button
            BrutalistMenuButton(
                icon = Icons.Filled.Sensors,
                title = "RFID",
                subtitle = "ESCANEO DE ETIQUETAS UHF",
                onClick = onRFIDScanClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UHFMenuScreenPreview() {
    UHFMenuScreen(
        onAccessControlClick = {},
        onRFIDScanClick = {}
    )
}
