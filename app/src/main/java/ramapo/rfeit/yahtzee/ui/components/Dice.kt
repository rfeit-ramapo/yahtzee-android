package ramapo.rfeit.yahtzee.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ramapo.rfeit.yahtzee.R
import ramapo.rfeit.yahtzee.viewmodel.GameViewModel

@Composable
fun DiceSet(
    gameViewModel: GameViewModel = GameViewModel(null),
    isSelectable: Boolean = false // Allows user to select dice
) {
    // Get dice list that updates
    val diceList = gameViewModel.diceFaces.observeAsState().value!!
    // Get locked dice counts that update - each count represents (index + 1) face counts
    val lockedCounts = gameViewModel.lockedDice.observeAsState().value!!

    // Create a mutable copy of lockedCounts to decrement as dice are matched
    val lockedCountsCopy = lockedCounts.toMutableList()

    // Generate a list indicating if each die is locked
    val isLockedList = diceList.map { dieValue ->
        val faceIndex = dieValue - 1 // Index corresponding to the die face
        if (faceIndex in lockedCountsCopy.indices && lockedCountsCopy[faceIndex] > 0) {
            lockedCountsCopy[faceIndex]-- // Decrement the count for this face
            true // Mark this die as locked
        } else {
            false // Not locked
        }
    }

    // Get the selected dice from the gameViewModel
    val isSelectedList = gameViewModel.selectedDice.observeAsState().value!!

    // Row of dice
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Iterate over the dice list to render each Die component
        diceList.forEachIndexed { index, dieValue ->
            val isLocked = isLockedList.getOrElse(index) { false } // Default to unlocked if index is out of bounds
            val isSelected = isSelectedList.getOrElse(index) { false }

            // If isSelectable, only pass selectable = true for unlocked dice
            val selectable = isSelectable && !isLocked

            Die(
                value = dieValue,
                locked = isLocked,
                selected = isSelected,
                selectable = selectable,
                onClick = {
                    // If selectable, toggle the selection state in the ViewModel
                    if (selectable) {
                        gameViewModel.toggleSelectedDie(index)
                    }
                }
            )
        }
    }
}

@Composable
fun Die(
    value: Int = 1,
    locked: Boolean = false,
    selected: Boolean = false,
    selectable: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val drawFile =
        if (locked) {
            when (value) {
                1 -> R.drawable.ace_locked
                2 -> R.drawable.two_locked
                3 -> R.drawable.three_locked
                4 -> R.drawable.four_locked
                5 -> R.drawable.five_locked
                6 -> R.drawable.six_locked
                else -> R.drawable.error_die
            }
        } else if (selected) {
            when (value) {
                1 -> R.drawable.ace_selected
                2 -> R.drawable.two_selected
                3 -> R.drawable.three_selected
                4 -> R.drawable.four_selected
                5 -> R.drawable.five_selected
                6 -> R.drawable.six_selected
                else -> R.drawable.error_die
            }
        } else {
            when (value) {
                1 -> R.drawable.ace_unlocked
                2 -> R.drawable.two_unlocked
                3 -> R.drawable.three_unlocked
                4 -> R.drawable.four_unlocked
                5 -> R.drawable.five_unlocked
                6 -> R.drawable.six_unlocked
                else -> R.drawable.error_die
            }
        }

    Image(
        painter = painterResource(drawFile),
        contentDescription = null,
        modifier = Modifier
            .size(75.dp)
            .padding(2.dp)
            .clickable(enabled = selectable) {
                onClick?.invoke()
            }
    )
}

@Preview(showBackground = true)
@Composable
fun DiceSetPreview() {
    // Correctly create MutableLiveData with the required type
    val diceFaces = MutableLiveData(mutableListOf(1, 5, 3, 3, 1))
    val lockedDice = MutableLiveData(mutableListOf(1, 0, 1, 0, 0, 0))
    val selectedDice = MutableLiveData(mutableListOf(false, true, false, false, false))

    val gameViewModel = GameViewModel(null).apply {
        this.diceFaces = diceFaces
        this.lockedDice = lockedDice
        this.selectedDice = selectedDice
    }

    DiceSet(gameViewModel = gameViewModel)
}