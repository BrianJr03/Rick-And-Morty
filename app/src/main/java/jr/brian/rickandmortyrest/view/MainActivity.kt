package jr.brian.rickandmortyrest.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import jr.brian.rickandmortyrest.model.local.database.CharacterDao
import jr.brian.rickandmortyrest.model.remote.ApiWorker
import jr.brian.rickandmortyrest.ui.theme.RickAndMortyRESTTheme
import jr.brian.rickandmortyrest.view.screens.CharacterScreen
import jr.brian.rickandmortyrest.view.screens.HomeScreen
import jr.brian.rickandmortyrest.viewmodel.MainViewModel
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
        refreshCharacters { msg ->
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
        }
        setContent {
            RickAndMortyRESTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    dao?.let {
                        NavigationComposeShared(
                            dao = it,
                            viewModel = vm,
                            scaffoldPaddingValues = innerPadding
                        )
                    }
                }
            }
        }
    }

    private fun refreshCharacters(
        onStateChange: (msg: String) -> Unit
    ) {
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
                        onStateChange(msg)
                        Log.d(TAG, msg)
                    }

                    WorkInfo.State.FAILED -> {
                        val msg = "Operation Failed"
                        onStateChange(msg)
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
    scaffoldPaddingValues: PaddingValues
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
                    onCharacterClick = {
                        navController.navigate("character/${it.id}")
                    }
                )
            }

            composable(
                route = MainActivity.CHARACTER_SCREEN_ROUTE,
                arguments = listOf(navArgument(MainActivity.ID) { type = NavType.StringType })
            ) {
                CharacterScreen(
                    dao = dao,
                    backStackEntry = it
                )
            }
        }
    }
}