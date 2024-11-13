package ramapo.rfeit.yahtzee.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ramapo.rfeit.yahtzee.data.Dice

class GameViewModel : ViewModel() {
    // scorecard
    // players
    // dice
    private val dice = Dice()
    // MutableLiveData to store the die roll value
    private val _dieRollPlayer = MutableLiveData<Int>(1)
    private val _dieRollComp = MutableLiveData<Int>(1)
    // Exposed LiveData to observe the die roll
    val dieRollPlayer: LiveData<Int> get() = _dieRollPlayer
    val dieRollComp: LiveData<Int> get() = _dieRollComp

    fun rollOneEach() {
        _dieRollPlayer.value = dice.rollOne()
        _dieRollComp.value = dice.rollOne()
    }
    // round num
}
