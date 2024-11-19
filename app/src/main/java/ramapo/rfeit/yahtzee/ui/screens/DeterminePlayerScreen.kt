package ramapo.rfeit.yahtzee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import ramapo.rfeit.yahtzee.ui.components.DiceSet
import ramapo.rfeit.yahtzee.viewmodel.GameViewModel
import ramapo.rfeit.yahtzee.ui.components.Die
import ramapo.rfeit.yahtzee.ui.components.ManualDiceInput
import ramapo.rfeit.yahtzee.ui.components.NextButton
import ramapo.rfeit.yahtzee.ui.components.RollButton
import ramapo.rfeit.yahtzee.ui.components.ScorecardTable

@Preview(showBackground = true)
@Composable
fun DeterminePlayerScreen(
    onNext: () -> Unit = {},
    gameViewModel: GameViewModel = GameViewModel(null)
) {

    // Track if this screen has already been loaded
    val isFirstLoad = remember { mutableStateOf(true) }

    // Observe the dice roll from the ViewModel
    val dieRollPlayer = gameViewModel.dieRollPlayer.observeAsState(1).value
    val dieRollComp = gameViewModel.dieRollComp.observeAsState(1).value

    val onRollClick = {
        gameViewModel.rollOneEach()
    }
    val onNextClick = {
        isFirstLoad.value = true
        onNext()
    }

    // Skip screen if scores are not equal
    val playerScore = gameViewModel.humScore.observeAsState(0).value
    val compScore = gameViewModel.compScore.observeAsState(0).value
    val isRollNeeded = playerScore == compScore

    Column(
        modifier = Modifier.fillMaxWidth(), // This stretches the Column to the full width of the screen
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Display the result text based on the dice roll
        when {
            isFirstLoad.value && isRollNeeded -> {
                DeterminePlayerText(R.string.determine_player)
            }
            (dieRollPlayer == dieRollComp && isRollNeeded) -> {
                DeterminePlayerText(R.string.determine_player_tie)
            }
            (dieRollPlayer > dieRollComp && isRollNeeded) || playerScore < compScore -> {
                gameViewModel.switchToHuman()
                DeterminePlayerText(R.string.determine_player_human)
            }
            else -> {
                gameViewModel.switchToComputer()
                DeterminePlayerText(R.string.determine_player_comp)
            }
        }

        if (isRollNeeded || !isFirstLoad.value) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Die(value = dieRollPlayer)
                Die(value = dieRollComp)
            }
            Spacer(Modifier.padding(10.dp))
        }


        // Show the correct button based on the dice result
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if ((isRollNeeded && (isFirstLoad.value || (dieRollPlayer == dieRollComp))) ) {
                RollButton(onRoll = onRollClick)
                ManualDiceInput(num = 2, { diceValues ->
                    gameViewModel.dieRollPlayer.value = diceValues[0]
                    gameViewModel.dieRollComp.value = diceValues[1]
                })
                isFirstLoad.value = false
            } else {
                NextButton(onNext = onNextClick)
            }
        }

        Text(
            text = stringResource(R.string.roll_button_desc),
            fontSize = 15.sp,
            lineHeight = 10.sp)
    }
}

@Composable
fun DeterminePlayerText(resultStringId: Int) {
    Text(
        text = stringResource(resultStringId),
        fontSize = 20.sp,
        lineHeight = 30.sp,
        modifier = Modifier.padding(25.dp)
    )
}