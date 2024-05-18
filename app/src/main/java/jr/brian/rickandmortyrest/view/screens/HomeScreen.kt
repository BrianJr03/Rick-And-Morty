package jr.brian.rickandmortyrest.view.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import jr.brian.rickandmortyrest.model.AppState
import jr.brian.rickandmortyrest.model.local.Character
import jr.brian.rickandmortyrest.model.local.database.CharacterDao
import jr.brian.rickandmortyrest.util.getScaleAndAlpha
import jr.brian.rickandmortyrest.util.showShortToast
import jr.brian.rickandmortyrest.view.composables.CharacterCard
import jr.brian.rickandmortyrest.view.composables.CustomDialog
import jr.brian.rickandmortyrest.view.composables.DividerSection
import jr.brian.rickandmortyrest.view.composables.LabelSection
import jr.brian.rickandmortyrest.viewmodel.MainViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    dao: CharacterDao,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onFinish: () -> Unit
) {
    val characters = dao.getCharacters().collectAsState(initial = emptyList())
    val charactersFromSearch = remember { mutableStateOf<List<Character>>(emptyList()) }
    val selectedCharacter = remember { mutableStateOf(Character.EMPTY) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val text = remember { mutableStateOf("") }
    val errorMsg = remember { mutableStateOf("") }

    val isLoading = remember { mutableStateOf(false) }
    val isError = remember { mutableStateOf(false) }
    val isShowingCharacterDialog = remember { mutableStateOf(false) }
    val isSavedCharacter = remember { mutableStateOf(false) }
    val isConfirmationRowShowing = remember { mutableStateOf(false) }
    val canShowConfirmationRow = isConfirmationRowShowing.value
            && characters.value.isNotEmpty()

    val appState = viewModel.state.collectAsState()
    val gridState = rememberLazyStaggeredGridState()

    val backPressTime = remember { mutableLongStateOf(0L) }
    val backPressJob = remember { mutableStateOf<Job?>(null) }

    val handleBackPress = {
        focusManager.clearFocus()
        isConfirmationRowShowing.value = false
        scope.launch {
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressTime.longValue <= 2000) {
                onFinish()
            } else {
                gridState.animateScrollToItem(0)
                backPressTime.longValue = currentTime
                context.showShortToast("One more time to exit")
                backPressJob.value?.cancel()
                backPressJob.value = launch {
                    delay(3000)
                }
            }
        }
    }

    BackHandler {
        handleBackPress()
    }

    val searchOnClick = {
        focusManager.clearFocus()
        if (!isLoading.value) {
            charactersFromSearch.value = emptyList()
            if (text.value.isNotBlank()) {
                scope.launch {
                    viewModel.getCharactersByName(text.value)
                }
            }
        }
    }

    when (val currentState = appState.value) {
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

    CustomDialog(
        showDialog = isShowingCharacterDialog.value,
        onDismissRequest = { isShowingCharacterDialog.value = false }
    ) {
        CharacterScreen(
            character = selectedCharacter.value,
            isSavedCharacter = isSavedCharacter.value,
            onDeleteCard = {
                dao.removeCharacter(it)
                isShowingCharacterDialog.value = false
            }
        )
    }

    LazyVerticalStaggeredGrid(
        state = gridState,
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        columns = StaggeredGridCells.Fixed(2),
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            Spacer(modifier = Modifier.height(15.dp))
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            TextField(
                value = text.value,
                onValueChange = {
                    text.value = it
                    isConfirmationRowShowing.value = false
                },
                label = {
                    Text(text = "Name of R&M Character")
                },
                keyboardActions = KeyboardActions(
                    onSearch = {
                        searchOnClick()
                        isConfirmationRowShowing.value = false
                    }
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier
                    .padding(
                        start = 15.dp,
                        end = 15.dp,
                        bottom = 15.dp
                    )
                    .onFocusChanged {
                        if (it.hasFocus) {
                            isConfirmationRowShowing.value = false
                        }
                    },
            )
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            AnimatedVisibility(visible = canShowConfirmationRow.not()) {
                Row(horizontalArrangement = Arrangement.Center) {
                    Button(
                        shape = RectangleShape,
                        modifier = Modifier.padding(
                            start = 15.dp,
                            bottom = 10.dp,
                            end = 15.dp
                        ),
                        onClick = {
                            searchOnClick()
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
                            isConfirmationRowShowing.value =
                                isConfirmationRowShowing.value.not()
                        }
                    ) {
                        Text(text = "Clear All")
                    }
                }
            }
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            AnimatedVisibility(visible = canShowConfirmationRow) {
                Row(horizontalArrangement = Arrangement.Center) {
                    Button(
                        shape = RectangleShape,
                        modifier = Modifier.padding(
                            start = 15.dp,
                            bottom = 10.dp,
                            end = 15.dp
                        ),
                        onClick = {
                            focusManager.clearFocus()
                            charactersFromSearch.value = emptyList()
                            dao.removeAllCharacters()
                            isConfirmationRowShowing.value = false
                        }
                    ) {
                        Text(text = "Yes")
                    }

                    Button(
                        shape = RectangleShape,
                        modifier = Modifier.padding(
                            start = 15.dp,
                            bottom = 10.dp,
                            end = 15.dp
                        ),
                        onClick = {
                            isConfirmationRowShowing.value = false
                        }
                    ) {
                        Text(text = "No")
                    }
                }
            }
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            if (isError.value) {
                LabelSection(label = "An error occurred. Please try again.")
            }
        }

        if (charactersFromSearch.value.isNotEmpty()
            && charactersFromSearch.value != characters.value
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                DividerSection(label = "Search Results (${charactersFromSearch.value.size})")
            }
        }

        if (charactersFromSearch.value.isNotEmpty()
            && charactersFromSearch.value != characters.value
        ) {
            items(charactersFromSearch.value.size) {
                val (scale, alpha) = getScaleAndAlpha(
                    index = it,
                    gridState = gridState
                )
                CharacterCard(
                    character = charactersFromSearch.value[it],
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            alpha = alpha,
                            scaleX = scale,
                            scaleY = scale
                        )
                        .clickable {
                            selectedCharacter.value = charactersFromSearch.value[it]
                            isShowingCharacterDialog.value = true
                            isSavedCharacter.value = false
                        }
                )
            }
        }

        if (characters.value.isNotEmpty()) {
            item(span = StaggeredGridItemSpan.FullLine) {
                DividerSection(label = "Saved (${characters.value.size})")
            }
        }

        items(characters.value.size) {
            val (scale, alpha) = getScaleAndAlpha(
                index = it,
                gridState = gridState
            )
            CharacterCard(
                character = characters.value[it],
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        alpha = alpha,
                        scaleX = scale,
                        scaleY = scale
                    )
                    .clickable {
                        selectedCharacter.value = characters.value[it]
                        isShowingCharacterDialog.value = true
                        isSavedCharacter.value = true
                    }
            )
        }

        if (characters.value.isEmpty()) {
            item(span = StaggeredGridItemSpan.FullLine) {
                LabelSection(label = "No Characters Saved")
            }
        }
    }
}