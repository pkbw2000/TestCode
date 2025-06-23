package com.example.screwpuzzle.data

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize

object LevelRepository {
    private val levelsList: List<Level> = listOf(
        Level(
            id = "level1",
            name = "First Steps",
            initialScrews = listOf(
                Screw(id = "s1", position = Offset(75f, 75f), initialPlateId = "p1", color = Color(0xFFBDC3C7)), // Silver
                Screw(id = "s2", position = Offset(175f, 75f), initialPlateId = "p1", color = Color(0xFFBDC3C7))
            ),
            initialPlates = listOf(
                Plate(id = "p1", screwIds = listOf("s1", "s2"), shape = PlateShape(Offset(25f, 50f), IntSize(200, 50)), depth = 0, color = Color(0xFF7F8C8D)) // Asbestos
            ),
            initialHoles = listOf(
                Hole(id = "h1", position = Offset(75f, 175f)),
                Hole(id = "h2", position = Offset(175f, 175f))
            ),
            boardSize = IntSize(250, 250) // Board dimensions (e.g., in dp or conceptual units)
        ),
        Level(
            id = "level2",
            name = "Stacked Plates",
            initialScrews = listOf(
                Screw(id = "s1_l2", position = Offset(75f, 75f), initialPlateId = "p1_l2", color = Color(0xFFBDC3C7)), // Screw for top plate
                Screw(id = "s2_l2", position = Offset(125f, 175f), initialPlateId = "p2_l2", color = Color(0xFFBDC3C7))  // Screw for bottom plate
            ),
            initialPlates = listOf(
                Plate(id = "p1_l2", screwIds = listOf("s1_l2"), shape = PlateShape(Offset(25f, 50f), IntSize(100, 50)), depth = 1, color = Color(0xFF95A5A6)), // Concrete (Top plate)
                Plate(id = "p2_l2", screwIds = listOf("s2_l2"), shape = PlateShape(Offset(75f, 150f), IntSize(100, 50)), depth = 0, color = Color(0xFF7F8C8D))  // Asbestos (Bottom plate)
            ),
            initialHoles = listOf(
                Hole(id = "h1_l2", position = Offset(75f, 250f)), // Hole for s1_l2
                Hole(id = "h2_l2", position = Offset(125f, 25f))  // Hole for s2_l2
            ),
            boardSize = IntSize(300, 300)
        ),
        Level(
            id = "level3",
            name = "Blocked Screw",
            initialScrews = listOf(
                // Screw s1_l3 for plate p1_l3 is initially accessible
                Screw(id = "s1_l3", position = Offset(50f, 50f), initialPlateId = "p1_l3", color = Color(0xFFBDC3C7)),
                // Screw s2_l3 for plate p2_l3 is under p1_l3.
                // For this to be a puzzle, s2_l3 needs to be at a location covered by p1_l3.
                // Let's adjust p1_l3 to cover s2_l3.
                Screw(id = "s2_l3", position = Offset(150f, 100f), initialPlateId = "p2_l3", color = Color(0xFFBDC3C7))
            ),
            initialPlates = listOf(
                // Plate p1_l3 is on top.
                Plate(id = "p1_l3", screwIds = listOf("s1_l3"),
                      shape = PlateShape(Offset(25f, 25f), IntSize(150, 50)), // Covers x=25 to 175, y=25 to 75
                      depth = 1, color = Color(0xFF95A5A6)),
                // Plate p2_l3 is below p1_l3. s2_l3 is at (150,100). p1_l3 needs to cover this.
                // Let's change p1_l3: shape = PlateShape(Offset(125f, 75f), IntSize(50, 50)),
                // New p1_l3:
                Plate(id = "p1_l3_revised", screwIds = listOf("s1_l3"),
                      shape = PlateShape(Offset(125f, 75f), IntSize(50, 50)), // Covers x=125 to 175, y=75 to 125. This makes s2_l3 (150,100) under it.
                      depth = 1, color = Color(0xFF95A5A6)),

                Plate(id = "p2_l3", screwIds = listOf("s2_l3"),
                      shape = PlateShape(Offset(100f, 150f), IntSize(100, 50)), // p2_l3 itself is not under p1_l3_revised
                      depth = 0, color = Color(0xFF7F8C8D))
            ),
            initialHoles = listOf(
                Hole(id = "h1_l3", position = Offset(50f, 200f)),
                Hole(id = "h2_l3", position = Offset(150f, 25f))
            ),
            boardSize = IntSize(250, 250)
        )
    )

    private val levelsMap: Map<String, Level> = levelsList.associateBy { it.id }

    fun getLevel(levelId: String): Level? = levelsMap[levelId]
    fun getFirstLevelId(): String? = levelsList.firstOrNull()?.id
    fun getNextLevelId(currentLevelId: String): String? {
        val currentIndex = levelsList.indexOfFirst { it.id == currentLevelId }
        return if (currentIndex != -1 && currentIndex < levelsList.size - 1) {
            levelsList[currentIndex + 1].id
        } else {
            null // No next level or current level not found
        }
    }
}
// Note: Level 3 design needs a rule in GameViewModel to check if a screw is covered.
// Screw s2_l3 (150,100) is covered by p1_l3_revised (rect 125,75 to 175,125)
// The `onScrewTap` in GameViewModel should prevent selecting s2_l3 until p1_l3_revised is removed.
// This logic is marked as "TODO: Advanced Accessibility Check" in GameViewModel.
// For now, the ViewModel doesn't implement this check, so level 3 won't play as intended yet.
// I will remove the revised plate and use the original one for now.
// The original p1_l3 (25,25 to 175,75) for s1_l3 (50,50)
// and p2_l3 (100,150 to 200,200) for s2_l3 (150,100) - s2_l3 is NOT covered by p1_l3.
// Let's fix Level 3 to actually have a blocked screw.
// p1_l3 (top plate) secures s1_l3.
// p2_l3 (bottom plate) secures s2_l3.
// p1_l3 must cover s2_l3.
// s1_l3 position: (50,50)
// s2_l3 position: (100,100)
// p1_l3 shape: Offset(25,25), Size(100,100) -> covers (25,25) to (125,125). This covers s2_l3.
// p2_l3 shape: Offset(75,125), Size(100,50) -> (75,125) to (175,175)
object RevisedLevelRepository {
     private val levelsList: List<Level> = listOf(
        Level(
            id = "level1",
            name = "First Steps",
            initialScrews = listOf(
                Screw(id = "s1", position = Offset(75f, 75f), initialPlateId = "p1", color = Color(0xFFBDC3C7)),
                Screw(id = "s2", position = Offset(175f, 75f), initialPlateId = "p1", color = Color(0xFFBDC3C7))
            ),
            initialPlates = listOf(
                Plate(id = "p1", screwIds = listOf("s1", "s2"), shape = PlateShape(Offset(25f, 50f), IntSize(200, 50)), depth = 0, color = Color(0xFF7F8C8D))
            ),
            initialHoles = listOf(
                Hole(id = "h1", position = Offset(75f, 175f)),
                Hole(id = "h2", position = Offset(175f, 175f))
            ),
            boardSize = IntSize(250, 250)
        ),
        Level(
            id = "level2",
            name = "Stacked Plates",
            initialScrews = listOf(
                Screw(id = "s1_l2", position = Offset(75f, 75f), initialPlateId = "p1_l2", color = Color(0xFFBDC3C7)),
                Screw(id = "s2_l2", position = Offset(125f, 175f), initialPlateId = "p2_l2", color = Color(0xFFBDC3C7))
            ),
            initialPlates = listOf(
                Plate(id = "p1_l2", screwIds = listOf("s1_l2"), shape = PlateShape(Offset(25f, 50f), IntSize(100, 50)), depth = 1, color = Color(0xFF95A5A6)),
                Plate(id = "p2_l2", screwIds = listOf("s2_l2"), shape = PlateShape(Offset(75f, 150f), IntSize(100, 50)), depth = 0, color = Color(0xFF7F8C8D))
            ),
            initialHoles = listOf(
                Hole(id = "h1_l2", position = Offset(75f, 250f)),
                Hole(id = "h2_l2", position = Offset(125f, 25f))
            ),
            boardSize = IntSize(300, 300)
        ),
        Level(
            id = "level3",
            name = "Blocked Screw",
            initialScrews = listOf(
                Screw(id = "s1_l3", position = Offset(50f, 50f), initialPlateId = "p1_l3", color = Color(0xFFBDC3C7)), // Screw for top plate
                Screw(id = "s2_l3", position = Offset(100f, 100f), initialPlateId = "p2_l3", color = Color(0xFFBDC3C7)) // Screw for bottom plate, should be covered by p1_l3
            ),
            initialPlates = listOf(
                // p1_l3 is on top and covers s2_l3.
                Plate(id = "p1_l3", screwIds = listOf("s1_l3"),
                      shape = PlateShape(Offset(25f, 25f), IntSize(100, 100)), // Covers region from (25,25) to (125,125). s2_l3 at (100,100) is within this.
                      depth = 1, color = Color(0xFF95A5A6)), // Top plate, higher depth
                // p2_l3 is the bottom plate. Its screw s2_l3 is at (100,100).
                Plate(id = "p2_l3", screwIds = listOf("s2_l3"),
                      shape = PlateShape(Offset(75f, 150f), IntSize(100, 50)), // Plate itself is not under p1_l3, but its screw is.
                      depth = 0, color = Color(0xFF7F8C8D))  // Bottom plate, lower depth
            ),
            initialHoles = listOf(
                Hole(id = "h1_l3", position = Offset(50f, 200f)), // Hole for s1_l3
                Hole(id = "h2_l3", position = Offset(100f, 225f)) // Hole for s2_l3
            ),
            boardSize = IntSize(250, 250)
        )
    )

    private val levelsMap: Map<String, Level> = levelsList.associateBy { it.id }

    fun getLevel(levelId: String): Level? = levelsMap[levelId]
    fun getFirstLevelId(): String? = levelsList.firstOrNull()?.id
    fun getNextLevelId(currentLevelId: String): String? {
        val currentIndex = levelsList.indexOfFirst { it.id == currentLevelId }
        return if (currentIndex != -1 && currentIndex < levelsList.size - 1) {
            levelsList[currentIndex + 1].id
        } else {
            null
        }
    }
}
