# Quackback Android SDK

The official Android SDK for [Quackback](https://quackback.com) — embed your feedback widget in any Android app with a single call.

## Requirements

- Android API 24+
- AndroidX

## Installation

Add the dependency to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.quackback:sdk:0.1.0")
}
```

Or with Groovy DSL (`build.gradle`):

```groovy
dependencies {
    implementation 'com.quackback:sdk:0.1.0'
}
```

## Quick Start

### 1. Configure in your Application class

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Quackback.configure(
            context = this,
            config = QuackbackConfig(
                appId = "YOUR_APP_ID",
                baseURL = "https://feedback.yourapp.com"
            )
        )
    }
}
```

### 2. Identify the current user

```kotlin
// With user attributes
Quackback.identify(
    userId = "user_123",
    email = "user@example.com",
    name = "Jane Smith"
)

// Or with an SSO token
Quackback.identify(ssoToken = "your-sso-token")
```

### 3. Show the floating trigger button

```kotlin
// In your Activity
Quackback.showTrigger()
```

### 4. Open programmatically

```kotlin
// Open the default board
Quackback.open()

// Open a specific board by slug
Quackback.open(board = "feature-requests")
```

## API

| Method | Description |
|---|---|
| `configure(context, config)` | Initialize the SDK. Call once in `Application.onCreate()`. |
| `identify(userId, email, name?, avatarURL?)` | Identify the current user with attributes. |
| `identify(ssoToken)` | Identify with an SSO token. |
| `logout()` | Clear the current user session. |
| `open(board?)` | Open the feedback panel, optionally to a specific board slug. |
| `close()` | Close the feedback panel. |
| `showTrigger()` | Install the floating trigger button on the current activity. |
| `hideTrigger()` | Remove the floating trigger button. |
| `on(event, handler)` | Subscribe to an event. Returns an `EventToken`. |
| `off(token)` | Unsubscribe using the token returned by `on`. |
| `destroy()` | Tear down all SDK state (useful in tests or on sign-out). |

## QuackbackConfig

| Property | Type | Default | Description |
|---|---|---|---|
| `appId` | `String` | required | Your Quackback app ID. |
| `baseURL` | `String` | required | Base URL of your Quackback instance. |
| `theme` | `QuackbackTheme` | `SYSTEM` | `LIGHT`, `DARK`, or `SYSTEM` (follows device setting). |
| `position` | `QuackbackPosition` | `BOTTOM_RIGHT` | Position of the trigger button: `BOTTOM_RIGHT` or `BOTTOM_LEFT`. |
| `buttonColor` | `String?` | `null` | Hex color for the trigger button (e.g. `"#2563EB"`). |
| `locale` | `String?` | `null` | BCP 47 locale tag to override the widget language (e.g. `"fr"`). |

## Events

Subscribe to widget events using `Quackback.on`:

| Event | Payload keys | Description |
|---|---|---|
| `QuackbackEvent.READY` | — | Widget has loaded and is ready. |
| `QuackbackEvent.VOTE` | `postId`, `type` | User voted on a post. |
| `QuackbackEvent.SUBMIT` | `postId`, `title` | User submitted a new post. |
| `QuackbackEvent.CLOSE` | — | User closed the widget. |
| `QuackbackEvent.NAVIGATE` | `board`, `postId` | User navigated within the widget. |

```kotlin
val token = Quackback.on(QuackbackEvent.SUBMIT) { data ->
    println("New post submitted: ${data["title"]}")
}

// Later, to unsubscribe:
Quackback.off(token)
```

## License

MIT
