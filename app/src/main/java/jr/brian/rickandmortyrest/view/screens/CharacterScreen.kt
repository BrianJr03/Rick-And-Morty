package jr.brian.rickandmortyrest.view.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jr.brian.rickandmortyrest.model.local.Character
import jr.brian.rickandmortyrest.util.formatDate
import jr.brian.rickandmortyrest.view.composables.CharacterCard

@Composable
fun CharacterScreen(
    modifier: Modifier = Modifier,
    character: Character
) {
    Column(modifier = modifier) {
        CharacterCard(character = character)
        Text(text = character.gender)
        Text(text = character.created.formatDate())
        Text(text = character.species)
    }
}