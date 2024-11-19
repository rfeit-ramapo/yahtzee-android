package ramapo.rfeit.yahtzee.data

/**
 * Abstract class representing a scoring category in the Yahtzee game.
 * Each category has a name, description, and score and defines methods for scoring and reroll strategies.
 *
 * @property name the name of the scoring category.
 * @property description a description of how to qualify for this category.
 * @property score a [String] of how to calculate points earned for this category.
 */
abstract class Category(
    internal val name: String,
    internal val description: String,
    internal val score: String
) {

    /**
     * The winner of the category.
     */
    internal var winner: String = ""

    /**
     * The points earned in this category.
     */
    internal var points: Int = 0

    /**
     * The round in which this category was claimed.
     */
    internal var round: Int = 0

    /**
     * Indicates whether this category is full (no more points can be scored).
     */
    internal var full: Boolean = false

    /**
     * Calculates the score for this category based on the given dice.
     *
     * @param dice the set of [Dice] to calculate the score for.
     * @return the calculated score for the category.
     *
     * @reference None.
     */
    abstract fun score(dice: Dice): Int

    /**
     * Determines the reroll strategy for this category, if applicable.
     *
     * @param dice the set of [Dice] to consider for the reroll strategy.
     * @return the reroll strategy or `null` if scoring is impossible.
     *
     * @reference None.
     */
    abstract fun getRerollStrategy(dice: Dice): Strategy?
}