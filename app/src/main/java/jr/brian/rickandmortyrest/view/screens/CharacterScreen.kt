package jr.brian.rickandmortyrest.view.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jr.brian.rickandmortyrest.model.local.Character
import jr.brian.rickandmortyrest.util.formatDate
import jr.brian.rickandmortyrest.view.composables.CharacterCard

@Composable
fun CharacterScreen(
    character: Character,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            CharacterCard(
                character = character,
                modifier = Modifier.fillMaxWidth(),
                imageModifier = Modifier.fillMaxSize()
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = character.species)
                Text(
                    text = "•", modifier = Modifier.padding(
                        start = 5.dp,
                        end = 5.dp
                    )
                )
                Text(text = character.gender)
            }
            Text(text = "Created: ${character.created.formatDate()}")
            Text(text = "Status: ${character.status}")
            Text(text = "From: ${character.origin.name}")
            Text(text = "Resides: ${character.location.name}")
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}