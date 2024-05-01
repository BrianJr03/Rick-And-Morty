package jr.brian.rickandmortyrest.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import jr.brian.rickandmortyrest.model.AppState
import jr.brian.rickandmortyrest.model.local.Character
import jr.brian.rickandmortyrest.model.local.database.CharacterDao
import jr.brian.rickandmortyrest.view.composables.CharacterCard
import jr.brian.rickandmortyrest.view.composables.DividerSection
import jr.brian.rickandmortyrest.view.composables.LabelSection
import jr.brian.rickandmortyrest.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    dao: CharacterDao,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val characters = dao.getCharacters().collectAsState(initial = emptyList())
    val charactersFromSearch = remember { mutableStateOf<List<Character>>(emptyList()) }

    val fm = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val text = remember { mutableStateOf("") }
    val errorMsg = remember { mutableStateOf("") }

    val isLoading = remember { mutableStateOf(false) }
    val isError = remember { mutableStateOf(false) }

    val state = viewModel.state.collectAsState()

    when (val currentState = state.value) {
        is AppState.Success -> {
            isLoading.value = false
            isError.value = false
            currentState.data?.let {
                it.results.onEach { character ->
                    dao.insertCharacter(character)
                }
                charactersFromSearch.value = it.results
            }
        }

        is AppState.Error -> {
            isLoading.value = false
            isError.value = true
            errorMsg.value = currentState.data
        }

        is AppState.Loading -> {
            isError.value = false
            isLoading.value = true
        }

        is AppState.Idle -> {
            isLoading.value = false
        }
    }

    LazyVerticalStaggeredGrid(
        horizontalArrangement = Arrangement.Center,
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier,
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            Spacer(modifier = Modifier.height(15.dp))
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            TextField(
                value = text.value,
                onValueChange = {
                    text.value = it
                },
                label = {
                    Text(text = "Character Name")
                },
                modifier = Modifier.padding(
                    start = 15.dp,
                    end = 15.dp,
                    bottom = 15.dp
                )
            )
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            Row(horizontalArrangement = Arrangement.Center) {
                Button(
                    shape = RectangleShape,
                    modifier = Modifier.padding(
                        start = 15.dp,
                        bottom = 10.dp,
                        end = 15.dp
                    ),
                    onClick = {
                        if (!isLoading.value) {
                            fm.clearFocus()
                            charactersFromSearch.value = emptyList()
                            if (text.value.isNotBlank()) {
                                scope.launch {
                                    viewModel.getCharacterByName(text.value)
                                }
                            }
                        }
                    }
                ) {
                    if (isLoading.value) {
                        CircularProgressIndicator(color = Color.Black)
                    } else {
                        Text(text = "Search")
                    }
                }

                Button(
                    shape = RectangleShape,
                    modifier = Modifier.padding(
                        start = 15.dp,
                        bottom = 10.dp,
                        end = 15.dp
                    ),
                    onClick = {
                        dao.removeAllCharacters()
                    }
                ) {
                    Text(text = "Clear All")
                }
            }
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            if (isError.value) {
                LabelSection(label = errorMsg.value)
            }
        }

        if (charactersFromSearch.value.isNotEmpty()
            && charactersFromSearch.value != characters.value
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                DividerSection(label = "Search Results")
            }
        }

        if (charactersFromSearch.value.isNotEmpty()
            && charactersFromSearch.value != characters.value
        ) {
            items(charactersFromSearch.value.size) {
                CharacterCard(character = charactersFromSearch.value[it])
            }
        }

        if (characters.value.isNotEmpty()) {
            item(span = StaggeredGridItemSpan.FullLine) {
                DividerSection(label = "Saved")
            }
        }

        items(characters.value.size) {
            CharacterCard(character = characters.value[it])
        }

        if (characters.value.isEmpty()) {
            item(span = StaggeredGridItemSpan.FullLine) {
                LabelSection(label = "No Characters Saved")
            }
        }
    }
}