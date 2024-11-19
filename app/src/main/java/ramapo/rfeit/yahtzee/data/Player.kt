package ramapo.rfeit.yahtzee.data

/**
 * Represents a player within the game.
 * Abstract class with more specific subclasses of Human and Computer.
 *
 * @param internalName the name to use internally and for scorecard for this player.
 * @param score an [Integer] representing how many points this player has earned.
 */
abstract class Player(
    internal val internalName: String,
    internal var score: Int = 0
) {

    /**
     * Adds a value to the current score of this player.
     *
     * @param add the [Integer] value to add to the score.
     * @return Unit
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    fun addScore(add: Int) {
        score += add
    }
}