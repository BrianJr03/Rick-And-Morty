package jr.brian.rickandmortyrest.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import jr.brian.rickandmortyrest.model.AppState
import jr.brian.rickandmortyrest.model.local.Character
import jr.brian.rickandmortyrest.ui.theme.RickAndMortyRESTTheme
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
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            val fm = LocalFocusManager.current
            val scope = rememberCoroutineScope()

            val text = remember { mutableStateOf("") }
            val errorMsg = remember { mutableStateOf("") }

            val isLoading = remember { mutableStateOf(false) }
            val isError = remember { mutableStateOf(false) }

            val characters = remember { mutableStateOf(Character.EMPTY) }

            val state = viewModel.state.collectAsState()

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = text.value,
                onValueChange = {
                    text.value = it
                },
                label = {}
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = {
                fm.clearFocus()
                if (text.value.isNotBlank()) {
                    scope.launch {
                        viewModel.getCharacterById(text.value)
                    }
                }
            }) {
                Text(text = "Get User")
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (val currentState = state.value) {
                is AppState.Success -> {
                    isLoading.value = false
                    isError.value = false
                    currentState.data?.let {
                        characters.value = it
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

            if (isLoading.value) {
                CircularProgressIndicator()
            } else if (isError.value) {
                Text(text = errorMsg.value)
            } else {
                Card(Modifier.padding(10.dp)) {
                    Text(
                        text = "Name: ${characters.value.name}",
                        modifier = Modifier.padding(
                            10.dp
                        )
                    )
                    Text(
                        text = "Status: ${characters.value.status}",
                        modifier = Modifier.padding(
                            10.dp
                        )
                    )
                }
            }
        }
    }
}