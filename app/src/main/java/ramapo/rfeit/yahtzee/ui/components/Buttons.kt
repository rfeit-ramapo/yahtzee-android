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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun NextButton(onNext: () -> Unit) {
    Button(
        onClick = onNext,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black // Set background color to black
        ),
        modifier = Modifier.padding(10.dp)
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
        modifier = Modifier.padding(10.dp)
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
    onDiceValuesSubmit: (List<Int>) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val diceValues = remember { mutableStateListOf<String>().apply { repeat(num) { add("") } } }

    Button(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
        modifier = Modifier.padding(10.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.keyboard),
            contentDescription = null,
            modifier = Modifier.size(15.dp)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Enter Dice Values") },
            text = {
                // Wrap the Column in a ScrollableColumn
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .heightIn(max = 300.dp) // Set a maximum height
                            .verticalScroll(rememberScrollState()) // Make it scrollable
                    ) {
                        diceValues.forEachIndexed { index, value ->
                            OutlinedTextField(
                                value = value,
                                onValueChange = {
                                    if (it.length <= 1 && it.all { char -> char.isDigit() && char in '1'..'6' }) {
                                        diceValues[index] = it
                                    }
                                },
                                label = { Text("Die ${index + 1}") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val diceList = diceValues.mapNotNull { it.toIntOrNull() }
                        if (diceList.size == num) {
                            onDiceValuesSubmit(diceList)
                        } else {
                            println("Invalid input")
                        }
                        showDialog = false
                    }
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
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
        modifier = Modifier.padding(10.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.stand),
            contentDescription = null,
            modifier = Modifier.size(15.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SubmitButton(
    onNext: () -> Unit = {},
    validator: () -> Boolean = {true},
    errorMessageId: Int = R.string.error,
    showError: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (validator()) {
                    showError.value = false // Hide error if validation passes
                    onNext() // Proceed with the action
                } else {
                    showError.value = true // Show error if validation fails
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black // Set background color to black
            ),
            modifier = Modifier.padding(10.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.next_button),
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
        }

        // Error message
        if (showError.value) {
            Text(
                text = stringResource(errorMessageId),
                color = Color.Red,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpButton(onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black // Set background color to black
        ),
        modifier = Modifier.padding(10.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.help),
            contentDescription = null,
            modifier = Modifier.size(17.dp)
        )
    }
}