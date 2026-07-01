package dev.emanueldias.fitcollectsmartwatch.data.model

import androidx.annotation.DrawableRes
import dev.emanueldias.fitcollectsmartwatch.R

enum class Sport(
    val displayName: String,
    @DrawableRes val iconRes: Int
) {
    RUNNING("Corrida", R.drawable.outline_directions_run_24),
    CYCLING("Ciclismo", R.drawable.outline_directions_bike_24),
    SWIMMING("Natação", R.drawable.outline_pool_24),
    GYM("Academia", R.drawable.outline_fitness_center_24),
    HIKING("Trilha", R.drawable.outline_mountain_flag_24),
    GYMNASTICS("Ginástica", R.drawable.outline_sports_gymnastics_24),
    HEART_RATE("Batimentos", R.drawable.rounded_ecg_heart_24)
}
