package ramapo.rfeit.yahtzee.data

import kotlin.random.Random

class Dice {
    companion object {
        // Constants
        const val NUM_DICE = 5
        const val NUM_DICE_FACES = 6
    }

    // Properties
    private var diceList = MutableList(NUM_DICE) { 1 }
    private var diceCount = MutableList(NUM_DICE_FACES) { 0 }
    private var locked = MutableList(NUM_DICE_FACES) { 0 }

    // Selectors
    fun getLockedDice(): List<Int> = locked
    fun getDiceCount(): List<Int> = diceCount

    // Functions

    // Roll a single die and return the value (1-6)
    fun rollOne(): Int = generateDieValue()

    // Function to lock all dice
    fun lockAllDice() {
        locked = diceCount.toMutableList()
    }

    // Function to unlock all dice
    fun unlockAllDice() {
        locked = MutableList(NUM_DICE_FACES) { 0 }
    }

    // Convert a list of dice to a count of each face
    fun listToCount(diceList: List<Int>): List<Int> {
        val diceCount = MutableList(NUM_DICE_FACES) { 0 }
        diceList.forEach { face -> diceCount[face - 1]++ }
        return diceCount
    }

    // Convert a dice count to a list of dice faces
    fun countToList(diceCount: List<Int>): List<Int> {
        val diceList = mutableListOf<Int>()
        for (i in 0 until NUM_DICE_FACES) {
            repeat(diceCount[i]) { diceList.add(i + 1) }
        }
        return diceList
    }

    // Lock dice that are not part of the kept dice
    fun lockDice(keptDice: List<Int>) {
        val newLocked = MutableList(NUM_DICE_FACES) { 0 }
        for (i in 0 until NUM_DICE_FACES) {
            val numToLock = diceCount[i] - keptDice[i]
            newLocked[i] = numToLock
        }
        locked = newLocked
    }

    // Function to roll all dice that are not locked
    fun rollAll(): List<Int> {
        val lockedRemaining = locked.toMutableList()
        for (i in diceList.indices) {
            if (lockedRemaining[diceList[i] - 1] > 0) {
                lockedRemaining[diceList[i] - 1]--
                continue
            }
            diceList[i] = generateDieValue()
            diceList[diceList[i] - 1]++
        }
        return diceList
    }

    // Manual roll input for free dice
    fun manualRoll(input: List<Int>) {
        diceCount = locked.toMutableList()
        diceList = countToList(locked).toMutableList()
        diceList.addAll(input)
        input.forEach { face -> diceCount[face - 1]++ }
    }

    // Get unlocked and unscored dice
    fun getUnlockedUnscored(required: List<Int>): List<Int> {
        return diceList.filter { !required.contains(it) }
            .groupBy { it }
            .map { it.value.size }
    }

    // Get the free (unlocked) dice
    fun getFreeDice(): List<Int> {
        return diceList.filter { locked[diceList.indexOf(it)] == 0 }
    }

    // Print the dice
    fun printDice() {
        diceList.forEachIndexed { index, face ->
            if (locked[index] > 0) {
                print("\u001B[31m$face\u001B[0m ") // Red for locked
            } else {
                print("$face ")
            }
        }
        println()
    }

    // Randomly generate a die value (1-6)
    private fun generateDieValue(): Int {
        return Random.nextInt(1, NUM_DICE_FACES + 1)
    }
}
