# Foodly PAC 🍽️

A modern Android food management application built with Kotlin and Jetpack Compose for tracking pantry ingredients, discovering recipes, and monitoring nutritional statistics.

## 📱 Features

### Core Functionality
- **User Authentication** - Secure login and registration system
- **Pantry Management** - Track ingredients with quantities and units
- **Recipe Recommendations** - Get recipe suggestions based on available ingredients
- **Nutritional Statistics** - View weekly nutritional analysis and health scores
- **User Preferences** - Manage dietary preferences (vegan, vegetarian, gluten-free)

### Technical Highlights
- **Material 3 Design** - Modern UI following Material Design 3 guidelines
- **Jetpack Compose** - Declarative UI toolkit for native Android
- **MVVM Architecture** - Clean separation of concerns with ViewModels
- **Kotlin Coroutines** - Asynchronous programming for smooth user experience
- **Ktor Client** - HTTP client for API communication
- **StateFlow/LiveData** - Reactive data streams
- **SharedPreferences** - Local data persistence

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Networking**: Ktor Client
- **Serialization**: Kotlinx Serialization
- **Dependency Injection**: Manual DI
- **Testing**: JUnit, Mockito
- **Code Coverage**: Kover
- **Build System**: Gradle (Kotlin DSL)

## 📋 Prerequisites

- **Android Studio** Hedgehog | 2023.1.1 or later
- **JDK 17** or later
- **Android SDK** with minimum API level 24
- **Gradle 8.0+**

## ⚡ Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd foodlyPAC/PAC
```

### 2. Setup Java (if not already installed)
```bash
# Using Homebrew (macOS)
brew install openjdk@17
echo 'export JAVA_HOME="/opt/homebrew/opt/openjdk@17"' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verify installation
java -version
```

### 3. Open in Android Studio
1. Launch Android Studio
2. Open the project folder: `foodlyPAC/PAC`
3. Wait for Gradle sync to complete
4. Connect an Android device or start an emulator
5. Click **Run** (▶️) to build and install the app

### 4. API Configuration
Update the base URL in `BaseApiClient.kt` if needed:
```kotlin
private const val BASE_URL = "your-api-base-url"
```

## 🏗️ Project Structure

```
app/src/main/java/com/example/foodly/
├── api/                    # API clients and response models
│   ├── BaseApiClient.kt    # Base HTTP client with common functionality
│   ├── StatisticsApiClient.kt
│   └── response/           # Data models for API responses
├── home/                   # Main navigation and home screen
├── auth/                   # Authentication (login/registration)
├── pantry/                 # Pantry management features
├── recipes/                # Recipe recommendations
├── statistics/             # Nutritional statistics and analytics
├── settings/               # User settings and preferences
└── utils/                  # Utility classes and helpers
```

## 🎨 UI Architecture

The app follows **Material 3 Design** principles with:
- **Dynamic theming** based on system colors
- **Adaptive layouts** for different screen sizes
- **Bottom navigation** for main feature access
- **Consistent elevation** and surface treatments
- **Accessible** color contrasts and touch targets

### Key Screens
1. **HomeScreen** - Bottom navigation hub
2. **PantryScreen** - Ingredient management with floating action button
3. **RecipeRecommendationsScreen** - Recipe discovery interface
4. **StatisticsScreen** - Nutritional charts and health metrics
5. **SettingsScreen** - User profile and dietary preferences

## 🔗 API Integration

The app communicates with a backend API using **Ktor Client**:

### Base Response Format
```kotlin
data class BasicResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)
```

### Error Handling
- **Network errors** - Graceful degradation with user-friendly messages
- **API errors** - Specific error codes and messages from server
- **Loading states** - Visual feedback during data operations
- **Retry mechanisms** - Allow users to retry failed operations

## 🧪 Testing

### Unit Tests
Run simple unit tests for core functionality:

```bash
# Run all tests
./gradlew test

# Run specific test classes
./gradlew test --tests "com.example.foodly.SimpleNutritionalDataTest"
./gradlew test --tests "com.example.foodly.SimpleStatisticsResponseTest"
```

### Code Coverage with Kover
Generate code coverage reports:

```bash
# Generate HTML coverage report
./gradlew koverHtmlReport

# View report at: app/build/reports/kover/html/index.html
```

### Test Classes
- `SimpleNutritionalDataTest` - Data class validation
- `SimpleStatisticsResponseTest` - API response model testing

## 🚀 Build & Release

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Run Tests with Coverage
```bash
./gradlew check
```

## 📱 Features Deep Dive

### Pantry Management
- Add ingredients with autocomplete suggestions
- Track quantities with different units (grams, liters, pieces)
- Edit and delete pantry items
- Real-time pantry updates

### Recipe Recommendations
- Get recipes based on available ingredients
- View recipe details and nutritional information
- Filter by dietary preferences
- Save favorite recipes

### Nutritional Statistics
- Weekly nutritional breakdown (calories, carbs, fats, proteins, fiber)
- Health score calculation
- Interactive charts and visualizations
- Historical data tracking

### User Settings
- Manage dietary preferences (vegan, vegetarian, gluten-free)
- View and edit profile information
- Secure logout functionality
- Persistent preference storage

## 🔧 Configuration

### Gradle Configuration
The project uses **Gradle Kotlin DSL** with the following key configurations:

- **Compile SDK**: 34
- **Target SDK**: 34
- **Min SDK**: 24
- **Kotlin**: 1.9.10
- **Compose**: 2024.02.00

### Dependencies
- Jetpack Compose BOM for UI components
- Ktor for networking
- Kotlinx Serialization for JSON parsing
- Material 3 for design components
- Lifecycle ViewModels for state management

## 🐛 Troubleshooting

### Common Issues

**Java Runtime Not Found**
```bash
# Install OpenJDK and set JAVA_HOME
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
```

**Gradle Sync Failed**
- Clean project: `Build → Clean Project`
- Invalidate caches: `File → Invalidate Caches and Restart`

**Tests Not Running**
- Ensure JDK is properly configured
- Check test dependencies in `build.gradle.kts`

## 📄 License

This project is developed for educational purposes as part of a university course (PAC).

## 🤝 Contributing

This is an academic project. For any questions or suggestions, please contact the development team.

---

**Built with ❤️ using Kotlin and Jetpack Compose**