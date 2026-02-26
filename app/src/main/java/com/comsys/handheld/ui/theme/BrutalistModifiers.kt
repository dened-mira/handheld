package com.comsys.handheld.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Size

/**
 * Brutalist style with regular border and offset shadow
 * - Regular border around the element
 * - Shadow strips on bottom and right edges only
 * - Creates classic brutalist 3D effect
 */
fun Modifier.brutalistBorder(
    borderColor: Color = BrutalistColors.Black,
    shadowColor: Color = BrutalistColors.Black,
    borderWidth: Dp = 2.dp,
    shadowOffset: Dp = 5.dp,
    cornerRadius: Dp = 0.dp
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .drawWithContent {
        val borderPx = borderWidth.toPx()
        val offsetPx = shadowOffset.toPx()
        val radiusPx = cornerRadius.toPx()
        val corner = CornerRadius(radiusPx, radiusPx)

        drawContent()

        drawRoundRect(
            color = shadowColor,
            topLeft = Offset(offsetPx, size.height),
            size = Size(size.width, offsetPx),
            cornerRadius = corner
        )

        drawRoundRect(
            color = shadowColor,
            topLeft = Offset(size.width, offsetPx),
            size = Size(offsetPx, size.height),
            cornerRadius = corner
        )

        drawRoundRect(
            color = borderColor,
            topLeft = Offset(borderPx / 2, borderPx / 2),
            size = Size(size.width - borderPx, size.height - borderPx),
            cornerRadius = corner,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = borderPx)
        )
    }
