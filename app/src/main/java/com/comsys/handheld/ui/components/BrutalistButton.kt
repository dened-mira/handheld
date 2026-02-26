package com.comsys.handheld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.comsys.handheld.ui.theme.BrutalistColors
import com.comsys.handheld.ui.theme.BrutalistTypography

/**
 * Brutalist menu button component
 *
 * Features:
 * - Large icon at the top
 * - Title text below icon
 * - Subtitle text below title
 * - Transparent background normally
 * - Highlights when pressed
 * - Heavy black borders
 *
 * @param icon The icon to display
 * @param title The main title text
 * @param subtitle The subtitle text
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for the component
 * @param pressedBackgroundColor Background color when pressed
 */
@Composable
fun BrutalistMenuButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    pressedBackgroundColor: Color = BrutalistColors.BrightYellow
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val offsetX = 2.dp.toPx()
                val offsetY = 2.dp.toPx()
                val cornerRadius = 16.dp.toPx()
                
                drawRoundRect(
                    color = Color.Black,
                    topLeft = Offset(offsetX, offsetY),
                    size = size,
                    cornerRadius = CornerRadius(cornerRadius)
                )
            }
            .background(
                if (isPressed) pressedBackgroundColor else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = modifier
                    .background(
                        color = if (isPressed) BrutalistColors.DarkBlue else BrutalistColors.IconBoxBgGray,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        color = if (isPressed) BrutalistColors.BorderBlue else BrutalistColors.BorderGray,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(56.dp),
                    tint = if (isPressed) Color.White else Color.Black
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = title.uppercase(),
                    style = BrutalistTypography.Header,
                    color = if (isPressed) BrutalistColors.White else BrutalistColors.Black,
                    textAlign = TextAlign.Center,
                    // modifier = Modifier.padding(top = 16.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle.uppercase(),
                    style = BrutalistTypography.NavLabel,
                    color = if (isPressed) BrutalistColors.PressedTextGray else BrutalistColors.TextGray,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BrutalistMenuButtonPreview() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BrutalistMenuButton(
            icon = Icons.Filled.Person,
            title = "ACCESOS",
            subtitle = "REGISTROS DE ENTRADA Y SALIDA",
            onClick = {}
        )

        BrutalistMenuButton(
            icon = Icons.Filled.Star,
            title = "RFID",
            subtitle = "ESCANEO DE ETIQUETAS UHF",
            onClick = {}
        )
    }
}
