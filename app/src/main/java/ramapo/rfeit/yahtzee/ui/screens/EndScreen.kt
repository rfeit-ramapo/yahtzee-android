package ramapo.rfeit.yahtzee.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ramapo.rfeit.yahtzee.viewmodel.GameViewModel

@Preview(showBackground = true)
@Composable
fun EndScreen(gameViewModel: GameViewModel = GameViewModel(null)) {
    Text(text = "You reached the end. Congrats.")
}