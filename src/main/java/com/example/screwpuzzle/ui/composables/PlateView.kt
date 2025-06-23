package com.example.screwpuzzle.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.screwpuzzle.data.Plate
import com.example.screwpuzzle.data.PlateShape

val PLATE_CORNER_RADIUS_DP = 8.dp

@Composable
fun PlateView(
    plate: Plate,
    // The PlateView itself doesn't need to be clickable directly in the current design
    // Interactions are primarily with screws.
) {
    if (plate.isRemoved) return // Don't draw if removed

    val density = LocalDensity.current
    val plateShape = plate.shape // Assuming PlateShape position is in Dp or similar units

    // Convert PlateShape position and size to Dp if they are not already
    // For now, assuming they are effectively in Dp-like units for Modifier.offset
    // If shape.position is absolute board pixels, conversion will be needed in GameBoard
    val offsetX = with(density) { plateShape.position.x.dp }
    val offsetY = with(density) { plateShape.position.y.dp }
    val widthDp = with(density) { plateShape.size.width.dp }
    val heightDp = with(density) { plateShape.size.height.dp }

    Canvas(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToPx(), offsetY.roundToPx()) } // Position the canvas itself
            .size(widthDp, heightDp) // Size of the canvas
    ) {
        drawPlateShape(plate, plateShape)
    }
}

private fun DrawScope.drawPlateShape(plate: Plate, shape: PlateShape) {
    // Drawing is relative to the Canvas, so use (0,0) up to (size.width, size.height)
    drawRoundRect(
        color = plate.color,
        topLeft = Offset.Zero, // Draw at the top-left of the Canvas
        size = Size(size.width, size.height), // Use the full size of the Canvas
        cornerRadius = CornerRadius(PLATE_CORNER_RADIUS_DP.toPx())
    )
    // Add shadows or highlights for a more metallic look if desired
    // e.g., draw another slightly offset darker rect underneath for shadow
}
