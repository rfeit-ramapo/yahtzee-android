package ramapo.rfeit.yahtzee.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ramapo.rfeit.yahtzee.R
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun NextButton(onNext: () -> Unit) {
    Button(
        onClick = onNext,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black // Set background color to black
        ),
        modifier = Modifier.padding(15.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.next_button),
            contentDescription = null,
            modifier = Modifier.size(15.dp)
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
        modifier = Modifier.padding(15.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.roll_dice),
            contentDescription = null,
            modifier = Modifier.size(15.dp)
        )
    }
}

@Composable
fun ManualDiceInput(
    num: Int,
    onDiceValuesSubmit: (List<Int>) -> Unit // Callback function to access the dice values
) {
    // State for controlling the visibility of the dialog
    var showDialog by remember { mutableStateOf(false) }

    // State for storing the input values for dice faces
    val diceValues = remember { mutableStateListOf<String>().apply { repeat(num) { add("") } } }

    // Button that triggers the dialog
    Button(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black // Set background color to black
        ),
        modifier = Modifier.padding(15.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.keyboard),
            contentDescription = null,
            modifier = Modifier.size(15.dp)
        )
    }

    // Dialog that pops up when the button is clicked
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Enter Dice Values") },
            text = {
                Column {
                    // Input fields for each dice value
                    diceValues.forEachIndexed { index, value ->
                        OutlinedTextField(
                            value = value,
                            onValueChange = {
                                if (it.length <= 1 && it.all { char -> char.isDigit() && char in '1'..'6' }) {
                                    diceValues[index] = it
                                }
                            },
                            label = { Text("Die ${index + 1}") },
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Convert the dice input to integers and submit them
                        val diceList = diceValues.mapNotNull { it.toIntOrNull() }
                        if (diceList.size == num) {
                            onDiceValuesSubmit(diceList) // Pass the values to the callback
                        } else {
                            // Handle invalid input (you could show a toast, or another message)
                            println("Invalid input")
                        }
                        showDialog = false // Close the dialog after submitting
                    }
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false } // Close dialog without doing anything
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewManualDiceInput() {
    // Example usage of the composable
    ManualDiceInput(num = 3) { diceValues ->
        // This is where you handle the dice values after submission
        println("User entered dice values: $diceValues")
    }
}

@Composable
fun StandButton(onNext: () -> Unit) {
    Button(
        onClick = onNext,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black // Set background color to black
        ),
        modifier = Modifier.padding(15.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.stand),
            contentDescription = null,
            modifier = Modifier.size(15.dp)
        )
    }
}