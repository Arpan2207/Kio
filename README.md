# Kioskable

An Android application that transforms standard devices—tablets, TVs, or touchscreens—into secure, feature-rich display terminals. It is aimed at small-to-medium businesses (such as hotels, cafés, coworking spaces) that need kiosk-like functionality without purchasing expensive, proprietary hardware.

## Project Structure

- `app/` - Android application code
- `backend/` - Spring Boot backend API

## Development Environment Setup

### Prerequisites

- JDK 17
- Android Studio
- MongoDB
- Git

### Android Version Support

**Current Target:**
- Minimum SDK: 26 (Android 8.0 Oreo)
- Target SDK: 34
- Compile SDK: 35

> **Note:** The app currently only supports Android 8.0+ devices to utilize adaptive icons and other modern Android features. Support for older versions may be added in future releases.

### Backend Setup

1. Navigate to the backend directory:
   ```
   cd backend
   ```

2. Build the project:
   ```
   ./gradlew build
   ```

3. Run the application in development mode:
   ```
   ./gradlew bootRun --args='--spring.profiles.active=dev'
   ```

### Android App Setup

1. Open the project in Android Studio.

2. Build the app:
   ```
   ./gradlew assembleDebug
   ```

3. Install on device or emulator:
   ```
   ./gradlew installDebug
   ```

## Environment Configuration

### Development Environment
- Uses in-memory database or local MongoDB
- Debug logging enabled
- CSRF protection disabled for easier API testing

### Staging Environment
- Uses a staging database
- More restrictive security settings
- Simulates production environment for testing

### Production Environment
- Uses production database
- Full security features enabled
- Optimized for performance
- Uses environment variables for sensitive configuration

## Running with Different Profiles

### Backend
- Development: `java -jar app.jar --spring.profiles.active=dev`
- Production: `java -jar app.jar --spring.profiles.active=prod`

### Android App
- Debug build: For development and testing
- Release build: For production deployment

## CI/CD Workflow

The project uses GitHub Actions for continuous integration:

1. On code push or pull request to main:
   - Build is triggered
   - Tests are run
   - Code is analyzed

2. For deployment:
   - Manual approval triggers the deployment pipeline
   - Backend is deployed to cloud infrastructure
   - Android app is prepared for release 