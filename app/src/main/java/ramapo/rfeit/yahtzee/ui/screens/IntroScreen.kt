package ramapo.rfeit.yahtzee.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ramapo.rfeit.yahtzee.R
import ramapo.rfeit.yahtzee.ui.components.NextButton
import ramapo.rfeit.yahtzee.viewmodel.GameViewModel

@Preview(showBackground = true)
@Composable
fun IntroScreen(onNext: () -> Unit = {}, gameViewModel: GameViewModel = GameViewModel(null)) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IntroText()
        NextButton(onNext)
        SerializeLoadScreen(onNext, gameViewModel)
    }
}

@Composable
fun IntroText() {
    Text(
        text = stringResource(R.string.yahtzee_intro),
        fontSize = 15.sp,
        lineHeight = 25.sp,
        modifier = Modifier.padding(15.dp)
    )
}