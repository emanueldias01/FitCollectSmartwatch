package dev.emanueldias.fitcollectsmartwatch.data.model

import androidx.annotation.DrawableRes
import dev.emanueldias.fitcollectsmartwatch.R

enum class Sport(
    val displayName: String,
    @DrawableRes val iconRes: Int,
    val tracksDistance: Boolean = false
) {
    RUNNING("Corrida", R.drawable.outline_directions_run_24, true),
    WALKING("Caminhada", R.drawable.outline_directions_walk_24, true),
    CYCLING("Ciclismo", R.drawable.outline_directions_bike_24, true),
    ELLIPTICAL("Elíptico", R.drawable.outline_pedal_bike_24, false),
    SWIMMING("Natação", R.drawable.outline_pool_24, true),
    GYM("Academia", R.drawable.outline_fitness_center_24, false),
    HIKING("Trilha", R.drawable.outline_mountain_flag_24, true),
    GYMNASTICS("Ginástica", R.drawable.outline_sports_gymnastics_24, false),
    TREADMILL("Esteira", R.drawable.outline_directions_run_24, false)
}
