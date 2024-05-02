package jr.brian.rickandmortyrest.view.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import jr.brian.rickandmortyrest.model.local.Character
import jr.brian.rickandmortyrest.model.local.database.CharacterDao
import jr.brian.rickandmortyrest.view.MainActivity
import jr.brian.rickandmortyrest.view.composables.CharacterCard

@Composable
fun CharacterScreen(
    dao: CharacterDao,
    backStackEntry: NavBackStackEntry,
    modifier: Modifier = Modifier,
) {
    val id = backStackEntry.arguments?.getInt(MainActivity.ID) ?: 1
    val character = dao.getCharacterById(id).collectAsState(initial = Character.EMPTY)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val char = character.value ?: Character.EMPTY
        CharacterCard(character = char)
        Text(text = char.gender)
        Text(text = char.created)
        Text(text = char.species)
    }
}