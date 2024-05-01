package jr.brian.rickandmortyrest.view

import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import coil.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint
import jr.brian.rickandmortyrest.model.AppState
import jr.brian.rickandmortyrest.model.local.Character
import jr.brian.rickandmortyrest.model.local.database.CharacterDao
import jr.brian.rickandmortyrest.model.remote.ApiWorker
import jr.brian.rickandmortyrest.ui.theme.RickAndMortyRESTTheme
import jr.brian.rickandmortyrest.util.formatDate
import jr.brian.rickandmortyrest.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()
    private lateinit var workRequest: PeriodicWorkRequest

    var dao: CharacterDao? = null
        @Inject set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        refreshCharacters()
        setContent {
            RickAndMortyRESTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    dao?.let {
                        HomeScreen(
                            dao = it,
                            viewModel = vm,
                            progressStr = progressStr,
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    private fun refreshCharacters() {
        workRequest = PeriodicWorkRequest.Builder(
            ApiWorker::class.java,
            15,
            TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder().apply {
                    setRequiredNetworkType(NetworkType.CONNECTED)
                    setRequiresBatteryNotLow(true)
                }.build()
            )
            .build()

        val workManager = WorkManager.getInstance(this)

        workManager.enqueue(workRequest)

        workManager
            .getWorkInfoByIdLiveData(workRequest.id)
            .observe(this) { workInfo: WorkInfo ->

                when (workInfo.state) {
                    WorkInfo.State.ENQUEUED -> {
                        val msg = "Operation Enqueued"
                        Log.d(TAG, msg)
                    }

                    WorkInfo.State.RUNNING -> {
                        val msg = "Operation Running"
                        Log.d(TAG, msg)
                    }

                    WorkInfo.State.SUCCEEDED -> {
                        val msg =
                            "Room DB has been updated"
                        Log.d(TAG, msg)
                    }

                    WorkInfo.State.FAILED -> {
                        val msg = "Operation Failed"
                        Log.d(TAG, msg)
                    }

                    WorkInfo.State.BLOCKED -> {
                        val msg = "Operation Blocked"
                        Log.d(TAG, msg)
                    }

                    WorkInfo.State.CANCELLED -> {
                        val msg = "Operation Cancelled"
                        Log.d(TAG, msg)
                    }
                }
            }

    }

    companion object {
        const val TAG = "MyWorker"
        var progressStr = ""
    }
}

@Composable
fun HomeScreen(
    dao: CharacterDao,
    progressStr: String,
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
        item(
            span = StaggeredGridItemSpan.FullLine
        ) {
            Row(modifier = Modifier.padding(10.dp)) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = progressStr,
                    style = TextStyle(fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

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
                    charactersFromSearch.value = emptyList()
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
                dao.removeAllCharacters()
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

        items(charactersFromSearch.value.size) {
            CharacterCard(character = charactersFromSearch.value[it])
        }

        if (charactersFromSearch.value.isNotEmpty()) {
            item(span = StaggeredGridItemSpan.FullLine) {
                HorizontalDivider(thickness = 10.dp)
            }
        }

        items(characters.value.size) {
            CharacterCard(character = characters.value[it])
        }
    }
}

@Composable
fun CharacterCard(character: Character) {
    Card(
        modifier = Modifier.padding(15.dp),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Box {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = character.image,
                    contentDescription = character.name,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = "Name: ${character.name}",
                    modifier = Modifier.padding(
                        5.dp
                    )
                )
                Text(
                    text = "Status: ${character.status}",
                    modifier = Modifier.padding(
                        5.dp
                    )
                )
                Text(
                    text = "Created: ${character.created.formatDate()}",
                    modifier = Modifier.padding(
                        5.dp
                    )
                )
            }
        }
    }
}