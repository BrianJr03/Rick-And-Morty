package jr.brian.rickandmortyrest.view.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import jr.brian.rickandmortyrest.model.local.Character

@Composable
fun CharacterCard(
    character: Character,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(15.dp),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Box {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = modifier.height(10.dp))
                AsyncImage(
                    model = character.image,
                    contentDescription = character.name,
                    modifier = modifier.fillMaxSize()
                )
                Text(
                    text = character.name,
                    modifier = modifier.padding(
                        10.dp
                    )
                )
            }
        }
    }
}