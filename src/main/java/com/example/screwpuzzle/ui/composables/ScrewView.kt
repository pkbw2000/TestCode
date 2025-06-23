package com.example.screwpuzzle.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.screwpuzzle.data.Screw

val SCREW_HEAD_RADIUS_DP = 10.dp
val SCREW_SLOT_LENGTH_DP = 8.dp
val SELECTED_SCREW_BORDER_DP = 2.dp
val SELECTED_SCREW_BORDER_COLOR = Color.Yellow

@Composable
fun ScrewView(
    screw: Screw,
    isSelected: Boolean,
    radius: Dp = SCREW_HEAD_RADIUS_DP,
    onScrewTap: (Screw) -> Unit
) {
    Canvas(
        modifier = Modifier
            .size(radius * 2)
            .clickable { if (!screw.isUnscrewed) onScrewTap(screw) }
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val r = radius.toPx()

        // Draw screw head
        drawCircle(
            color = if (screw.isUnscrewed) Color.LightGray.copy(alpha = 0.5f) else screw.color,
            radius = r,
            center = center
        )

        if (!screw.isUnscrewed) {
            // Draw screw slot (cross)
            val slotLength = SCREW_SLOT_LENGTH_DP.toPx()
            drawLine(
                color = Color.Black.copy(alpha = 0.6f),
                start = Offset(center.x - slotLength / 2, center.y),
                end = Offset(center.x + slotLength / 2, center.y),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = Color.Black.copy(alpha = 0.6f),
                start = Offset(center.x, center.y - slotLength / 2),
                end = Offset(center.x, center.y + slotLength / 2),
                strokeWidth = 2.dp.toPx()
            )
        }

        if (isSelected && !screw.isUnscrewed) {
            drawCircle(
                color = SELECTED_SCREW_BORDER_COLOR,
                radius = r + SELECTED_SCREW_BORDER_DP.toPx() / 2,
                center = center,
                style = Stroke(width = SELECTED_SCREW_BORDER_DP.toPx())
            )
        }
    }
}
