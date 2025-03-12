# Kioskable Overview

**Kioskable** is an Android application that transforms standard devices—tablets, TVs, or touchscreens—into secure, feature-rich display terminals. It is aimed at small-to-medium businesses (such as hotels, cafés, coworking spaces) that need kiosk-like functionality without purchasing expensive, proprietary hardware.

---

## Key Objectives

- **Hardware-Agnostic**: Runs on any device with the Android OS (tablet, smart TV, etc.).
- **Modular Features**: Users can enable or disable functions such as info display, self-ordering, staff portal, and local guide.
- **Easy Setup**: Minimal technical steps to lock down the device and configure content.
- **Branding & Customization**: Ability to incorporate a business's unique style with logos, color schemes, and layouts.

---

## Core Features & Functional Requirements

### 1. Kiosk/Lockdown Mode
- **Lock Task Mode Integration**: Ensures users cannot exit the Kioskable interface without proper credentials.
- **Disable OS Navigation**: Hides or disables Android navigation bars, notifications, and system dialogs.
- **Auto-Launch on Startup**: On device reboot, Kioskable automatically resumes kiosk mode.

### 2. Main Modules

#### Display (Info / Promotions / Digital Signage)
- **Purpose**: Show dynamic content such as images, videos, text slideshows, or external links.
- **Functionality**:
  - Upload or select from local assets.
  - Schedule content rotation (e.g., "Show Slide A for 5 seconds, then Slide B").
  - Include optional transition effects or looping.

#### Explore (Local Guides / Events)
- **Purpose**: Provide curated lists of attractions, restaurants, local shops, or events.
- **Functionality**:
  - Use a card-based layout with images and short descriptions.
  - Offer search or filter capabilities for extensive lists.
  - Allow marking of "favorites" or featuring highlighted items at the top.

#### Self-Ordering (Restaurants / Cafés)
- **Purpose**: Allow customers to browse menus and place orders directly.
- **Functionality**:
  - Display categories (e.g., Appetizers, Mains, Desserts).
  - Show menu items with images, descriptions, and prices; include an "Add to Cart" button.
  - Provide a cart summary with an itemized list, total cost, and a "Checkout" button.
  - Integrate payment processing (optional integration with payment gateways).
  - Option for automatic printing or emailing of receipts (with optional hardware integration).

#### Staff Portal (Clock In/Out)
- **Purpose**: Offer employees a time-tracking interface.
- **Functionality**:
  - Secure login or PIN entry for each staff member.
  - Display buttons such as "Clock In", "Take Break", "End Break", and "Clock Out".
  - Log work hours with options for potential backend synchronization or CSV export.

#### Future Add-Ons
- **Coming Soon**: Advanced features such as analytics dashboards, remote monitoring, multi-language support, etc.

---

## Detailed User Flows & UI Structure

### A. Kioskable Setup Screen

#### Initial Startup
- **Setup Wizard**: 
  - Prompts for a quick admin login/sign up.
  - Allows module selection (Display, Self-Ordering, Explore, Staff Portal).
  - Provides branding options (upload logo, choose primary color).

#### Main Configuration Page
- **Device Preview**: Shows the device or a simulation of how the content is displayed.
- **Module Toggles**: Lists the enabled modules with toggle switches.
- **Activate Kiosk Mode**: A button becomes available once setup is complete.

### B. Display Module Flow

#### Admin View
- **Manage Assets**: Upload local content and other assets.
- **Reorder Slides**: Organize the sequence of slides.
- **Set Durations**: Configure how long each slide or content piece is displayed.

#### End-User View
- **Content Presentation**: Full-screen rotating slides or static displays.
- **Optional Interaction**: May include touch interactions (e.g., tapping a slide for additional information).

### C. Explore Module Flow

#### Admin View
- **Content Management**: Add, edit, or remove local attractions or listed items.
- **Category Creation**: Optionally define categories (e.g., Restaurants, Sightseeing, Events).

#### End-User View
- **Layout Options**: Display content in a grid or list layout.
- **Detailed View**: Tap into items for extended information or external web links.
- **Navigation**: An always-visible "Back" or "Home" button for easy return.

### D. Self-Ordering Flow

#### Admin View
- **Menu Management**: Create categories and add menu items (with photos, descriptions, and prices).
- **Configuration**: Set up tax rules or disclaimers as needed.

#### Customer View
- **Navigation**: Display a category list on one side and menu items on the other.
- **Interactive Ordering**: "Add to Cart" button updates the visible cart summary.
- **Checkout Flow**: 
  - Provides order summary with total cost.
  - Integrates with payment gateways if available.
  - Moves to a final confirmation which can include on-screen confirmation, email receipt, or printed ticket.

### E. Staff Portal (Clock In/Out)

#### Admin Setup
- **Profile Management**: Create and manage staff profiles (name, unique PIN/password).
- **Rule Configuration**: Set up break rules or adjustments as needed.

#### Staff Interaction
- **Login Interface**: Provide secure PIN entry.
- **Shift Management**:
  - Display "Clock In" when a staff member is off-shift.
  - Switch to "Clock Out" when they are on-shift.
  - Include options for "Take Break" and "End Break" to update time logs.
- **Logging**: Track work hours and allow for potential backend synchronization or CSV export.

---

## Technical Architecture

### Frontend (Android App)
- **Language**: Primarily Kotlin using the standard Android SDK.
- **Kiosk Mode Implementation**: Use LockTaskMode or the "lock task" pinned screen feature available on Android 5.0+.
- **UI Framework**: Utilize Android native layouts, optionally enhanced with Jetpack Compose.

### Backend 
- **Purpose**: Store admin accounts, staff logs, menu items, images, etc.
- **Implementations**:
  - **MongoDB**: As the primary database for storing all application data in a flexible document-oriented structure.
  - **Spring Boot**: Java/Kotlin backend framework for building the RESTful API and business logic.
  - **Spring Data MongoDB**: For seamless MongoDB integration with repository pattern support.
  - **Spring Security**: For authentication, authorization, and API security.
  - **Local Storage**: Employ Room database in the Android app for offline configurations with online sync capabilities.

### Security Considerations
- **Secure Lockdown**: Prevent any unintended exit from the app or unauthorized access to the OS.
- **Encryption**: Encrypt sensitive data (e.g., staff PINs, admin credentials) at rest.
- **User Permissions**: Distinguish between admin-level functions (content editing, toggling modules) and end-user kiosk interactions.

### Customization & Theming
- **Brand Colors**: Allow businesses to choose primary accent colors for headers and buttons.
- **Logo Insertion**: Support inclusion of an uploaded logo in strategic locations (e.g., splash screen, top corners).
- **Layout Adjustments**: Provide simple drag-and-drop or toggle methods to rearrange modules on the home screen.

---

## Development Steps

1. **Core Kiosk Functionality**
   - Implement Lock Task Mode.
   - Ensure that the app auto-restarts and remains pinned on device reboot.
   
2. **Modular Architecture**
   - Develop separate packages for each module: Display, Explore, Self-Ordering, and Staff Portal.

3. **Admin UI & Permissions**
   - Create an internal admin panel that is accessible via secure login/PIN.
   - Allow management of modules, content (slides, items, local guides), and staff user accounts.
   - Provide a direct "Exit Kiosk Mode" option available only for admins.

4. **Testing & Compatibility**
   - Test on various Android versions (from Lollipop 5.0 to the latest).
   - Validate on different screen sizes (e.g., 7-inch tablets, 10-inch tablets, large TVs).
   - Optimize for minimal resource use and ensure application stability.

5. **Payment Integration (Optional)**
   - If self-ordering is enabled, integrate with payment gateways such as Stripe or PayPal.
   - Offer a fallback offline ordering mode when there is no network connectivity.

6. **Deployment**
   - Package the application as an APK or AAB for distribution on the Google Play Store or via private channels.
   - Include a setup wizard or tutorial for first-time installation.

---

## User Experience & Design Principles

- **Large Touch Targets**: Ensure buttons, tabs, and lists are large and accessible for kiosk usage from a distance.
- **Clear Navigation**: Incorporate prominently placed "Back" or "Home" buttons in each module.
- **High Contrast**: Use consistent high-contrast colors for headers, buttons, and content for readability in bright settings.
- **Accessible Language**: Use clear, concise labels (e.g., "Check In," "Clock Out," "Add to Cart") and consider multi-language support where needed.

---

## Example Use Cases

- **Hotel Lobby**
  - Display local attractions on a digital signage screen.
  - Use the Staff Portal for front-desk management.
  - Future integration with restaurant ordering for the hotel café.

- **Busy Café**
  - Set up a self-ordering kiosk on a tablet.
  - Provide real-time menu updates via an admin panel.
  - Integrate payment and receipt options to streamline service.

- **Coworking Space**
  - Display daily events or room availability with digital signage.
  - Use the Staff Portal for clock-in functionality for part-time reception staff.
  - Present local guide information for nearby dining options.

---

## Future Enhancements

- **Analytics Dashboard**: Track usage patterns, most viewed items, average session time, and popular menu items.
- **Remote Content Management**: Develop a web-based dashboard to push updates to multiple kiosks simultaneously.
- **Advanced Theming & Widgets**: Create drag-and-drop widgets (e.g., weather updates, news feeds, social media streams) for real-time updates.
- **Offline Operation**: Implement local data caching for reliable performance in areas with limited connectivity.

---

## Database Schema (MongoDB)

### Overview
Kioskable uses MongoDB as its primary database, leveraging its flexible document-oriented structure for efficient data storage and retrieval. The schema is designed to support all core modules while maintaining separation of concerns.

### Collections

#### 1. Users
```json
{
  "_id": ObjectId,
  "email": String,
  "passwordHash": String,
  "salt": String,
  "role": String, // "admin", "staff"
  "firstName": String,
  "lastName": String,
  "businessId": ObjectId, // Reference to Business
  "createdAt": Date,
  "updatedAt": Date,
  "lastLogin": Date
}
```

#### 2. Businesses
```json
{
  "_id": ObjectId,
  "name": String,
  "address": {
    "street": String,
    "city": String,
    "state": String,
    "zipCode": String,
    "country": String
  },
  "contactEmail": String,
  "contactPhone": String,
  "logo": {
    "url": String,
    "altText": String
  },
  "theme": {
    "primaryColor": String,
    "secondaryColor": String,
    "fontFamily": String
  },
  "enabledModules": [String], // ["display", "explore", "selfOrdering", "staffPortal"]
  "createdAt": Date,
  "updatedAt": Date
}
```

#### 3. Devices
```json
{
  "_id": ObjectId,
  "businessId": ObjectId, // Reference to Business
  "name": String,
  "deviceId": String, // Unique device identifier
  "status": String, // "active", "inactive", "maintenance"
  "lastPing": Date,
  "installedVersion": String,
  "osVersion": String,
  "screenSize": String,
  "activeModules": [String], // Which modules are enabled on this device
  "location": String, // Where device is placed, e.g., "Main Entrance"
  "createdAt": Date,
  "updatedAt": Date
}
```

#### 4. Content (Display Module)
```json
{
  "_id": ObjectId,
  "businessId": ObjectId,
  "title": String,
  "type": String, // "image", "video", "text", "webLink"
  "content": {
    "url": String, // For images/videos
    "text": String, // For text slides
    "link": String // For web links
  },
  "duration": Number, // In seconds
  "order": Number,
  "active": Boolean,
  "scheduleStart": Date, // Optional scheduling
  "scheduleEnd": Date,
  "createdAt": Date,
  "updatedAt": Date,
  "createdBy": ObjectId // Reference to User
}
```

#### 5. ExploreCategories
```json
{
  "_id": ObjectId,
  "businessId": ObjectId,
  "name": String,
  "description": String,
  "icon": String,
  "order": Number,
  "active": Boolean,
  "createdAt": Date,
  "updatedAt": Date
}
```

#### 6. ExploreItems
```json
{
  "_id": ObjectId,
  "businessId": ObjectId,
  "categoryId": ObjectId, // Reference to ExploreCategories
  "name": String,
  "description": String,
  "details": String, // Longer description
  "images": [
    {
      "url": String,
      "altText": String
    }
  ],
  "address": String,
  "contactInfo": {
    "phone": String,
    "email": String,
    "website": String
  },
  "featured": Boolean,
  "order": Number,
  "active": Boolean,
  "createdAt": Date,
  "updatedAt": Date
}
```

#### 7. MenuCategories
```json
{
  "_id": ObjectId,
  "businessId": ObjectId,
  "name": String,
  "description": String,
  "image": {
    "url": String,
    "altText": String
  },
  "order": Number,
  "active": Boolean,
  "createdAt": Date,
  "updatedAt": Date
}
```

#### 8. MenuItems
```json
{
  "_id": ObjectId,
  "businessId": ObjectId,
  "categoryId": ObjectId, // Reference to MenuCategories
  "name": String,
  "description": String,
  "price": Number,
  "discountPrice": Number, // Optional sale price
  "images": [
    {
      "url": String,
      "altText": String
    }
  ],
  "options": [
    {
      "name": String,
      "choices": [
        {
          "name": String,
          "priceAdjustment": Number // Can be positive or negative
        }
      ]
    }
  ],
  "allergens": [String],
  "dietary": [String], // "vegetarian", "vegan", "gluten-free", etc.
  "featured": Boolean,
  "order": Number,
  "active": Boolean,
  "createdAt": Date,
  "updatedAt": Date
}
```

#### 9. Orders
```json
{
  "_id": ObjectId,
  "businessId": ObjectId,
  "deviceId": ObjectId, // Which device placed the order
  "orderNumber": String, // Human-readable order number
  "status": String, // "pending", "preparing", "ready", "completed", "cancelled"
  "items": [
    {
      "menuItemId": ObjectId,
      "name": String,
      "price": Number,
      "quantity": Number,
      "options": [
        {
          "name": String,
          "choice": String,
          "priceAdjustment": Number
        }
      ],
      "subtotal": Number
    }
  ],
  "subtotal": Number,
  "tax": Number,
  "total": Number,
  "paymentStatus": String, // "pending", "paid", "failed"
  "paymentMethod": String, // "card", "cash", etc.
  "paymentDetails": {
    "transactionId": String,
    "processor": String
  },
  "customerInfo": {
    "name": String, // Optional for pickup orders
    "email": String,
    "phone": String
  },
  "notes": String,
  "createdAt": Date,
  "updatedAt": Date
}
```

#### 10. Staff
```json
{
  "_id": ObjectId,
  "businessId": ObjectId,
  "userId": ObjectId, // Optional reference to User
  "name": String,
  "pin": String, // Hashed PIN for clock in/out
  "role": String,
  "department": String,
  "hourlyRate": Number, // Optional
  "active": Boolean,
  "createdAt": Date,
  "updatedAt": Date
}
```

#### 11. TimeEntries
```json
{
  "_id": ObjectId,
  "businessId": ObjectId,
  "staffId": ObjectId, // Reference to Staff
  "clockIn": Date,
  "clockOut": Date,
  "breaks": [
    {
      "start": Date,
      "end": Date,
      "duration": Number // In minutes
    }
  ],
  "totalHours": Number,
  "notes": String,
  "createdAt": Date,
  "updatedAt": Date
}
```

#### 12. Settings
```json
{
  "_id": ObjectId,
  "businessId": ObjectId,
  "module": String,
  "settings": {
    // Module-specific settings stored as key-value pairs
  },
  "createdAt": Date,
  "updatedAt": Date
}
```

---

## Application Folder Structure

### Android Application Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/kioskable/app/
│   │   │   ├── application/
│   │   │   │   ├── App.kt
│   │   │   │   ├── di/            # Dependency Injection modules
│   │   │   │   └── AppConstants.kt
│   │   │   ├── data/
│   │   │   │   ├── local/         # Local data sources
│   │   │   │   │   ├── dao/       # Room DAOs
│   │   │   │   │   ├── db/        # Database setup
│   │   │   │   │   └── entity/    # Local database entities
│   │   │   │   ├── remote/        # Remote data sources
│   │   │   │   │   ├── api/       # Retrofit API interfaces
│   │   │   │   │   ├── dto/       # Data Transfer Objects
│   │   │   │   │   └── service/   # API service implementations
│   │   │   │   ├── repository/    # Repository implementations
│   │   │   │   └── preference/    # Shared Preferences management
│   │   │   ├── domain/
│   │   │   │   ├── model/         # Domain models
│   │   │   │   ├── repository/    # Repository interfaces
│   │   │   │   └── usecase/       # Use cases for each feature
│   │   │   ├── ui/
│   │   │   │   ├── base/          # Base classes for activities, fragments, etc.
│   │   │   │   ├── common/        # Common UI components
│   │   │   │   │   ├── adapter/   # Recycler adapters
│   │   │   │   │   ├── dialog/    # Custom dialogs
│   │   │   │   │   └── view/      # Custom views
│   │   │   │   ├── modules/
│   │   │   │   │   ├── display/   # Display module UI
│   │   │   │   │   ├── explore/   # Explore module UI
│   │   │   │   │   ├── ordering/  # Self-ordering module UI
│   │   │   │   │   └── staff/     # Staff portal module UI
│   │   │   │   ├── admin/         # Admin interface screens
│   │   │   │   ├── auth/          # Authentication screens
│   │   │   │   ├── setup/         # Setup wizard screens
│   │   │   │   └── main/          # Main kiosk interface
│   │   │   ├── util/              # Utility classes
│   │   │   │   ├── extension/     # Kotlin extensions
│   │   │   │   ├── formatter/     # Formatting utilities
│   │   │   │   └── validation/    # Input validation
│   │   │   └── service/           # Background services
│   │   │       ├── kiosk/         # Kiosk lockdown services
│   │   │       └── sync/          # Data synchronization
│   │   ├── res/                   # Resources
│   │   │   ├── drawable/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   └── xml/
│   │   └── AndroidManifest.xml
│   └── test/                      # Unit tests
│       └── java/
└── build.gradle                   # App module gradle file

# Backend Server Structure (Java/Kotlin with Spring Boot)

backend/
├── src/
│   ├── main/
│   │   ├── java/com/kioskable/api/
│   │   │   ├── KioskableApiApplication.java  # Main application entry point
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── MongoConfig.java           # MongoDB configuration
│   │   │   │   ├── SecurityConfig.java        # Security settings
│   │   │   │   ├── WebConfig.java             # Web configuration
│   │   │   │   └── SwaggerConfig.java         # API documentation
│   │   │   ├── controller/          # REST controllers
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── BusinessController.java
│   │   │   │   ├── ContentController.java
│   │   │   │   ├── ExploreController.java
│   │   │   │   ├── MenuController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   ├── StaffController.java
│   │   │   │   └── DeviceController.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── request/         # Request DTOs
│   │   │   │   └── response/        # Response DTOs
│   │   │   ├── exception/           # Custom exceptions
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── UnauthorizedException.java
│   │   │   ├── model/               # MongoDB document models
│   │   │   │   ├── User.java
│   │   │   │   ├── Business.java
│   │   │   │   ├── Device.java
│   │   │   │   ├── Content.java
│   │   │   │   ├── ExploreCategory.java
│   │   │   │   ├── ExploreItem.java
│   │   │   │   ├── MenuCategory.java
│   │   │   │   ├── MenuItem.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── Staff.java
│   │   │   │   ├── TimeEntry.java
│   │   │   │   └── Setting.java
│   │   │   ├── repository/          # MongoDB repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── BusinessRepository.java
│   │   │   │   ├── DeviceRepository.java
│   │   │   │   ├── ContentRepository.java
│   │   │   │   ├── ExploreCategoryRepository.java
│   │   │   │   ├── ExploreItemRepository.java
│   │   │   │   ├── MenuCategoryRepository.java
│   │   │   │   ├── MenuItemRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── StaffRepository.java
│   │   │   │   ├── TimeEntryRepository.java
│   │   │   │   └── SettingRepository.java
│   │   │   ├── security/            # Security components
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── UserPrincipal.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── service/             # Business logic services
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── BusinessService.java
│   │   │   │   ├── ContentService.java
│   │   │   │   ├── ExploreService.java
│   │   │   │   ├── MenuService.java
│   │   │   │   ├── OrderService.java
│   │   │   │   ├── StaffService.java
│   │   │   │   ├── DeviceService.java
│   │   │   │   └── FileStorageService.java
│   │   │   └── util/                # Utility classes
│   │   │       ├── DateUtils.java
│   │   │       ├── ValidationUtils.java
│   │   │       └── EncryptionUtils.java
│   │   ├── kotlin/com/kioskable/api/ # Kotlin-specific components
│   │   │   ├── extensions/          # Kotlin extensions
│   │   │   └── coroutines/          # Coroutine implementations
│   │   └── resources/
│   │       ├── application.properties # Main configuration file
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/
│       ├── java/com/kioskable/api/  # Java tests
│       │   ├── controller/
│       │   ├── service/
│       │   └── repository/
│       └── kotlin/com/kioskable/api/ # Kotlin tests
├── build.gradle                     # Gradle build configuration
└── settings.gradle                  # Gradle settings

# Database and File Storage
db/                                # MongoDB data directory
uploads/                           # Uploaded files storage
    ├── logos/                     # Business logos
    ├── content/                   # Display module content
    ├── menu/                      # Menu item images
    └── explore/                   # Explore item images

The above structure represents a comprehensive implementation of both the Android client application and the Java/Kotlin backend server. This architecture follows clean architecture principles with a clear separation of concerns between data, domain, and presentation layers.