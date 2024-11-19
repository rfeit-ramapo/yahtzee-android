package ramapo.rfeit.yahtzee.ui.screens

import android.app.Activity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ramapo.rfeit.yahtzee.viewmodel.GameViewModel

@Preview(showBackground = true)
@Composable
fun SerializeSaveScreen(
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
            text = "Save and quit:",
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
                text = "Error: Unable to save the file. Please check the name and try again.",
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        val app = (LocalContext.current as Activity)
        // Submit button
        Button(onClick = {
            // Call serializeLoad and handle success/failure
            val isSaved = gameViewModel.serializeSave(fileName)

            if (isSaved) {
                // Quit the game
                println("Saved! Exiting game.")
                app.finish()
            } else {
                isError.value = true // Show error message if saving fails
            }
        }) {
            Text("Save File")
        }
    }
}