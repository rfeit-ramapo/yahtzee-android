package ramapo.rfeit.yahtzee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
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
import ramapo.rfeit.yahtzee.ui.components.InfoBox
import ramapo.rfeit.yahtzee.ui.components.ManualDiceInput
import ramapo.rfeit.yahtzee.ui.components.RollButton
import ramapo.rfeit.yahtzee.ui.components.ScorecardTable
import ramapo.rfeit.yahtzee.ui.components.SelectLimit
import ramapo.rfeit.yahtzee.ui.components.StandButton
import ramapo.rfeit.yahtzee.ui.components.SubmitButton
import ramapo.rfeit.yahtzee.viewmodel.GameViewModel

enum class TurnPhase {
    ROLL, AVAILABLE_CATEGORIES, PURSUE_CATEGORIES, SELECT_CATEGORY
}

@Preview(showBackground = true)
@Composable
fun TurnScreen(
    onNext: () -> Unit = {},
    onEndGame: () -> Unit = {},
    gameViewModel: GameViewModel = GameViewModel(null),
) {

    val playerTurn = gameViewModel.currPlayer.observeAsState().value is Human
    val isSecondTurn = remember { mutableStateOf(false)}
    val currentPhase = remember { mutableStateOf(TurnPhase.ROLL) }


    when (currentPhase.value) {
        TurnPhase.ROLL -> RollScreen(
            onNext = {
                gameViewModel.finalizeDice()
                currentPhase.value = TurnPhase.SELECT_CATEGORY
            },
            afterRoll = { currentPhase.value = TurnPhase.AVAILABLE_CATEGORIES },
            isHuman = playerTurn,
            gameViewModel = gameViewModel)
        TurnPhase.AVAILABLE_CATEGORIES -> AvailableCategoriesScreen(
            onNext = { currentPhase.value = TurnPhase.PURSUE_CATEGORIES },
            isHuman = playerTurn,
            gameViewModel = gameViewModel
        )
        TurnPhase.PURSUE_CATEGORIES -> PursueCategoriesScreen(
            onNext = {
                gameViewModel.clearSelectedCategories()
                gameViewModel.nextRoll()
                currentPhase.value = TurnPhase.ROLL
            },
            isHuman = playerTurn,
            gameViewModel = gameViewModel
        )
        TurnPhase.SELECT_CATEGORY -> SelectCategoryScreen(
            onNext = {
                // End round if this was the second turn
                if (isSecondTurn.value) {
                    isSecondTurn.value = false
                    currentPhase.value = TurnPhase.ROLL
                    if (gameViewModel.isGameOver()) onEndGame()
                    else onNext()
                }
                // Otherwise only end round if this player ended the game
                else {
                    if (gameViewModel.isGameOver()) {
                        isSecondTurn.value = false
                        currentPhase.value = TurnPhase.ROLL
                        onEndGame()
                    } else {
                        isSecondTurn.value = true
                        if (playerTurn) gameViewModel.switchToComputer()
                        else gameViewModel.switchToHuman()
                        currentPhase.value = TurnPhase.ROLL
                    }
                }
            },
            isHuman = playerTurn,
            gameViewModel = gameViewModel
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RollScreen(
    onNext: () -> Unit = {}, // goes to the category selection screen; locks all dice
    afterRoll: () -> Unit = {},
    isHuman: Boolean = true,
    gameViewModel: GameViewModel = GameViewModel(null)
) {
    // Find out how many dice are selected for reroll
    val selectedDice = gameViewModel.selectedDice.observeAsState().value!!
    val selectedCount = selectedDice.count { it }
    val showHelp = remember { mutableStateOf(false) }

    val rollNum = gameViewModel.rollNum.observeAsState().value ?: gameViewModel.setRoll(1)

    // Function to run when the roll automatically button is clicked
    val onRoll = {
        showHelp.value = false
        gameViewModel.prepRolls()
        gameViewModel.autoRoll()
        if (rollNum == 3) {
            gameViewModel.nextRoll()
            onNext()
        }
        else {
            afterRoll()
        }
    }
    val onManualRoll = {
        diceValues: List<Int> ->
            showHelp.value = false
            gameViewModel.prepRolls()
            gameViewModel.manualRoll(diceValues)
            if (rollNum == 3) {
                gameViewModel.nextRoll()
                onNext()
            }
            else {
                afterRoll()
            }
    }

    if (rollNum != 1 && !isHuman) {
        gameViewModel.autoSelectDice()
    }

    Column(
        modifier = Modifier.fillMaxWidth(), // This stretches the Column to the full width of the screen
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // If the computer is standing, make the user go to the next screen
        if ((selectedCount == 0) && !isHuman && (rollNum != 1)) {
            TurnInstructionText(stringResource(R.string.stand_instruction_comp))
        } else {
            TurnInstructionText(stringResource(R.string.roll_instruction))
        }

        // Dice selection

        // For first roll or nonhuman player, do not allow dice selection
        if ((rollNum == 1) || !isHuman) {
            DiceSet(gameViewModel)
        }
        // Otherwise, show instructions and provide selection functionality
        else {
            TurnInstructionText(stringResource(R.string.reroll_instruction_human))
            if (showHelp.value) TurnInstructionText(gameViewModel.getStratString(isHuman = true))
            DiceSet(gameViewModel, true)
        }

        // The roll buttons (stand, automatic, and manual)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // If none are selected (and not the first roll), stand
            if ((selectedCount == 0) && (rollNum != 1)) {
                StandButton(onNext)
            }
            // Otherwise, provide random and automatic options
            else {
                RollButton({ onRoll() })
                ManualDiceInput(if (rollNum == 1) 5 else selectedCount, {
                    diceValues -> onManualRoll(diceValues)
                })
            }
        }

        InfoBox(
            gameViewModel,
            onHelp =
            if (isHuman && rollNum != 1) {
                {
                    showHelp.value = !showHelp.value
                    gameViewModel.autoSelectDice()
                }
            } else null)
    }
}

@Preview(showBackground = true)
@Composable
fun AvailableCategoriesScreen(
    onNext: () -> Unit = {}, // goes to the pursue categories screen
    isHuman: Boolean = true,
    gameViewModel: GameViewModel = GameViewModel(null)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display instructions or explanation for available category selection
        if (isHuman) {
            TurnInstructionText(stringResource(R.string.available_categories_instructions_human))
        } else {
            gameViewModel.autoSelectAvailableCategories()
            TurnInstructionText(stringResource(R.string.available_categories_instructions_computer))
        }

        // Dice view
        DiceSet(gameViewModel)

        // The submit button
        SubmitButton(
            onNext = onNext,
            validator = {
                gameViewModel.selectedCategories.value == gameViewModel.getStrictAvailableCategories()
            },
            errorMessageId = R.string.incorrect_available
        )

        InfoBox(gameViewModel, onHelp =
            if (isHuman) {
                {
                    gameViewModel.autoSelectAvailableCategories()
                }
            } else null)

        ScorecardTable(gameViewModel, if (isHuman) SelectLimit.NONE else SelectLimit.DISABLED)
    }
}

@Preview(showBackground = true)
@Composable
fun PursueCategoriesScreen(
    onNext: () -> Unit = {}, // clears selected categories and progresses to reroll screen
    isHuman: Boolean = true,
    gameViewModel: GameViewModel = GameViewModel(null)
) {
    val showHelp = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display instructions or explanation for available category selection
        if (isHuman) {
            TurnInstructionText(stringResource(R.string.pursue_categories_instructions_human))
            if (showHelp.value) TurnInstructionText(gameViewModel.getStratString(isHuman = true))
        } else {
            gameViewModel.autoSelectPursuedCategory()
            TurnInstructionText(gameViewModel.getStratString(isHuman = false))
        }

        // Dice view
        DiceSet(gameViewModel)

        // The submit button
        SubmitButton(
            onNext = {
                showHelp.value = false
                onNext()
            },
        )

        InfoBox(gameViewModel, onHelp =
        if (isHuman) {
            {
                gameViewModel.autoSelectPursuedCategory()
                showHelp.value = !showHelp.value
            }
        } else null)

        ScorecardTable(gameViewModel, if (isHuman) SelectLimit.ALL_AVAILABLE else SelectLimit.DISABLED)
    }
}

@Composable
fun SelectCategoryScreen(
    onNext: () -> Unit = {}, // switches turn, or ends the round
    isHuman: Boolean = true,
    gameViewModel: GameViewModel = GameViewModel(null)
) {
    val showHelp = remember { mutableStateOf(false) }
    val isCategoryAvailable = (gameViewModel.strategy.value != null)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display instructions or explanation for available category selection
        if (!isCategoryAvailable) {
            TurnInstructionText(stringResource(R.string.no_categories))
        }
        else if (isHuman) {
            TurnInstructionText(stringResource(R.string.select_category_instructions_human))
            if (showHelp.value) TurnInstructionText(gameViewModel.getStratString(isHuman = true))
        } else {
            gameViewModel.autoSelectPursuedCategory()
            TurnInstructionText(gameViewModel.getStratString(isHuman = false))
        }

        // Dice view
        DiceSet(gameViewModel)

        // The submit button
        SubmitButton(
            onNext = {
                showHelp.value = false
                gameViewModel.finalizeTurn()
                onNext()
            },
            validator = {
                !isCategoryAvailable || (gameViewModel.selectedCategories.value.size == 1)
            },
            errorMessageId = R.string.no_category_error
        )

        InfoBox(gameViewModel, onHelp =
        if (isHuman) {
            {
                gameViewModel.autoSelectPursuedCategory()
                showHelp.value = !showHelp.value            }
        } else null)

        ScorecardTable(gameViewModel, if (isHuman) SelectLimit.ONE_AVAILABLE else SelectLimit.DISABLED)
    }
}

@Composable
fun TurnInstructionText(string: String) {
    Text(
        text = string,
        fontSize = 15.sp,
        lineHeight = 25.sp,
        modifier = Modifier.padding(10.dp)
    )
}