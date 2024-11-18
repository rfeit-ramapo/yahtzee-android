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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import ramapo.rfeit.yahtzee.viewmodel.GameViewModel

enum class SelectLimit {
    NONE, ALL_AVAILABLE, ONE_AVAILABLE, DISABLED
}

@Preview(showBackground = true)
@Composable
fun ScorecardTable(
    gameViewModel: GameViewModel = GameViewModel(null),
    selectLimit: SelectLimit = SelectLimit.DISABLED
) {
    // Use collectAsState() for both StateFlows
    val scorecard by gameViewModel.scorecard.collectAsState()
    val selectedCategories by gameViewModel.selectedCategories.collectAsState()

    // For ALL_AVAILABLE and ONE_AVAILABLE modes, get available categories
    val availableCategories = remember(selectLimit) {
        when (selectLimit) {
            SelectLimit.ALL_AVAILABLE, SelectLimit.ONE_AVAILABLE ->
                gameViewModel.getAllAvailableCategories()
            else -> mutableListOf()
        }
    }

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

        LazyColumn(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            items(
                items = scorecard.categories,
                key = { category ->
                    category.hashCode()
                }
            ) { category ->
                val categoryIndex = scorecard.categories.indexOf(category)
                val isSelected = selectedCategories.contains(categoryIndex)

                // Determine if the category is clickable based on SelectLimit
                val isClickable = when (selectLimit) {
                    SelectLimit.NONE -> true
                    SelectLimit.DISABLED -> false
                    SelectLimit.ALL_AVAILABLE -> availableCategories.contains(categoryIndex)
                    SelectLimit.ONE_AVAILABLE -> availableCategories.contains(categoryIndex)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .then(
                            if (isClickable) {
                                Modifier.clickable {
                                    when (selectLimit) {
                                        SelectLimit.NONE ->
                                            gameViewModel.toggleSelectedCategory(categoryIndex)

                                        SelectLimit.ALL_AVAILABLE ->
                                            gameViewModel.toggleSelectedCategory(categoryIndex)

                                        SelectLimit.ONE_AVAILABLE -> {
                                            // Clear previous selections and select only this category
                                            gameViewModel.toggleSelectedCategory(
                                                categoryIndex,
                                                removesOthers = true
                                            )
                                        }
                                        else -> {}
                                    }
                                }
                            } else Modifier
                        )
                        .background(
                            when {
                                isSelected -> Color(0xFF6200EE).copy(alpha = 0.2f)
                                !isClickable -> Color.Gray.copy(alpha = 0.1f)
                                else -> Color.LightGray
                            },
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp)
                ) {
                    // Modify text color for non-clickable items
                    val textStyle = if (isClickable)
                        MaterialTheme.typography.bodyMedium
                    else
                        MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)

                    TableCell(
                        text = category.name,
                        modifier = Modifier.weight(1f),
                        style = textStyle
                    )
                    TableCell(
                        text = category.description,
                        modifier = Modifier.weight(2f),
                        style = textStyle
                    )
                    TableCell(
                        text = category.score.toString(),
                        modifier = Modifier.weight(1f),
                        style = textStyle
                    )
                    TableCell(
                        text = category.winner.ifEmpty { "-" },
                        modifier = Modifier.weight(1f),
                        style = textStyle
                    )
                    TableCell(
                        text = if (category.points > 0) category.points.toString() else "-",
                        modifier = Modifier.weight(1f),
                        style = textStyle
                    )
                    TableCell(
                        text = if (category.round > 0) category.round.toString() else "-",
                        modifier = Modifier.weight(1f),
                        style = textStyle
                    )
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
fun TableCell(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Text(
        text = text,
        style = style,
        modifier = modifier.padding(8.dp)
    )
}