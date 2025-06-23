package com.example.screwpuzzle.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.screwpuzzle.data.Hole

val HOLE_RADIUS_DP = 12.dp // Slightly larger than screw head for visual fit
val HOLE_COLOR = Color.Black.copy(alpha = 0.7f)

@Composable
fun HoleView(
    hole: Hole,
    radius: Dp = HOLE_RADIUS_DP,
    onHoleTap: (Hole) -> Unit
) {
    Canvas(
        modifier = Modifier
            .size(radius * 2)
            .clickable { onHoleTap(hole) }
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        drawCircle(
            color = HOLE_COLOR,
            radius = radius.toPx(),
            center = center
        )
        // Optionally, add a border or highlight if it's a target for a selected screw
    }
}
