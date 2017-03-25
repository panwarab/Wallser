# Wallser
A wallpaper browsing and downloading app

## Rubric

- [x] App integrates a third-party library.

   > App uses Google's Volley API for making asynchronous network calls.
   
   > App uses Picasso API for image caching and downloading
   
- [x] App validates all input from servers and users. If data does not exist or is in the wrong format, the app logs this fact and does not crash.
   
   > In such a case, App displays a message on the relevant screen

- [x] App includes support for accessibility. That includes content descriptions, navigation using a D-pad, and, if applicable, non-audio versions of audio cues.

- [x] App keeps all strings in a strings.xml file and enables RTL layout switching on all layouts.

- [x] App provides a widget to provide relevant information to the user on the home screen.
    
    > App uses a stack widget to display all the favorite wallpapers
    
- [x] If Analytics is used, the app creates only one analytics instance. If Analytics was not used, meets specifications.

- [x] If Admob is used, the app displays test ads. If Admob was not used,meets specifications.

- [x] App theme extends AppCompat.

- [x] App uses an app bar and associated toolbars.

- [x] App uses standard and simple transitions between activities.

    > App uses shared element transition

    > App uses a left_in and right_out transition once user marks an image as unfavorite.

- [x] App builds from a clean repository checkout with no additional configuration.

- [x] App builds and deploys using the installRelease Gradle task.

- [x] App is equipped with a signing configuration, and the keystore and passwords are included in the repository. Keystore is referred to by a relative path.

- [x] All app dependencies are managed by Gradle.

    > Gradle of filth
    
- [x] App stores data locally either by implementing a ContentProvider or using Firebase

- [x] It it performs short duration, on-demand requests(such as search), app uses an AsyncTask.

    > App uses Async task to make Database operations.
    
- [x] App uses a Loader to move its data to its views.

    > Loader Manager is deployed on Cursor Loader to move data to views.
    
 - [x] Implement sharing functionality in your app, making use of intent extras to share rich content (i.e. a paragraph of content-specific text, a link and description, an image, etc).
 
 - [x] material design patterns such as shared element transitions across activities
    



    
