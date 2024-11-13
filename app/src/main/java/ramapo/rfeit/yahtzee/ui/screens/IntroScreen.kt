package ramapo.rfeit.yahtzee.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ramapo.rfeit.yahtzee.R
import ramapo.rfeit.yahtzee.ui.components.NextButton

@Composable
fun IntroScreen(onStartGame: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(), // This stretches the Column to the full width of the screen
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IntroText()
        NextButton(onStartGame)
    }
}

@Composable
fun IntroText() {
    Text(
        text = stringResource(R.string.yahtzee_intro),
        fontSize = 15.sp,
        lineHeight = 25.sp,
        modifier = Modifier.padding(24.dp)
    )
}