package com.example.screwpuzzle.game

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.screwpuzzle.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// import com.example.screwpuzzle.data.LevelRepository // Ensure this matches the object name if it's different
// If LevelDefinitions.kt created `RevisedLevelRepository`, use that:
import com.example.screwpuzzle.data.RevisedLevelRepository as LevelRepository


class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    // To store the original level definition for reset and for board size etc.
    private val _currentLevelData = MutableStateFlow<Level?>(null)
    val currentLevelData: StateFlow<Level?> = _currentLevelData.asStateFlow()


    init {
        LevelRepository.getFirstLevelId()?.let {
            loadLevel(it)
        }
    }

    fun loadLevel(levelId: String) {
        viewModelScope.launch {
            val level = LevelRepository.getLevel(levelId)
            _currentLevelData.value = level
            level?.let {
                _gameState.value = GameState(
                    currentLevelId = it.id,
                    // Deep copy mutable elements to prevent modification of the templates in LevelRepository
                    screws = it.initialScrews.associateBy { s -> s.id }.mapValues { entry -> entry.value.copy() }.toMutableMap(),
                    plates = it.initialPlates.associateBy { p -> p.id }.mapValues { entry -> entry.value.copy() }.toMutableMap(),
                    holes = it.initialHoles.associateBy { h -> h.id }.mapValues { entry -> entry.value.copy() }.toMutableMap(),
                    selectedScrewId = null,
                    moves = 0,
                    score = 0, // Implement scoring later
                    isLevelComplete = false,
                    message = "Level: ${it.name}"
                )
            }
        }
    }

    fun resetLevel() {
        _gameState.value?.currentLevelId?.let { loadLevel(it) }
    }

    fun onScrewTap(tappedScrew: Screw) {
        _gameState.update { currentState ->
            if (currentState == null || tappedScrew.isUnscrewed) return@update currentState

            // Accessibility Check (Basic: is the screw part of an already removed plate?)
            val plateOfScrew = currentState.plates.values.find { it.screwIds.contains(tappedScrew.id) }
            if (plateOfScrew?.isRemoved == true) { // Should not happen if screw.isUnscrewed is false
                 return@update currentState.copy(message = "Screw's plate already removed.")
            }

            // TODO: Advanced Accessibility Check: Is the screw head covered by another plate?
            // This involves checking if any other plate P' (not plateOfScrew) with P'.depth > plateOfScrew.depth
            // visually covers the screw's position. For simplicity, this is omitted for now.

            if (currentState.selectedScrewId == tappedScrew.id) {
                currentState.copy(selectedScrewId = null, message = "Screw deselected.")
            } else {
                currentState.copy(selectedScrewId = tappedScrew.id, message = "Screw ${tappedScrew.id} selected.")
            }
        }
    }

    fun onHoleTap(tappedHole: Hole) {
        _gameState.update { currentState ->
            if (currentState == null) return@update null
            val currentSelectedScrewId = currentState.selectedScrewId
            if (currentSelectedScrewId == null) {
                return@update currentState.copy(message = "Select a screw first.")
            }
            if (tappedHole.occupiedByScrewId != null) {
                return@update currentState.copy(message = "Hole is occupied by ${tappedHole.occupiedByScrewId}.")
            }

            val selectedScrew = currentState.screws[currentSelectedScrewId]
                ?: return@update currentState.copy(message = "Error: Selected screw not found.", selectedScrewId = null)

            // --- Perform the move ---
            val updatedScrews = currentState.screws.toMutableMap()
            val updatedHoles = currentState.holes.toMutableMap()

            val movedScrew = selectedScrew.copy(
                position = tappedHole.position,
                isUnscrewed = true
            )
            updatedScrews[currentSelectedScrewId] = movedScrew

            val updatedTappedHole = tappedHole.copy(occupiedByScrewId = currentSelectedScrewId)
            updatedHoles[tappedHole.id] = updatedTappedHole

            var message = "Screw $currentSelectedScrewId moved to hole ${tappedHole.id}."
            val newMoves = currentState.moves + 1

            // --- Check for plate removal ---
            val updatedPlates = currentState.plates.toMutableMap()
            currentLevelDefinition?.initialPlates?.forEach { initialPlate ->
                 val currentPlateState = updatedPlates[initialPlate.id] ?: return@forEach
                 if (currentPlateState.isRemoved) return@forEach

                 val allScrewsUnscrewed = initialPlate.screwIds.all { screwId ->
                     updatedScrews[screwId]?.isUnscrewed == true
                 }
                 if (allScrewsUnscrewed) {
                     updatedPlates[initialPlate.id] = currentPlateState.copy(isRemoved = true)
                     message += "\nPlate ${initialPlate.id} removed!"
                 }
            }

            // --- Check for win condition ---
            val allPlatesNowRemoved = updatedPlates.values.all { it.isRemoved }
            var levelNowComplete = false
            if (allPlatesNowRemoved) {
                message += "\nLevel Complete! Moves: $newMoves"
                levelNowComplete = true
                // TODO: Trigger next level load or victory screen
            }

            currentState.copy(
                screws = updatedScrews,
                holes = updatedHoles,
                plates = updatedPlates,
                selectedScrewId = null,
                moves = newMoves,
                isLevelComplete = levelNowComplete,
                message = message
            )
        }
    }
}
