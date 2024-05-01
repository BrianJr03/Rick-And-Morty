package jr.brian.rickandmortyrest.model.local

data class Info(
    val count: Int,
    val next: String,
    val pages: Int,
    val prev: Any
)