package ramapo.rfeit.yahtzee.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import ramapo.rfeit.yahtzee.viewmodel.GameViewModel

@Preview(showBackground = true)
@Composable
fun ScorecardTable(
    gameViewModel: GameViewModel = GameViewModel(null),
) {
    // Use collectAsState() for both StateFlows
    val scorecard by gameViewModel.scorecard.collectAsState()
    val selectedCategories by gameViewModel.selectedCategories.collectAsState()
    val expandedState = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            TableHeader("Category", Modifier.weight(1f))
            TableHeader("Description", Modifier.weight(2f))
            TableHeader("Score", Modifier.weight(1f))
            TableHeader("Winner", Modifier.weight(1f))
            TableHeader("Points", Modifier.weight(1f))
            TableHeader("Round", Modifier.weight(1f))
        }
        HorizontalDivider(thickness = 1.dp, color = Color.Black)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (expandedState.value) Modifier.fillMaxHeight() else Modifier.height(300.dp)
                )
        ) {
            items(
                items = scorecard.categories,
                key = { category ->
                    category.hashCode()
                }
            ) { category ->
                val isSelected = selectedCategories.contains(scorecard.categories.indexOf(category))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            val index = scorecard.categories.indexOf(category)
                            gameViewModel.toggleSelectedCategory(index)
                        }
                        .background(
                            if (isSelected) Color(0xFF6200EE).copy(alpha = 0.2f) else Color.LightGray,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp)
                ) {
                    TableCell(category.name, Modifier.weight(1f))
                    TableCell(category.description, Modifier.weight(2f))
                    TableCell(category.score.toString(), Modifier.weight(1f))
                    TableCell(category.winner.ifEmpty { "-" }, Modifier.weight(1f))
                    TableCell(if (category.points > 0) category.points.toString() else "-", Modifier.weight(1f))
                    TableCell(if (category.round > 0) category.round.toString() else "-", Modifier.weight(1f))
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun TableHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        modifier = modifier.padding(8.dp)
    )
}

@Composable
fun TableCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier.padding(8.dp)
    )
}