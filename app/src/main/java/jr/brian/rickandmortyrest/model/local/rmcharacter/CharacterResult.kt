package jr.brian.rickandmortyrest.model.local.rmcharacter

import jr.brian.rickandmortyrest.model.local.Character

data class CharacterResult(
    val info: Info,
    val results: List<Character>
)