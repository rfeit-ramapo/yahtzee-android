package ramapo.rfeit.yahtzee

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import ramapo.rfeit.yahtzee.viewmodel.GameViewModel
import ramapo.rfeit.yahtzee.ui.screens.DeterminePlayerScreen
import ramapo.rfeit.yahtzee.ui.screens.IntroScreen
import ramapo.rfeit.yahtzee.ui.screens.SerializeLoadScreen
import ramapo.rfeit.yahtzee.ui.screens.SerializeSaveScreen
import ramapo.rfeit.yahtzee.ui.screens.TurnScreen
import ramapo.rfeit.yahtzee.viewmodel.GameViewModelFactory

enum class GameScreen {
    INTRO, DETERMINE_PLAYER, SERIALIZE_LOAD, SERIALIZE_SAVE, GAME_END, TURN
}

@Composable
fun ScreenManager() {
    var currentScreen by remember { mutableStateOf(GameScreen.INTRO) }

    // Use ViewModelProvider with a custom factory
    val context = LocalContext.current.applicationContext as Application
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(context))

    when (currentScreen) {
        GameScreen.INTRO -> IntroScreen(
            onStartGame = { currentScreen = GameScreen.DETERMINE_PLAYER },
            onLoadGame = { currentScreen = GameScreen.SERIALIZE_LOAD })
        GameScreen.SERIALIZE_LOAD -> SerializeLoadScreen(
            onNext = { currentScreen = if (gameViewModel.isGameOver()) GameScreen.GAME_END else GameScreen.DETERMINE_PLAYER},
            gameViewModel = gameViewModel)
        GameScreen.DETERMINE_PLAYER -> DeterminePlayerScreen(onNext = {currentScreen = GameScreen.TURN}, gameViewModel = gameViewModel)
        GameScreen.TURN -> TurnScreen(
            onNext = { currentScreen = if (gameViewModel.isGameOver()) GameScreen.GAME_END else GameScreen.SERIALIZE_SAVE},
            gameViewModel = gameViewModel)
        GameScreen.SERIALIZE_SAVE -> SerializeSaveScreen(
            onNext = { currentScreen = GameScreen.DETERMINE_PLAYER},
            gameViewModel = gameViewModel)
        GameScreen.GAME_END -> IntroScreen()
    }
}