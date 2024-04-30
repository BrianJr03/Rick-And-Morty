package jr.brian.rickandmortyrest.model.local

data class Character(
    val created: String,
    val episode: List<String>,
    val gender: String,
    val id: Int,
    val image: String,
    val location: Location,
    val name: String,
    val origin: Origin,
    val species: String,
    val status: String,
    val type: String,
    val url: String
) {
    companion object {
        val EMPTY = Character(
            created = "",
            episode = listOf(),
            gender = "",
            id = 0,
            image = "",
            location = Location(name = "", url = ""),
            name = "",
            origin = Origin(name = "", url = ""),
            species = "",
            status = "",
            type = "",
            url = ""
        )
    }
}