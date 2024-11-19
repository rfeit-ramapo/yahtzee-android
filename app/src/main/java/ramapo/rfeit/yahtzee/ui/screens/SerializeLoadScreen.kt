package ramapo.rfeit.yahtzee.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ramapo.rfeit.yahtzee.viewmodel.GameViewModel

@Preview(showBackground = true)
@Composable
fun SerializeLoadScreen(
    onNext: () -> Unit = {},
    gameViewModel: GameViewModel = GameViewModel(null)
) {
    // Track the file name entered by the user
    val fileName = remember { mutableStateOf("") }
    // Track if there was an error loading the file
    val isError = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Load a saved game:",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // TextField for file name input
        val textState = remember { mutableStateOf("") }

        TextField(
            value = fileName.value,
            onValueChange = { fileName.value = it },
            label = { Text("File Name") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Error message
        if (isError.value) {
            Text(
                text = "Error: Unable to load the file. Please check the name and try again.",
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Submit button
        Button(onClick = {
            // Call serializeLoad and handle success/failure
            val isLoaded = gameViewModel.serializeLoad(fileName)
            if (isLoaded) {
                gameViewModel.logLine("Successfully loaded game from ${fileName.value}")
                onNext() // Go to the next screen if successful
            } else {
                gameViewModel.logLine("Unable to load game from ${fileName.value}")
                isError.value = true // Show error message if loading fails
            }
        }) {
            Text("Load File")
        }
    }
}