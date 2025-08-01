# AuthenticSneaker

A modern Android app built with Jetpack Compose for authentic sneaker authentication and marketplace.

## Features

### 🔐 Authentication Flow
- **Splash Screen**: Beautiful animated splash screen with AuthenticSneaker branding
- **Login Screen**: Modern login interface with email/password authentication
- **Sign Up Screen**: Complete registration form with validation
- **Google Sign-In**: Integration ready for Google authentication
- **Navigation**: Seamless flow between authentication screens
- **Session Management**: Automatic login state persistence

### 🛍️ Marketplace (CRUD Operations)
- **Browse Sneakers**: View all available sneakers with beautiful cards
- **Search & Filter**: Search by name and filter by brand
- **Add Sneakers**: Create new sneaker listings with detailed forms (CREATE)
- **View Details**: See comprehensive sneaker information (READ)
- **Update Sneakers**: Edit existing sneaker data (UPDATE)
- **Delete Sneakers**: Remove sneaker listings (DELETE)
- **Real-time Updates**: Instant synchronization with Firebase

### ❤️ Favorites System (CRUD Operations)
- **Add to Favorites**: Save sneakers to personal collection (CREATE)
- **View Favorites**: Browse your saved sneakers with timestamps (READ)
- **Remove Favorites**: Delete from favorites list (DELETE)
- **Real-time Sync**: Instant updates across all screens

### 🎨 UI/UX Design
- **Dark Theme**: Modern dark gradient design with cyan accents
- **Material Design 3**: Latest Material Design components
- **Responsive Layout**: Optimized for different screen sizes
- **Smooth Animations**: Loading states and transitions
- **Form Validation**: Real-time input validation and error handling
- **Image Loading**: Efficient image display with Coil
- **Error Handling**: User-friendly error messages and retry options

### Technical Stack
- **Jetpack Compose**: Modern declarative UI toolkit
- **Firebase Authentication**: Complete authentication system
- **Firebase Firestore**: Real-time database for CRUD operations
- **Material 3**: Latest Material Design components
- **Kotlin**: Modern Android development language
- **MVVM Architecture**: Clean separation of concerns
- **Coroutines**: Asynchronous operations
- **Coil**: Image loading library

## Screens

### 1. Splash Screen
- Animated logo and branding
- Loading indicator
- Auto-navigation to login after 2.5 seconds

### 2. Login Screen
- Email and password fields
- Show/hide password functionality
- "Forgot Password" link
- Google Sign-In button
- Navigation to Sign Up screen
- Form validation

### 3. Sign Up Screen
- Full name, email, password, and confirm password fields
- Terms and conditions checkbox
- Password confirmation validation
- Google Sign-In option
- Navigation back to Login screen

### 4. Home Screen (Placeholder)
- Welcome message
- Logout functionality
- Ready for main app content integration

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on device or emulator

## Firebase Setup

The app is configured for Firebase integration. To complete the setup:

1. Add your `google-services.json` file to the `app/` directory
2. Configure Firebase Authentication in the Firebase Console
3. Enable Google Sign-In provider
4. Update the authentication logic in the respective screen files

## Customization

### Colors
The app uses a consistent color scheme:
- Primary: `#00D4FF` (Cyan)
- Background: Dark gradient from `#1E1E1E` to `#2D2D2D`
- Text: White and Gray variants

### Branding
- Replace the "AS" logo placeholder with your actual logo
- Update the app name and taglines
- Customize the splash screen animation

## Next Steps

1. Implement actual Firebase authentication logic
2. Add password strength validation
3. Implement forgot password functionality
4. Add biometric authentication
5. Create the main app content (sneaker marketplace)
6. Add user profile management
7. Implement push notifications

## Dependencies

The app uses the following key dependencies:
- Jetpack Compose BOM
- Material 3
- Firebase Auth
- Firebase Database
- Google Sign-In
- AndroidX Core and Lifecycle

## License

This project is licensed under the MIT License. 