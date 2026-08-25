# FitCollectSmartwatch

FitCollectSmartwatch is a data collection application designed for Wear OS. It allows users to track their physical activities in real-time, gathering essential metrics such as heart rate, distance, calories burned, and duration across various workout modalities.

## Features

- **Workout Tracking**: Support for multiple sport modalities, including:
    - Running, Walking, Cycling, Swimming, Hiking (with distance tracking).
    - Gym, Elliptical, Gymnastics, Treadmill (stationary tracking).
- **Real-time Metrics**:
    - **Heart Rate**: Continuous monitoring during exercise.
    - **Distance & Calories**: Calculated based on the activity type.
    - **Duration**: Accurate tracking of active workout time.
- **Workout History**: Review past activities with detailed metrics and heart rate logs.
- **Ongoing Activity Support**: Integration with Wear OS Ongoing Activities, allowing users to return to the active workout easily from the watch face.
- **Persistent Storage**: All workout data is stored locally using Room, including time-stamped heart rate measurements.

## Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose for Wear OS](https://developer.android.com/training/wearables/compose)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Data Persistence**: [Room Database](https://developer.android.com/training/data-storage/room)
- **Fitness Integration**: [Health Services API](https://developer.android.com/training/wearables/health-services)
- **Background Processing**: Foreground Services for continuous tracking.
- **Dependency Injection**: Manual injection (ViewModelFactory).
- **Serialization**: [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) for workout data logs.

## Project Structure

- `data/`: Contains Room database definitions, entities, and data models.
- `health/`: Managers for Health Services API and sensor permissions.
- `presentation/`: Composable screens, ViewModels, and UI-related logic.
- `services/`: Background services (SportService) for handling active workout sessions.

## Getting Started

1. **Clone the repository**:
   ```bash
   git clone https://github.com/emanueldias/FitCollectSmartwatch.git
   ```
2. **Open in Android Studio**:
   Import the project and wait for Gradle sync.
3. **Permissions**:
   The app requires `BODY_SENSORS` and `ACTIVITY_RECOGNITION` permissions to function correctly. Ensure these are granted on the Wear OS device.
4. **Run**:
   Deploy to a Wear OS emulator or a physical smartwatch (API 30+).

## Permissions

To provide accurate tracking, the app requests:
- `android.permission.BODY_SENSORS`: To read heart rate data.
- `android.permission.ACTIVITY_RECOGNITION`: To detect physical activity.
- `android.permission.FOREGROUND_SERVICE_HEALTH`: To keep tracking active in the background.
