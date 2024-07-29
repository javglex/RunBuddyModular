RunBuddy (⚠️ under construction ⚠️)
-----

Offline-first Android application for tracking runs (ktor/room/koin/multi-module/convention-plugins). Create accounts, track runs, and perform background syncs.
Includes wearOS app for controlling workouts and sending heart rate data.

## Getting Started
Clone this repository (main branch), build & run.

For auth key, please email me directly at javglex@gmail.com. Otherwise, you'll be able to download the app from the playstore shortly.

## Introduction
This run tracking application demonstrates multi-module architecture to separate features and layers, each module configured via convention plugins to facilitate build config management.
This enforces the data flow rule when communicating between layers (data/domain/presentation).
One feature in particular (analytics screen) is separated into a dynamic feature, to showcase benefits of multi-module architecture.

## Overview
Some of the main screens include:

| Home Dark | Home Light | Tracking Run |
|--------------|--------------|--------------|
| ![Screenshot_20240624_220552](https://github.com/user-attachments/assets/61c32225-55ee-42ef-a4db-0a34dccbc319) | ![Screenshot_20240624_220254](https://github.com/user-attachments/assets/feca1c32-6686-4746-bc94-4f7d42882a5e) | ![Screenshot_20240624_222814](https://github.com/user-attachments/assets/0edc07a5-6c7f-43f4-af17-c71796cb74ed) |

| Intro | Sign-In Light | Sign-in Dark |
|--------------|--------------|--------------|
| ![Screenshot_20240624_220410](https://github.com/user-attachments/assets/3e590b86-cad8-4df4-a687-ee1a9691c330) | ![Screenshot_20240624_220443](https://github.com/user-attachments/assets/b088be28-caba-47da-9979-932cbb80e314) | ![Screenshot_20240624_220509](https://github.com/user-attachments/assets/f570931d-f9c5-41f9-80ae-8b181d6886d3) |

| WearOS pre-workout | WearOS in-workout
|--------------|--------------|
| ![Screenshot_20240624_221608](https://github.com/javglex/RunBuddyModular/assets/6698872/f5f54a80-0218-46b2-b74e-63a316addbb1) | ![Screenshot_20240624_221524](https://github.com/javglex/RunBuddyModular/assets/6698872/270cc30b-122a-478d-91ed-81b1981c1d41) |


Main features:

* Registration (currently offline, but ready for online support)
* Authentication (currently offline, but ready for online support)
* Starting a run
* Tracking a run on google maps
* Saving a run, it's metrics, and the tracked route.
* Pausing a run mid-workout and having multiple routes
* Workout analytics
* Offline-first functionality
* Foreground service for tracking runs while app is closed or in background.
* WearOS heart-rate tracking
* WearOS workout controls




## Tech Stack (Dependencies)
* **ktor** for network api requests
* **room** for storing data locally
* **koin** for dependency injection (and DI across modules)
* **compose** for UI
* **kotlin coroutines** for async programming

Highlight folders:
