package ramapo.rfeit.yahtzee.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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