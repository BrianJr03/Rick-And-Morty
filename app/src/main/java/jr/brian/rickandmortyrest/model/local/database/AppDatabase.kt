package jr.brian.rickandmortyrest.model.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import jr.brian.rickandmortyrest.model.local.Character
import jr.brian.rickandmortyrest.model.local.Converters

@Database(
    entities = [Character::class],
    version = 5,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): CharacterDao
}