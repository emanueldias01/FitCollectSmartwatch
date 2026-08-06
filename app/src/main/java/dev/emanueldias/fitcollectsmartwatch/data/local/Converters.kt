package dev.emanueldias.fitcollectsmartwatch.data.local

import androidx.room.TypeConverter
import dev.emanueldias.fitcollectsmartwatch.data.model.Sport

class Converters {
    @TypeConverter
    fun fromSport(sport: Sport): String {
        return sport.name
    }

    @TypeConverter
    fun toSport(value: String): Sport {
        return Sport.valueOf(value)
    }
}