# Album Gallery

Album Gallery is an Android application for organizing and viewing photo albums. Users can create albums, add photos to them, and view them in a gallery format.

## Features

- **Create Albums**: Users can create new albums to organize their photos.
- **Add Photos**: Users can add photos to their albums from their device's gallery or by taking new photos with the camera.
- **View Albums**: Albums are displayed in a grid format, allowing users to easily browse through them.
- **View Photos**: Users can view individual photos in fullscreen mode with zoom functionality.
- **Delete Photos**: Users can delete photos from their albums.
- **Search**: Users can search for specific photos or albums using keywords.
- **Security**: The application provides options for securing albums with passwords.

## Technologies Used

- **Language**: Java
- **Database**: Firebase Realtime Database
- **Authentication**: Firebase Authentication
- **Image Processing**: Firebase ML Kit for text recognition and image labeling
- **UI**: Android XML layout files

## Installation

To run the application locally, follow these steps:

1. Clone the repository:
   [git clone https://github.com/<username>/Album-Gallery.git](https://github.com/Huy203/Album-Gallery.git)

2. Open the project in Android Studio.

3. Set up Firebase:
- Create a new Firebase project on the Firebase Console.
- Connect the project to your Android app by following the setup instructions and downloading the `google-services.json` file.
- Place the `google-services.json` file in the `app` directory of your project.

4. Build and run the project on an Android device or emulator.

## Usage

- Upon launching the application, users are prompted to sign in or sign up.
- After authentication, users land on the home screen where they can see their albums and navigate to different sections of the app.
- Users can create new albums, add photos to existing albums, view albums, and view individual photos.
- Various features such as searching for photos, securing albums with passwords, and performing text recognition on images are accessible from the home screen.

## Contributing

Contributions are welcome! If you'd like to contribute to this project, please fork the repository and submit a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
