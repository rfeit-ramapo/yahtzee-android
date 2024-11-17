package ramapo.rfeit.yahtzee.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.compose.runtime.MutableState
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import ramapo.rfeit.yahtzee.data.Computer
import ramapo.rfeit.yahtzee.data.Dice
import ramapo.rfeit.yahtzee.data.Human
import ramapo.rfeit.yahtzee.data.Scorecard
import ramapo.rfeit.yahtzee.data.Serializer
import androidx.lifecycle.map
import ramapo.rfeit.yahtzee.data.Player

class GameViewModel(application: Application?): ViewModel() {
    // Scorecard
    private val _scorecard = MutableLiveData(Scorecard())
    val scorecard: LiveData<Scorecard> get() = _scorecard

    // Serializer - create Serializer instance here
    private val _serializer = Serializer(application?.applicationContext)

    // Players
    private val _humPlayer = MutableLiveData(Human())
    private val _compPlayer = MutableLiveData(Computer())
    val humPlayer: LiveData<Human> get() = _humPlayer
    val compPlayer: LiveData<Computer> get() = _compPlayer
    val humScore: LiveData<Int> = _humPlayer.map { it.score }
    val compScore: LiveData<Int> = _compPlayer.map { it.score }

    // Current Player dynamically references one of the players
    private val _currPlayer = MediatorLiveData<Player>().apply {
        addSource(_humPlayer) { value = it } // Listen to changes in _humPlayer
    }
    val currPlayer: LiveData<Player> get() = _currPlayer

    // Dice
    private val _dice = MutableLiveData(Dice())
    var diceFaces = _dice.map { it.diceList }
    val diceCounts = _dice.map { it.diceCount }
    var lockedDice = _dice.map { it.locked }
    var selectedDice = MutableLiveData(mutableListOf(false, false, false, false, false))

    // LiveData for singular rolls for UI access
    val dieRollPlayer = MutableLiveData<Int>(1)
    val dieRollComp = MutableLiveData<Int>(1)

    // Serialization


    // Functions

    fun rollOneEach() {
        dieRollPlayer.value = _dice.value!!.rollOne()
        dieRollComp.value = _dice.value!!.rollOne()
    }

    fun serializeLoad(fileName: MutableState<String>): Boolean {
        return _serializer.loadGame(_roundNum, _scorecard, _humPlayer, _compPlayer, fileName.value)
    }

    fun serializeSave(fileName: MutableState<String>): Boolean {
        return _serializer.saveGame(_roundNum.value!!, _scorecard.value!!, fileName.value)
    }

    fun isGameOver(): Boolean {
        return _scorecard.value!!.isFull()
    }

    // Function to switch the current player
    fun switchToHuman() {
        _currPlayer.removeSource(_compPlayer)
        _currPlayer.removeSource(_humPlayer)
        _currPlayer.addSource(_humPlayer) { newValue -> _currPlayer.value = newValue }
    }

    fun switchToComputer() {
        _currPlayer.removeSource(_humPlayer)
        _currPlayer.removeSource(_compPlayer)
        _currPlayer.addSource(_compPlayer) { newValue -> _currPlayer.value = newValue }
    }

    fun toggleSelectedDie(index: Int) {
        val updatedSelectedDice = selectedDice.value?.toMutableList() ?: mutableListOf()

        if (updatedSelectedDice.size > index) {
            // Toggle the selection state
            updatedSelectedDice[index] = !updatedSelectedDice[index]
        } else {
            // Add a new selection if this is a new die being clicked
            updatedSelectedDice.add(index, true)
        }

        // Update the LiveData with the new selected dice list
        selectedDice.value = updatedSelectedDice
    }

    // Function to prepare dice before a roll
    fun prepRolls() {
        // Create a list of kept dice by filtering the dice faces based on selected status
        val keptDice = diceFaces.value!!.filterIndexed { index, _ ->
            selectedDice.value!!.getOrNull(index) == true
        }
        // Convert the kept dice into a count of each face using listToCount
        val keptDiceCount = _dice.value!!.listToCount(keptDice)

        // Update locked dice using lockDice with the kept dice count
        _dice.value!!.lockDice(keptDiceCount)

        // Trigger LiveData observers to update with the new locked state
        _dice.value = _dice.value // Reassign to notify observers

        // Reset selected dice
        selectedDice.value = mutableListOf(false, false, false, false, false)
    }

    fun manualRoll(rollValues: List<Int>) {
        _dice.value!!.manualRoll(rollValues)
        // set new strategy
    }

    fun autoRoll() {
        _dice.value!!.rollAll()
        // set new strategy
    }

    // to do now
    // create a function to update the strategy based on current dice
    // make the computer select dice based on the strategy (except for first roll)
    // create help box that gives info
    // do pursuit and selection screens post roll

    // Round number
    private val _roundNum = MutableLiveData<Int>(1)
    val roundNum: LiveData<Int> get() = _roundNum

}
