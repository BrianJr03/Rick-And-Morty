package jr.brian.rickandmortyrest.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint
import jr.brian.rickandmortyrest.model.AppState
import jr.brian.rickandmortyrest.model.local.Character
import jr.brian.rickandmortyrest.ui.theme.RickAndMortyRESTTheme
import jr.brian.rickandmortyrest.util.formatDate
import jr.brian.rickandmortyrest.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RickAndMortyRESTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        viewModel = vm,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val characters = remember { mutableStateOf<List<Character>>(emptyList()) }

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
                characters.value = it.results
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
        item(
            span = StaggeredGridItemSpan.FullLine
        ) {
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

        item(
            span = StaggeredGridItemSpan.FullLine
        ) {
            Button(
                shape = RectangleShape,
                modifier = Modifier.padding(
                    start = 15.dp,
                    end = 15.dp
                ),
                onClick = {
                    fm.clearFocus()
                    if (text.value.isNotBlank()) {
                        scope.launch {
                            viewModel.getCharacterByName(text.value)
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
        }

        item(
            span = StaggeredGridItemSpan.FullLine
        ) {
            if (isError.value) {
                characters.value = emptyList()
                Row(modifier = Modifier.padding(10.dp)) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = errorMsg.value,
                        style = TextStyle(fontSize = 20.sp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        items(characters.value.size) {
            Card(
                modifier = Modifier.padding(15.dp),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Box {
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        AsyncImage(
                            model = characters.value[it].image,
                            contentDescription = characters.value[it].name,
                            modifier = Modifier.fillMaxSize()
                        )
                        Text(
                            text = "Name: ${characters.value[it].name}",
                            modifier = Modifier.padding(
                                5.dp
                            )
                        )
                        Text(
                            text = "Status: ${characters.value[it].status}",
                            modifier = Modifier.padding(
                                5.dp
                            )
                        )
                        Text(
                            text = "Created: ${characters.value[it].created.formatDate()}",
                            modifier = Modifier.padding(
                                5.dp
                            )
                        )
                    }
                }
            }
        }
    }
}