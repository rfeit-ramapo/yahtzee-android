package ramapo.rfeit.yahtzee.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ramapo.rfeit.yahtzee.R

@Preview(showBackground = true)
@Composable
fun InfoBox() {
    Column(horizontalAlignment = Alignment.Start) {
        RoundText()
    }
}

@Composable
fun RoundText(roundNum: Int = 1) {
    Text(
        text = stringResource(R.string.round) + roundNum.toString(),
        fontSize = 15.sp,
        lineHeight = 25.sp,
        modifier = Modifier.padding(2.dp)
    )
}