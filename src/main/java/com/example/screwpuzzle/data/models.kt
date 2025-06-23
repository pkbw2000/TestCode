package com.example.screwpuzzle.data

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.graphics.Color // Added for potential use in Screw/Plate

// Represents a screw in the game
data class Screw(
    val id: String,
    var position: Offset, // Current position on the board (either on a plate or in a hole)
    var isUnscrewed: Boolean = false, // True if the screw is in a hole, not securing a plate
    val initialPlateId: String? = null, // ID of the plate it initially secures
    val color: Color = Color.Gray // Default color, can be customized per screw
)

// Represents a metal plate or object to be removed
data class Plate(
    val id: String,
    val screwIds: List<String>, // IDs of screws holding this plate
    val shape: PlateShape, // Defines the visual appearance and boundaries
    val depth: Int, // Layering order, higher numbers are on top
    var isRemoved: Boolean = false, // True if the plate has been removed from the board
    val color: Color = Color.LightGray // Default color, can be customized per plate
)

// Defines the shape of a plate.
data class PlateShape(
    val position: Offset, // Top-left position of the plate relative to board origin
    val size: IntSize      // Width and height of the plate
) {
    // Helper to get the bounding box as a Rect, useful for drawing and hit detection
    fun getBoundingRect(): androidx.compose.ui.geometry.Rect {
        return androidx.compose.ui.geometry.Rect(position, Offset(position.x + size.width.toFloat(), position.y + size.height.toFloat()))
    }
}

// Represents an empty hole on the board where a screw can be placed
data class Hole(
    val id: String,
    val position: Offset, // Center position of the hole
    var occupiedByScrewId: String? = null // ID of the screw currently in this hole, if any
)

// Represents a single level configuration
data class Level(
    val id: String,
    val name: String,
    val initialScrews: List<Screw>, // Initial configuration of screws
    val initialPlates: List<Plate>, // Initial configuration of plates
    val initialHoles: List<Hole>,   // Configuration of holes
    val boardSize: IntSize // Defines the playable area dimensions (e.g., in grid units or pixels)
)

// Represents the overall state of the game, to be managed by a ViewModel
data class GameState(
    val currentLevelId: String,
    val screws: Map<String, Screw>, // Current state of all screws (ID -> Screw)
    val plates: Map<String, Plate>, // Current state of all plates (ID -> Plate)
    val holes: Map<String, Hole>,   // Current state of all holes (ID -> Hole)
    val selectedScrewId: String? = null,
    val score: Int = 0,
    val moves: Int = 0,
    val isLevelComplete: Boolean = false,
    val message: String? = null // For displaying messages like "Level Complete!"
)
