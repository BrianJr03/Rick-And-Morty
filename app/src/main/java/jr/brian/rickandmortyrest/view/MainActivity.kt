package jr.brian.rickandmortyrest.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import jr.brian.rickandmortyrest.model.local.database.CharacterDao
import jr.brian.rickandmortyrest.ui.theme.RickAndMortyRESTTheme
import jr.brian.rickandmortyrest.view.screens.HomeScreen
import jr.brian.rickandmortyrest.viewmodel.MainViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    var dao: CharacterDao? = null
        @Inject set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        fetchDataInBackground(this) { msg ->
//            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
//        }
        setContent {
            RickAndMortyRESTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    dao?.let {
                        NavigationComposeShared(
                            dao = it,
                            viewModel = vm,
                            scaffoldPaddingValues = innerPadding,
                            onFinish = { finish() }
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val ID = "id"
        const val TAG = "MyWorker"
        const val HOME_SCREEN_ROUTE = "home"
        const val CHARACTER_SCREEN_ROUTE = "character/{id}"
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavigationComposeShared(
    dao: CharacterDao,
    viewModel: MainViewModel,
    scaffoldPaddingValues: PaddingValues,
    onFinish: () -> Unit
) {
    SharedTransitionLayout {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = MainActivity.HOME_SCREEN_ROUTE
        ) {
            composable(route = MainActivity.HOME_SCREEN_ROUTE) {
                HomeScreen(
                    dao = dao,
                    viewModel = viewModel,
                    modifier = Modifier.padding(scaffoldPaddingValues),
                    onFinish = { onFinish() }
                )
            }

            composable(
                route = MainActivity.CHARACTER_SCREEN_ROUTE,
                arguments = listOf(navArgument(MainActivity.ID) { type = NavType.StringType })
            ) {
//                CharacterScreen(
//                    dao = dao,
//                    backStackEntry = it
//                )
            }
        }
    }
}