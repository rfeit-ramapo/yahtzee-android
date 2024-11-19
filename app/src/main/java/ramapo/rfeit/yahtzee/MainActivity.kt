/************************************************************
* Name:  Rebecca Feit                                      *
* Project:  Yahtzee Android (Java/Kotlin)                  *
* Class:  CMPS 366                                         *
* Date:  11/20/2024                                        *
************************************************************/

/*

example comment

/**
 * Calculates the average grade in a class.
 *
 * @param grades Array of individual grades (passed by value).
 * @param size Number of students in the class (integer).
 * @return The average grade in the class (real value).
 *
 * @algorithm
 * 1) Add all the grades.
 * 2) Divide the sum by the number of students to calculate the average.
 *
 * @reference None.
 */


 */


package ramapo.rfeit.yahtzee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import ramapo.rfeit.yahtzee.ui.theme.YahtzeeTheme

// Class to hold the entire game activity.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YahtzeeTheme {
                YahtzeeApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

/**
 * Displays the main UI composable for the entire Yahtzee app.
 *
 * @param modifier Modifier applied to the layout node (default: [Modifier]).
 * @return Unit
 *
 * @reference None.
 */
@Preview(showBackground = true)
@Composable
fun YahtzeeApp(modifier: Modifier = Modifier) {
    Surface(color = Color.LightGray) {
        ScreenManager()
    }
}

