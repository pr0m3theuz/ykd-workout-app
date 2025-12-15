# YKD

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-blue)
![License](https://img.shields.io/badge/License-GPLv3-green)

An offline-first Android workout tracking application built with Jetpack Compose. Features advanced program management, real-time synchronization via Couchbase, and detailed analytics.

![App Screenshot](docs/images/placeholder.png)

## Key Features

*   **Workout Tracking:** Real-time session tracking, set/rep logging, and rest timers.
*   **Program Management:** Import/Export functionality (supporting PKL templates), Advanced Program Templates.
*   **Analytics:** Progress charts (Vico), Muscle Engagement Heatmaps, 1RM estimates.
*   **Sync & Backup:** Offline-first architecture using Couchbase Lite and Sync Gateway.
*   **User Profile:** Weight tracking, equipment profiles.

## Tech Stack

*   **Language:** Kotlin (JDK 17)
*   **UI:** Jetpack Compose (Material 3)
*   **Architecture:** MVVM, Koin (DI)
*   **Database:** Couchbase Lite (Local), Couchbase Server + Sync Gateway (Backend)
*   **Network/Sync:** Couchbase Replication, Protobuf
*   **Observability:** Firebase Crashlytics & Analytics, New Relic
*   **Testing:** JUnit 5, Espresso, Robolectric, Mockk

## Getting Started

### Prerequisites

*   **Android Studio:** Ladybug or newer recommended.
*   **JDK:** Version 17.
*   **Docker & Docker Compose:** Required for running the backend infrastructure.

### Installation

1.  **Clone the Repository**
    ```bash
    git clone https://github.com/your-repo/pr0m3theuz-workout-app.git
    cd pr0m3theuz-workout-app
    ```

2.  **Backend Setup**
    The backend services (Couchbase Server and Sync Gateway) are containerized.

    ```bash
    cd couchbase-config

    # Create the environment configuration
    cp .env.example .env

    # Edit .env and set your own secure passwords if needed
    # nano .env

    # Start the services
    docker-compose up -d
    ```

    *   **Note:** The database initializes with the bucket `workouts` and user `admin`.
    *   **Credentials:** Default local credentials are User: `Administrator`, Password: `bWHqoG2v3ko3fG5E` (or as configured in your `.env`).

3.  **Android Setup**

    *   **`google-services.json`:** The project integrates with Firebase.
        *   A sample file might be present at `app/google-services.json`, but for full functionality (or if you want to use your own Firebase project), you should generate your own.
        *   Go to the [Firebase Console](https://console.firebase.google.com/), create a project, add an Android app with package name `com.example.myapplication` (or `com.prometheuz.workout` if renamed), and download the `google-services.json`.
        *   Place the file in the `app/` directory.

    *   **Build:**
        Open the project in Android Studio and sync Gradle.
        ```bash
        ./gradlew build
        ```

## Configuration

### Environment Variables
The backend configuration is managed via the `.env` file in `couchbase-config/`.

| Variable | Description | Default |
| :--- | :--- | :--- |
| `COUCHBASE_ADMINISTRATOR_PASSWORD` | Admin password for Couchbase Console | `bWHqoG2v3ko3fG5E` |
| `COUCHBASE_RBAC_PASSWORD` | Password for the RBAC user | `bWHqoG2v3ko3fG5E` |

See `.env.example` for the full list of variables.

## Contribution

We welcome contributions! Please see our [CONTRIBUTING.md](CONTRIBUTING.md) for details.

*   **Code Style:** We use Kotlin Lint/Ktfmt.
*   **Testing:** Please run `./gradlew test` before submitting a Pull Request.

## License

This project is licensed under the **GPLv3 License**. See the [LICENSE](LICENSE) file for details.
