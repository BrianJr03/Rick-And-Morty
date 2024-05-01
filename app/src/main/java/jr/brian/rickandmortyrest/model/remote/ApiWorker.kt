package jr.brian.rickandmortyrest.model.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import jr.brian.rickandmortyrest.model.AppState
import jr.brian.rickandmortyrest.model.Repository
import jr.brian.rickandmortyrest.model.local.database.CharacterDao
import javax.inject.Inject

@HiltWorker
class ApiWorker @AssistedInject constructor(
    private val repository: Repository,
    @Assisted val context: Context,
    @Assisted workerParameters: WorkerParameters
) :
    CoroutineWorker(context, workerParameters) {
    var dao: CharacterDao? = null
        @Inject set

    private val pageNumbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

    private fun isInternetConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override suspend fun doWork(): Result {
        if (isInternetConnected()) {
            repository.getAllCharacters(pageNumbers.random()).collect {
                when (val currentState = it) {
                    is AppState.Success -> {
                        currentState.data?.let { characterResult ->
                            dao?.let { dao ->
                                characterResult.results.onEach { character ->
                                    dao.insertCharacter(character)
                                }
                            }
                        }
                    }

                    is AppState.Error -> {
                        currentState.data.let {
                            Log.i("myTag", "Failed")
                        }
                    }

                    is AppState.Loading -> {
                        Log.i("myTag", "Loading")
                    }

                    is AppState.Idle -> {
                        Log.i("myTag", "Idle")
                    }
                }
            }
            return Result.Success.success()
        } else {
            return Result.retry()
        }
    }
}