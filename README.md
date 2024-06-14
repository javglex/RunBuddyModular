RunBuddy (READ ME AND PROJECT UNDER CONSTRUCTION. DO NOT BUILD.)
-----

Offline-first Android application for tracking runs (ktor/room/koin/multi-module/convention-plugins).
Includes wearOS implementation for controlling workouts and sending heart rate data.

## Getting Started
Clone this repository (main branch), build & run.

No auth key currently provided and app will only store data locally. (until backend migration is complete)

## Introduction
This run tracking application uses multi-module architecture to separate features and layers, each module configured via convention plugins to facilitate build config management.
This enforces the data flow rule when communicating between layers (data/domain/presentation).
One feature in particular (analytics screen) is separated into a dynamic feature, to showcase benefits of multi-module architecture.

## Overview
This app is an exercise for ATEC. It showcases the use of uses BLOC architecture,
themeing, styling, and clean code practices.

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
* 
## Tech Stack (Dependencies)
* **ktor** for network api requests
* **room** for storing data locally
* **koin** for dependency injection (and DI across modules)
* **compose** for UI
* **kotlin coroutines** for async programming

Highlight folders:
