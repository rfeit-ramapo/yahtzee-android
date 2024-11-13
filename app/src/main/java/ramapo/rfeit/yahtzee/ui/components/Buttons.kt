package ramapo.rfeit.yahtzee.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ramapo.rfeit.yahtzee.R

@Composable
fun NextButton(onNext: () -> Unit) {
    Button(
        onClick = onNext,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black // Set background color to black
        ),
        modifier = Modifier.padding(30.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.next_button),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
fun RollButton(onRoll: () -> Unit) {
    Button(
        onClick = onRoll,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black // Set background color to black
        ),
        modifier = Modifier.padding(30.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.roll_dice),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
    }
}