package ramapo.rfeit.yahtzee.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ramapo.rfeit.yahtzee.R
import ramapo.rfeit.yahtzee.ui.components.InfoBox
import ramapo.rfeit.yahtzee.ui.components.NextButton
import ramapo.rfeit.yahtzee.ui.components.ScorecardTable
import ramapo.rfeit.yahtzee.viewmodel.GameViewModel

@Preview(showBackground = true)
@Composable
fun RoundSummaryScreen(
    onNext: () -> Unit = {}, // goes to determine player screen
    gameViewModel: GameViewModel = GameViewModel(null)
) {

    gameViewModel.setRoll(null)
    Column(
        modifier = Modifier.fillMaxWidth(), // This stretches the Column to the full width of the screen
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.round_summary),
            fontSize = 15.sp,
            lineHeight = 25.sp,
            modifier = Modifier.padding(10.dp)
        )
        InfoBox(gameViewModel, null)
        NextButton({
            gameViewModel.nextRound()
            onNext()
        })
        SerializeSaveScreen(gameViewModel)
        ScorecardTable(gameViewModel)
    }
}
