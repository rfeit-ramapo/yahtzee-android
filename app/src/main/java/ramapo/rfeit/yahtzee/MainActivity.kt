package ramapo.rfeit.yahtzee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ramapo.rfeit.yahtzee.ui.theme.YahtzeeTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.stringResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YahtzeeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WelcomePreview(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeText(modifier: Modifier = Modifier) {
    Surface(color = Color.LightGray) {
        Text(
            text = stringResource(R.string.yahtzee_intro),
            fontSize = 15.sp,
            lineHeight = 25.sp,
            modifier = modifier.padding(24.dp)
        )
    }
}

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        WelcomeText()
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomePreview(modifier: Modifier = Modifier) {
    YahtzeeTheme {
        Column {
            WelcomeScreen(modifier = modifier)
            DiceSet(modifier = modifier)
        }
    }
}

@Composable
fun Die(value: Int, locked: Boolean, modifier: Modifier = Modifier) {
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
        modifier = modifier
            .size(75.dp)
            .padding(2.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun DiceSet(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Die(1, false, modifier)
        Die(5,true, modifier)
        Die(3,true, modifier)
        Die(3, false, modifier)
        Die(2, false, modifier)
    }

}