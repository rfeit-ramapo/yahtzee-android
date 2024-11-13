package ramapo.rfeit.yahtzee.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ramapo.rfeit.yahtzee.R
import ramapo.rfeit.yahtzee.ui.GameViewModel
import ramapo.rfeit.yahtzee.ui.components.Die
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import ramapo.rfeit.yahtzee.ui.components.RollButton

@Preview(showBackground = true)
@Composable
fun DeterminePlayerScreen(
    roundNum: Int = 1,
    onNext: () -> Unit = {},
    gameViewModel: GameViewModel = GameViewModel()
) {

    // Observe the dice roll from the ViewModel
    val dieRollPlayer = gameViewModel.dieRollPlayer.observeAsState(1).value
    val dieRollComp = gameViewModel.dieRollComp.observeAsState(1).value
    val onRollClick = {
        gameViewModel.rollOneEach()
    }

    Column(
        modifier = Modifier.fillMaxWidth(), // This stretches the Column to the full width of the screen
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DeterminePlayerText()
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Die(value = dieRollPlayer)
            Die(value = dieRollComp)
        }
        RollButton(onRoll = onRollClick)
    }
}

@Composable
fun DeterminePlayerText() {
    Text(
        text = stringResource(R.string.determine_player),
        fontSize = 15.sp,
        lineHeight = 25.sp,
        modifier = Modifier.padding(24.dp)
    )
}