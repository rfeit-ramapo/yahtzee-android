package ramapo.rfeit.yahtzee.ui.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ramapo.rfeit.yahtzee.R
import ramapo.rfeit.yahtzee.viewmodel.GameViewModel
import java.io.File

@Preview(showBackground = true)
@Composable
fun InfoBox(
    gameViewModel: GameViewModel = GameViewModel(null),
    onHelp: (() -> Unit)? = {}
) {
    val showLogDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Get info to display from the GameViewModel
    val roundNum = gameViewModel.roundNum.observeAsState(1).value
    val rollNum = gameViewModel.rollNum.observeAsState(null).value
    val humScore = gameViewModel.humScore.observeAsState(0).value
    val compScore = gameViewModel.compScore.observeAsState(0).value


    // Log dialog
    if (showLogDialog.value) {
        AlertDialog(
            onDismissRequest = { showLogDialog.value = false },
            title = { Text("Game Log") },
            text = {
                val logFile = File(context.filesDir, "log.txt")
                val logContent = if (logFile.exists()) logFile.readText() else "No log entries found."

                Box(modifier = Modifier.fillMaxHeight(0.7f)) {
                    Text(
                        text = logContent,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLogDialog.value = false }) {
                    Text("Close")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .padding(2.dp)
            .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(2.dp),
            horizontalAlignment = Alignment.Start
        ) {
            InfoText(
                stringResource(R.string.round) +
                        roundNum.toString() +
                        if (rollNum != null) ";   " + stringResource(R.string.roll) + rollNum.toString()
                        else "")
            InfoText(stringResource(R.string.hum_score) + humScore.toString())
            InfoText(stringResource(R.string.comp_score) + compScore.toString())
        }

        // Combined help and log buttons
        Column(horizontalAlignment = Alignment.End) {
            if (onHelp != null) HelpButton(onHelp)

            // Log button
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp))
                    .clickable { showLogDialog.value = true }
                    .padding(4.dp)
            ) {
                Text("Log", fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryFillInputBox(
    isHuman: Boolean = true,
    roundInput: MutableState<String> = remember { mutableStateOf("") },
    pointsInput: MutableState<String> = remember { mutableStateOf("") },
    showHelp: MutableState<Boolean> = remember { mutableStateOf(false) },
) {


    Row(
        modifier = Modifier
            .padding(2.dp)
            .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (isHuman) InfoText("Input the following for the selected category:")
            else InfoText("The computer's scorecard info is shown below:")

            HorizontalDivider(thickness = 2.dp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.width(100.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    InfoText("Round")
                    InfoText("Points Scored")
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        enabled = isHuman && !showHelp.value,
                        value = roundInput.value,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull()?.let { it > 0 } == true)) {
                                roundInput.value = newValue
                                Log.d("CategoryFillInputBox", "Set roundInput to: '${roundInput.value}'")
                            }
                        },
                        modifier = Modifier
                            .width(80.dp)
                            .height(48.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 14.sp)
                    )
                    OutlinedTextField(
                        enabled = isHuman && !showHelp.value,
                        value = pointsInput.value,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.toIntOrNull()?.let { it > 0 } == true)) {
                                pointsInput.value = newValue
                                Log.d("CategoryFillInputBox", "Set pointsInput to: '${pointsInput.value}'")
                            }
                        },
                        modifier = Modifier
                            .width(80.dp)
                            .height(48.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 14.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoText(string: String) {
    Text(
        text = string,
        fontSize = 15.sp,
        lineHeight = 25.sp,
        modifier = Modifier.padding(2.dp)
    )
}

// scorecard
// current scores
// view log button