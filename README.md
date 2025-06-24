# AidConnect

AidConnect is an Android mobile application that serves as a crowdfunding platform for charitable campaigns and disaster relief efforts. The app connects donors with campaign creators, enabling users to create fundraising campaigns and make donations to support various causes.

## 🌟 Features

### Core Functionality
- **Campaign Management**: Create, view, update, and manage fundraising campaigns
- **Donation System**: Secure donation processing with multiple payment methods
- **User Authentication**: Firebase-based user registration and login system
- **Real-time Updates**: Live tracking of donation progress and campaign statistics

### User Features
- **Browse Campaigns**: Discover active fundraising campaigns across different categories
- **Campaign Creation**: Create detailed campaigns with images, descriptions, and funding goals
- **Donation Tracking**: View personal donation history and campaign contributions
- **Profile Management**: Manage user profile and account settings
- **Search & Filter**: Find campaigns by category or search terms

### Campaign Categories
- Health
- Charity
- Natural Disasters
- Others

### Payment Methods
- Bkash
- Nagad
- Rocket

## 🏗️ Architecture

### Technology Stack
- **Platform**: Android (Java)
- **Backend**: Firebase
  - Firebase Authentication
  - Firebase Firestore (Database)
  - Firebase Storage (Image storage)
- **UI Components**: Material Design
- **Charts**: MPAndroidChart for donation progress visualization
- **Image Loading**: Glide

### Project Structure
```
app/src/main/java/com/example/aidconnect/
├── Activities/
│   ├── SplashActivity.java          # App launch screen
│   ├── LoginActivity.java           # User authentication
│   ├── RegistrationActivity.java    # User registration
│   ├── CampaignActivity.java        # Main campaign listing
│   ├── CampaignDetailsActivity.java # Campaign details view
│   ├── CreateCampaignActivity.java  # Campaign creation
│   ├── UpdateCampaignActivity.java  # Campaign editing
│   ├── DonationActivity.java        # Donation processing
│   ├── ProfileActivity.java         # User profile
│   ├── MyCampaignsActivity.java     # User's campaigns
│   └── MyDonationsActivity.java     # User's donations
├── Models/
│   ├── Campaign.java                # Campaign data model
│   ├── Donation.java                # Donation data model
│   └── User.java                    # User data model
├── Adapters/
│   ├── CampaignAdapter.java         # Campaign list adapter
│   ├── MyCampaignAdapter.java       # User campaigns adapter
│   └── DonationAdapter.java         # Donations list adapter
└── BaseActivity.java                # Common activity functionality
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK (API level 24 or higher)
- Firebase project setup
- Google Services configuration

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd AidConnect
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the AidConnect directory

3. **Firebase Setup**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Add an Android app to your Firebase project
   - Download the `google-services.json` file
   - Place it in the `app/` directory

4. **Configure Firebase Services**
   - Enable Authentication (Email/Password)
   - Set up Firestore Database
   - Configure Firebase Storage

5. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```

### Firebase Configuration

#### Firestore Collections
The app uses the following Firestore collections:

- **users**: User profile information
- **campaigns**: Campaign details and metadata
- **donations**: Donation records and transactions

#### Storage Structure
- **campaign_images/**: Campaign cover images
- **user_profiles/**: User profile pictures (if implemented)

## 📱 App Flow

1. **Splash Screen**: App initialization and Firebase setup
2. **Authentication**: Login or registration for new users
3. **Campaign Listing**: Browse available campaigns with search and filter options
4. **Campaign Details**: View detailed information about specific campaigns
5. **Donation Process**: Select payment method and complete donation
6. **Profile Management**: View and edit user profile, manage campaigns and donations

## 🔧 Configuration

### Minimum Requirements
- **Android Version**: API 24 (Android 7.0)
- **Target SDK**: API 34
- **Compile SDK**: API 34
- **Java Version**: 1.8

### Dependencies
Key dependencies include:
- Firebase SDK (Auth, Firestore, Storage)
- Material Design Components
- Glide for image loading
- MPAndroidChart for progress visualization
- AndroidX libraries

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team

## 🔮 Future Enhancements

- Push notifications for campaign updates
- Social media integration
- Advanced analytics dashboard
- Multi-language support
- Enhanced payment gateway integration
- Campaign verification system

---

**AidConnect** - Connecting hearts through technology, one donation at a time.
