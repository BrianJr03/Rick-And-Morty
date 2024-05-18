package jr.brian.rickandmortyrest.view.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jr.brian.rickandmortyrest.R
import jr.brian.rickandmortyrest.model.local.Character
import jr.brian.rickandmortyrest.ui.theme.customPink
import jr.brian.rickandmortyrest.ui.theme.customRed
import jr.brian.rickandmortyrest.util.annotatedString
import jr.brian.rickandmortyrest.util.formatDate
import jr.brian.rickandmortyrest.util.getCharacterScreenColors
import jr.brian.rickandmortyrest.util.getStatusColor
import jr.brian.rickandmortyrest.view.composables.CharacterCard
import jr.brian.rickandmortyrest.util.ShakeConfig
import jr.brian.rickandmortyrest.util.rememberShakeController
import jr.brian.rickandmortyrest.util.addShakeController

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterScreen(
    character: Character,
    modifier: Modifier = Modifier,
    isSavedCharacter: Boolean = false,
    onDeleteCard: (Character) -> Unit
) {
    val (speciesColor, genderColor) = getCharacterScreenColors()
    val shakeController = rememberShakeController()
    LazyColumn(
        modifier = modifier,
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#${character.id}",
                    color = speciesColor,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            shakeController.shake(ShakeConfig.horizontalConfig())
                        },
                        onDoubleClick = {
                            shakeController.shake(ShakeConfig.verticalConfig())
                        }
                    )
                    .addShakeController(shakeController),
                imageModifier = Modifier.fillMaxSize()
            )
            Column(modifier = Modifier.padding(start = 15.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = character.species,
                        color = speciesColor,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "•", modifier = Modifier.padding(
                            start = 5.dp,
                            end = 5.dp
                        )
                    )
                    Text(
                        text = character.gender,
                        color = genderColor,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Status: ",
                        color = customPink,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "•",
                        modifier = Modifier.padding(end = 5.dp),
                        style = TextStyle(fontSize = 30.sp),
                        color = character.status.getStatusColor()
                    )
                    Text(
                        text = character.status,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                }
                Text(
                    text = annotatedString(
                        coloredText = "Created:",
                        regularText = character.created.formatDate(),
                    ),
                    modifier = Modifier.padding(bottom = 10.dp),
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = annotatedString(
                        coloredText = "From:",
                        regularText = character.origin.name,
                    ),
                    modifier = Modifier.padding(bottom = 10.dp),
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = annotatedString(
                        coloredText = "Resides:",
                        regularText = character.location.name,
                    ),
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                if (isSavedCharacter) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = customRed),
                        onClick = {
                            onDeleteCard(character)
                        }
                    ) {
                        Text(text = "Delete Card", color = Color.White)
                    }
                }
            }
        }
    }
}