# Family Map Client

An Android app that visualizes a user's family tree on an interactive map. Each life event (birth, marriage, death) for a person and their ancestors is plotted as a pin, color-coded by event type, with lines connecting family members and spouses.

## Features

- **Login / Register** against a Family Map server, using an auth token for all subsequent requests
- **Interactive map** of every life event for the logged-in user and their ancestors, with markers colored by event type
- **Family tree lines** connecting a person to their parents and spouse
- **Search** across people (by name) and events (by country, city, event type, or year)
- **Person and event detail views**, with a person's full life story and relatives
- **Filters** (Settings screen) to toggle life-story lines, family-tree lines, spouse lines, paternal/maternal ancestors, and male/female events independently

## Architecture

```
application/
  DataCache.java       - in-memory singleton holding the logged-in user's people/events,
                          relationship logic, and the active event/ancestor filters
  ServerProxy.java      - HTTP client for the server's login/register/person/event endpoints
  tasks/                - background tasks (login, register, data sync) run off the UI thread

userinterface/
  activities/           - MainActivity (map), SearchActivity, PersonActivity, EventActivity,
                          SettingsActivity
  activities/fragments/  - LoginFragment, MapsFragment, SettingsFragment
```

`DataCache` computes family relationships (parent/child/spouse) and ancestor filters once the data is loaded, so activities and fragments query it directly rather than recomputing relationships themselves.

## Requirements

- Android Studio (compileSdk/targetSdk 33, minSdk 24)
- A running Family Map server that implements the `/user/login`, `/user/register`, `/person`, and `/event` endpoints this client calls (not included in this repo)
- A Google Maps API key

## Setup

1. Clone the repo and open it in Android Studio.
2. Get a [Google Maps API key](https://developers.google.com/maps/documentation/android-sdk/get-api-key) and add it to a `secrets.properties` file in the project root (gitignored):
   ```
   MAPS_API_KEY=your_key_here
   ```
   A safe placeholder lives in `local.defaults.properties` so the project still builds without a real key (map tiles just won't load).
3. Build and run on an emulator or device.
4. On the login screen, enter the host/port of your running Family Map server along with your credentials.

## Testing

Unit tests cover `DataCache`'s relationship/filter logic and `ServerProxy`'s HTTP handling:

```bash
./gradlew test
```

`ServerProxyTest` is an integration test and expects a Family Map server running on `localhost:8080`.
