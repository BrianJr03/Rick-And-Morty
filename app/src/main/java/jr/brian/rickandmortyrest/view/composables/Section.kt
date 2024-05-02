package jr.brian.rickandmortyrest.view.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DividerSection(
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                start = 5.dp,
                end = 15.dp
            )
        )
        HorizontalDivider(
            thickness = 10.dp,
            modifier = Modifier
                .padding(end = 5.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
fun LabelSection(
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(10.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = label,
            style = TextStyle(fontSize = 20.sp)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}