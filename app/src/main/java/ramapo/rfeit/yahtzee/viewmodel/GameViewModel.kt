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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ramapo.rfeit.yahtzee.data.Logger
import ramapo.rfeit.yahtzee.data.Player
import ramapo.rfeit.yahtzee.data.Strategy
import ramapo.rfeit.yahtzee.data.StrategyEngine

class GameViewModel(application: Application?): ViewModel() {
    // Use StateFlow instead of LiveData for better composition
    private val _scorecard = MutableStateFlow(Scorecard())
    val scorecard: StateFlow<Scorecard> = _scorecard.asStateFlow()

    private val _selectedCategories = MutableStateFlow<List<Int>>(emptyList())
    val selectedCategories: StateFlow<List<Int>> = _selectedCategories.asStateFlow()

    // Serializer - create Serializer instance here
    private val _serializer = Serializer(application?.applicationContext)

    // Logger
    private val _logger = Logger(application?.applicationContext)

    // Players
    private val _humPlayer = MutableLiveData(Human())
    private val _compPlayer = MutableLiveData(Computer())
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
    var lockedDice = _dice.map { it.locked }
    var selectedDice = MutableLiveData(mutableListOf(false, false, false, false, false))

    // LiveData for singular rolls for UI access
    val dieRollPlayer = MutableLiveData(1)
    val dieRollComp = MutableLiveData(1)

    // Strategizing
    private val _strategyEngine = StrategyEngine()
    private val _strategy: MutableLiveData<Strategy?> = MutableLiveData(null)
    val strategy: LiveData<Strategy?> get() = _strategy


    // Functions

    fun rollOneEach() {
        dieRollPlayer.value = _dice.value!!.rollOne()
        dieRollComp.value = _dice.value!!.rollOne()
        logLine("The player rolled a ${dieRollPlayer.value} and the computer rolled a ${dieRollComp.value}")
    }

    fun serializeLoad(fileName: MutableState<String>): Boolean {
        return _serializer.loadGame(_roundNum, _scorecard, _humPlayer, _compPlayer, fileName.value)
    }

    fun serializeSave(fileName: MutableState<String>): Boolean {
        return _serializer.saveGame(_roundNum.value!!, _scorecard.value, fileName.value)
    }

    fun isGameOver(): Boolean {
        return _scorecard.value.isFull()
    }

    // Function to switch the current player
    fun switchToHuman() {
        _currPlayer.removeSource(_compPlayer)
        _currPlayer.removeSource(_humPlayer)
        _currPlayer.addSource(_humPlayer) { newValue -> _currPlayer.value = newValue }
        logLine("Switching to Human turn.")
    }

    fun switchToComputer() {
        _currPlayer.removeSource(_humPlayer)
        _currPlayer.removeSource(_compPlayer)
        _currPlayer.addSource(_compPlayer) { newValue -> _currPlayer.value = newValue }
        logLine("Switching to Computer turn.")
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
        val keptDice = if (_rollNum.value == 1) diceFaces.value!! else
            diceFaces.value!!.filterIndexed { index, _ ->
            selectedDice.value!!.getOrNull(index) == true
        }
        logLine("Rerolling dice: $keptDice")

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
        // Explicitly update tp notify UI of change
        _dice.value = _dice.value
        logLine("Manually rolled dice: ${_dice.value}")
        updateStrategy()
    }

    fun autoRoll() {
        _dice.value!!.rollAll()
        logLine("Automatically rolled dice: ${_dice.value}")
        updateStrategy()
    }

    private fun updateStrategy() {
        _strategy.value = _strategyEngine.strategize(_scorecard.value, _dice.value!!)
    }

    // Select dice based on current strategy
    fun autoSelectDice() {
        // Skip selection if there is no strategy, or the strategy is to stand
        val toReroll = _strategy.value?.rerollCounts?.toMutableList() ?: return // Exit if no strategy is present
        if (toReroll.size == 0) return

        // Get the current dice counts and locked status
        val lockedDice = _dice.value!!.locked.toMutableList()

        // Create a mutable list to represent which individual dice are selected for rerolling
        val newSelectedDice = MutableList(Dice.NUM_DICE) { false }

        // Iterate through individual die to determine which should be rerolled
        _dice.value!!.diceList.forEachIndexed { index, face ->
            // Check if this die face is already locked (less rerolls are needed)
            if (lockedDice[face - 1] > 0) {
                // Reduce the locked count for this face
                lockedDice[face - 1]--
            } else if (toReroll[face - 1] > 0) {
                // If the die face matches one we need to reroll, mark it for rerolling
                newSelectedDice[index] = true
                // Reduce the number of rerolls needed for this face
                toReroll[face - 1]--
            }
        }

        // Update the LiveData with the new selection
        selectedDice.value = newSelectedDice
    }

    fun autoSelectAvailableCategories() {
        _selectedCategories.value = getStrictAvailableCategories()
    }

    fun finalizeDice() {
        _dice.value!!.lockAllDice()
        updateStrategy()
        _dice.value = _dice.value
    }

    fun getStrictAvailableCategories(): MutableList<Int> {
       return _strategyEngine.getPossibleCategories(_scorecard.value, _dice.value!!, true)
    }

    fun getAllAvailableCategories(): MutableList<Int> {
        return _strategyEngine.getPossibleCategories(_scorecard.value, _dice.value!!, false)
    }

    fun toggleSelectedCategory(index: Int, removesOthers: Boolean = false) {
        val currentList = _selectedCategories.value.toMutableList()

        if (removesOthers) {
            _selectedCategories.value = listOf(index)
        } else {
            if (currentList.contains(index)) {
                currentList.remove(index)
            } else {
                currentList.add(index)
            }
            currentList.sort()
            _selectedCategories.value = currentList
        }
    }

    fun autoSelectPursuedCategory() {
        // If no feasible strategy was found, do not select anything
        if (_strategy.value == null) _selectedCategories.value = emptyList()
        // Otherwise, select the best strategy
        else _selectedCategories.value = listOf(_scorecard.value.getCategoryIndex(_strategy.value!!.categoryName))
    }

    fun clearSelectedCategories() {
        _selectedCategories.value = emptyList()
    }

    fun getStratString(): String {
        val isHuman = currPlayer.value is Human
        return _strategy.value?.getString(isHuman) ?: "No available categories to pursue."
    }

    fun setRoll(num: Int?): Int? {
        _rollNum.value = num
        return num
    }

    fun nextRoll() {
        when (_rollNum.value) {
            null -> _rollNum.value = 1
            3 -> _rollNum.value = null
            else -> _rollNum.value = _rollNum.value!! + 1
        }
    }

    fun getScoredPoints(categoryIndex: Int? = selectedCategories.value.firstOrNull()): Int {
        if (categoryIndex == null) return 0
        return _scorecard.value.categories[categoryIndex].score(_dice.value!!)
    }

    // Validates a turn. If correct input, fills the category, awards points, and resets dice
    fun finalizeTurn(inPoints: Int? = null, inRound: Int? = null): Boolean {
        val categoryIndex = selectedCategories.value.firstOrNull()

        // If no category was fillable
        if (categoryIndex == null) {
            logLine("Skipping turn because no categories are fillable given the current diceset.")
        }
        // Fill the selected category after confirming point and round numbers
        else {
            val points = getScoredPoints(categoryIndex)
            val round = _roundNum.value!!
            val categoryName = _scorecard.value.categories[categoryIndex].name

            // Validate any inputs
            if (inPoints != null && inRound != null) {
                if (inPoints != points || inRound != round) return false
            }
            _scorecard.value.fillCategory(categoryIndex, points, round, _currPlayer.value!!.internalName)
            _currPlayer.value!!.addScore(points)

            logLine("${currPlayer.value?.internalName} filled the $categoryName category with $points points in Round $round")
        }

        clearSelectedCategories()
        _humPlayer.value = _humPlayer.value
        _compPlayer.value = _compPlayer.value
        logLine("The scores are now ${humScore.value} (Human) and ${compScore.value} (Computer)")
        _dice.value!!.resetDice()
        _dice.value = _dice.value

        return true
    }

    fun nextRound() {
        _roundNum.value = _roundNum.value!! + 1
        logLine("\nStarting Round ${roundNum.value}")
    }

    fun readLog(): String {
        return _logger.readLog()
    }

    fun logLine(line: String): Boolean {
        return _logger.logLine(line)
    }

    fun logAvailableCategories() {
        val availableCategories = getStrictAvailableCategories()
        val categoryNames = availableCategories.map { index ->
            _scorecard.value.categories[index].name
        }
        logLine("Available categories: $categoryNames")
    }

    fun logPursuedCategories() {
        val pursuedCategories = selectedCategories.value.map {
            index -> _scorecard.value.categories[index].name
        }
        logLine("The player chose to pursue $pursuedCategories")
    }

    fun logEndGame() {
        logLine("\nGame Complete")
        if (humScore.value!! > compScore.value!!) logLine("Human won the game with ${humScore.value} points to the Computer's ${compScore.value}")
        else if (compScore.value!! > humScore.value!!) logLine("Computer won the game with ${compScore.value} points to the Human's ${humScore.value}")
        else logLine("Both players tied with ${humScore.value} points")
    }

    // Round number
    private val _roundNum = MutableLiveData(1)
    val roundNum: LiveData<Int> get() = _roundNum

    // Roll number
    private val _rollNum = MutableLiveData<Int?>(null)
    val rollNum: LiveData<Int?> get() = _rollNum

}
