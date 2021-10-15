<p align="center">
   <img src="https://user-images.githubusercontent.com/61379336/137374988-b12e6c7b-2d54-448e-a6ed-73ff5b9aa47c.png">
</p>

# Symmetrical spoon
Symmetrical spoon is an application which encourages users to explore their nearby areas and  complete various tasks to receive ingame rewards. It follows the MVVM design pattern.

## Getting started

The application will work just by downloading or cloning the repository, there are no APIs used  which would require any API key setup.

## Tech used
* Kotlin
* MVVM design pattern
* Material design
* Internal sensor (step detector sensor)
*  Fragment
*  LiveData
* DataBinding
* Room Database
* Graph
* Retrofit
* SharedPreferences
* Location & Geocoding
* Glide
* ARCore & Sceneform
* Navigation & Safe Args
* Coroutines

## Features

* #### Interactive map which displays randomly generated locations within 5km radius with latitude/longitude values that have various tasks generated within them.
    * Upon reaching the location physically, users are given the possibility to enter AR (Augmented Reality) mode to start the task that has been generated for this location.
    * Tasks can include 4 different kinds of objectives to complete:
        1. **Normal quiz**, where user is required to answer correctly to a simple multiple choice question
        2. **Image quiz**, where the user is required to recognize the image they are presented
        3. **Find the correct image**, where the user is presented multiple images around them and they have to find the matching image to the task description
        4. **Find the spheres**, where there are randomly generated 3D spheres around the user which user has to collect

     * Upon completing this task the user is rewarded with the currency that was generated for the location.

* #### There is a reward shop in the application which users are able to browse and spend in-game currency at
    * Rewards are 3D models that users are able to play with in an always available AR session that is available once user has atleast one reward purchased
    * All of the rewards are bought with the currecies that are generated for completing the AR tasks mentioned above
    * The amount of currencies required vary between the purchasable objects, the more interesting rewards are more expensive whereas the cheap rewards might not be so interesting
    * Some of the rewards are animated and might have sound included in them as well

* #### Graphs
    * Graphs are used to display users activity by displaying how much of the different currencies they have collected
    * The activity level is displayed with a simple barchart
* #### Daily quests
    * Daily quests are tasks that are generated once per day to offer a special task to user to gain some of the rarer currencies
* #### Step counter
    * Feature that allows users to track their daily step count as they would ideally complete the AR tasks by walking to the locations

## Data persistence

The application uses Room SQLite database for data persistence. Included tables are:

1. **Mapdetails table** for storing single day entries, including collected currency amounts
2. **Latitude/Longitude table** for storing single latitude/longitude values with associated rewards and gametypes. Contains foreign key to Mapdetails table, a single day can have 15 lat/lng entries
3. **Inventory table** table which is used to save all currency values
4. **CollectedItem table** table which contains the information of the rewards bought from the in-game shop
5. **DailyQuest table** for storing the daily quest associated with a single day. Contains foreign key to Mapdetails table

## Web services

The application uses an API called [ *Open Trivia Database*](https://opentdb.com/), the API doesn't
require any API key for usage. This API is used to generate the content for normal quizzes.

Content for *Image quiz*, *Find the correct image task* and *Rewards displayed in the reward shop* are acquired from single .json files by fetching them from a webdisk
using [Retrofit](https://square.github.io/retrofit/).

The application doesn't have any backend service connected to it, but simple modifications on the aforementioned json files allow for some simple modifications and additions to content without requiring updating from the client side.

## Map

A library called [Osmdroid](https://github.com/osmdroid/osmdroid) is used for creating the map in the app's mapview, it's also responsible for drawing markers and polygons in the map and measuring the distance between user and selected marker.

User's current location is received  from [fused location provider](https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient.html), which is provided by the Google Location Services API, part of Google Play Services, which provides a more powerful, high-level framework that automates tasks such as location provider choice and power management.

## Game AR

Augmented Reality is provided by [ARCore](https://developers.google.com/ar) which is Google’s platform for building augmented reality experiences. Using different APIs, ARCore enables the phone to sense its environment, understand the world and interact with information.

The scene graph API used to create the virtual objects is called Sceneform, it's responsible for displaying the simple widgets, images and 3D objects that appear in the AR fragment of the application. Sceneform was deprecated and open sourced by Google a few years ago but this application uses a [fork](https://thomasgorisse.github.io/sceneform-android-sdk/) which is maintained by community.

## App design
<p align="center">
<img src="https://user-images.githubusercontent.com/61379336/137375886-b90ae5f0-c74e-4f6a-a729-4e7b9979f6dd.png">
   Color theme
   </p>

This application is designed under the [material design guidelines](https://material.io/design).


## Sensors
This application uses a step detector sensor to store the number of steps that the user has taken.
  Step sensor is a kind of [motion sensor](https://developer.android.com/guide/topics/sensors/sensors_motion#java), and it generates an event every single time when the user takes a step. Latency is expected to be less than 2 seconds.

To keep track of your steps, physical activity permission must be allowed in the application.

