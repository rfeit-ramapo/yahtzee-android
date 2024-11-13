package ramapo.rfeit.yahtzee

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import ramapo.rfeit.yahtzee.ui.GameViewModel
import ramapo.rfeit.yahtzee.ui.screens.DeterminePlayerScreen
import ramapo.rfeit.yahtzee.ui.screens.IntroScreen

enum class GameScreen {
    INTRO, DETERMINE_PLAYER
}

@Composable
fun ScreenManager() {
    var currentScreen by remember { mutableStateOf(GameScreen.INTRO) }
    val gameViewModel: GameViewModel = GameViewModel()

    when (currentScreen) {
        GameScreen.INTRO -> IntroScreen(onStartGame = { currentScreen = GameScreen.DETERMINE_PLAYER })
        GameScreen.DETERMINE_PLAYER -> DeterminePlayerScreen(onNext = {}, gameViewModel = gameViewModel)
    }
}