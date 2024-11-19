package ramapo.rfeit.yahtzee

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import ramapo.rfeit.yahtzee.viewmodel.GameViewModel
import ramapo.rfeit.yahtzee.ui.screens.DeterminePlayerScreen
import ramapo.rfeit.yahtzee.ui.screens.EndScreen
import ramapo.rfeit.yahtzee.ui.screens.IntroScreen
import ramapo.rfeit.yahtzee.ui.screens.RoundSummaryScreen
import ramapo.rfeit.yahtzee.ui.screens.RoundScreen

// Class to hold all the main screens of the game. Managed by ScreenManager function.
enum class GameScreen {
    INTRO, DETERMINE_PLAYER, GAME_END, ROUND, ROUND_SUMMARY
}

/**
 * Manages and displays active UI screen for the app, changing based on the game state.
 *
 * @return Unit
 *
 * @algorithm
 * 1) Obtain the application context to initialize the [GameViewModel].
 * 2) Remember and track the current screen state.
 * 3) Display the Screen Composable based on current screen state.
 *
 * @reference None.
 */
@Composable
fun ScreenManager() {

    // Load the application context to create the GameViewModel for this Tournament.
    val context = LocalContext.current.applicationContext as Application
    var gameViewModel: GameViewModel by remember {
        mutableStateOf(GameViewModel(context))
    }

    // Remember current screen to cause automatic recomposition.
    var currentScreen by remember { mutableStateOf(GameScreen.INTRO) }

    // Change which screen is displayed based on above variable.
    when (currentScreen) {
        GameScreen.INTRO -> IntroScreen(
            {
                gameViewModel.logLine("\nStarting Round ${gameViewModel.roundNum.value}")
                currentScreen = GameScreen.DETERMINE_PLAYER
            },
            gameViewModel)

        GameScreen.DETERMINE_PLAYER -> DeterminePlayerScreen(
            { currentScreen = GameScreen.ROUND },
            gameViewModel)

        GameScreen.ROUND -> RoundScreen(
            { currentScreen = GameScreen.ROUND_SUMMARY},
            {
                gameViewModel.logEndGame()
                currentScreen = GameScreen.GAME_END
            },
            gameViewModel)

        GameScreen.ROUND_SUMMARY -> RoundSummaryScreen(
            { currentScreen = GameScreen.DETERMINE_PLAYER },
            gameViewModel)

        GameScreen.GAME_END -> EndScreen(
            {
                // Reset all game data before restarting
                gameViewModel = GameViewModel(context)
                currentScreen = GameScreen.INTRO
            },
            gameViewModel
        )
    }
}