package jr.brian.rickandmortyrest.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jr.brian.rickandmortyrest.R
import jr.brian.rickandmortyrest.model.local.Character
import jr.brian.rickandmortyrest.ui.theme.customGreen
import jr.brian.rickandmortyrest.ui.theme.customPink
import jr.brian.rickandmortyrest.ui.theme.customYellow
import jr.brian.rickandmortyrest.util.annotatedString
import jr.brian.rickandmortyrest.util.formatDate
import jr.brian.rickandmortyrest.util.getStatusColor
import jr.brian.rickandmortyrest.view.composables.CharacterCard

@Composable
fun CharacterScreen(
    character: Character,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#${character.id}",
                    color = customGreen,
                    modifier = Modifier.padding(start = 15.dp),
                    style = TextStyle(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.weight(.5f))
                Image(
                    painter = painterResource(id = R.drawable.rick_icon),
                    contentDescription = "Rick Icon",
                )
            }
            CharacterCard(
                character = character,
                modifier = Modifier.fillMaxWidth(),
                imageModifier = Modifier.fillMaxSize()
            )
            Column(modifier = Modifier.padding(start = 15.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = character.species, color = customGreen)
                    Text(
                        text = "•", modifier = Modifier.padding(
                            start = 5.dp,
                            end = 5.dp
                        )
                    )
                    Text(
                        text = character.gender,
                        color = customYellow
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Status: ",
                        color = customPink
                    )
                    Text(text = character.status)
                    Text(
                        text = "•",
                        modifier = Modifier.padding(
                            start = 5.dp,
                            end = 5.dp
                        ),
                        style = TextStyle(fontSize = 30.sp),
                        color = character.status.getStatusColor()
                    )
                }
                Text(
                    text = annotatedString(
                        coloredText = "Created:",
                        regularText = character.created.formatDate(),
                    ),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    text = annotatedString(
                        coloredText = "From:",
                        regularText = character.origin.name,
                    ),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    text = annotatedString(
                        coloredText = "Resides:",
                        regularText = character.location.name,
                    )
                )
            }
        }
    }
}