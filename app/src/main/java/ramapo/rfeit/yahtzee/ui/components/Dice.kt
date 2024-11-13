package ramapo.rfeit.yahtzee.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ramapo.rfeit.yahtzee.R

@Composable
fun DiceSet(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().padding(5.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        Die(1, false)
        Die(5,true)
        Die(3,true)
        Die(3, false)
        Die(2, false)
    }

}

@Composable
fun Die(value: Int = 1, locked: Boolean = false) {
    val drawFile =
        if (locked) {
            when(value) {
                1 -> R.drawable.ace_locked
                2 -> R.drawable.two_locked
                3 -> R.drawable.three_locked
                4 -> R.drawable.four_locked
                5 -> R.drawable.five_locked
                6 -> R.drawable.six_locked
                else -> R.drawable.error_die
            }
        }
        else {
            when(value) {
                1 -> R.drawable.ace_unlocked
                2 -> R.drawable.two_unlocked
                3 -> R.drawable.three_unlocked
                4 -> R.drawable.four_unlocked
                5 -> R.drawable.five_unlocked
                6 -> R.drawable.six_unlocked
                else -> R.drawable.error_die
            }
        }

    Image(
        painter = painterResource(drawFile),
        contentDescription = null,
        modifier = Modifier
            .size(75.dp)
            .padding(2.dp)
    )
}