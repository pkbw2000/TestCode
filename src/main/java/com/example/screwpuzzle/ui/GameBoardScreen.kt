package com.example.screwpuzzle.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.screwpuzzle.data.GameState
import com.example.screwpuzzle.data.Hole
import com.example.screwpuzzle.data.Plate // Assuming Plate is used by PlateView
import com.example.screwpuzzle.data.Screw
import com.example.screwpuzzle.data.RevisedLevelRepository // For next level logic
import com.example.screwpuzzle.game.GameViewModel
import com.example.screwpuzzle.ui.composables.HoleView
import com.example.screwpuzzle.ui.composables.HOLE_RADIUS_DP
import com.example.screwpuzzle.ui.composables.PlateView
import com.example.screwpuzzle.ui.composables.ScrewView
import com.example.screwpuzzle.ui.composables.SCREW_HEAD_RADIUS_DP


@Composable
fun GameBoardScreen(
    gameViewModel: GameViewModel = viewModel() // Obtain ViewModel instance
) {
    val gameState by gameViewModel.gameState.collectAsState()
    val currentLevelData by gameViewModel.currentLevelData.collectAsState() // This provides the static Level object

    // Handle loading state or error state if gameState is null initially
    // or if currentLevelData (which holds static info like name, boardSize) is null
    if (gameState == null || currentLevelData == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading level data...")
            // You might want a button to explicitly load the first level if auto-load fails for some reason
            Button(onClick = { gameViewModel.loadLevel(RevisedLevelRepository.getFirstLevelId() ?: "level1") }) {
                 Text("Load First Level")
            }
        }
        return
    }

    // Now we are sure gameState and currentLevelData are not null
    val currentGameState = gameState!!
    val levelStaticData = currentLevelData!! // This is the Level object

    val boardWidthDp = with(LocalDensity.current) { levelStaticData.boardSize.width.dp }
    val boardHeightDp = with(LocalDensity.current) { levelStaticData.boardSize.height.dp }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Level: ${levelStaticData.name}", style = MaterialTheme.typography.headlineSmall)
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Moves: ${currentGameState.moves}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(end = 16.dp))
            // Score not fully implemented yet in ViewModel, but placeholder is fine
            Text("Score: ${currentGameState.score}", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))

        GameBoard(
            gameState = currentGameState, // Pass the dynamic state
            boardWidthDp = boardWidthDp,
            boardHeightDp = boardHeightDp,
            onScrewTap = { screw -> gameViewModel.onScrewTap(screw) },
            onHoleTap = { hole -> gameViewModel.onHoleTap(hole) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { gameViewModel.resetLevel() }) {
                Text("Reset Level")
            }
            if (currentGameState.isLevelComplete) {
                val nextLevelId = RevisedLevelRepository.getNextLevelId(currentGameState.currentLevelId)
                if (nextLevelId != null) {
                    Button(onClick = { gameViewModel.loadLevel(nextLevelId) }) {
                        Text("Next Level")
                    }
                } else {
                    Text("All Levels Cleared!", color = Color.Green, style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        currentGameState.message?.let {
            Text(it, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun GameBoard(
    gameState: GameState,
    boardWidthDp: Dp,
    boardHeightDp: Dp,
    onScrewTap: (Screw) -> Unit,
    onHoleTap: (Hole) -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = boardWidthDp, height = boardHeightDp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
    ) {
        // Draw Holes
        gameState.holes.values.forEach { hole ->
            val holeRadiusPx = with(LocalDensity.current) { HOLE_RADIUS_DP.toPx() }
            Box(
                modifier = Modifier.offset {
                    IntOffset(
                        (hole.position.x - holeRadiusPx).toInt(),
                        (hole.position.y - holeRadiusPx).toInt()
                    )
                }
            ) {
                HoleView(hole = hole, onHoleTap = { if (it.occupiedByScrewId == null) onHoleTap(it) })
            }
        }

        // Draw Plates, sorted by depth
        val sortedPlates = gameState.plates.values.filter { !it.isRemoved }.sortedBy { it.depth }
        sortedPlates.forEach { plate ->
            PlateView(plate = plate)
        }

        // Draw Screws
        gameState.screws.values.forEach { screw ->
            val screwRadiusPx = with(LocalDensity.current) { SCREW_HEAD_RADIUS_DP.toPx() }
            Box(
                modifier = Modifier.offset {
                    IntOffset(
                        (screw.position.x - screwRadiusPx).toInt(),
                        (screw.position.y - screwRadiusPx).toInt()
                    )
                }
            ) {
                ScrewView(
                    screw = screw,
                    isSelected = gameState.selectedScrewId == screw.id,
                    onScrewTap = onScrewTap
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun GameBoardScreenPreview_WithViewModel() {
    MaterialTheme {
        GameBoardScreen(gameViewModel = GameViewModel())
    }
}
