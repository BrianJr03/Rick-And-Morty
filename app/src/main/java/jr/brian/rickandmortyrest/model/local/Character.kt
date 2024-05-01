package jr.brian.rickandmortyrest.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "characters")
data class Character(
    @PrimaryKey val id: Int,
    val created: String,
    val episode: List<String>,
    val gender: String,
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

class Converters {
    @TypeConverter
    fun fromLocation(location: Location): String {
        return Gson().toJson(location)
    }

    @TypeConverter
    fun toLocation(value: String): Location {
        return Gson().fromJson(value, Location::class.java)
    }

    @TypeConverter
    fun fromOrigin(origin: Origin): String {
        return Gson().toJson(origin)
    }

    @TypeConverter
    fun toOrigin(value: String): Origin {
        return Gson().fromJson(value, Origin::class.java)
    }

    @TypeConverter
    fun fromEpisode(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromEpisode(list: List<String>): String {
        return Gson().toJson(list)
    }
}