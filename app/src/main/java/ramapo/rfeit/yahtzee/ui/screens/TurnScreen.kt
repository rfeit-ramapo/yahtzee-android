package ramapo.rfeit.yahtzee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ramapo.rfeit.yahtzee.R
import ramapo.rfeit.yahtzee.data.Human
import ramapo.rfeit.yahtzee.ui.components.DiceSet
import ramapo.rfeit.yahtzee.ui.components.ManualDiceInput
import ramapo.rfeit.yahtzee.ui.components.NextButton
import ramapo.rfeit.yahtzee.ui.components.RollButton
import ramapo.rfeit.yahtzee.ui.components.StandButton
import ramapo.rfeit.yahtzee.viewmodel.GameViewModel

enum class TurnPhase {
    ROLL, AVAILABLE_CATEGORIES, PURSUE_CATEGORIES, SELECT_DICE, SELECT_CATEGORY, END
}

@Preview(showBackground = true)
@Composable
fun TurnScreen(
    onNext: () -> Unit = {},
    gameViewModel: GameViewModel = GameViewModel(null),
) {

    val playerTurn = gameViewModel.currPlayer.observeAsState().value is Human
    var currentPhase = remember { mutableStateOf(TurnPhase.ROLL) }


    when (currentPhase.value) {
        TurnPhase.ROLL -> RollScreen(isHuman = playerTurn, gameViewModel = gameViewModel)
        else -> {}
    }



    // for rolls
    // instructions
    // diceset
    // random button (or next for computer)
    // manual input (based on how many are locked)
    // info on round number & roll number & scores

    // for after roll one - available categories
    // instructions/computer explanation
    // diceset
    // scorecard with selectable categories
        // computer shows selected already (and not changeable)
    // submit button (or next for computer)
    // only works when correct number is selected; otherwise, adds error message
    // help button -> provides textual explanation of which to pick
    // info on round number & roll number & scores

    // for after roll one - pursue categories
    // instructions/computer explanation
    // diceset
    // scorecard with selectable categories
        // computer shows selected already (and not changeable)
        // available are highlighted
        // you can only select ones that are highlighted
    // submit button (or next for computer)
        // this one should never be wrong
    // help button -> provides textual explanation
    // info on round number & roll number & scores

    // for after roll one - select reroll
    // instructions/computer explanation
    // diceset
        // clicking on them changes values
        // red (locked) are not changeable
        // others can be changed blue (to reroll)
    // scorecard (not selectable, for reference)
    // reroll button (or next for computer)
        // changes to stand button no new are selected
    // help button -> provides textual explanation of strategy
    // info on round number & roll number & scores

    // for after stand/end - select category to pick
    // instructions/computer explanation
    // diceset
        // all locked now
    // scorecard
        // available are highlighted
        // able to select any available (but only 1 at a time)
    // submit button (or next for computer)
        // only selectable if there are none available, or one is selected
        // error says "Please select one available category to fill."
    // help button -> provides textual explanation of strategy
    // info on round number & roll number & scores
}

@Preview(showBackground = true)
@Composable
fun RollScreen(
    onNext: () -> Unit = {}, // goes to the category selection screen; locks all dice
    isHuman: Boolean = true,
    gameViewModel: GameViewModel = GameViewModel(null)
) {
    // Find out how many dice are selected for reroll
    val selectedDice = gameViewModel.selectedDice.observeAsState().value!!
    val selectedCount = selectedDice.count { it }

    val rollNum = remember { mutableIntStateOf(1) }

    // Function to run when the roll automatically button is clicked
    val onRoll = {
        gameViewModel.prepRolls()
        gameViewModel.autoRoll()
        if (rollNum.intValue == 3) onNext()
        else rollNum.intValue++
    }
    val onManualRoll = {
        diceValues: List<Int> ->
            gameViewModel.prepRolls()
            gameViewModel.manualRoll(diceValues)
            if (rollNum.intValue == 3) onNext()
            else rollNum.intValue++
    }

    Column(
        modifier = Modifier.fillMaxWidth(), // This stretches the Column to the full width of the screen
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // If the computer is standing, make the user go to the next screen
        if ((selectedCount == 0) && !isHuman && (rollNum.intValue != 1)) {
            TurnInstructionText(R.string.stand_instruction_comp)
        } else {
            TurnInstructionText(R.string.roll_instruction)
        }

        // Dice selection

        // For first roll or nonhuman player, do not allow dice selection
        if ((rollNum.intValue == 1) || !isHuman) {
            DiceSet(gameViewModel)
        }
        // Otherwise, show instructions and provide selection functionality
        else {
            println("now showing dice for roll2")
            TurnInstructionText(R.string.reroll_instruction_human)
            DiceSet(gameViewModel, true)
        }

        // The roll buttons (stand, automatic, and manual)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // If none are selected (and not the first roll), stand
            if ((selectedCount == 0) && (rollNum.intValue != 1)) {
                StandButton(onNext)
            }
            // Otherwise, provide random and automatic options
            else {
                RollButton({onRoll()})
                ManualDiceInput(if (rollNum.intValue == 1) 5 else selectedCount, {
                    diceValues -> onManualRoll(diceValues)
                })
            }
        }
    }
}

@Composable
fun TurnInstructionText(instructionStringId: Int) {
    Text(
        text = stringResource(instructionStringId),
        fontSize = 15.sp,
        lineHeight = 25.sp,
        modifier = Modifier.padding(24.dp)
    )
}