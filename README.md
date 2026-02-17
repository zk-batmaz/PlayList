# PlayList

A modern Android application designed to discover games, view details, and search the extensive RAWG database. Built with industry-standard development practices.

## Tech Stack & Architecture
This project demonstrates the usage of Modern Android Development (MAD) skills:
* **Architecture:** Clean Architecture (Domain, Data, Presentation layers)
* **Design Pattern:** MVVM (Model-View-ViewModel)
* **UI:** Jetpack Compose (Modern declarative UI)
* **Network:** Retrofit2 & Gson (REST API consumption)
* **Asynchronous Programming:** Kotlin Coroutines & Flow
* **Image Loading:** Coil
* **Navigation:** Jetpack Compose Navigation
* **Data Management:** Pagination (Infinite scrolling) & Single Source of Truth principle

## Features
* Browse a vast library of games with infinite scrolling (Pagination).
* Search for specific games instantly.
* Master-Detail flow: Click on a game to view detailed descriptions, release dates, and high-quality cover images.
* Robust error handling and loading states managed via Sealed Classes.

## How to Run
1. Clone this repository.
2. Get a free API key from [RAWG.io](https://rawg.io/apidocs).
3. Insert your API key into the `GameRepositoryImpl` class.
4. Build and run the project in Android Studio.
