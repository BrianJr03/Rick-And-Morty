package jr.brian.rickandmortyrest.view.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import jr.brian.rickandmortyrest.model.local.Character
import jr.brian.rickandmortyrest.util.getCharacterScreenColors

@Composable
fun CharacterCard(
    character: Character,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier
) {
    val (primary, _) = getCharacterScreenColors()
    Card(
        modifier = modifier.padding(15.dp),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Box {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = modifier.height(10.dp))
                AsyncImage(
                    model = character.image,
                    modifier = imageModifier,
                    contentDescription = character.name,
                )
                Text(
                    text = character.name,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(10.dp),
                    color = primary
                )
            }
        }
    }
}